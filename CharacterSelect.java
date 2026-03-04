        import java.awt.*;
import javax.swing.*;

public class CharacterSelect extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private GameLogic logic;
    private Image backgroundImage;

    public CharacterSelect(CardLayout cardLayout, JPanel mainContainer, GameLogic logic) {
        this.cardLayout = cardLayout;
        this.mainContainer = mainContainer;
        this.logic = logic;

        // 1. โหลดภาพพื้นหลังหน้าเลือกตัวละคร
        try {
            ImageIcon bgIcon = new ImageIcon("res/MC.jpg");
            backgroundImage = bgIcon.getImage();
        } catch (Exception e) {
            System.out.println("Error loading background: " + e.getMessage());
        }

        // 2. ใช้ BorderLayout เพื่อจัดวางส่วนหัวและส่วนเนื้อหา
        setLayout(new BorderLayout());

        // --- ส่วนหัวข้อ: ใช้ HTML เพื่อทำตัวอักษรสีขาวเน้นขอบดำให้อ่านง่าย ---
        JLabel title = new JLabel("<html><div style='text-align: center; color: #FFFFFF; " +
        "text-shadow: 2px 2px 0px #000, -2px -2px 0px #000, 2px -2px 0px #000, " +
        "-2px 2px 0px #000, 0px 2px 0px #000, 0px -2px 0px #000, 2px 0px #000, -2px 0px #000;'>" +
        "เลือกสาวที่อยากทำความรู้จัก</div></html>", SwingConstants.CENTER);

        title.setFont(new Font("Tahoma", Font.BOLD, 42));
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 10, 0)); 
        add(title, BorderLayout.NORTH);

        // --- ส่วนแสดงตัวละคร (Center) ---
        JPanel charContainer = new JPanel(new GridLayout(1, 3, 30, 0));
        charContainer.setOpaque(false);
        charContainer.setBorder(BorderFactory.createEmptyBorder(10, 60, 20, 60));

        // ตรวจสอบว่าชื่อเหล่านี้ตรงกับเงื่อนไขใน GameUI.java
        charContainer.add(createCharCard("มีน", "res/Mean.png"));
        charContainer.add(createCharCard("พลอย", "res/Emean.png"));
        charContainer.add(createCharCard("ลิลลี่", "res/Lilli.png"));

        add(charContainer, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private JPanel createCharCard(String name, String imgPath) {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setOpaque(false);

        // --- ส่วนรูปภาพตัวละคร ---
        JLabel imgLabel = new JLabel();
        ImageIcon icon = new ImageIcon(imgPath);
        
        if (icon.getIconWidth() > 0) {
            // ปรับขนาดรูปให้ชิดขอบล่างของ Card
            Image scaled = icon.getImage().getScaledInstance(260, 480, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        } else {
            imgLabel.setText("ไม่พบไฟล์: " + name);
            imgLabel.setForeground(Color.RED);
        }
        
        imgLabel.setHorizontalAlignment(JLabel.CENTER);
        imgLabel.setVerticalAlignment(JLabel.BOTTOM);

        // --- ส่วนปุ่มกด ---
        JButton selectBtn = new JButton("เลือก " + name);
        styleButton(selectBtn);
        
        selectBtn.addActionListener(e -> {
            // *** แก้ไขแล้ว: เปิดใช้งานเพื่อส่งชื่อตัวละครไปที่ Logic ***
            logic.setSelectedCharacter(name);
            
            // สลับหน้าจอไปยังหน้า Gameplay
            cardLayout.show(mainContainer, "GAMEPLAY");
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        btnPanel.setOpaque(false);
        btnPanel.add(selectBtn);

        card.add(imgLabel, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    private void styleButton(JButton btn) {
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setFont(new Font("Tahoma", Font.BOLD, 18));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(255, 20, 147));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(255, 182, 193), 3));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 240, 245));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });
    }
}