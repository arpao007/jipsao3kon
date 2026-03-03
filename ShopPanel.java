import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;

/**
 * ShopPanel.java
 * ร้านค้าแบบ Dialog
 * - แก้ปัญหา 2 อย่าง:
 *   1) ซื้อของแล้ว "ความชอบ" ไม่เพิ่ม -> ตอนซื้อจะเรียก gameLogic.addAffection(...)
 *   2) ซื้อของเกินโควต้า 3 ครั้ง/วัน -> ใช้ gameLogic.consumeGiftQuota() เพื่อหักโควต้า
 */
public class ShopPanel extends JPanel {

    private final GameLogic gameLogic;
    private final Runnable  onClose;

    // สีหลัก
    private static final Color BG      = new Color(0xFFF0F5);
    private static final Color PINK    = new Color(0xE8759A);
    private static final Color PINK2   = new Color(0xF5A8C5);
    private static final Color TEXT    = new Color(0x5A3060);
    private static final Color BORDER  = new Color(0xE0A0C8);

    // ข้อมูลไอเท็ม (ปรับได้ตามใจ)
    private static class Item {
        final String name;
        final int price;
        final int affectionDelta;
        final int energyDelta;  // เผื่ออนาคต (ตอนนี้ใส่ 0 ไว้)
        final String desc;

        Item(String name, int price, int affectionDelta, int energyDelta, String desc) {
            this.name = name;
            this.price = price;
            this.affectionDelta = affectionDelta;
            this.energyDelta = energyDelta;
            this.desc = desc;
        }
    }

    // ✅ รายการสินค้า (ยกตัวอย่าง 4 ช่อง 2x2 ตาม UI ของคุณ)
    private static final List<Item> ITEMS = List.of(
        new Item("ดอกกุหลาบ", 150, 15, 0, "สัญลักษณ์แห่งความรัก ♡"),
        new Item("ช็อกโกแลต", 120, 8,  0, "หวานละมุน ใจอ่อน ♡"),
        new Item("ขนม",        30,  3,  0, "กินด้วยกันสนุกๆ ♡"),
        new Item("ช่อดอกไม้", 200, 25, 0, "สวยงาม หอมกรุ่น ♡")
    );

    // UI components
    private JLabel moneyLbl;
    private JLabel affLbl;
    private JLabel quotaLbl;

    public ShopPanel(GameLogic gameLogic, Runnable onClose) {
        this.gameLogic = gameLogic;
        this.onClose = onClose;
        setLayout(new BorderLayout());
        setBackground(BG);
        buildUI();
        refreshStatus();
    }

    // ──────────────────────────────────────────────
    public static void showAsDialog(JFrame owner, GameLogic logic) {
        JDialog dialog = new JDialog(owner, "ร้านค้า", true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        ShopPanel panel = new ShopPanel(logic, dialog::dispose);
        dialog.setContentPane(panel);

        dialog.setSize(820, 560);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    // ──────────────────────────────────────────────
    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,PINK, getWidth(),0,PINK2));
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };
        header.setPreferredSize(new Dimension(10, 74));
        header.setOpaque(false);

        JLabel title = new JLabel("  ร้านค้า");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Tahoma", Font.BOLD, 26));
        header.add(title, BorderLayout.WEST);

        JButton closeBtn = new JButton("✕") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,40));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Tahoma", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                String t = getText();
                g2.drawString(t, (getWidth()-fm.stringWidth(t))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        closeBtn.setOpaque(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setPreferredSize(new Dimension(54, 34));
        closeBtn.addActionListener(e -> { if (onClose != null) onClose.run(); });
        JPanel closeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 18));
        closeWrap.setOpaque(false);
        closeWrap.add(closeBtn);
        header.add(closeWrap, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Center: grid 2x2
        JPanel grid = new JPanel(new GridLayout(2,2,18,18));
        grid.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
        grid.setBackground(BG);

        for (Item it : ITEMS) {
            grid.add(makeItemCard(it));
        }
        add(grid, BorderLayout.CENTER);

        // Footer status
        JPanel footer = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,190));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),18,18);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,18,18);
            }
        };
        footer.setOpaque(false);
        footer.setPreferredSize(new Dimension(10, 64));
        add(footer, BorderLayout.SOUTH);

        moneyLbl = new JLabel();
        moneyLbl.setFont(new Font("Tahoma", Font.BOLD, 16));
        moneyLbl.setForeground(new Color(0xFF9800));
        moneyLbl.setBounds(18, 10, 220, 40);
        footer.add(moneyLbl);

        affLbl = new JLabel();
        affLbl.setFont(new Font("Tahoma", Font.BOLD, 16));
        affLbl.setForeground(TEXT);
        affLbl.setBounds(260, 10, 220, 40);
        footer.add(affLbl);

        quotaLbl = new JLabel();
        quotaLbl.setFont(new Font("Tahoma", Font.BOLD, 16));
        quotaLbl.setForeground(new Color(0xA076BB));
        quotaLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        quotaLbl.setBounds(520, 10, 270, 40);
        footer.add(quotaLbl);
    }

    private JPanel makeItemCard(Item it) {
        JPanel card = new JPanel(null) {
            boolean hov=false;
            { setOpaque(false);
              addMouseListener(new MouseAdapter() {
                  @Override public void mouseEntered(MouseEvent e){ hov=true; repaint(); }
                  @Override public void mouseExited(MouseEvent e) { hov=false; repaint(); }
              });
            }
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? new Color(255, 245, 252) : Color.WHITE);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),18,18);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,18,18);
            }
        };
        card.setBackground(Color.WHITE);

        JLabel name = new JLabel(it.name, SwingConstants.CENTER);
        name.setFont(new Font("Tahoma", Font.BOLD, 18));
        name.setForeground(TEXT);
        name.setBounds(0, 18, 10, 22); // width set later
        card.add(name);

        JLabel desc = new JLabel(it.desc, SwingConstants.CENTER);
        desc.setFont(new Font("Tahoma", Font.PLAIN, 12));
        desc.setForeground(new Color(0x7A4A80));
        desc.setBounds(0, 44, 10, 18);
        card.add(desc);

        JLabel eff = new JLabel(effectText(it), SwingConstants.CENTER);
        eff.setFont(new Font("Tahoma", Font.BOLD, 13));
        eff.setForeground(new Color(0xE8759A));
        eff.setBounds(0, 68, 10, 18);
        card.add(eff);

        JLabel price = new JLabel("฿ " + it.price, SwingConstants.LEFT);
        price.setFont(new Font("Tahoma", Font.BOLD, 18));
        price.setForeground(new Color(0xFF9800));
        price.setBounds(18, 102, 140, 26);
        card.add(price);

        JButton buyBtn = new JButton("ซื้อ") {
            boolean hov=false;
            { setOpaque(false); setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
              setCursor(new Cursor(Cursor.HAND_CURSOR));
              addMouseListener(new MouseAdapter() {
                  @Override public void mouseEntered(MouseEvent e){ hov=true; repaint(); }
                  @Override public void mouseExited(MouseEvent e) { hov=false; repaint(); }
              });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color top = hov ? PINK2 : new Color(0xF0C0D8);
                Color bot = hov ? PINK  : new Color(0xD06088);
                g2.setPaint(new GradientPaint(0,0,top,0,getHeight(),bot));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(new Color(255,255,255,160));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,14,14);
                g2.setFont(new Font("Tahoma", Font.BOLD, 14));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                String t = getText();
                g2.drawString(t, (getWidth()-fm.stringWidth(t))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        buyBtn.setBounds(220, 96, 80, 36);
        buyBtn.addActionListener(e -> handleBuy(it));
        card.add(buyBtn);

        // resize listener to set label widths properly
        card.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                int w = card.getWidth();
                name.setBounds(0, 18, w, 22);
                desc.setBounds(0, 44, w, 18);
                eff.setBounds(0, 68, w, 18);
                buyBtn.setBounds(w - 98, card.getHeight() - 54, 80, 36);
                price.setBounds(18, card.getHeight() - 48, 160, 26);
            }
        });

        return card;
    }

    private String effectText(Item it) {
        String a = (it.affectionDelta >= 0 ? "+" : "") + it.affectionDelta;
        String e = (it.energyDelta >= 0 ? "+" : "") + it.energyDelta;
        if (it.energyDelta != 0) return "ความชอบ " + a + "   พลังงาน " + e;
        return "ความชอบ " + a;
    }

    // ──────────────────────────────────────────────
    // ซื้อของ (แก้โควต้า + เพิ่มความชอบจริงลง GameLogic)
    // ──────────────────────────────────────────────
    private void handleBuy(Item it) {
        // 1) เช็คโควต้า (วันละ 3)
        int quota = gameLogic.getGiftQuota();
        if (quota <= 0) {
            JOptionPane.showMessageDialog(this,
                "วันนี้ซื้อของครบโควต้าแล้ว (3 ครั้ง/วัน)\nลองใหม่พรุ่งนี้นะคะ",
                "โควต้าเต็ม", JOptionPane.INFORMATION_MESSAGE);
            refreshStatus();
            return;
        }

        // 2) เช็คเงินพอไหม
        if (gameLogic.getMoney() < it.price) {
            JOptionPane.showMessageDialog(this,
                "เงินไม่พอค่ะ (ต้องใช้ ฿ " + it.price + ")",
                "ซื้อไม่สำเร็จ", JOptionPane.WARNING_MESSAGE);
            refreshStatus();
            return;
        }

        // 3) ทำรายการ: หักเงิน + หักโควต้า + เพิ่มค่าความชอบ (และพลังงานถ้ามี)
        //    หักโควต้าให้หัก "ตอนสำเร็จจริง" เท่านั้น
        boolean okQuota = gameLogic.consumeGiftQuota();
        if (!okQuota) {
            // กัน edge case ถ้า resetDailyLimitsIfNeeded ทำให้ quota เปลี่ยน
            JOptionPane.showMessageDialog(this,
                "วันนี้ซื้อของครบโควต้าแล้ว (3 ครั้ง/วัน)",
                "โควต้าเต็ม", JOptionPane.INFORMATION_MESSAGE);
            refreshStatus();
            return;
        }

        gameLogic.spendMoney(it.price);
        if (it.affectionDelta != 0) gameLogic.addAffection(it.affectionDelta);

        refreshStatus();

        // 4) แจ้งผล
        String msg = "ซื้อ " + it.name + " สำเร็จ!\n" +
                     "ความชอบ " + (it.affectionDelta >= 0 ? "+" : "") + it.affectionDelta +
                     "\nโควต้าเหลือ: " + gameLogic.getGiftQuota() + "/3";
        JOptionPane.showMessageDialog(this, msg, "สำเร็จ ♡", JOptionPane.INFORMATION_MESSAGE);

        // 5) บังคับ repaint หน้าหลัก (กันกรณี bar ด้านหลังไม่อัปเดตทันที)
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) w.repaint();
    }

    private void refreshStatus() {
        moneyLbl.setText("฿ " + gameLogic.getMoney());
        affLbl.setText("ความชอบ: " + gameLogic.getCurrentAffection() + "/100");
        quotaLbl.setText("โควต้า: " + gameLogic.getGiftQuota() + "/3");
        revalidate();
        repaint();
    }
}
