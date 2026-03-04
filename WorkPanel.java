import javax.swing.*;
import java.awt.*;

/**
 * WorkPanel.java
 * หน้าเลือกงาน — เปิดจาก hamburger button
 * งาน: พนักงานพาร์ทไทม์, ร้านหนังสือ, เซเว่น
 * แต่ละงานมีมินิเกมคำถาม 3 ข้อ (WorkGameLogic)
 */
public class WorkPanel extends JPanel {

    private static final Color BG      = new Color(0xF0F8FF);
    private static final Color ACCENT  = new Color(0x1565C0);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color GREEN   = new Color(0x2E7D32);

    private final GameLogic      gameLogic;
    private final WorkGameLogic  workLogic;
    private final Runnable       onClose;

    private JLabel moneyLabel;
    private JLabel energyLabel;
    private JLabel roundsLabel;

    // ── Quiz state ───────────────────────────
    private JDialog  quizDialog;

    public WorkPanel(GameLogic gameLogic, Runnable onClose) {
        this.gameLogic = gameLogic;
        this.workLogic = new WorkGameLogic(gameLogic);
        this.onClose   = onClose;

        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildJobList(),   BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ── Header ──────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(ACCENT);
        p.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("💼  เลือกงานทำ");
        title.setFont(new Font("Tahoma", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 18));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> onClose.run());

        p.add(title,    BorderLayout.WEST);
        p.add(closeBtn, BorderLayout.EAST);
        return p;
    }

    // ── Job Cards (1 column × 3) ─────────────
    private JPanel buildJobList() {
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(BG);
        list.setBorder(BorderFactory.createEmptyBorder(20, 40, 16, 40));

        // { jobType, emoji, ชื่องาน, คำอธิบาย, เงิน, energy }
        Object[][] jobs = {
            {1, "🏪", "พนักงานพาร์ทไทม์",   "ตอบคำถาม Logic Gate 3 ข้อ\nผ่านครบ = ได้เงิน", 50,  20},
            {2, "📚", "พนักงานร้านหนังสือ",  "ตอบโจทย์คณิตศาสตร์ 3 ข้อ\nผ่านครบ = ได้เงิน",  50,  20},
            {3, "🏬", "พนักงานเซเว่น",       "ตอบฟิสิกส์เวกเตอร์ 3 ข้อ\nผ่านครบ = ได้เงิน",   50,  20},
        };

        for (Object[] j : jobs) {
            list.add(makeJobCard(
                (int)j[0], (String)j[1], (String)j[2],
                (String)j[3], (int)j[4], (int)j[5]
            ));
            list.add(Box.createVerticalStrut(14));
        }
        return list;
    }

    private JPanel makeJobCard(int jobType, String emoji, String name,
                                String desc, int pay, int eng) {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xBBDEFB), 1, true),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        // left: emoji
        JLabel emojiLbl = new JLabel(emoji);
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        emojiLbl.setHorizontalAlignment(SwingConstants.CENTER);
        emojiLbl.setPreferredSize(new Dimension(56, 56));

        // center: name + desc
        JPanel mid = new JPanel();
        mid.setOpaque(false);
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Tahoma", Font.BOLD, 17));
        nameLbl.setForeground(new Color(0x0D47A1));

        // multi-line desc via html
        JLabel descLbl = new JLabel("<html>" + desc.replace("\n","<br>") + "</html>");
        descLbl.setFont(new Font("Tahoma", Font.PLAIN, 13));
        descLbl.setForeground(new Color(0x666666));

        JPanel tags = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tags.setOpaque(false);
        tags.add(makeTag("💰 +" + pay + " บาท", new Color(0xF57F17)));
        tags.add(makeTag("⚡ -" + eng,           new Color(0x1565C0)));
        tags.add(makeTag("📋 3 ข้อ",             new Color(0x6A1B9A)));

        mid.add(nameLbl);
        mid.add(Box.createVerticalStrut(3));
        mid.add(descLbl);
        mid.add(Box.createVerticalStrut(4));
        mid.add(tags);

        // right: start button
        JButton startBtn = new JButton("เริ่มงาน");
        startBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        startBtn.setBackground(ACCENT);
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setPreferredSize(new Dimension(90, 38));
        startBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startBtn.addActionListener(e -> startJob(jobType, name));

        card.add(emojiLbl, BorderLayout.WEST);
        card.add(mid,      BorderLayout.CENTER);
        card.add(startBtn, BorderLayout.EAST);
        return card;
    }

    private JLabel makeTag(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.BOLD, 12));
        l.setForeground(color);
        l.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
        l.setOpaque(true);
        l.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 25));
        return l;
    }

    // ── Status Bar ───────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        bar.setBackground(new Color(0xE3F2FD));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xBBDEFB)));

        moneyLabel  = new JLabel();
        energyLabel = new JLabel();
        roundsLabel = new JLabel();

        for (JLabel l : new JLabel[]{moneyLabel, energyLabel, roundsLabel}) {
            l.setFont(new Font("Tahoma", Font.BOLD, 14));
            l.setForeground(new Color(0x0D47A1));
            bar.add(l);
        }
        refreshStatus();
        return bar;
    }

    private void refreshStatus() {
        moneyLabel.setText("💰 " + gameLogic.getMoney() + " บาท");
        energyLabel.setText("⚡ " + gameLogic.getEnergy() + "/" + gameLogic.getMaxEnergy());
        roundsLabel.setText("💼 งานวันนี้: " + workLogic.getRoundsPlayedToday() + "/" + WorkGameLogic.MAX_ROUNDS_PER_DAY);
    }

    // ── Start Job → Quiz Dialog ───────────────
    private void startJob(int jobType, String jobName) {
        WorkGameLogic.StartResult r = workLogic.startRound(jobType);
        switch (r) {
            case NO_ROUNDS_LEFT:
                JOptionPane.showMessageDialog(this,
                    "ทำงานครบ " + WorkGameLogic.MAX_ROUNDS_PER_DAY + " รอบแล้ววันนี้!",
                    "หมดโควต้า", JOptionPane.WARNING_MESSAGE);
                return;
            case NOT_ENOUGH_ENERGY:
                JOptionPane.showMessageDialog(this,
                    "พลังงานไม่พอ! ต้องการ ⚡" + WorkGameLogic.ENERGY_COST
                    + "  มี ⚡" + gameLogic.getEnergy(),
                    "พลังงานไม่พอ", JOptionPane.WARNING_MESSAGE);
                return;
            case OK:
                openQuizDialog(jobName);
                break;
        }
    }

    private void openQuizDialog(String jobName) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = (owner instanceof JFrame)
            ? new JDialog((JFrame) owner, jobName + " — ทำแบบทดสอบ", true)
            : new JDialog((JDialog) owner, jobName + " — ทำแบบทดสอบ", true);

        JPanel quizPanel = new QuizRunnerPanel(workLogic, jobName, () -> {
            dialog.dispose();
            refreshStatus();
        });
        dialog.setContentPane(quizPanel);
        dialog.setSize(520, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    // ── Static launcher ──────────────────────
    public static void showAsDialog(JFrame parent, GameLogic gl) {
        JDialog d = new JDialog(parent, "เลือกงาน", true);
        d.setContentPane(new WorkPanel(gl, d::dispose));
        d.setSize(580, 520);
        d.setLocationRelativeTo(parent);
        d.setResizable(false);
        d.setVisible(true);
    }
}