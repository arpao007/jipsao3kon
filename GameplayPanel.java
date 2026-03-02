import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GameplayPanel.java
 * หน้าเกมหลัก — ระบบจีบสาววันต่อวัน
 * เรียกใช้ onGameStart(playerName, girlData) จาก RunGame หลังจาก setupgame เสร็จ
 */
public class GameplayPanel extends JPanel {

    // ── References ──────────────────────────────────────────────────────────────
    private final CardLayout cardLayout;
    private final JPanel     mainContainer;
    private final GameLogic  logic;
    private final GameDate   gameDate;

    // ── Girl data (รับจาก setupgame) ────────────────────────────────────────────
    private setupgame.GirlData currentGirl;
    private String              playerName;

    // ── UI components ────────────────────────────────────────────────────────────
    private JPanel     leftPanel, centerPanel, rightPanel;
    private JLabel     dateLbl, girlNameLbl, affectionTitleLbl;
    private JLabel     girlMoodLbl, dialogueLbl;
    private JProgressBar affectionBar;
    private JLabel     affectionPctLbl;
    private JPanel     actionPanel;
    private JTextArea  logArea;
    private JLabel     heartLbl;
    private JPanel     statsPanel;
    private JLabel     scoreLbl, dayLbl, endingLbl;

    // ── Event system ─────────────────────────────────────────────────────────────
    private static final String[][] EVENTS_SAKURA = {
        {"ทำอาหารกลางวัน", "ซากุระทำข้าวกล่องให้คุณ\nกลิ่นหอมน่ากินมาก!", "🍱", "10"},
        {"เดินกลับบ้านด้วยกัน", "ซากุระชวนเดินกลับบ้านด้วยกัน\nบรรยากาศเย็นสบาย", "🌸", "8"},
        {"ช่วยทำการบ้าน", "ซากุระขอให้คุณช่วยสอนการบ้าน\nเธอยิ้มอย่างอบอุ่น", "📚", "12"},
        {"ดูดาวด้วยกัน", "คืนนี้ท้องฟ้าใส\nซากุระชวนออกไปดูดาวด้วยกัน", "⭐", "15"},
        {"ไปตลาดนัด", "ซากุระอยากไปตลาดนัดใกล้บ้าน\nเธอตื่นเต้นมาก!", "🛍️", "9"},
        {"ทำขนมด้วยกัน", "ซากุระชวนทำคุกกี้ด้วยกัน\nแป้งเลอะมือไปหมด!", "🍪", "13"},
    };
    private static final String[][] EVENTS_HANA = {
        {"ไปห้องสมุดด้วยกัน", "ฮานะชวนคุณไปห้องสมุด\nเธออ่านนิยายอย่างตั้งใจ", "📚", "10"},
        {"ช่วยงานกรรมการ", "ฮานะขอความช่วยเหลือจัดงาน\nเธอยิ้มขอบคุณอย่างเขินอาย", "📋", "12"},
        {"พักกินข้าวด้วยกัน", "ฮานะเผลอนั่งข้างๆ คุณ\nแล้วก็เขินแดงขึ้นมา", "🍜", "14"},
        {"แลกแนะนำหนังสือ", "ฮานะแนะนำนิยายที่เธอชอบ\nดวงตาเธอเป็นประกาย", "📖", "11"},
        {"ช่วยหาของที่หาย", "ฮานะทำปากกาหาย คุณช่วยหาให้\nเธอขอบคุณอย่างอบอุ่น", "✏️", "9"},
        {"เดินชมสวน", "ฮานะเดินชมสวนคนเดียว\nคุณตามไปพูดคุยด้วย", "🌺", "13"},
    };
    private static final String[][] EVENTS_YUKI = {
        {"ฝึกซ้อมด้วยกัน", "ยูกิชวนคุณซ้อมวิ่งรอบสนาม\nเธอวิ่งหน้าตาจริงจัง", "⚡", "12"},
        {"แข่งขันกีฬา", "ยูกิท้าแข่งตีปิงปอง\nเธอแพ้แล้วยื่นมือมาจับ", "🏓", "10"},
        {"แมลงบินผ่าน!", "แมลงบินผ่านมา ยูกิกรี๊ดแล้วกอดแขนคุณ\nหน้าแดงมาก!", "🦋", "18"},
        {"กินน้ำแข็งไสด้วยกัน", "ยูกิชวนกินน้ำแข็งไส\nเธอยิ้มกว้างและพูดตรงๆ", "🍧", "11"},
        {"ช่วยยกของ", "ยูกิขนของหนัก คุณช่วยถือ\nเธอขอบคุณแบบเขินๆ", "💪", "9"},
        {"เชียร์การแข่งขัน", "ยูกิแข่งกีฬา คุณไปเชียร์\nเธอชนะแล้ววิ่งมาหาคุณ", "🏆", "16"},
    };

    // ── Action choices ────────────────────────────────────────────────────────────
    private static final String[][] ACTIONS = {
        {"💬 คุยด้วย",    "+3 ความสัมพันธ์",   "3"},
        {"🎁 ให้ของขวัญ", "+8 ความสัมพันธ์",   "8"},
        {"😄 ทำให้หัวเราะ","+5 ความสัมพันธ์",  "5"},
        {"🤝 ช่วยเหลือ",  "+6 ความสัมพันธ์",   "6"},
        {"⏭ ข้ามวัน",    "ผ่านไป 1 วัน",      "0"},
        {"💾 บันทึกเกม",  "บันทึก save",       "-1"},
    };

    // ── Animation ────────────────────────────────────────────────────────────────
    private float heartScale = 1f;
    private boolean heartGrow = true;
    private final Timer heartTimer;
    private final List<FloatingHeart> floatingHearts = new ArrayList<>();
    private final Timer floatTimer;

    // ─────────────────────────────────────────────────────────────────────────────
    public GameplayPanel(CardLayout cardLayout, JPanel mainContainer,
                         GameLogic logic, GameDate gameDate) {
        this.cardLayout    = cardLayout;
        this.mainContainer = mainContainer;
        this.logic         = logic;
        this.gameDate      = gameDate;

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(0xFFF0F5));
        buildUI();

        // Heart pulse timer
        heartTimer = new Timer(50, e -> {
            heartScale += heartGrow ? 0.015f : -0.015f;
            if (heartScale > 1.15f) heartGrow = false;
            if (heartScale < 0.90f) heartGrow = true;
            if (heartLbl != null) heartLbl.repaint();
        });
        heartTimer.start();

        // Floating hearts timer
        floatTimer = new Timer(30, e -> {
            floatingHearts.removeIf(h -> h.alpha <= 0);
            for (FloatingHeart h : floatingHearts) h.update();
            repaint();
        });
        floatTimer.start();
    }

    /** เรียกจาก RunGame หลัง setupgame เสร็จ */
    public void onGameStart(String playerName, setupgame.GirlData girl) {
        this.playerName  = playerName;
        this.currentGirl = girl;
        refreshUI();
        addLog("✨ ยินดีต้อนรับ " + playerName + " สู่เส้นทางแห่งรัก!");
        addLog("🌸 คุณเลือก " + girl.emoji + " " + girl.name + " เป็นคนพิเศษ");
        addLog("💡 เลือกกิจกรรมด้านซ้ายเพื่อเพิ่มความสัมพันธ์");
        triggerRandomEvent();
    }

    // ════════════════════════════════════════════════════════════════════════════
    // UI Building
    // ════════════════════════════════════════════════════════════════════════════
    private void buildUI() {
        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildMainArea(),  BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    // ── Top bar: date + player name ───────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0,0,new Color(0xFFD6E2),getWidth(),0,new Color(0xFFB0CC)));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose(); super.paintComponent(g);
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 56));
        bar.setBorder(new EmptyBorder(8, 20, 8, 20));

        JButton backBtn = pinkBtn("← เมนู");
        backBtn.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this,
                    "กลับเมนูหลัก? (ข้อมูลที่ยังไม่ได้บันทึกจะหายไป)",
                    "ยืนยัน", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) cardLayout.show(mainContainer, "MENU");
        });

        dateLbl = new JLabel("", SwingConstants.CENTER);
        dateLbl.setFont(new Font("Tahoma", Font.BOLD, 16));
        dateLbl.setForeground(new Color(0x8B2560));

        JLabel playerLbl = new JLabel("ผู้เล่น: —");
        playerLbl.setName("playerLbl");
        playerLbl.setFont(new Font("Tahoma", Font.BOLD, 15));
        playerLbl.setForeground(new Color(0x9B2560));

        scoreLbl = new JLabel("คะแนน: 0");
        scoreLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
        scoreLbl.setForeground(new Color(0xB04070));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setOpaque(false);
        right.add(scoreLbl);

        bar.add(backBtn,   BorderLayout.WEST);
        bar.add(dateLbl,   BorderLayout.CENTER);
        bar.add(right,     BorderLayout.EAST);
        return bar;
    }

    // ── Main area: 3 columns ──────────────────────────────────────────────────
    private JPanel buildMainArea() {
        JPanel area = new JPanel(new BorderLayout(12, 0));
        area.setOpaque(false);
        area.setBorder(new EmptyBorder(12, 16, 8, 16));

        area.add(buildLeftPanel(),   BorderLayout.WEST);
        area.add(buildCenterPanel(), BorderLayout.CENTER);
        area.add(buildRightPanel(),  BorderLayout.EAST);
        return area;
    }

    // ── Left: Action buttons ──────────────────────────────────────────────────
    private JPanel buildLeftPanel() {
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(200, 0));
        leftPanel.setBorder(new EmptyBorder(0, 0, 0, 8));

        JLabel title = new JLabel("🎮 กิจกรรม");
        title.setFont(new Font("Tahoma", Font.BOLD, 16));
        title.setForeground(new Color(0x8B2560));
        title.setAlignmentX(CENTER_ALIGNMENT);
        leftPanel.add(title);
        leftPanel.add(Box.createVerticalStrut(12));

        actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setOpaque(false);

        for (String[] action : ACTIONS) {
            JButton btn = actionBtn(action[0], action[1]);
            final String[] act = action;
            btn.addActionListener(e -> handleAction(act));
            actionPanel.add(btn);
            actionPanel.add(Box.createVerticalStrut(8));
        }
        leftPanel.add(actionPanel);
        return leftPanel;
    }

    // ── Center: Girl display + dialogue ──────────────────────────────────────
    private JPanel buildCenterPanel() {
        centerPanel = new JPanel(new BorderLayout(0, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,new Color(0xFFF8FC),0,getHeight(),new Color(0xFFEEF5)));
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),20,20));
                g2.setColor(new Color(0xFFB0CC));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0,0,getWidth()-1,getHeight()-1,20,20));
                // Floating hearts
                for (FloatingHeart h : floatingHearts) h.draw(g2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Girl avatar
        heartLbl = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Glow circle
                int cx = getWidth()/2, cy = getHeight()/2, r = 95;
                g2.setPaint(new RadialGradientPaint(cx, cy, r,
                        new float[]{0f, 1f},
                        new Color[]{new Color(255,180,210,80), new Color(255,180,210,0)}));
                g2.fillOval(cx-r, cy-r, r*2, r*2);
                // Avatar emoji scaled
                float sz = 100 * heartScale;
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, (int)sz));
                String em = currentGirl != null ? currentGirl.emoji : "🌸";
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(new Color(0,0,0,20));
                g2.drawString(em, cx - fm.stringWidth(em)/2 + 3, cy + fm.getAscent()/2 + 3);
                g2.setColor(Color.BLACK);
                g2.drawString(em, cx - fm.stringWidth(em)/2, cy + fm.getAscent()/2);
                g2.dispose();
            }
        };
        heartLbl.setPreferredSize(new Dimension(0, 220));

        // Girl name + mood
        girlNameLbl = new JLabel("—", SwingConstants.CENTER);
        girlNameLbl.setFont(new Font("Tahoma", Font.BOLD, 22));
        girlNameLbl.setForeground(new Color(0x8B2560));

        girlMoodLbl = new JLabel("😊 อารมณ์ดี", SwingConstants.CENTER);
        girlMoodLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
        girlMoodLbl.setForeground(new Color(0xB06080));

        // Dialogue box
        JPanel dialogBox = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xFFF0F8));
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),16,16));
                g2.setColor(new Color(0xFFB0CC));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0,0,getWidth()-1,getHeight()-1,16,16));
                g2.dispose(); super.paintComponent(g);
            }
        };
        dialogBox.setOpaque(false);
        dialogBox.setBorder(new EmptyBorder(12, 14, 12, 14));

        dialogueLbl = new JLabel("<html><div style='text-align:center'>กดกิจกรรมเพื่อเริ่มต้น…</div></html>",
                SwingConstants.CENTER);
        dialogueLbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
        dialogueLbl.setForeground(new Color(0x6B4060));
        dialogBox.add(dialogueLbl, BorderLayout.CENTER);

        JPanel nameBox = new JPanel(new GridLayout(2,1,0,2));
        nameBox.setOpaque(false);
        nameBox.add(girlNameLbl);
        nameBox.add(girlMoodLbl);

        centerPanel.add(heartLbl,  BorderLayout.NORTH);
        centerPanel.add(nameBox,   BorderLayout.CENTER);
        centerPanel.add(dialogBox, BorderLayout.SOUTH);
        return centerPanel;
    }

    // ── Right: Affection + Stats + Log ───────────────────────────────────────
    private JPanel buildRightPanel() {
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(220, 0));

        // ── Affection section ──
        JPanel affBox = new JPanel(new BorderLayout(0, 6)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,new Color(0xFFF0F8),0,getHeight(),new Color(0xFFE0F0)));
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),16,16));
                g2.setColor(new Color(0xFFB0CC)); g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0,0,getWidth()-1,getHeight()-1,16,16));
                g2.dispose(); super.paintComponent(g);
            }
        };
        affBox.setOpaque(false);
        affBox.setBorder(new EmptyBorder(12, 12, 12, 12));
        affBox.setMaximumSize(new Dimension(220, 130));

        affectionTitleLbl = new JLabel("💕 ความสัมพันธ์");
        affectionTitleLbl.setFont(new Font("Tahoma", Font.BOLD, 14));
        affectionTitleLbl.setForeground(new Color(0x8B2560));

        affectionBar = new JProgressBar(0, 100) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xFFD6E8));
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),10,10));
                int fw = (int)(getWidth() * getValue() / 100.0);
                if (fw > 0) {
                    g2.setPaint(new GradientPaint(0,0,new Color(0xFF6B9B),fw,0,new Color(0xFF9BB5)));
                    g2.fill(new RoundRectangle2D.Float(0,0,fw,getHeight(),10,10));
                }
                g2.dispose();
            }
        };
        affectionBar.setBorderPainted(false);
        affectionBar.setOpaque(false);
        affectionBar.setPreferredSize(new Dimension(0, 18));

        affectionPctLbl = new JLabel("0 / 100", SwingConstants.CENTER);
        affectionPctLbl.setFont(new Font("Tahoma", Font.BOLD, 13));
        affectionPctLbl.setForeground(new Color(0xE8759A));

        endingLbl = new JLabel("🔒 ยังไม่ถึง Ending", SwingConstants.CENTER);
        endingLbl.setFont(new Font("Tahoma", Font.ITALIC, 12));
        endingLbl.setForeground(new Color(0xC090B0));

        affBox.add(affectionTitleLbl, BorderLayout.NORTH);
        affBox.add(affectionBar,      BorderLayout.CENTER);
        JPanel affPct = new JPanel(new GridLayout(2,1,0,2));
        affPct.setOpaque(false);
        affPct.add(affectionPctLbl);
        affPct.add(endingLbl);
        affBox.add(affPct, BorderLayout.SOUTH);

        // ── Stats section ──
        statsPanel = new JPanel(new GridLayout(3,1,0,4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0,0,new Color(0xFFF8FC),0,getHeight(),new Color(0xFFECF4)));
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),12,12));
                g2.setColor(new Color(0xFFB0CC)); g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0,0,getWidth()-1,getHeight()-1,12,12));
                g2.dispose(); super.paintComponent(g);
            }
        };
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10,12,10,12));
        statsPanel.setMaximumSize(new Dimension(220, 90));

        scoreLbl = new JLabel("⭐ คะแนน: 0");
        scoreLbl.setFont(new Font("Tahoma", Font.PLAIN, 13));
        scoreLbl.setForeground(new Color(0x8B6040));

        dayLbl = new JLabel("📅 วันที่: 1");
        dayLbl.setFont(new Font("Tahoma", Font.PLAIN, 13));
        dayLbl.setForeground(new Color(0x406080));

        JLabel livesLbl = new JLabel("❤ ชีวิต: 3");
        livesLbl.setName("livesLbl");
        livesLbl.setFont(new Font("Tahoma", Font.PLAIN, 13));
        livesLbl.setForeground(new Color(0xE85070));

        statsPanel.add(scoreLbl);
        statsPanel.add(dayLbl);
        statsPanel.add(livesLbl);

        // ── Log section ──
        JLabel logTitle = new JLabel("📜 บันทึก");
        logTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
        logTitle.setForeground(new Color(0x8B2560));
        logTitle.setAlignmentX(LEFT_ALIGNMENT);

        logArea = new JTextArea();
        logArea.setFont(new Font("Tahoma", Font.PLAIN, 12));
        logArea.setForeground(new Color(0x6B4060));
        logArea.setBackground(new Color(0xFFF8FC));
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xFFB0CC), 1, true));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        rightPanel.add(affBox);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(statsPanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(logTitle);
        rightPanel.add(Box.createVerticalStrut(6));
        rightPanel.add(scroll);
        return rightPanel;
    }

    // ── Bottom bar ────────────────────────────────────────────────────────────
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0,0,new Color(0xFFE8F0),getWidth(),0,new Color(0xFFD0E4)));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose(); super.paintComponent(g);
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 52));

        JLabel tip = new JLabel("💡 เพิ่มความสัมพันธ์ให้ถึง 100 เพื่อดู Ending ♡");
        tip.setFont(new Font("Tahoma", Font.ITALIC, 13));
        tip.setForeground(new Color(0xC070A0));
        bar.add(tip);
        return bar;
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Game Logic
    // ════════════════════════════════════════════════════════════════════════════

    /** รีเฟรช UI ทั้งหมดตามสถานะปัจจุบัน */
    private void refreshUI() {
        if (currentGirl == null) return;

        // Top bar
        dateLbl.setText(gameDate.getFullDateString());

        // Find playerLbl in top bar and update
        updateAllLabels();

        // Center
        girlNameLbl.setText(currentGirl.emoji + "  " + currentGirl.name
                + "  \"" + currentGirl.nickname + "\"");
        updateMood();

        // Right
        int aff = logic.getAffection();
        affectionBar.setValue(aff);
        affectionPctLbl.setText(aff + " / 100");
        updateEndingLabel(aff);

        scoreLbl.setText("⭐ คะแนน: " + logic.getScore());
        dayLbl.setText("📅 วันที่: " + logic.getDay());

        // Girl-specific color
        updateGirlTheme();
        repaint();
    }

    private void updateAllLabels() {
        // walk component tree to find named labels
        for (Component c : getComponents()) updateLabel(c);
    }
    private void updateLabel(Component c) {
        if (c instanceof JLabel) {
            JLabel l = (JLabel) c;
            if ("playerLbl".equals(l.getName()))
                l.setText("ผู้เล่น: " + (playerName != null ? playerName : "—"));
            if ("livesLbl".equals(l.getName()))
                l.setText("❤ ชีวิต: " + logic.getLives());
        }
        if (c instanceof Container)
            for (Component ch : ((Container)c).getComponents()) updateLabel(ch);
    }

    private void updateMood() {
        int aff = logic.getAffection();
        String mood;
        if (aff >= 80)      mood = "😍 หลงรัก!";
        else if (aff >= 60) mood = "🥰 ชอบคุณมากๆ";
        else if (aff >= 40) mood = "😊 เป็นมิตร";
        else if (aff >= 20) mood = "🙂 เริ่มคุ้นเคย";
        else                mood = "😐 ยังไม่คุ้น";
        girlMoodLbl.setText(mood);
    }

    private void updateEndingLabel(int aff) {
        if (aff >= 100)     endingLbl.setText("💝 True Ending ♡");
        else if (aff >= 80) endingLbl.setText("💖 Good Ending ใกล้แล้ว!");
        else if (aff >= 50) endingLbl.setText("💗 ความสัมพันธ์ดีขึ้น");
        else                endingLbl.setText("🔒 ยังไม่ถึง Ending");
        endingLbl.setForeground(aff >= 100 ? new Color(0xE8759A)
                : aff >= 80 ? new Color(0xD06090)
                : new Color(0xC090B0));
    }

    private void updateGirlTheme() {
        if (currentGirl == null) return;
        // tint affection bar to girl's color
        // (already handled by custom paint in affectionBar)
    }

    // ── Handle action button click ────────────────────────────────────────────
    private void handleAction(String[] action) {
        if (currentGirl == null) return;
        String label = action[0];
        int    gain  = Integer.parseInt(action[2]);

        if (gain == -1) {
            // Save
            SaveManager.save(logic, gameDate);
            addLog("💾 บันทึกเกมสำเร็จ!");
            JOptionPane.showMessageDialog(this, "บันทึกสำเร็จ ♡\nวันที่ " + logic.getDay()
                    + " | ความสัมพันธ์ " + logic.getAffection() + "/100",
                    "บันทึกสำเร็จ", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (gain == 0) {
            // Skip day
            logic.nextDay();
            gameDate.advanceDay();
            logic.addScore(5);
            addLog("⏭ ผ่านไป 1 วัน — วันที่ " + logic.getDay());
            triggerRandomEvent();
        } else {
            // Normal action
            logic.increaseAffection(gain);
            logic.addScore(gain * 2);
            addLog(label + " → +" + gain + " ความสัมพันธ์");
            spawnHearts(gain);
            updateMood();

            // Girl response
            showGirlResponse(gain);

            // Check ending
            if (logic.getAffection() >= 100) triggerEnding();
        }

        refreshUI();
    }

    // ── Show girl dialogue based on affection gain ─────────────────────────────
    private void showGirlResponse(int gain) {
        if (currentGirl == null) return;
        String[] responses;
        if (gain >= 15) responses = new String[]{
            "\"หัวใจฉันเต้นแรงมากเลย…!\"",
            "\"ฉัน… ฉันชอบคุณมากๆ นะ!\"",
            "\"วันนี้มีความสุขมากเลย ♡\""
        };
        else if (gain >= 8) responses = new String[]{
            "\"ขอบคุณนะ คุณใจดีมากเลย\"",
            "\"อยู่ด้วยกันสนุกดีนะ~\"",
            "\"ฉันดีใจที่ได้รู้จักคุณ\""
        };
        else responses = new String[]{
            "\"อือม… ขอบคุณนะ\"",
            "\"โอเค! ไปด้วยกันได้เลย\"",
            "\"ฮะ? อ๋อ ดีนะ~\""
        };
        String resp = responses[new Random().nextInt(responses.length)];
        dialogueLbl.setText("<html><div style='text-align:center'>"
                + currentGirl.emoji + " " + currentGirl.name + "<br><i>" + resp + "</i></div></html>");
    }

    // ── Random daily event ────────────────────────────────────────────────────
    private void triggerRandomEvent() {
        if (currentGirl == null) return;
        String[][] pool;
        switch (currentGirl.id) {
            case "SAKURA": pool = EVENTS_SAKURA; break;
            case "HANA":   pool = EVENTS_HANA;   break;
            default:       pool = EVENTS_YUKI;   break;
        }
        String[] ev = pool[new Random().nextInt(pool.length)];
        String title = ev[0], desc = ev[1], emoji = ev[2];
        int gain = Integer.parseInt(ev[3]);

        // Show event dialog
        int result = JOptionPane.showConfirmDialog(this,
                emoji + " " + title + "\n\n" + desc + "\n\n(ได้รับ +" + gain + " ความสัมพันธ์)",
                "เหตุการณ์วันที่ " + logic.getDay(), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null);

        if (result == JOptionPane.YES_OPTION) {
            logic.increaseAffection(gain);
            logic.addScore(gain * 3);
            spawnHearts(gain);
            addLog(emoji + " " + title + " → +" + gain + " ความสัมพันธ์");
            showGirlResponse(gain);
            if (logic.getAffection() >= 100) triggerEnding();
        } else {
            addLog("⏩ ข้าม: " + title);
        }
        refreshUI();
    }

    // ── Ending sequence ───────────────────────────────────────────────────────
    private void triggerEnding() {
        spawnHearts(50);
        String msg = "💝 True Ending! 💝\n\n"
                + playerName + " และ " + currentGirl.name + " (" + currentGirl.nickname + ")\n"
                + "มีความสัมพันธ์ที่แน่นแฟ้นแล้ว ♡\n\n"
                + "คะแนนรวม: " + logic.getScore() + " คะแนน\n"
                + "วันที่ใช้: " + logic.getDay() + " วัน";

        JOptionPane.showMessageDialog(this, msg, "💝 จบเกม — True Ending!", JOptionPane.INFORMATION_MESSAGE);
        SaveManager.save(logic, gameDate);
        addLog("💝 True Ending ได้แล้ว! เก็บ save สำเร็จ");
    }

    // ── Log helper ────────────────────────────────────────────────────────────
    private void addLog(String text) {
        logArea.append(text + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // ── Floating hearts animation ──────────────────────────────────────────────
    private void spawnHearts(int count) {
        int cx = centerPanel.getX() + centerPanel.getWidth() / 2;
        int cy = centerPanel.getY() + 120;
        int n  = Math.min(count / 2 + 2, 10);
        for (int i = 0; i < n; i++) floatingHearts.add(new FloatingHeart(cx, cy));
    }

    // ════════════════════════════════════════════════════════════════════════════
    // UI helpers
    // ════════════════════════════════════════════════════════════════════════════
    private JButton actionBtn(String label, String sub) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? new Color(0xFF6B9B) : new Color(0xFFB0CC);
                Color bg2= getModel().isRollover() ? new Color(0xFF9BB5) : new Color(0xFFD6E8);
                g2.setPaint(new GradientPaint(0,0,bg,getWidth(),getHeight(),bg2));
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),14,14));
                g2.setColor(new Color(0xFFFFFF, true));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0,0,getWidth()-1,getHeight()-1,14,14));
                // main label
                g2.setFont(new Font("Tahoma", Font.BOLD, 14));
                g2.setColor(getModel().isRollover() ? Color.WHITE : new Color(0x8B2560));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, 14, 20);
                // sub label
                g2.setFont(new Font("Tahoma", Font.PLAIN, 11));
                g2.setColor(getModel().isRollover() ? new Color(255,255,255,200) : new Color(0xC090B0));
                g2.drawString(sub, 14, 34);
                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(190, 44));
        btn.setMaximumSize(new Dimension(190, 44));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        return btn;
    }

    private static JButton pinkBtn(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isRollover() ? new Color(0xFF6B9B) : new Color(0xFFB0CC);
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),16,16));
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setFont(new Font("Tahoma", Font.BOLD, 14));
        b.setForeground(new Color(0x8B2560));
        b.setOpaque(false); b.setContentAreaFilled(false);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(100, 36));
        return b;
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Inner — FloatingHeart
    // ════════════════════════════════════════════════════════════════════════════
    static class FloatingHeart {
        float x, y, vx, vy, alpha, size;
        static final Random R = new Random();

        FloatingHeart(int cx, int cy) {
            x = cx + R.nextInt(80) - 40;
            y = cy;
            vx = (R.nextFloat() - 0.5f) * 2f;
            vy = -(1.5f + R.nextFloat() * 2f);
            alpha = 1f;
            size  = 14 + R.nextFloat() * 18;
        }
        void update() { x += vx; y += vy; alpha -= 0.02f; }
        void draw(Graphics2D g) {
            if (alpha <= 0) return;
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.setColor(new Color(0xFF6B9B));
            drawHeart(g, (int)x, (int)y, (int)size);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
        static void drawHeart(Graphics2D g, int cx, int cy, int s) {
            int[] xs = new int[20], ys = new int[20];
            for (int i = 0; i < 20; i++) {
                double t = -Math.PI + i * (2 * Math.PI / 20);
                xs[i] = cx + (int)(s * 0.5 * 16 * Math.pow(Math.sin(t), 3) / 16.0);
                ys[i] = cy - (int)(s * 0.5 * (13*Math.cos(t) - 5*Math.cos(2*t)
                        - 2*Math.cos(3*t) - Math.cos(4*t)) / 16.0);
            }
            g.fillPolygon(xs, ys, 20);
        }
    }
}
