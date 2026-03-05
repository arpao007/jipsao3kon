import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CalendarPanel extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel     mainContainer;
    private final GameLogic  logic;

    // สร้างครั้งเดียวใน constructor ไม่ rebuild ทุกครั้ง
    private final JLabel     header;
    private final JLabel[]   dayCells = new JLabel[28];
    private final JPanel[]   dayPanels = new JPanel[28];

    public CalendarPanel(CardLayout cardLayout, JPanel mainContainer, GameLogic logic) {
        this.cardLayout    = cardLayout;
        this.mainContainer = mainContainer;
        this.logic         = logic;

        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(0xFFF7FB));

        // ── Header ──────────────────────────────────
        header = new JLabel("", SwingConstants.CENTER);
        header.setFont(new Font("Tahoma", Font.BOLD, 24));
        header.setForeground(new Color(0x5A3060));
        header.setBorder(BorderFactory.createEmptyBorder(14, 0, 6, 0));
        add(header, BorderLayout.NORTH);

        // ── Grid ────────────────────────────────────
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        JPanel grid = new JPanel(new GridLayout(5, 7, 6, 6));
        grid.setOpaque(false);

        // แถวหัว Mon–Sun
        String[] dayNames = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
        for (String d : dayNames) {
            JLabel lb = new JLabel(d, SwingConstants.CENTER);
            lb.setFont(new Font("Tahoma", Font.BOLD, 13));
            lb.setForeground(new Color(0xA076BB));
            JPanel cell = new JPanel(new BorderLayout());
            cell.setBackground(new Color(0xF3E6F7));
            cell.setBorder(BorderFactory.createLineBorder(new Color(0xE0A0C8), 1, true));
            cell.add(lb, BorderLayout.CENTER);
            grid.add(cell);
        }

        // วัน 1-28 สร้างไว้ก่อน updateDayCells() จะ fill ทีหลัง
        for (int i = 0; i < 28; i++) {
            dayCells[i] = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
            dayCells[i].setFont(new Font("Tahoma", Font.BOLD, 15));
            dayCells[i].setForeground(new Color(0x5A3060));

            dayPanels[i] = new JPanel(new BorderLayout());
            dayPanels[i].setBackground(Color.WHITE);
            dayPanels[i].setBorder(BorderFactory.createLineBorder(new Color(0xE0A0C8), 1, true));
            dayPanels[i].add(dayCells[i], BorderLayout.CENTER);
            grid.add(dayPanels[i]);
        }

        center.add(grid, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // ── Bottom buttons ───────────────────────────
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

        // ── Trigger refresh ทุกครั้งที่เปิดหน้านี้ ──
        // addNotify() ถูกเรียกเมื่อ component ถูก attach เข้า hierarchy จริงๆ
        // ใช้ HierarchyListener จับ SHOWING_CHANGED แทน componentShown
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                updateDayCells();
            }
        });
    }

    /** อัปเดตแค่ header text + highlight วันปัจจุบัน ไม่ rebuild */
    private void updateDayCells() {
        header.setText("📅 " + logic.getSeasonName()
                + "   ปีที่ " + logic.getYearIndex()
                + "   วัน " + logic.getDayInSeason() + " / 28");

        int today = logic.getDayInSeason(); // 1..28
        for (int i = 0; i < 28; i++) {
            boolean isToday = (i + 1 == today);
            dayPanels[i].setBackground(isToday ? new Color(0xFFE0EE) : Color.WHITE);
            dayPanels[i].setBorder(BorderFactory.createLineBorder(
                isToday ? new Color(0xE8759A) : new Color(0xE0A0C8),
                isToday ? 2 : 1, true));
            dayCells[i].setForeground(isToday ? new Color(0xC0306A) : new Color(0x5A3060));
            dayCells[i].setFont(new Font("Tahoma",
                isToday ? Font.BOLD : Font.PLAIN, 15));
        }
        repaint();
    }
}