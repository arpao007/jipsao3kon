import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * HamburgerMenu.java
 * ปุ่ม ☰ ที่มุมบนขวา — กดแล้วเปิด slide-in menu
 * รายการ: 🛍 ร้านค้า | 💼 ทำงาน | 💾 บันทึก | 🚪 ออก
 */
public class HamburgerMenu {

    private final JFrame     frame;
    private final GameLogic  gameLogic;
    private final GameDate   gameDate;
    private final Runnable   onSave;
    private final Runnable   onExit;

    // popup panel
    private JWindow popup;

    public HamburgerMenu(JFrame frame, GameLogic gameLogic, GameDate gameDate,
                         Runnable onSave, Runnable onExit) {
        this.frame     = frame;
        this.gameLogic = gameLogic;
        this.gameDate  = gameDate;
        this.onSave    = onSave;
        this.onExit    = onExit;
    }

    /** สร้างปุ่ม ☰ พร้อม action */
    public JButton createButton() {
        JButton btn = new JButton("☰");
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setToolTipText("เมนู");
        btn.addActionListener(e -> toggleMenu(btn));
        return btn;
    }

    private void toggleMenu(JButton anchor) {
        if (popup != null && popup.isVisible()) {
            popup.dispose();
            popup = null;
            return;
        }
        showMenu(anchor);
    }

    private void showMenu(JButton anchor) {
        popup = new JWindow(frame);
        popup.setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDDDDDD), 1, true),
            BorderFactory.createEmptyBorder(8, 0, 8, 0)
        ));

        panel.add(makeMenuItem("🛍️  ร้านค้า",   new Color(0xE91E8C), () -> {
            closeMenu();
            ShopPanel.showAsDialog(frame, gameLogic);
        }));
        panel.add(makeDivider());
        panel.add(makeMenuItem("💼  ทำงาน",     new Color(0x1565C0), () -> {
            closeMenu();
            WorkPanel.showAsDialog(frame, gameLogic);
        }));
        panel.add(makeDivider());
        panel.add(makeMenuItem("💾  บันทึกเกม", new Color(0x2E7D32), () -> {
            closeMenu();
            if (onSave != null) onSave.run();
        }));
        panel.add(makeDivider());
        panel.add(makeMenuItem("🚪  ออกเกม",    new Color(0x757575), () -> {
            closeMenu();
            if (onExit != null) onExit.run();
        }));

        popup.setContentPane(panel);
        popup.setSize(200, panel.getPreferredSize().height + 16);

        // วางตำแหน่งใต้ปุ่ม
        Point loc = anchor.getLocationOnScreen();
        popup.setLocation(loc.x - popup.getWidth() + anchor.getWidth(),
                          loc.y + anchor.getHeight() + 4);
        popup.setVisible(true);

        // คลิกนอก popup ให้ปิด
        Toolkit.getDefaultToolkit().addAWTEventListener(evt -> {
            if (evt instanceof MouseEvent) {
                MouseEvent me = (MouseEvent) evt;
                if (me.getID() == MouseEvent.MOUSE_PRESSED) {
                    if (popup != null && !popup.getBounds().contains(me.getLocationOnScreen())) {
                        closeMenu();
                    }
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK);
    }

    private JButton makeMenuItem(String text, Color color, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Tahoma", Font.BOLD, 15));
        btn.setForeground(color);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
                btn.setContentAreaFilled(true);
            }
            public void mouseExited(MouseEvent e) { btn.setContentAreaFilled(false); }
        });
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private JSeparator makeDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0xEEEEEE));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private void closeMenu() {
        if (popup != null) { popup.dispose(); popup = null; }
    }
}