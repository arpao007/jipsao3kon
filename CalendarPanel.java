import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CalendarPanel extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel mainContainer;
    private final GameLogic logic;

    private JLabel header;

    public CalendarPanel(CardLayout cardLayout, JPanel mainContainer, GameLogic logic) {
        this.cardLayout = cardLayout;
        this.mainContainer = mainContainer;
        this.logic = logic;

        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(0xFFF7FB));

        header = new JLabel("", SwingConstants.CENTER);
        header.setFont(new Font("Tahoma", Font.BOLD, 28));
        header.setForeground(new Color(0x5A3060));
        add(header, BorderLayout.NORTH);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent e) { refresh(); }
            @Override public void componentResized(ComponentEvent e){ refresh(); }
        });
    }

    private void refresh() {
        header.setText("📅 " + logic.getSeasonName() + "  |  ปีที่ " + logic.getYearIndex()
                + "  |  วัน " + logic.getDayInSeason() + "/28");

        // ล้างกลางจอแล้วสร้างใหม่
        if (getComponentCount() > 1) {
            // remove CENTER + SOUTH ถ้ามี
            for (int i = getComponentCount()-1; i >= 1; i--) remove(i);
        }

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        // วันในสัปดาห์
        String[] days = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
        JPanel grid = new JPanel(new GridLayout(5, 7, 8, 8)); // 1 แถวหัว + 4 สัปดาห์
        grid.setOpaque(false);

        // Header row
        for (String d : days) {
            JLabel lb = new JLabel(d, SwingConstants.CENTER);
            lb.setFont(new Font("Tahoma", Font.BOLD, 14));
            lb.setForeground(new Color(0xA076BB));
            grid.add(wrapCell(lb, new Color(0xF3E6F7), new Color(0xE0A0C8), false));
        }

        int today = logic.getDayInSeason(); // 1..28
        for (int day = 1; day <= 28; day++) {
            JLabel lb = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            lb.setFont(new Font("Tahoma", Font.BOLD, 16));
            lb.setForeground(new Color(0x5A3060));

            boolean isToday = (day == today);
            Color bg = isToday ? new Color(0xFFE0EE) : Color.WHITE;
            Color border = isToday ? new Color(0xE8759A) : new Color(0xE0A0C8);

            grid.add(wrapCell(lb, bg, border, isToday));
        }

        center.add(grid, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // bottom buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 10));
        bottom.setBackground(new Color(0xFFF0F5));

        JButton back = new JButton("← กลับ");
        back.setFont(new Font("Tahoma", Font.BOLD, 14));
        back.setFocusPainted(false);
        back.addActionListener(e -> cardLayout.show(mainContainer, "STORY"));

        JButton goHome = new JButton("🏠 ไปบ้าน");
        goHome.setFont(new Font("Tahoma", Font.BOLD, 14));
        goHome.setFocusPainted(false);
        goHome.addActionListener(e -> cardLayout.show(mainContainer, "HOME"));

        bottom.add(back);
        bottom.add(goHome);
        add(bottom, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JComponent wrapCell(JLabel label, Color bg, Color border, boolean highlight) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bg);
        p.setBorder(BorderFactory.createLineBorder(border, highlight ? 2 : 1, true));
        p.add(label, BorderLayout.CENTER);
        return p;
    }
}