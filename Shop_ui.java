import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class Shop_ui extends JPanel {

    private final Shoplogic shopLogic;
    private final GameLogic gameLogic;
    private final CardLayout cardLayout;
    private final JPanel mainContainer;

    private JLabel statusLabel;

    // =============================================
    // Constructor
    // =============================================
    public Shop_ui(CardLayout cardLayout, JPanel mainContainer, GameLogic gameLogic) {
        this.cardLayout     = cardLayout;
        this.mainContainer  = mainContainer;
        this.gameLogic      = gameLogic;
        this.shopLogic      = new Shoplogic(gameLogic);

        setLayout(null);
        setBackground(new Color(255, 240, 248));
        setPreferredSize(new Dimension(1200, 800));

        buildUI();
    }

    // =============================================
    // สร้าง UI ทั้งหมด
    // =============================================
    private void buildUI() {

        // ---- หัวเรื่อง ----
        JLabel titleLabel = new JLabel("🛍️  ร้านค้า", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 48));
        titleLabel.setForeground(new Color(220, 80, 140));
        titleLabel.setBounds(0, 20, 1200, 70);
        add(titleLabel);

        // ---- Status Bar ----
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(220, 80, 140));
        statusLabel.setBounds(100, 105, 1000, 45);
        statusLabel.setBorder(new EmptyBorder(0, 20, 0, 20));
        refreshStatus();
        add(statusLabel);

        // ---- รายการสินค้า ----
        List<Shoplogic.ShopItem> items = shopLogic.getAllItems();

        int cardW      = 280;
        int cardH      = 420;
        int totalWidth = items.size() * cardW + (items.size() - 1) * 60;
        int startX     = (1200 - totalWidth) / 2;
        int cardY      = 170;

        for (int i = 0; i < items.size(); i++) {
            Shoplogic.ShopItem item = items.get(i);
            int x = startX + i * (cardW + 60);
            JPanel card = createItemCard(item, cardW, cardH);
            card.setBounds(x, cardY, cardW, cardH);
            add(card);
        }

        // ---- ปุ่มกลับ ----
        JButton backBtn = makeButton("← กลับ");
        backBtn.setBounds(50, 720, 180, 50);
        backBtn.addActionListener(e -> cardLayout.show(mainContainer, "GAMEPLAY"));
        add(backBtn);
    }

    // =============================================
    // Card สินค้าแต่ละชิ้น
    // =============================================
    private JPanel createItemCard(Shoplogic.ShopItem item, int w, int h) {
        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 105, 180), 3, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // ---- กรอบรูปภาพ ----
        JLabel imgFrame = new JLabel();
        imgFrame.setBounds(20, 15, w - 40, 200);
        imgFrame.setOpaque(true);
        imgFrame.setBackground(new Color(255, 230, 245));
        imgFrame.setBorder(new LineBorder(new Color(255, 160, 200), 2, true));
        imgFrame.setHorizontalAlignment(SwingConstants.CENTER);
        imgFrame.setVerticalAlignment(SwingConstants.CENTER);

        // โหลดรูปหรือแสดง placeholder
        loadItemImage(imgFrame, item.imagePath, w - 60, 180);

        card.add(imgFrame);

        // ---- ชื่อสินค้า ----
        JLabel nameLabel = new JLabel(item.name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 26));
        nameLabel.setForeground(new Color(200, 60, 120));
        nameLabel.setBounds(0, 228, w, 36);
        card.add(nameLabel);

        // ---- คำอธิบาย ----
        JLabel descLabel = new JLabel("<html><div style='text-align:center'>" + item.description + "</div></html>", SwingConstants.CENTER);
        descLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
        descLabel.setForeground(new Color(100, 100, 100));
        descLabel.setBounds(5, 268, w - 10, 50);
        card.add(descLabel);

        // ---- ราคา ----
        JLabel priceLabel = new JLabel("💰 " + item.price + " บาท", SwingConstants.CENTER);
        priceLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        priceLabel.setForeground(new Color(30, 130, 30));
        priceLabel.setBounds(0, 322, w, 30);
        card.add(priceLabel);

        // ---- พลังงานที่ใช้ ----
        JLabel energyLabel = new JLabel("⚡ ใช้พลังงาน: " + item.energyCost, SwingConstants.CENTER);
        energyLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
        energyLabel.setForeground(new Color(180, 100, 0));
        energyLabel.setBounds(0, 352, w, 24);
        card.add(energyLabel);

        // ---- ปุ่มซื้อ ----
        JButton buyBtn = makeButton("ซื้อ & มอบให้");
        buyBtn.setBounds(20, 384, w - 40, 46);
        buyBtn.setFont(new Font("Tahoma", Font.BOLD, 18));
        buyBtn.addActionListener(e -> handleBuy(item));
        card.add(buyBtn);

        return card;
    }

    // =============================================
    // โหลดรูปหรือแสดง placeholder
    // =============================================
    private void loadItemImage(JLabel label, String path, int w, int h) {
        try {
            ImageIcon icon = new ImageIcon(path);
            // ตรวจว่ารูปโหลดได้จริง
            if (icon.getIconWidth() <= 0) throw new Exception("Image not found");
            Image img = icon.getImage();
            BufferedImage bimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bimg.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(img, 0, 0, w, h, null);
            g2.dispose();
            label.setIcon(new ImageIcon(bimg));
            label.setText("");
        } catch (Exception e) {
            // Placeholder ถ้าไม่มีรูป
            label.setIcon(null);
            label.setText("<html><div style='color:#ccc; font-size:14px; text-align:center'>📷<br>วางรูปที่นี่</div></html>");
        }
    }

    // =============================================
    // จัดการการซื้อ
    // =============================================
    private void handleBuy(Shoplogic.ShopItem item) {
        Shoplogic.BuyResult result = shopLogic.buyItem(item.id);
        String msg = shopLogic.getBuyResultMessage(result, item.name);

        refreshStatus();

        if (result == Shoplogic.BuyResult.SUCCESS) {
            JOptionPane.showMessageDialog(this, msg, "✅ ซื้อสำเร็จ", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, msg, "❌ ไม่สำเร็จ", JOptionPane.WARNING_MESSAGE);
        }
    }

    // =============================================
    // อัปเดต Status Bar
    // =============================================
    public void refreshStatus() {
        if (statusLabel != null) {
            statusLabel.setText(shopLogic.getShopStatusText());
        }
    }

    // =============================================
    // สไตล์ปุ่ม (เหมือน GameUI)
    // =============================================
    private JButton makeButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Tahoma", Font.BOLD, 20));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(255, 105, 180));
        btn.setBorder(new LineBorder(new Color(255, 105, 180), 2, true));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}