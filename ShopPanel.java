import javax.swing.*;
import java.awt.*;

/**
 * ShopPanel.java
 * หน้าร้านค้า — เปิดจาก hamburger button
 * สินค้า: ดอกกุหลาบ, ช็อคโกแลต, ขนมกินเล่น, ช่อดอกไม้
 */
public class ShopPanel extends JPanel {

    private static final Color BG      = new Color(0xFFF0F5);
    private static final Color ACCENT  = new Color(0xE91E8C);
    private static final Color GOLD    = new Color(0xFFB300);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color GRAY    = new Color(0x888888);

    private final GameLogic  gameLogic;
    private final Shoplogic  shopLogic;
    private final Runnable   onClose;

    private JLabel moneyLabel;
    private JLabel energyLabel;
    private JLabel affectionLabel;

    public ShopPanel(GameLogic gameLogic, Runnable onClose) {
        this.gameLogic = gameLogic;
        this.shopLogic = new Shoplogic(gameLogic);
        this.onClose   = onClose;

        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildGrid(),      BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ── Header ──────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(ACCENT);
        p.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("🛍️  ร้านค้า");
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

    // ── 2×2 Grid ────────────────────────────
    private JPanel buildGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setBackground(BG);
        grid.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        // { emoji, ชื่อ, คำอธิบาย, ราคา, affection, energy, id }
        Object[][] items = {
            {"🌹", "ดอกกุหลาบ",  "สัญลักษณ์แห่งความรัก ❤️",  150, 15, 5, "rose"},
            {"🍫", "ช็อคโกแลต", "หวานละมุน ใจเขาอ่อน 💕",    80,  8,  2, "choco"},
            {"🍿", "ขนมกินเล่น","กินด้วยกันสนุกๆ 😄",         30,  3,  1, "snack"},
            {"🌸", "ช่อดอกไม้",  "สวยงาม หอมกรุ่น 🌺",        200, 25, 8, "bouquet"},
        };

        for (Object[] it : items) {
            grid.add(makeCard(
                (String)it[0], (String)it[1], (String)it[2],
                (int)it[3],    (int)it[4],    (int)it[5],   (String)it[6]
            ));
        }
        return grid;
    }

    private JPanel makeCard(String emoji, String name, String desc,
                             int price, int aff, int eng, String id) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xF8BBD0), 1, true),
            BorderFactory.createEmptyBorder(14, 14, 12, 14)
        ));

        // top row: emoji + name
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        top.setOpaque(false);
        JLabel emojiLbl = new JLabel(emoji);
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Tahoma", Font.BOLD, 17));
        top.add(emojiLbl); top.add(nameLbl);

        // desc
        JLabel descLbl = new JLabel(desc, SwingConstants.CENTER);
        descLbl.setFont(new Font("Tahoma", Font.PLAIN, 13));
        descLbl.setForeground(GRAY);

        // stats
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        stats.setOpaque(false);
        stats.add(makeTag("💝 +" + aff, new Color(0xE91E8C)));
        stats.add(makeTag("⚡ -" + eng,  new Color(0xFF8F00)));

        // bottom: price + buy button
        JPanel bot = new JPanel(new BorderLayout(8, 0));
        bot.setOpaque(false);
        JLabel priceLbl = new JLabel("฿ " + price);
        priceLbl.setFont(new Font("Tahoma", Font.BOLD, 20));
        priceLbl.setForeground(GOLD);

        JButton buyBtn = new JButton("ซื้อ");
        buyBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        buyBtn.setBackground(ACCENT);
        buyBtn.setForeground(Color.WHITE);
        buyBtn.setFocusPainted(false);
        buyBtn.setPreferredSize(new Dimension(70, 32));
        buyBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buyBtn.addActionListener(e -> handleBuy(id, name, price, aff, eng));

        bot.add(priceLbl, BorderLayout.WEST);
        bot.add(buyBtn,   BorderLayout.EAST);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        descLbl.setAlignmentX(CENTER_ALIGNMENT);
        stats.setAlignmentX(CENTER_ALIGNMENT);
        center.add(descLbl);
        center.add(Box.createVerticalStrut(4));
        center.add(stats);

        card.add(top,    BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(bot,    BorderLayout.SOUTH);
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
        bar.setBackground(new Color(0xFCE4EC));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xF8BBD0)));

        moneyLabel     = new JLabel();
        energyLabel    = new JLabel();
        affectionLabel = new JLabel();

        for (JLabel l : new JLabel[]{moneyLabel, energyLabel, affectionLabel}) {
            l.setFont(new Font("Tahoma", Font.BOLD, 14));
            l.setForeground(new Color(0x880E4F));
            bar.add(l);
        }
        refreshStatus();
        return bar;
    }

    private void refreshStatus() {
        moneyLabel.setText("💰 " + gameLogic.getMoney() + " บาท");
        energyLabel.setText("⚡ " + gameLogic.getEnergy() + "/" + gameLogic.getMaxEnergy());
        affectionLabel.setText("💝 " + gameLogic.getCurrentAffection() + "/100");
    }

    // ── Buy Handler ──────────────────────────
    private void handleBuy(String id, String name, int price, int aff, int eng) {
        Shoplogic.ShopItem target = null;
        for (Shoplogic.ShopItem it : shopLogic.getShopItems())
            if (it.id.equals(id)) { target = it; break; }
        if (target == null)
            target = new Shoplogic.ShopItem(id, name, price, aff, eng);

        Shoplogic.Result r = shopLogic.buyItem(target);
        String msg;
        switch (r) {
            case SUCCESS:
                msg = "✅ ซื้อ " + name + " สำเร็จ!\n💝 ความชอบ +" + aff; break;
            case NOT_ENOUGH_MONEY:
                msg = "❌ เงินไม่พอ!\nต้องการ ฿" + price + "  มี ฿" + gameLogic.getMoney(); break;
            default:
                msg = "❌ พลังงานไม่พอ!\nต้องการ ⚡" + eng + "  มี ⚡" + gameLogic.getEnergy();
        }
        JOptionPane.showMessageDialog(this, msg,
            r == Shoplogic.Result.SUCCESS ? "สำเร็จ 🎉" : "ไม่สำเร็จ",
            r == Shoplogic.Result.SUCCESS ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        refreshStatus();
    }

    // ── Static launcher ──────────────────────
    public static void showAsDialog(JFrame parent, GameLogic gl) {
        JDialog d = new JDialog(parent, "ร้านค้า", true);
        d.setContentPane(new ShopPanel(gl, d::dispose));
        d.setSize(640, 520);
        d.setLocationRelativeTo(parent);
        d.setResizable(false);
        d.setVisible(true);
    }
}