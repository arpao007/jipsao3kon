import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * LanDiscovery.java
 * ฟัง UDP broadcast จาก LanServer แล้วแจ้งว่าพบห้องอะไรบ้าง
 */
public class LanDiscovery {

    public static class RoomInfo {
        public final String roomCode;
        public final String hostName;
        public final String ip;
        public final int    port;
        public long lastSeen; // ms

        RoomInfo(String roomCode, String hostName, String ip, int port) {
            this.roomCode = roomCode;
            this.hostName = hostName;
            this.ip       = ip;
            this.port     = port;
            this.lastSeen = System.currentTimeMillis();
        }

        @Override public String toString() {
            return roomCode + "  (Host: " + hostName + ")";
        }
    }

    public interface DiscoveryListener {
        void onRoomsChanged(List<RoomInfo> rooms); // เรียกใน EDT
    }

    private final List<RoomInfo>      rooms    = new CopyOnWriteArrayList<>();
    private DiscoveryListener         listener;
    private volatile boolean          running  = false;
    private DatagramSocket            socket;

    // ────────────────────────────────────────────────
    public void setListener(DiscoveryListener l) { this.listener = l; }

    public void start() {
        running = true;
        Thread t = new Thread(this::listenLoop, "LanDiscovery");
        t.setDaemon(true);
        t.start();

        // thread ลบห้องเก่า (ไม่ได้ broadcast มา > 6 วิ)
        Thread cleaner = new Thread(this::cleanLoop, "LanDiscovery-Clean");
        cleaner.setDaemon(true);
        cleaner.start();
    }

    public void stop() {
        running = false;
        if (socket != null) socket.close();
    }

    // ────────────────────────────────────────────────
    private void listenLoop() {
        try {
            socket = new DatagramSocket(LanServer.UDP_BROADCAST_PORT);
            socket.setSoTimeout(3000);
            byte[] buf = new byte[256];
            while (running) {
                DatagramPacket pkt = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(pkt);
                    String msg = new String(pkt.getData(), 0, pkt.getLength(), "UTF-8");
                    String senderIp = pkt.getAddress().getHostAddress();
                    handleBroadcast(msg, senderIp);
                } catch (SocketTimeoutException ignored) {
                    // timeout → loop ต่อ
                }
            }
        } catch (Exception e) {
            if (running) System.err.println("[LanDiscovery] " + e.getMessage());
        }
    }

    private void handleBroadcast(String msg, String ip) {
        // format: "FIRSTLOVE:<roomCode>:<hostName>:<port>"
        if (!msg.startsWith("FIRSTLOVE:")) return;
        String[] parts = msg.split(":");
        if (parts.length < 4) return;

        String code     = parts[1];
        String hostName = parts[2];
        int    port;
        try { port = Integer.parseInt(parts[3]); } catch (NumberFormatException e) { return; }

        // หาว่ามีอยู่แล้วไหม
        boolean found = false;
        for (RoomInfo r : rooms) {
            if (r.roomCode.equals(code)) {
                r.lastSeen = System.currentTimeMillis();
                found = true;
                break;
            }
        }
        if (!found) {
            rooms.add(new RoomInfo(code, hostName, ip, port));
            notifyListener();
            System.out.println("[LanDiscovery] พบห้องใหม่: " + code + " จาก " + ip);
        }
    }

    private void cleanLoop() {
        while (running) {
            try {
                Thread.sleep(3000);
                long now = System.currentTimeMillis();
                boolean changed = rooms.removeIf(r -> now - r.lastSeen > 6000);
                if (changed) notifyListener();
            } catch (InterruptedException ignored) {}
        }
    }

    private void notifyListener() {
        if (listener != null) {
            List<RoomInfo> copy = new ArrayList<>(rooms);
            javax.swing.SwingUtilities.invokeLater(() -> listener.onRoomsChanged(copy));
        }
    }

    public List<RoomInfo> getRooms() { return new ArrayList<>(rooms); }
}