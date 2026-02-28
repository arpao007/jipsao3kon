import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import javax.swing.*;

/**
 * MultiplayerLobby.java
 * UI ครบชุด:
 *   STEP 1 — ใส่ชื่อ
 *   STEP 2 — สร้างห้อง / เข้าร่วม
 *   STEP 3 — ห้องรอ (Lobby)
 */
public class MultiplayerLobby extends JPanel {

    private final CardLayout parentLayout;
    private final JPanel     parentContainer;

    private final CardLayout innerLayout    = new CardLayout();
    private final JPanel     innerContainer = new JPanel(innerLayout);

    private String myName = "";

    // networking
    private LanServer    server;
    private LanClient    client;
    private LanDiscovery discovery;

    // lobby UI refs
    private final JLabel[] slotLabels = new JLabel[3];
    private final JPanel[] slotCards  = new JPanel[3];
    private JLabel roomCodeLabel;
    private JLabel lobbyStatusLabel;
    private JPanel roomListPanel;

    // ────────────────────────────────────────────────
    public MultiplayerLobby(CardLayout parentLayout, JPanel parentContainer) {
        this.parentLayout    = parentLayout;
        this.parentContainer = parentContainer;

        setLayout(null);
        setBackground(new Color(12, 10, 30));

        innerContainer.setOpaque(false);
        innerContainer.setBounds(0, 0, 1200, 800);
        add(innerContainer);

        innerContainer.add(buildNamePanel(),   "NAME");
        innerContainer.add(buildChoicePanel(), "CHOICE");
        innerContainer.add(buildLobbyPanel(),  "LOBBY");

        innerLayout.show(innerContainer, "NAME");
    }

    // ════════════════════════════════════════════════
    // STEP 1 — ใส่ชื่อ
    // ════════════════════════════════════════════════
    private JPanel buildNamePanel() {
        JPanel p = makeBgPanel();

        addLabel(p, "🎮  Multiplayer",
            new Font("Tahoma", Font.BOLD, 58), new Color(255, 200, 80), 0, 110, 1200, 80);
        addLabel(p, "ใส่ชื่อของคุณก่อนเข้าเล่น",
            new Font("Tahoma", Font.PLAIN, 24), new Color(200, 180, 255), 0, 210, 1200, 36);

        JTextField nameField = styledTextField();
        nameField.setBounds(400, 268, 400, 60);
        p.add(nameField);

        JButton nextBtn = makeBtn("ถัดไป  →", new Color(110, 70, 220));
        nextBtn.setBounds(490, 360, 220, 54);
        nextBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) { shake(nameField); return; }
            myName = name;
            innerLayout.show(innerContainer, "CHOICE");
        });
        nameField.addActionListener(e -> nextBtn.doClick());
        p.add(nextBtn);

        JButton backBtn = makeBtn("← กลับ", new Color(60, 58, 90));
        backBtn.setBounds(50, 725, 150, 44);
        backBtn.addActionListener(e -> parentLayout.show(parentContainer, "MENU"));
        p.add(backBtn);

        return p;
    }

    // ════════════════════════════════════════════════
    // STEP 2 — สร้าง / เข้าร่วม
    // ════════════════════════════════════════════════
    private JPanel buildChoicePanel() {
        JPanel p = makeBgPanel();

        addLabel(p, "เลือกโหมด",
            new Font("Tahoma", Font.BOLD, 48), new Color(255, 200, 80), 0, 80, 1200, 70);

        // ── Card สร้างห้อง ──
        JPanel cc = makeCard();
        cc.setBounds(230, 190, 320, 360);
        addLabel(cc, "🏠", new Font("Segoe UI Emoji", Font.PLAIN, 72), Color.WHITE, 0, 16, 320, 90);
        addLabel(cc, "สร้างห้อง", new Font("Tahoma", Font.BOLD, 30), new Color(255, 220, 100), 0, 112, 320, 42);
        addLabel(cc, "<html><div style='text-align:center;color:#aaa;font-size:13px'>สร้างห้องใหม่<br>แล้วรอเพื่อนที่อยู่ใน<br>Wi-Fi เดียวกันเข้าร่วม</div></html>",
            new Font("Tahoma", Font.PLAIN, 14), Color.GRAY, 10, 158, 300, 80);
        JButton createBtn = makeBtn("สร้างห้อง", new Color(60, 150, 70));
        createBtn.setBounds(40, 264, 240, 52);
        createBtn.addActionListener(e -> handleCreateRoom());
        cc.add(createBtn);
        p.add(cc);

        // ── Card เข้าร่วม ──
        JPanel jc = makeCard();
        jc.setBounds(650, 190, 320, 360);
        addLabel(jc, "🚪", new Font("Segoe UI Emoji", Font.PLAIN, 72), Color.WHITE, 0, 16, 320, 90);
        addLabel(jc, "เข้าร่วม", new Font("Tahoma", Font.BOLD, 30), new Color(100, 200, 255), 0, 112, 320, 42);
        addLabel(jc, "ห้องที่พบใน Wi-Fi เดียวกัน:", new Font("Tahoma", Font.PLAIN, 14),
            new Color(150, 140, 180), 0, 158, 320, 24);

        // รายการห้อง scroll
        roomListPanel = new JPanel();
        roomListPanel.setLayout(new BoxLayout(roomListPanel, BoxLayout.Y_AXIS));
        roomListPanel.setBackground(new Color(20, 18, 50));
        JScrollPane scroll = new JScrollPane(roomListPanel);
        scroll.setBounds(14, 185, 292, 120);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(70, 60, 120), 1, true));
        scroll.getViewport().setBackground(new Color(20, 18, 50));
        jc.add(scroll);

        JButton joinBtn = makeBtn("เข้าร่วม", new Color(40, 110, 200));
        joinBtn.setBounds(40, 316, 240, 44);
        joinBtn.setEnabled(false);
        jc.add(joinBtn);

        // เก็บ room ที่เลือก
        final LanDiscovery.RoomInfo[] selected = {null};
        joinBtn.addActionListener(e -> {
            if (selected[0] != null) handleJoinRoom(selected[0], joinBtn);
        });

        // เริ่ม scan
        discovery = new LanDiscovery();
        discovery.setListener(rooms -> {
            roomListPanel.removeAll();
            selected[0] = null;
            joinBtn.setEnabled(false);
            if (rooms.isEmpty()) {
                JLabel empty = new JLabel("  กำลังค้นหาห้อง...", SwingConstants.LEFT);
                empty.setForeground(new Color(120, 110, 160));
                empty.setFont(new Font("Tahoma", Font.ITALIC, 14));
                roomListPanel.add(empty);
            } else {
                ButtonGroup bg = new ButtonGroup();
                for (LanDiscovery.RoomInfo room : rooms) {
                    JToggleButton rb = new JToggleButton(room.toString());
                    rb.setFont(new Font("Tahoma", Font.PLAIN, 14));
                    rb.setBackground(new Color(30, 28, 65));
                    rb.setForeground(new Color(180, 170, 220));
                    rb.setFocusPainted(false);
                    rb.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                    rb.setMaximumSize(new Dimension(290, 34));
                    rb.addActionListener(ev -> {
                        selected[0] = room;
                        joinBtn.setEnabled(true);
                    });
                    bg.add(rb);
                    roomListPanel.add(rb);
                }
            }
            roomListPanel.revalidate();
            roomListPanel.repaint();
        });
        discovery.start();

        p.add(jc);

        JButton backBtn = makeBtn("← กลับ", new Color(60, 58, 90));
        backBtn.setBounds(50, 725, 150, 44);
        backBtn.addActionListener(e -> {
            discovery.stop();
            innerLayout.show(innerContainer, "NAME");
        });
        p.add(backBtn);

        return p;
    }

    // ════════════════════════════════════════════════
    // STEP 3 — ห้องรอ
    // ════════════════════════════════════════════════
    private JPanel buildLobbyPanel() {
        JPanel p = makeBgPanel();

        addLabel(p, "ห้องรอ",
            new Font("Tahoma", Font.BOLD, 52), new Color(255, 200, 80), 0, 36, 1200, 68);

        // รหัสห้อง
        roomCodeLabel = new JLabel("--------", SwingConstants.CENTER);
        roomCodeLabel.setFont(new Font("Courier New", Font.BOLD, 34));
        roomCodeLabel.setForeground(new Color(80, 220, 255));
        roomCodeLabel.setBounds(0, 112, 1200, 48);
        p.add(roomCodeLabel);

        addLabel(p, "(แจ้งรหัสนี้ให้เพื่อน หรือให้เพื่อนอยู่ใน Wi-Fi เดียวกัน ห้องจะขึ้นอัตโนมัติ)",
            new Font("Tahoma", Font.ITALIC, 14), new Color(120, 110, 170), 0, 162, 1200, 26);

        // 3 slots แนวตั้ง
        int sw = 460, sh = 80, sx = (1200 - sw) / 2, sy = 210;
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            slotCards[i] = new JPanel(null) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    boolean hasPlayer = slotLabels[idx] != null
                        && !slotLabels[idx].getText().contains("รอผู้เล่น");
                    g2.setColor(hasPlayer ? new Color(40, 34, 90) : new Color(24, 22, 52));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                    g2.setColor(hasPlayer ? new Color(130, 90, 240) : new Color(60, 54, 110));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 18, 18);
                }
            };
            slotCards[i].setOpaque(false);
            slotCards[i].setBounds(sx, sy + i * (sh + 18), sw, sh);

            JLabel num = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
            num.setFont(new Font("Tahoma", Font.BOLD, 22));
            num.setForeground(new Color(130, 110, 200));
            num.setBounds(8, 0, 40, sh);
            slotCards[i].add(num);

            slotLabels[i] = new JLabel("— รอผู้เล่น —", SwingConstants.CENTER);
            slotLabels[i].setFont(new Font("Tahoma", Font.PLAIN, 22));
            slotLabels[i].setForeground(new Color(100, 90, 150));
            slotLabels[i].setBounds(48, 0, sw - 56, sh);
            slotCards[i].add(slotLabels[i]);

            p.add(slotCards[i]);
        }

        lobbyStatusLabel = new JLabel("รอผู้เล่น 0/3", SwingConstants.CENTER);
        lobbyStatusLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lobbyStatusLabel.setForeground(new Color(160, 150, 200));
        lobbyStatusLabel.setBounds(0, 506, 1200, 32);
        p.add(lobbyStatusLabel);

        JButton leaveBtn = makeBtn("← ออกจากห้อง", new Color(140, 44, 44));
        leaveBtn.setBounds(50, 725, 230, 44);
        leaveBtn.addActionListener(e -> handleLeave());
        p.add(leaveBtn);

        return p;
    }

    // ════════════════════════════════════════════════
    // Handlers
    // ════════════════════════════════════════════════

    private void handleCreateRoom() {
        server = new LanServer(myName);
        server.setLobbyListener(players ->
            SwingUtilities.invokeLater(() -> updateLobbySlots(players)));

        try {
            server.start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "เปิด Server ไม่ได้: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // host connect ตัวเองผ่าน localhost
        client = new LanClient(myName);
        setupClientListener(true);
        // รอ server เปิดก่อน 200ms
        Timer t = new Timer(250, e -> client.connect("127.0.0.1", LanServer.TCP_PORT));
        t.setRepeats(false);
        t.start();

        roomCodeLabel.setText(server.getRoomCode());
        innerLayout.show(innerContainer, "LOBBY");
    }

    private void handleJoinRoom(LanDiscovery.RoomInfo room, JButton joinBtn) {
        joinBtn.setEnabled(false);
        joinBtn.setText("กำลังเชื่อมต่อ...");
        discovery.stop();

        client = new LanClient(myName);
        setupClientListener(false);
        client.connect(room.ip, room.port);

        roomCodeLabel.setText(room.roomCode);
        innerLayout.show(innerContainer, "LOBBY");
    }

    private void setupClientListener(boolean isHost) {
        client.setListener(new LanClient.ClientListener() {
            @Override public void onConnected(String roomCode) {
                roomCodeLabel.setText(roomCode);
            }
            @Override public void onPlayersChanged(List<String> players) {
                updateLobbySlots(players);
            }
            @Override public void onRoomFull() {
                JOptionPane.showMessageDialog(MultiplayerLobby.this,
                    "❌ ห้องเต็มแล้ว!", "ห้องเต็ม", JOptionPane.WARNING_MESSAGE);
                innerLayout.show(innerContainer, "CHOICE");
            }
            @Override public void onError(String msg) {
                JOptionPane.showMessageDialog(MultiplayerLobby.this,
                    "❌ " + msg, "เชื่อมต่อไม่ได้", JOptionPane.ERROR_MESSAGE);
                innerLayout.show(innerContainer, "CHOICE");
            }
            @Override public void onDisconnected() {
                if (isVisible())
                    JOptionPane.showMessageDialog(MultiplayerLobby.this,
                        "การเชื่อมต่อขาด", "Disconnected", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void updateLobbySlots(List<String> players) {
        for (int i = 0; i < 3; i++) {
            if (i < players.size()) {
                String name = players.get(i);
                boolean isMe = name.equals(myName);
                slotLabels[i].setText(isMe ? name + "  (Me)" : name);
                slotLabels[i].setForeground(isMe
                    ? new Color(120, 220, 130)   // เขียว = ตัวเอง
                    : new Color(200, 190, 255));  // ม่วงอ่อน = คนอื่น
                slotLabels[i].setFont(new Font("Tahoma",
                    isMe ? Font.BOLD : Font.PLAIN, 22));
            } else {
                slotLabels[i].setText("— รอผู้เล่น —");
                slotLabels[i].setForeground(new Color(100, 90, 150));
                slotLabels[i].setFont(new Font("Tahoma", Font.PLAIN, 22));
            }
            slotCards[i].repaint();
        }
        lobbyStatusLabel.setText("ผู้เล่น " + players.size() + " / 3");
    }

    private void handleLeave() {
        if (client  != null) client.disconnect();
        if (server  != null) server.stop();
        if (discovery != null) discovery.stop();
        client = null; server = null;
        innerLayout.show(innerContainer, "CHOICE");
        // reset slots
        for (int i = 0; i < 3; i++) {
            slotLabels[i].setText("— รอผู้เล่น —");
            slotLabels[i].setForeground(new Color(100, 90, 150));
            slotCards[i].repaint();
        }
    }

    // ════════════════════════════════════════════════
    // UI Helpers
    // ════════════════════════════════════════════════

    private JPanel makeBgPanel() {
        JPanel p = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0,0, new Color(12,10,30), 0,800, new Color(40,25,70));
                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(1200, 800));
        return p;
    }

    private JPanel makeCard() {
        return new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 26, 65));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),22,22);
                g2.setColor(new Color(100, 80, 180));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,22,22);
            }
        };
    }

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Tahoma", Font.BOLD, 18));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(bg.brighter(), 2, true));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private JTextField styledTextField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Tahoma", Font.PLAIN, 26));
        f.setHorizontalAlignment(JTextField.CENTER);
        f.setBackground(new Color(28, 24, 58));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(130, 90, 220), 2, true),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        return f;
    }

    private void addLabel(JPanel p, String text, Font font, Color color, int x, int y, int w, int h) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(font);
        lbl.setForeground(color);
        lbl.setBounds(x, y, w, h);
        p.add(lbl);
    }

    private void shake(JComponent c) {
        int origX = c.getX();
        Timer t = new Timer(30, null);
        final int[] step = {0};
        int[] offsets = {-8, 8, -6, 6, -4, 4, -2, 2, 0};
        t.addActionListener(e -> {
            if (step[0] < offsets.length) {
                c.setLocation(origX + offsets[step[0]++], c.getY());
            } else {
                c.setLocation(origX, c.getY());
                ((Timer) e.getSource()).stop();
            }
        });
        t.start();
    }
}