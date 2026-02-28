import java.io.*;
import java.net.*;
import java.util.*;

/**
 * LanClient.java
 * เชื่อมต่อไปยัง LanServer ด้วย TCP
 */
public class LanClient {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean running = false;

    private final String playerName;

    public interface ClientListener {
        void onConnected(String roomCode);
        void onPlayersChanged(List<String> players);
        void onRoomFull();
        void onError(String msg);
        void onDisconnected();
    }

    private ClientListener listener;

    public LanClient(String playerName) {
        this.playerName = playerName;
    }

    public void setListener(ClientListener l) { this.listener = l; }

    // ────────────────────────────────────────────────
    // เชื่อมต่อไปยัง host
    // ────────────────────────────────────────────────
    public void connect(String ip, int port) {
        Thread t = new Thread(() -> {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(ip, port), 5000);
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                running = true;
                System.out.println("[LanClient] เชื่อมต่อไปยัง " + ip + ":" + port);

                String line;
                while (running && (line = in.readLine()) != null) {
                    handleMessage(line);
                }
            } catch (SocketTimeoutException e) {
                if (listener != null)
                    javax.swing.SwingUtilities.invokeLater(() ->
                        listener.onError("ไม่สามารถเชื่อมต่อได้ — ตรวจสอบว่าอยู่ใน LAN เดียวกัน"));
            } catch (IOException e) {
                if (running && listener != null)
                    javax.swing.SwingUtilities.invokeLater(() ->
                        listener.onError("การเชื่อมต่อขาด: " + e.getMessage()));
            } finally {
                running = false;
                if (listener != null)
                    javax.swing.SwingUtilities.invokeLater(() -> listener.onDisconnected());
            }
        }, "LanClient-IO");
        t.setDaemon(true);
        t.start();
    }

    // ────────────────────────────────────────────────
    private void handleMessage(String msg) {
        System.out.println("[LanClient] รับ: " + msg);
        if (msg.startsWith("WELCOME:")) {
            String code = msg.substring(8);
            send("JOIN:" + playerName);
            if (listener != null)
                javax.swing.SwingUtilities.invokeLater(() -> listener.onConnected(code));

        } else if (msg.equals("FULL")) {
            if (listener != null)
                javax.swing.SwingUtilities.invokeLater(() -> listener.onRoomFull());
            disconnect();

        } else if (msg.equals("JOIN_OK")) {
            System.out.println("[LanClient] เข้าร่วมสำเร็จ");

        } else if (msg.startsWith("PLAYERS:")) {
            String raw = msg.substring(8);
            List<String> players = raw.isEmpty()
                ? new ArrayList<>()
                : Arrays.asList(raw.split(","));
            if (listener != null)
                javax.swing.SwingUtilities.invokeLater(() -> listener.onPlayersChanged(players));
        }
    }

    public void disconnect() {
        running = false;
        send("LEAVE");
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }

    private void send(String msg) { if (out != null) out.println(msg); }
    public boolean isConnected()  { return running; }
    public String getPlayerName() { return playerName; }
}