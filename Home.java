import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Home extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel     mainContainer;
    private final GameLogic  logic;
    private final GameDate   gameDate;
    private final Runnable   onStatusChanged; // callback → GameUI.updateStatus()

    private JLabel energyLabel;
    private JLabel moneyLabel;
    private JLabel dateLabel;

    // ────────────────────────────────────────────────
    public Home(CardLayout cardLayout, JPanel mainContainer,
                GameLogic logic, GameDate gameDate, Runnable onStatusChanged) {
        this.cardLayout       = cardLayout;
        this.mainContainer    = mainContainer;
        this.logic            = logic;
        this.gameDate         = gameDate;
        this.onStatusChanged  = onStatusChanged;

        setLayout(null);
        setPreferredSize(new Dimension(1200, 800));
        buildUI();
    }

    // ────────────────────────────────────────────────
    private void buildUI() {

        // ── พื้นหลัง (โทนอบอุ่นกลางคืน) ──
        JPanel bg = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                    0, 0,   new Color(25, 20, 50),
                    0, 800, new Color(70, 40, 80));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bg.setBounds(0, 0, 1200, 800);
        bg.setLayout(null);

        // ── ชื่อหน้า ──
        JLabel title = new JLabel("🏠  บ้าน", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 52));
        title.setForeground(new Color(255, 220, 120));
        title.setBounds(0, 40, 1200, 70);
        bg.add(title);

        // ── สถานะ (ด้านบน) ──
        energyLabel = makeInfoLabel("⚡ พลังงาน: --/100");
        moneyLabel  = makeInfoLabel("💰 เงิน: -- บาท");
        dateLabel   = makeInfoLabel("📅 วันที่: --");

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 8));
        statusBar.setOpaque(false);
        statusBar.add(energyLabel);
        statusBar.add(moneyLabel);
        statusBar.add(dateLabel);
        statusBar.setBounds(0, 120, 1200, 50);
        bg.add(statusBar);

        // ── เส้นคั่น ──
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 180, 80, 120));
        sep.setBounds(200, 175, 800, 2);
        bg.add(sep);

        // ── 2 ตัวเลือก ──
        JPanel cardRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 0));
        cardRow.setOpaque(false);
        cardRow.setBounds(0, 210, 1200, 380);

        cardRow.add(makeSleepCard());
        cardRow.add(makeCookCard());
        bg.add(cardRow);

        // ── ปุ่มกลับ ──
        JButton backBtn = makeActionButton("← กลับ", new Color(180, 180, 180), Color.WHITE);
        backBtn.setBounds(50, 720, 160, 48);
        backBtn.addActionListener(e -> cardLayout.show(mainContainer, "GAMEPLAY"));
        bg.add(backBtn);

        add(bg);
    }

    // ────────────────────────────────────────────────
    // Card: เข้านอน
    // ────────────────────────────────────────────────
    private JPanel makeSleepCard() {
        JPanel card = makeCard();

        JLabel icon = new JLabel("🌙", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        icon.setBounds(0, 24, 320, 90);
        card.add(icon);

        JLabel name = new JLabel("เข้านอน", SwingConstants.CENTER);
        name.setFont(new Font("Tahoma", Font.BOLD, 30));
        name.setForeground(new Color(200, 220, 255));
        name.setBounds(0, 120, 320, 40);
        card.add(name);

        JLabel desc = new JLabel(
            "<html><div style='text-align:center;color:#aac;font-size:14px'>" +
            "ฟื้นฟูพลังงานกลับมา 100<br>และข้ามไปวันถัดไป</div></html>",
            SwingConstants.CENTER);
        desc.setBounds(10, 168, 300, 70);
        card.add(desc);

        JButton btn = makeActionButton("นอนหลับ  💤", new Color(80, 80, 180), Color.WHITE);
        btn.setBounds(40, 268, 240, 52);
        btn.addActionListener(e -> {
            logic.sleep();
            gameDate.nextDay();
            // ── บันทึกเกมอัตโนมัติ ──
            SaveManager.save(logic, gameDate);
            refreshLabels();
            onStatusChanged.run();
            JOptionPane.showMessageDialog(this,
                "<html><div style='font-size:15px;text-align:center'>" +
                "😴 นอนหลับสบาย...<br><br>" +
                "⚡ พลังงานเต็ม 100/100<br>" +
                "📅 " + gameDate.toString() + "<br><br>" +
                "<span style='color:#88bb88;font-size:13px'>💾 บันทึกเกมอัตโนมัติแล้ว</span>" +
                "</div></html>",
                "นอนหลับ", JOptionPane.INFORMATION_MESSAGE);
        });
        card.add(btn);

        return card;
    }

    // ────────────────────────────────────────────────
    // Card: ทำกับข้าว
    // ────────────────────────────────────────────────
    private JPanel makeCookCard() {
        JPanel card = makeCard();

        JLabel icon = new JLabel("🍳", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        icon.setBounds(0, 24, 320, 90);
        card.add(icon);

        JLabel name = new JLabel("ทำกับข้าว", SwingConstants.CENTER);
        name.setFont(new Font("Tahoma", Font.BOLD, 30));
        name.setForeground(new Color(255, 220, 140));
        name.setBounds(0, 120, 320, 40);
        card.add(name);

        JLabel desc = new JLabel(
            "<html><div style='text-align:center;color:#eca;font-size:14px'>" +
            "ใช้เงิน 20 บาท<br>เพิ่มพลังงาน +50</div></html>",
            SwingConstants.CENTER);
        desc.setBounds(10, 168, 300, 70);
        card.add(desc);

        JButton btn = makeActionButton("ทำอาหาร  🍱", new Color(180, 100, 20), Color.WHITE);
        btn.setBounds(40, 268, 240, 52);
        btn.addActionListener(e -> handleCook());
        card.add(btn);

        return card;
    }

    private void handleCook() {
        int cost       = 20;
        int energyGain = 50;

        if (!logic.hasMoney(cost)) {
            JOptionPane.showMessageDialog(this,
                "<html><div style='font-size:15px;text-align:center'>" +
                "❌ เงินไม่พอ!<br>ต้องการ " + cost + " บาท<br>มีอยู่ " + logic.getMoney() + " บาท" +
                "</div></html>",
                "เงินไม่พอ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        logic.spendMoney(cost);
        logic.restoreEnergy(energyGain);
        refreshLabels();
        onStatusChanged.run();

        JOptionPane.showMessageDialog(this,
            "<html><div style='font-size:15px;text-align:center'>" +
            "🍳 ทำอาหารเสร็จแล้ว!<br><br>" +
            "💰 หักเงิน -" + cost + " บาท<br>" +
            "⚡ พลังงาน +" + energyGain + "  (ตอนนี้ " + logic.getEnergy() + "/" + logic.getMaxEnergy() + ")" +
            "</div></html>",
            "ทำกับข้าวสำเร็จ", JOptionPane.INFORMATION_MESSAGE);
    }

    // ────────────────────────────────────────────────
    // อัปเดต label สถานะ
    // ────────────────────────────────────────────────
    public void refreshLabels() {
        energyLabel.setText("⚡ พลังงาน: " + logic.getEnergy() + "/" + logic.getMaxEnergy());
        moneyLabel.setText("💰 เงิน: " + logic.getMoney() + " บาท");
        String[] mo = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        dateLabel.setText("📅 " + gameDate.getDay() + " " + mo[gameDate.getMonth()-1]
                + "  ปีที่ " + gameDate.getYear());
    }

    // ────────────────────────────────────────────────
    // Helpers
    // ────────────────────────────────────────────────
    private JPanel makeCard() {
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.setColor(new Color(255, 180, 80, 140));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 24, 24);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(320, 340));
        return card;
    }

    private JLabel makeInfoLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Tahoma", Font.BOLD, 17));
        lbl.setForeground(new Color(255, 230, 160));
        return lbl;
    }

    private JButton makeActionButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Tahoma", Font.BOLD, 18));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorder(BorderFactory.createLineBorder(fg, 2, true));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            Color orig = bg;
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(orig);
            }
        });
        return btn;
    }
}