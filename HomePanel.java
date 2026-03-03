import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class HomePanel extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel mainContainer;
    private final GameLogic logic;

    public HomePanel(CardLayout cardLayout, JPanel mainContainer, GameLogic logic) {
        this.cardLayout = cardLayout;
        this.mainContainer = mainContainer;
        this.logic = logic;

        setLayout(null);
        setOpaque(false);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { rebuild(); }
            @Override public void componentShown(ComponentEvent e)   { rebuild(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, new Color(0xF7D6E0), 0, getHeight(), new Color(0xD9AED0)));
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    private void rebuild() {
        int w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;

        removeAll();

        // Title
        JLabel title = new JLabel("🏠 Home", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 36));
        title.setForeground(new Color(0x5A3060));
        title.setBounds(0, 40, w, 50);
        add(title);

        // Status
        JLabel status = new JLabel("<html><div style='text-align:center;'>" +
                logic.getStatusText().replace("\n", "<br>") +
                "</div></html>", SwingConstants.CENTER);
        status.setFont(new Font("Tahoma", Font.PLAIN, 16));
        status.setForeground(new Color(0x5A3060));
        status.setBounds(w/2 - 420, 110, 840, 120);
        add(status);

        // Back to story (optional small)
        JButton back = new JButton("← กลับไปเนื้อเรื่อง");
        back.setFont(new Font("Tahoma", Font.BOLD, 14));
        back.setFocusPainted(false);
        back.setBounds(20, 20, 170, 34);
        back.addActionListener(e -> cardLayout.show(mainContainer, "STORY"));
        add(back);

        // Two big buttons bottom
        int btnW = Math.min(420, (int)(w * 0.38));
        int btnH = 90;
        int gap = 24;
        int totalW = btnW * 2 + gap;
        int startX = (w - totalW) / 2;
        int y = h - btnH - 80;

        JButton sleepBtn = bigBtn("🛌 เข้านอน (ข้ามวัน)", new Color(0xA076BB));
        sleepBtn.setBounds(startX, y, btnW, btnH);
        sleepBtn.addActionListener(e -> {
            logic.nextDay(); // ข้ามวัน + รีโควต้ารายวัน + พลังเต็ม (ตาม GameLogic)

            // ✅ Auto-save เมื่อเข้านอน
            try {
                SaveManager.save(logic, logic.getGameDate());
            } catch (Exception ex) {
                // ไม่ให้เกมพังถ้า save ไม่ได้
                System.out.println("[HomePanel] Auto-save failed: " + ex.getMessage());
            }

            JOptionPane.showMessageDialog(this,
                    "เข้านอนเรียบร้อย 🌙\nข้ามวันแล้ว!\n\n" + logic.getStatusText(),
                    "เข้านอน", JOptionPane.INFORMATION_MESSAGE);
            rebuild();
            repaint();
        });
        add(sleepBtn);

        JButton cookBtn = bigBtn("🍳 ทำกับข้าว (-50฿, +50⚡)", new Color(0xE8759A));
        cookBtn.setBounds(startX + btnW + gap, y, btnW, btnH);
        cookBtn.addActionListener(e -> {
            boolean ok = logic.cookFood();
            if (!ok) {
                JOptionPane.showMessageDialog(this,
                        "เงินไม่พอ! ต้องใช้ 50 บาท\nมีเงิน: " + logic.getMoney() + " บาท",
                        "ทำกับข้าว", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "ทำกับข้าวสำเร็จ 🍲\n-50 บาท / +50 พลังงาน\n\n" + logic.getStatusText(),
                        "ทำกับข้าว", JOptionPane.INFORMATION_MESSAGE);
            }
            rebuild();
            repaint();
        });
        add(cookBtn);

        revalidate();
        repaint();
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