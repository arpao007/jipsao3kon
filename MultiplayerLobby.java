import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

/**
 * MultiplayerLobby.java  — Cozy Romantic Theme (ให้เข้ากับ MainMenu)
 *   STEP 1 — ใส่ชื่อ
 *   STEP 2 — สร้างห้อง / เข้าร่วม
 *   STEP 3 — ห้องรอ (Lobby)
 */
public class MultiplayerLobby extends JPanel {

    // ── Palette (เดียวกับ MainMenu) ──────────────────
    private static final Color BG_TOP      = new Color(0xF7D6E0);
    private static final Color BG_BOT      = new Color(0xD9AED0);
    private static final Color PINK_DEEP   = new Color(0xE8759A);
    private static final Color PINK_LIGHT  = new Color(0xF5A8C5);
    private static final Color LILAC       = new Color(0xC9A0DC);
    private static final Color LILAC_DARK  = new Color(0xA076BB);
    private static final Color GOLD        = new Color(0xF0C060);
    private static final Color TEXT_WHITE  = new Color(0xFFF5FA);
    private static final Color TEXT_DARK   = new Color(0x5C3060);
    private static final Color CARD_BG     = new Color(255, 240, 248, 210);
    private static final Color CARD_BORDER = new Color(0xE8A0C0);
    private static final Color ROOM_BLUE   = new Color(0x7BBFD4);
    private static final Color GREEN_SOFT  = new Color(0x7DC88A);

    // ── Navigation ───────────────────────────────────
    private final CardLayout parentLayout;
    private final JPanel     parentContainer;
    private final CardLayout innerLayout    = new CardLayout();
    private final JPanel     innerContainer = new JPanel(innerLayout);

    private String myName = "";

    // ── Networking ───────────────────────────────────
    private LanServer    server;
    private LanClient    client;
    private LanDiscovery discovery;

    // ── Lobby UI refs ────────────────────────────────
    private final JLabel[] slotLabels = new JLabel[3];
    private final JPanel[] slotCards  = new JPanel[3];
    private JLabel roomCodeLabel;
    private JLabel lobbyStatusLabel;
    private JPanel roomListPanel;

    // ── Petal Animation ──────────────────────────────
    private final List<Petal> petals = new ArrayList<>();
    private Timer animTimer;
    private float wiggleTime = 0f;

    // ════════════════════════════════════════════════
    public MultiplayerLobby(CardLayout parentLayout, JPanel parentContainer) {
        this.parentLayout    = parentLayout;
        this.parentContainer = parentContainer;

        setLayout(null);
        setOpaque(false);
        initPetals();
        startAnimation();

        innerContainer.setOpaque(false);
        innerContainer.setBounds(0, 0, 1200, 800);
        add(innerContainer);

        innerContainer.add(buildNamePanel(),   "NAME");
        innerContainer.add(buildChoicePanel(), "CHOICE");
        innerContainer.add(buildLobbyPanel(),  "LOBBY");

        innerLayout.show(innerContainer, "NAME");

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                innerContainer.setBounds(0, 0, getWidth(), getHeight());
            }
        });
    }

    // ════════════════════════════════════════════════
    // Background painting (shared across all steps)
    // ════════════════════════════════════════════════
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gradient bg
        g2.setPaint(new GradientPaint(0, 0, BG_TOP, 0, h, BG_BOT));
        g2.fillRect(0, 0, w, h);

        // Falling petals
        for (Petal p : petals) drawPetal(g2, p, w);

        // Wave ribbon top
        Path2D wave = new Path2D.Float();
        wave.moveTo(0, 0);
        wave.curveTo(w * 0.25, h * 0.04, w * 0.5, -h * 0.012, w * 0.75, h * 0.03);
        wave.curveTo(w * 0.875, h * 0.047, w * 0.96, h * 0.012, w, h * 0.025);
        wave.lineTo(w, 0);
        wave.closePath();
        g2.setColor(new Color(0xF9C4DA));
        g2.fill(wave);

        // Wave ribbon bottom
        Path2D waveB = new Path2D.Float();
        waveB.moveTo(0, h);
        waveB.curveTo(w * 0.17, h * 0.956, w * 0.42, h * 0.981, w * 0.58, h * 0.963);
        waveB.curveTo(w * 0.75, h * 0.944, w * 0.92, h * 0.975, w, h * 0.95);
        waveB.lineTo(w, h);
        waveB.closePath();
        g2.setColor(new Color(0xC890C8, false));
        g2.fill(waveB);
    }

    // ════════════════════════════════════════════════
    // STEP 1 — ใส่ชื่อ
    // ════════════════════════════════════════════════
    private JPanel buildNamePanel() {
        JPanel p = makeBgPanel();

        // Heart decorations
        addDecorLabel(p, "♡", 80, 120, 52, new Color(PINK_DEEP.getRed(), PINK_DEEP.getGreen(), PINK_DEEP.getBlue(), 120));
        addDecorLabel(p, "✿", 1080, 150, 48, new Color(LILAC.getRed(), LILAC.getGreen(), LILAC.getBlue(), 130));
        addDecorLabel(p, "♡", 140, 600, 36, new Color(PINK_LIGHT.getRed(), PINK_LIGHT.getGreen(), PINK_LIGHT.getBlue(), 100));
        addDecorLabel(p, "✿", 1020, 580, 40, new Color(LILAC_DARK.getRed(), LILAC_DARK.getGreen(), LILAC_DARK.getBlue(), 110));

        // Title
        JLabel title = new JLabel("✦  Multiplayer  ✦", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 56));
        title.setForeground(GOLD);
        title.setBounds(0, 110, 1200, 76);
        addShadowLabel(p, title);

        // Subtitle
        addLabel(p, "ใส่ชื่อของคุณก่อนเข้าเล่น 💕",
            new Font("Tahoma", Font.PLAIN, 22), TEXT_DARK, 0, 205, 1200, 34);

        // Card container
        JPanel card = makeGlassCard(360, 260, 480, 200);
        p.add(card);

        // Input field
        JTextField nameField = makePinkTextField();
        nameField.setBounds(40, 40, 400, 56);
        card.add(nameField);

        // Next button
        JButton nextBtn = makePinkButton("ถัดไป  →", PINK_DEEP);
        nextBtn.setBounds(115, 120, 250, 52);
        nextBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) { shake(nameField); return; }
            myName = name;
            innerLayout.show(innerContainer, "CHOICE");
        });
        nameField.addActionListener(e -> nextBtn.doClick());
        card.add(nextBtn);

        // Back button
        JButton backBtn = makePinkButton("← กลับ", new Color(0xBB8899));
        backBtn.setBounds(40, 725, 150, 44);
        backBtn.addActionListener(e -> parentLayout.show(parentContainer, "MENU"));
        p.add(backBtn);

        return p;
    }

    // ════════════════════════════════════════════════
    // STEP 2 — สร้าง / เข้าร่วม
    // ════════════════════════════════════════════════
    private JPanel buildChoicePanel() {
        JPanel p = makeBgPanel();

        // Heart decorations
        addDecorLabel(p, "♡", 70, 100, 46, new Color(PINK_DEEP.getRed(), PINK_DEEP.getGreen(), PINK_DEEP.getBlue(), 110));
        addDecorLabel(p, "✿", 1090, 130, 44, new Color(LILAC.getRed(), LILAC.getGreen(), LILAC.getBlue(), 120));

        // Title
        JLabel title = new JLabel("✦  เลือกโหมด  ✦", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 46));
        title.setForeground(GOLD);
        title.setBounds(0, 70, 1200, 64);
        addShadowLabel(p, title);

        // ── Card สร้างห้อง ──────────────────────────
        JPanel cc = makeGlassCard(210, 165, 340, 400);
        p.add(cc);

        addLabel(cc, "🏠", new Font("Segoe UI Emoji", Font.PLAIN, 64), Color.WHITE, 0, 16, 340, 80);
        addLabel(cc, "สร้างห้อง", new Font("Tahoma", Font.BOLD, 28), TEXT_DARK, 0, 100, 340, 42);
        addLabel(cc, "<html><div style='text-align:center;color:#8860A0;font-size:13px'>"
            + "สร้างห้องใหม่<br>แล้วรอเพื่อนที่อยู่ใน<br>Wi-Fi เดียวกันเข้าร่วม</div></html>",
            new Font("Tahoma", Font.PLAIN, 14), LILAC_DARK, 10, 148, 320, 80);

        JButton createBtn = makePinkButton("✦ สร้างห้อง", new Color(0xD46890));
        createBtn.setBounds(45, 290, 250, 52);
        createBtn.addActionListener(e -> handleCreateRoom());
        cc.add(createBtn);

        // ── Card เข้าร่วม ───────────────────────────
        JPanel jc = makeGlassCard(650, 165, 340, 400);
        p.add(jc);

        addLabel(jc, "🚪", new Font("Segoe UI Emoji", Font.PLAIN, 64), Color.WHITE, 0, 16, 340, 80);
        addLabel(jc, "เข้าร่วม", new Font("Tahoma", Font.BOLD, 28), ROOM_BLUE, 0, 100, 340, 42);
        addLabel(jc, "ห้องที่พบใน Wi-Fi เดียวกัน:", new Font("Tahoma", Font.PLAIN, 13),
            new Color(0x8877AA), 0, 148, 340, 24);

        // Room list scroll
        roomListPanel = new JPanel();
        roomListPanel.setLayout(new BoxLayout(roomListPanel, BoxLayout.Y_AXIS));
        roomListPanel.setBackground(new Color(255, 235, 248));
        JScrollPane scroll = new JScrollPane(roomListPanel);
        scroll.setBounds(20, 176, 300, 110);
        scroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1, true));
        scroll.getViewport().setBackground(new Color(255, 235, 248));
        jc.add(scroll);

        JButton joinBtn = makePinkButton("✦ เข้าร่วม", new Color(0x7BAAD4));
        joinBtn.setBounds(45, 316, 250, 44);
        joinBtn.setEnabled(false);
        jc.add(joinBtn);

        final LanDiscovery.RoomInfo[] selected = {null};
        joinBtn.addActionListener(e -> {
            if (selected[0] != null) handleJoinRoom(selected[0], joinBtn);
        });

        // Discovery scan
        discovery = new LanDiscovery();
        discovery.setListener(rooms -> {
            roomListPanel.removeAll();
            selected[0] = null;
            joinBtn.setEnabled(false);
            if (rooms.isEmpty()) {
                JLabel empty = new JLabel("  💭 กำลังค้นหาห้อง...", SwingConstants.LEFT);
                empty.setForeground(new Color(0xAA90BB));
                empty.setFont(new Font("Tahoma", Font.ITALIC, 13));
                roomListPanel.add(empty);
            } else {
                ButtonGroup bg = new ButtonGroup();
                for (LanDiscovery.RoomInfo room : rooms) {
                    JToggleButton rb = new JToggleButton("♡  " + room.toString());
                    rb.setFont(new Font("Tahoma", Font.PLAIN, 13));
                    rb.setBackground(new Color(255, 235, 248));
                    rb.setForeground(TEXT_DARK);
                    rb.setFocusPainted(false);
                    rb.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                    rb.setMaximumSize(new Dimension(298, 34));
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

        // Back
        JButton backBtn = makePinkButton("← กลับ", new Color(0xBB8899));
        backBtn.setBounds(40, 725, 150, 44);
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

        // Decorations
        addDecorLabel(p, "♡", 60, 90, 50, new Color(PINK_DEEP.getRed(), PINK_DEEP.getGreen(), PINK_DEEP.getBlue(), 110));
        addDecorLabel(p, "✿", 1100, 120, 46, new Color(LILAC.getRed(), LILAC.getGreen(), LILAC.getBlue(), 120));

        // Title
        JLabel title = new JLabel("♡  ห้องรอ  ♡", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 50));
        title.setForeground(GOLD);
        title.setBounds(0, 36, 1200, 68);
        addShadowLabel(p, title);

        // Room code
        roomCodeLabel = new JLabel("--------", SwingConstants.CENTER);
        roomCodeLabel.setFont(new Font("Courier New", Font.BOLD, 36));
        roomCodeLabel.setForeground(ROOM_BLUE);
        roomCodeLabel.setBounds(0, 114, 1200, 52);
        p.add(roomCodeLabel);

        addLabel(p, "(แจ้งรหัสนี้ให้เพื่อน หรือให้เพื่อนอยู่ใน Wi-Fi เดียวกัน ห้องจะขึ้นอัตโนมัติ)",
            new Font("Tahoma", Font.ITALIC, 13), new Color(0xAA88BB), 0, 168, 1200, 26);

        // 3 Player slots
        int sw = 480, sh = 84, sx = (1200 - sw) / 2, sy = 210;
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            slotCards[i] = new JPanel(null) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    boolean hasPlayer = slotLabels[idx] != null
                        && !slotLabels[idx].getText().contains("รอผู้เล่น");
                    // Glass card background
                    if (hasPlayer) {
                        g2.setColor(new Color(255, 220, 240, 200));
                    } else {
                        g2.setColor(new Color(255, 240, 250, 160));
                    }
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                    // Border
                    g2.setStroke(new BasicStroke(2));
                    g2.setColor(hasPlayer ? PINK_DEEP : CARD_BORDER);
                    g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 22, 22);
                    // Inner glow on active
                    if (hasPlayer) {
                        g2.setColor(new Color(255, 180, 210, 50));
                        g2.setStroke(new BasicStroke(4));
                        g2.drawRoundRect(4, 4, getWidth()-8, getHeight()-8, 18, 18);
                    }
                }
            };
            slotCards[i].setOpaque(false);
            slotCards[i].setBounds(sx, sy + i * (sh + 16), sw, sh);

            // Slot number badge
            JLabel num = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
            num.setFont(new Font("Tahoma", Font.BOLD, 22));
            num.setForeground(LILAC_DARK);
            num.setBounds(10, 0, 40, sh);
            slotCards[i].add(num);

            // Heart icon
            JLabel heart = new JLabel("♡", SwingConstants.CENTER);
            heart.setFont(new Font("Tahoma", Font.PLAIN, 18));
            heart.setForeground(new Color(PINK_LIGHT.getRed(), PINK_LIGHT.getGreen(), PINK_LIGHT.getBlue(), 180));
            heart.setBounds(52, 0, 30, sh);
            slotCards[i].add(heart);

            slotLabels[i] = new JLabel("— รอผู้เล่น —", SwingConstants.CENTER);
            slotLabels[i].setFont(new Font("Tahoma", Font.PLAIN, 22));
            slotLabels[i].setForeground(new Color(0xBB99CC));
            slotLabels[i].setBounds(84, 0, sw - 92, sh);
            slotCards[i].add(slotLabels[i]);

            p.add(slotCards[i]);
        }

        // Status label
        lobbyStatusLabel = new JLabel("ผู้เล่น 1 / 3", SwingConstants.CENTER);
        lobbyStatusLabel.setFont(new Font("Tahoma", Font.PLAIN, 17));
        lobbyStatusLabel.setForeground(TEXT_DARK);
        lobbyStatusLabel.setBounds(0, 510, 1200, 32);
        p.add(lobbyStatusLabel);

        // Leave button
        JButton leaveBtn = makePinkButton("← ออกจากห้อง", new Color(0xC06070));
        leaveBtn.setBounds(40, 725, 230, 44);
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
        client = new LanClient(myName);
        setupClientListener(true);
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
                    "💔 ห้องเต็มแล้ว!", "ห้องเต็ม", JOptionPane.WARNING_MESSAGE);
                innerLayout.show(innerContainer, "CHOICE");
            }
            @Override public void onError(String msg) {
                JOptionPane.showMessageDialog(MultiplayerLobby.this,
                    "💔 " + msg, "เชื่อมต่อไม่ได้", JOptionPane.ERROR_MESSAGE);
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
                slotLabels[i].setText(isMe ? "♡  " + name + "  (Me)" : "✦  " + name);
                slotLabels[i].setForeground(isMe ? GREEN_SOFT : TEXT_DARK);
                slotLabels[i].setFont(new Font("Tahoma", isMe ? Font.BOLD : Font.PLAIN, 22));
            } else {
                slotLabels[i].setText("— รอผู้เล่น —");
                slotLabels[i].setForeground(new Color(0xBB99CC));
                slotLabels[i].setFont(new Font("Tahoma", Font.PLAIN, 22));
            }
            slotCards[i].repaint();
        }
        lobbyStatusLabel.setText("ผู้เล่น " + players.size() + " / 3");
    }

    private void handleLeave() {
        if (client    != null) client.disconnect();
        if (server    != null) server.stop();
        if (discovery != null) discovery.stop();
        client = null; server = null;
        innerLayout.show(innerContainer, "CHOICE");
        for (int i = 0; i < 3; i++) {
            slotLabels[i].setText("— รอผู้เล่น —");
            slotLabels[i].setForeground(new Color(0xBB99CC));
            slotCards[i].repaint();
        }
    }

    // ════════════════════════════════════════════════
    // Petal Animation (copied from MainMenu)
    // ════════════════════════════════════════════════
    private static class Petal {
        float x, y, size, speed, phase, rot, rotSpeed;
        Color color;
        Petal(int w) {
            Random r = new Random();
            x = r.nextFloat() * w; y = -r.nextFloat() * 800;
            size = 6 + r.nextFloat() * 14;
            speed = 0.6f + r.nextFloat() * 1.2f;
            phase = r.nextFloat() * (float)(Math.PI * 2);
            rot = r.nextFloat() * 360;
            rotSpeed = 0.5f + r.nextFloat() * 2f;
            Color[] c = { new Color(0xF8BBD9), new Color(0xE8A0C0),
                          new Color(0xD4B0E0), new Color(0xFFDDEE), new Color(0xC8A0D8) };
            color = c[r.nextInt(c.length)];
        }
        void update(float t) {
            y += speed;
            x += (float)(Math.sin(t * 0.03 + phase) * 0.8);
            rot += rotSpeed;
            if (y > 820) y = -20;
        }
    }

    private void initPetals() {
        for (int i = 0; i < 40; i++) petals.add(new Petal(1400));
    }

    private void startAnimation() {
        animTimer = new Timer(16, e -> {
            wiggleTime += 0.05f;
            for (Petal p : petals) p.update(wiggleTime);
            repaint();
        });
        animTimer.start();
    }

    private void drawPetal(Graphics2D g2, Petal p, int panelW) {
        float px = p.x * panelW / 1400f;
        Graphics2D pg = (Graphics2D) g2.create();
        pg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        pg.translate(px, p.y);
        pg.rotate(Math.toRadians(p.rot));
        pg.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), 200));
        Path2D path = new Path2D.Float();
        path.moveTo(0, -p.size);
        path.curveTo(p.size * 0.6f, -p.size * 0.5f, p.size * 0.6f, p.size * 0.5f, 0, p.size * 0.3f);
        path.curveTo(-p.size * 0.6f, p.size * 0.5f, -p.size * 0.6f, -p.size * 0.5f, 0, -p.size);
        pg.fill(path);
        pg.dispose();
    }

    // ════════════════════════════════════════════════
    // UI Helpers
    // ════════════════════════════════════════════════
    private JPanel makeBgPanel() {
        JPanel p = new JPanel(null);
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(1200, 800));
        return p;
    }

    /** Glass-morphism card with pink tint */
    private JPanel makeGlassCard(int x, int y, int w, int h) {
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2.setColor(new Color(180, 100, 140, 40));
                g2.fillRoundRect(4, 6, getWidth()-4, getHeight()-4, 26, 26);
                // Card bg
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-6, 24, 24);
                // Border
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawRoundRect(1, 1, getWidth()-6, getHeight()-8, 24, 24);
                // Inner highlight
                g2.setColor(new Color(255, 255, 255, 90));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(3, 3, getWidth()-10, getHeight()-12, 22, 22);
            }
        };
        card.setOpaque(false);
        card.setBounds(x, y, w, h);
        return card;
    }

    private JButton makePinkButton(String text, Color base) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                    ? base.brighter()
                    : getModel().isPressed() ? base.darker() : base;
                g2.setPaint(new GradientPaint(0, 0, bg.brighter(), 0, getHeight(), bg));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Tahoma", Font.BOLD, 17));
        btn.setForeground(TEXT_WHITE);
        btn.setBackground(base);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setEnabled(true);
        return btn;
    }

    private JTextField makePinkTextField() {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 245, 252));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setFont(new Font("Tahoma", Font.PLAIN, 24));
        f.setHorizontalAlignment(JTextField.CENTER);
        f.setOpaque(false);
        f.setForeground(TEXT_DARK);
        f.setCaretColor(PINK_DEEP);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PINK_DEEP, 2, true),
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

    /** Label ที่มี drop-shadow แบบ soft */
    private void addShadowLabel(JPanel p, JLabel original) {
        // Shadow
        JLabel shadow = new JLabel(original.getText(), SwingConstants.CENTER);
        shadow.setFont(original.getFont());
        shadow.setForeground(new Color(180, 100, 140, 70));
        shadow.setBounds(original.getX() + 2, original.getY() + 3,
            original.getWidth(), original.getHeight());
        p.add(shadow);
        p.add(original);
    }

    private void addDecorLabel(JPanel p, String text, int x, int y, int size, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Tahoma", Font.PLAIN, size));
        lbl.setForeground(color);
        lbl.setBounds(x, y, size + 10, size + 10);
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
