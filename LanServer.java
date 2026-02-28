import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * LanServer.java
 * เครื่อง Host — broadcast ตัวเองผ่าน UDP ทุก 2 วิ (แบบ Minecraft LAN)
 * รับ TCP connection จาก client สูงสุด 3 คน (รวม host เอง)
 */
public class LanServer {

    public static final int TCP_PORT       = 19566; // port TCP รับ client
    public static final int UDP_BROADCAST_PORT = 19567; // port UDP broadcast
    public static final int MAX_PLAYERS    = 3;

    private final String roomCode;
    private final String hostName;

    private ServerSocket tcpServer;
    private volatile boolean running = false;

    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public interface LobbyListener {
        void onPlayersChanged(List<String> players); // เรียกใน EDT
    }

    private LobbyListener lobbyListener;

    // ────────────────────────────────────────────────
    public LanServer(String hostName) {
        this.hostName = hostName;
        this.roomCode = generateCode();
    }

    private static String generateCode() {
        Random r = new Random();
        int n = 10000000 + r.nextInt(90000000);
        return String.valueOf(n); // 8 หลัก
    }

    public void setLobbyListener(LobbyListener l) { this.lobbyListener = l; }
    public String getRoomCode() { return roomCode; }

    // ────────────────────────────────────────────────
    // เริ่ม server
    // ────────────────────────────────────────────────
    public void start() throws IOException {
        tcpServer = new ServerSocket(TCP_PORT);
        running = true;

        // thread รับ connection
        Thread acceptThread = new Thread(this::acceptLoop, "LanServer-Accept");
        acceptThread.setDaemon(true);
        acceptThread.start();

        // thread broadcast UDP
        Thread broadcastThread = new Thread(this::broadcastLoop, "LanServer-Broadcast");
        broadcastThread.setDaemon(true);
        broadcastThread.start();

        System.out.println("[LanServer] เปิดแล้ว port=" + TCP_PORT + " รหัส=" + roomCode);
    }

    // ────────────────────────────────────────────────
    // UDP Broadcast ทุก 2 วิ
    // ────────────────────────────────────────────────
    private void broadcastLoop() {
        try (DatagramSocket udp = new DatagramSocket()) {
            udp.setBroadcast(true);
            // format: "FIRSTLOVE:<roomCode>:<hostName>:<TCP_PORT>"
            String msg = "FIRSTLOVE:" + roomCode + ":" + hostName + ":" + TCP_PORT;
            byte[] buf = msg.getBytes("UTF-8");
            InetAddress broadcast = InetAddress.getByName("255.255.255.255");
            DatagramPacket pkt = new DatagramPacket(buf, buf.length, broadcast, UDP_BROADCAST_PORT);
            while (running) {
                udp.send(pkt);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            if (running) System.err.println("[LanServer] broadcast error: " + e.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // TCP Accept Loop
    // ────────────────────────────────────────────────
    private void acceptLoop() {
        while (running) {
            try {
                Socket socket = tcpServer.accept();
                if (clients.size() >= MAX_PLAYERS) {
                    // ห้องเต็ม
                    PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                    pw.println("FULL");
                    socket.close();
                    continue;
                }
                ClientHandler handler = new ClientHandler(socket);
                handler.start();
            } catch (IOException e) {
                if (running) System.err.println("[LanServer] accept: " + e.getMessage());
            }
        }
    }

    // ────────────────────────────────────────────────
    // Broadcast player list ให้ทุกคน
    // ────────────────────────────────────────────────
    void broadcastPlayerList() {
        List<String> names = new ArrayList<>();
        for (ClientHandler c : clients) names.add(c.playerName);

        String msg = "PLAYERS:" + String.join(",", names);
        for (ClientHandler c : clients) c.send(msg);

        if (lobbyListener != null) {
            javax.swing.SwingUtilities.invokeLater(() -> lobbyListener.onPlayersChanged(names));
        }
        System.out.println("[LanServer] broadcast: " + msg);
    }

    // ────────────────────────────────────────────────
    public void stop() {
        running = false;
        for (ClientHandler c : clients) c.disconnect();
        try { if (tcpServer != null) tcpServer.close(); } catch (IOException ignored) {}
    }

    // ════════════════════════════════════════════════
    // ClientHandler
    // ════════════════════════════════════════════════
    class ClientHandler extends Thread {
        final Socket socket;
        PrintWriter out;
        BufferedReader in;
        String playerName = "";

        ClientHandler(Socket socket) {
            this.socket = socket;
            setDaemon(true);
        }

        @Override public void run() {
            try {
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

                send("WELCOME:" + roomCode);

                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("JOIN:")) {
                        playerName = line.substring(5).trim();
                        clients.add(this);
                        send("JOIN_OK");
                        broadcastPlayerList();
                    } else if (line.equals("PING")) {
                        send("PONG");
                    } else if (line.equals("LEAVE")) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("[LanServer] " + playerName + " หลุด: " + e.getMessage());
            } finally {
                clients.remove(this);
                disconnect();
                broadcastPlayerList();
            }
        }

        void send(String msg) { if (out != null) out.println(msg); }
        void disconnect() {
            try { if (socket != null && !socket.isClosed()) socket.close(); } catch (IOException ignored) {}
        }
    }
}