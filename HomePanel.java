import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class HomePanel extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel     mainContainer;
    private final GameLogic  logic;

    private JLabel statusLabel;

    public HomePanel(CardLayout cardLayout, JPanel mainContainer, GameLogic logic) {
        this.cardLayout    = cardLayout;
        this.mainContainer = mainContainer;
        this.logic         = logic;

        // ใช้ BorderLayout แทน null layout — render ได้ทันทีไม่ต้องรอขนาด
        setLayout(new BorderLayout());
        setOpaque(false);

        // ── Top bar: ปุ่มกลับ ─────────────────────────
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        topBar.setOpaque(false);
        JButton back = new JButton("\u2190 \u0e01\u0e25\u0e31\u0e1a\u0e44\u0e1b\u0e40\u0e19\u0e37\u0e49\u0e2d\u0e40\u0e23\u0e37\u0e48\u0e2d\u0e07");
        back.setFont(new Font("Tahoma", Font.BOLD, 14));
        back.setFocusPainted(false);
        back.addActionListener(e -> cardLayout.show(mainContainer, "STORY"));
        topBar.add(back);
        add(topBar, BorderLayout.NORTH);

        // ── Center: Title + Status ────────────────────
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        JLabel title = new JLabel("\uD83C\uDFE0  Home", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 36));
        title.setForeground(new Color(0x5A3060));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(title);
        center.add(Box.createVerticalStrut(20));

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        statusLabel.setForeground(new Color(0x5A3060));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(statusLabel);

        add(center, BorderLayout.CENTER);

        // ── Bottom: 2 ปุ่ม ───────────────────────────
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 20));
        bottom.setOpaque(false);

        JButton sleepBtn = bigBtn("\uD83D\uDECC  \u0e40\u0e02\u0e49\u0e32\u0e19\u0e2d\u0e19  (\u0e02\u0e49\u0e32\u0e21\u0e27\u0e31\u0e19)", new Color(0xA076BB));
        sleepBtn.setPreferredSize(new Dimension(320, 80));
        sleepBtn.addActionListener(e -> {
            logic.nextDay();
            try { SaveManager.save(logic, logic.getGameDate()); }
            catch (Exception ex) { System.out.println("[HomePanel] save failed: " + ex.getMessage()); }
            JOptionPane.showMessageDialog(this,
                "\u0e40\u0e02\u0e49\u0e32\u0e19\u0e2d\u0e19\u0e40\u0e23\u0e35\u0e22\u0e1a\u0e23\u0e49\u0e2d\u0e22 \uD83C\uDF19\n\u0e02\u0e49\u0e32\u0e21\u0e27\u0e31\u0e19\u0e41\u0e25\u0e49\u0e27!\n\n" + logic.getStatusText(),
                "\u0e40\u0e02\u0e49\u0e32\u0e19\u0e2d\u0e19", JOptionPane.INFORMATION_MESSAGE);
            refreshStatus();
        });

        JButton cookBtn = bigBtn("\uD83C\uDF73  \u0e17\u0e33\u0e01\u0e31\u0e1a\u0e02\u0e49\u0e32\u0e27  (-50\u0e3f +50\u26a1)", new Color(0xE8759A));
        cookBtn.setPreferredSize(new Dimension(320, 80));
        cookBtn.addActionListener(e -> {
            boolean ok = logic.cookFood();
            if (!ok) {
                JOptionPane.showMessageDialog(this,
                    "\u0e40\u0e07\u0e34\u0e19\u0e44\u0e21\u0e48\u0e1e\u0e2d! \u0e15\u0e49\u0e2d\u0e07\u0e43\u0e0a\u0e49 50 \u0e1a\u0e32\u0e17\n\u0e21\u0e35\u0e40\u0e07\u0e34\u0e19: " + logic.getMoney() + " \u0e1a\u0e32\u0e17",
                    "\u0e17\u0e33\u0e01\u0e31\u0e1a\u0e02\u0e49\u0e32\u0e27", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "\u0e17\u0e33\u0e01\u0e31\u0e1a\u0e02\u0e49\u0e32\u0e27\u0e2a\u0e33\u0e40\u0e23\u0e47\u0e08 \uD83C\uDF72\n-50 \u0e1a\u0e32\u0e17 / +50 \u0e1e\u0e25\u0e31\u0e07\u0e07\u0e32\u0e19\n\n" + logic.getStatusText(),
                    "\u0e17\u0e33\u0e01\u0e31\u0e1a\u0e02\u0e49\u0e32\u0e27", JOptionPane.INFORMATION_MESSAGE);
            }
            refreshStatus();
        });

        bottom.add(sleepBtn);
        bottom.add(cookBtn);
        add(bottom, BorderLayout.SOUTH);

        // HierarchyListener — update status ทุกครั้งที่เปิดหน้า
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                refreshStatus();
            }
        });
    }

    private void refreshStatus() {
        statusLabel.setText("<html><div style='text-align:center;'>" +
            logic.getStatusText().replace("\n", "<br>") + "</div></html>");
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, new Color(0xF7D6E0), 0, getHeight(), new Color(0xD9AED0)));
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    private JButton bigBtn(String text, Color base) {
        JButton b = new JButton(text);
        b.setFont(new Font("Tahoma", Font.BOLD, 18));
        b.setForeground(Color.WHITE);
        b.setBackground(base);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}