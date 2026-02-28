import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.*;

public class GameUI {
    private JFrame frame;
    private GameLogic logic;
    private CardLayout cardLayout;
    private JPanel mainContainer;

    private List<Dialogue> currentStory;
    private int currentStep = 0;
    private JLabel dialogLabel, speakerLabel, characterSprite, bgLabel;
    private JPanel choicePanel;

    private JLabel moneyLabel, affectionLabel, energyLabel, dateLabel;
    private Home homePanel;
    private JPanel menuPopup;
    private boolean menuOpen = false;
    private GameDate gameDate = new GameDate();
    private static final String[] MONTH_SHORT = {
        "Jan","Feb","Mar","Apr","May","Jun",
        "Jul","Aug","Sep","Oct","Nov","Dec"
    };

    public GameUI(GameLogic logic) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        this.logic = logic;
        initWindow();
    }

    public void initWindow() {
        frame = new JFrame("FirstLove - เกมจีบสาว");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        mainContainer.add(createMenuPanel(), "MENU");
        mainContainer.add(new CharacterSelect(cardLayout, mainContainer, logic), "CHAR_SELECT");
        mainContainer.add(createGameplayPanel(), "GAMEPLAY");
        mainContainer.add(new WorkGame_ui(cardLayout, mainContainer, logic), "WORK");
        mainContainer.add(new Shop_ui(cardLayout, mainContainer, logic), "SHOP");
        homePanel = new Home(cardLayout, mainContainer, logic, gameDate, this::updateStatus);
        mainContainer.add(homePanel, "HOME");
        mainContainer.add(new MultiplayerLobby(cardLayout, mainContainer), "MULTI");

        frame.add(mainContainer);
    }

    // ─── หน้าเมนูหลัก ───────────────────────────────────────────────────────
    public JPanel createMenuPanel() {
        JPanel p = new JPanel(null);

        JLabel title = new JLabel("<html><div style='text-align:center;color:#FF69B4;'>First Love</div></html>",
                SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 100));
        title.setBounds(0, 80, 1200, 150);

        int bx = 490;
        JButton startBtn = new JButton("START GAME");
        styleButton(startBtn); startBtn.setBounds(bx, 300, 220, 60);
        startBtn.addActionListener(e -> cardLayout.show(mainContainer, "CHAR_SELECT"));

        JButton settingsBtn = new JButton("SETTINGS");
        styleButton(settingsBtn); settingsBtn.setBounds(bx, 380, 220, 60);

        JButton loadBtn = new JButton("LOAD GAME");
        styleButton(loadBtn); loadBtn.setBounds(bx, 460, 220, 60);
        loadBtn.addActionListener(e -> {
            if (!SaveManager.hasSave()) {
                JOptionPane.showMessageDialog(frame,
                    "ไม่พบข้อมูล Save ครับ\nกด START GAME เพื่อเริ่มใหม่",
                    "ไม่พบ Save", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            boolean ok = SaveManager.load(logic, gameDate);
            if (ok) {
                homePanel.refreshLabels();
                updateStatus();
                cardLayout.show(mainContainer, "GAMEPLAY");
                JOptionPane.showMessageDialog(frame,
                    "<html><div style='font-size:15px;text-align:center'>" +
                    "✅ โหลดเกมสำเร็จ!<br>" +
                    "ยินดีต้อนรับกลับมา " + logic.getSelectedCharacter() +
                    "</div></html>",
                    "โหลดเกม", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                    "โหลดเกมไม่สำเร็จ", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton multiBtn = new JButton("MULTIPLAYER");
        styleButton(multiBtn); multiBtn.setBounds(bx, 540, 220, 60);
        multiBtn.setForeground(new Color(100, 180, 255));
        multiBtn.addActionListener(e -> cardLayout.show(mainContainer, "MULTI"));

        JButton exitBtn = new JButton("EXIT");
        styleButton(exitBtn); exitBtn.setBounds(bx, 620, 220, 60);
        exitBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(frame, "ออกจากเกม?", "Exit",
                    JOptionPane.YES_NO_OPTION) == 0) System.exit(0);
        });

        JLabel bg = new JLabel();
        bg.setBounds(0, 0, 1200, 800);
        updateImageLayer(bg, "res/school_bg.jpg", 1200, 800);

        p.add(title); p.add(startBtn); p.add(settingsBtn);
        p.add(loadBtn); p.add(multiBtn); p.add(exitBtn); p.add(bg);
        p.setComponentZOrder(bg, p.getComponentCount() - 1);
        return p;
    }

    // ─── หน้าเล่นเกม ────────────────────────────────────────────────────────
    public JPanel createGameplayPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.BLACK);

        // BG + Sprite
        bgLabel = new JLabel();
        bgLabel.setBounds(0, 0, 1200, 800);

        characterSprite = new JLabel();
        characterSprite.setBounds(0, 0, 1200, 800);
        characterSprite.setHorizontalAlignment(SwingConstants.CENTER);

        // Status HUD ซ้ายบน
        moneyLabel     = makeHudLabel("💰 500 บาท",       new Color(255, 230, 80));
        affectionLabel = makeHudLabel("💝 ความชอบ 0/100", new Color(255, 160, 210));
        energyLabel    = makeHudLabel("⚡ 100/100",        new Color(100, 220, 255));
        dateLabel    = makeHudLabel("📅 1 Jan",            new Color(180, 220, 255));

        JPanel hudLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 7));
        hudLeft.setOpaque(true);
        hudLeft.setBackground(new Color(15, 15, 15, 200));
        hudLeft.setBorder(BorderFactory.createLineBorder(new Color(255, 105, 180), 2, true));
        JSeparator s1 = new JSeparator(JSeparator.VERTICAL); s1.setPreferredSize(new Dimension(2, 24));
        JSeparator s2 = new JSeparator(JSeparator.VERTICAL); s2.setPreferredSize(new Dimension(2, 24));
        hudLeft.add(moneyLabel); hudLeft.add(s1);
        hudLeft.add(affectionLabel); hudLeft.add(s2);
        hudLeft.add(energyLabel);
        JSeparator s3 = new JSeparator(JSeparator.VERTICAL); s3.setPreferredSize(new Dimension(2, 24));
        hudLeft.add(s3);
        hudLeft.add(dateLabel);
        hudLeft.setSize(hudLeft.getPreferredSize());
        hudLeft.setBounds(8, 8, hudLeft.getPreferredSize().width, 46);

        // ปุ่ม ☰ ขวาบน
        JButton toggleBtn = new JButton("☰");
        toggleBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        toggleBtn.setBackground(new Color(20, 20, 20));
        toggleBtn.setForeground(new Color(255, 105, 180));
        toggleBtn.setBorder(BorderFactory.createLineBorder(new Color(255, 105, 180), 2, true));
        toggleBtn.setFocusPainted(false);
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleBtn.setBounds(1200 - 8 - 50, 8, 50, 46);
        toggleBtn.addActionListener(e -> {
            menuOpen = !menuOpen;
            menuPopup.setVisible(menuOpen);
        });

        // Menu Popup กลางจอ แนวตั้ง (ซ่อนไว้ก่อน)
        menuPopup = new JPanel();
        menuPopup.setLayout(new BoxLayout(menuPopup, BoxLayout.Y_AXIS));
        menuPopup.setOpaque(true);
        menuPopup.setBackground(new Color(15, 15, 15, 235));
        menuPopup.setBorder(BorderFactory.createLineBorder(new Color(255, 105, 180), 2, true));
        menuPopup.setVisible(false);

        JButton shopBtn = makeMenuButton("🛍  ร้านค้า", new Color(255, 105, 180));
        JButton jobBtn  = makeMenuButton("💼  งาน",     new Color(100, 220, 120));

        shopBtn.addActionListener(e -> {
            menuOpen = false; menuPopup.setVisible(false);
            cardLayout.show(mainContainer, "SHOP");
        });
        jobBtn.addActionListener(e -> {
            menuOpen = false; menuPopup.setVisible(false);
            cardLayout.show(mainContainer, "WORK");
        });

        JButton homeBtn     = makeMenuButton("🏠  กลับบ้าน",   new Color(255, 200, 80));
        JButton calendarBtn = makeMenuButton("📅  ปฏิทิน",      new Color(150, 200, 255));

        homeBtn.addActionListener(e -> {
            menuOpen = false; menuPopup.setVisible(false);
            homePanel.refreshLabels();
            cardLayout.show(mainContainer, "HOME");
        });
        calendarBtn.addActionListener(e -> {
            menuOpen = false; menuPopup.setVisible(false);
            showCalendarPopup();
        });

        menuPopup.add(Box.createVerticalStrut(12));
        menuPopup.add(shopBtn);
        menuPopup.add(Box.createVerticalStrut(8));
        menuPopup.add(jobBtn);
        menuPopup.add(Box.createVerticalStrut(8));
        menuPopup.add(homeBtn);
        menuPopup.add(Box.createVerticalStrut(8));
        menuPopup.add(calendarBtn);
        menuPopup.add(Box.createVerticalStrut(12));

        int popW = 300, popH = 310;
        menuPopup.setBounds((1200 - popW) / 2, (800 - popH) / 2, popW, popH);

        // Speaker + Dialog
        speakerLabel = new JLabel("");
        speakerLabel.setBounds(50, 560, 200, 40);
        speakerLabel.setOpaque(true);
        speakerLabel.setBackground(new Color(255, 105, 180));
        speakerLabel.setForeground(Color.WHITE);
        speakerLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        speakerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        dialogLabel = new JLabel("", SwingConstants.CENTER);
        dialogLabel.setBounds(50, 600, 1100, 130);
        dialogLabel.setOpaque(true);
        dialogLabel.setBackground(new Color(255, 255, 255, 180));
        dialogLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
        dialogLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 105, 180), 3));

        // Choice Panel
        choicePanel = new JPanel(new GridLayout(0, 1, 15, 15));
        choicePanel.setBounds(300, 150, 600, 350);
        choicePanel.setOpaque(false);
        choicePanel.setVisible(false);

        // เพิ่มและจัดเลเยอร์ (index 0 = หน้าสุด)
        panel.add(menuPopup);
        panel.add(toggleBtn);
        panel.add(hudLeft);
        panel.add(choicePanel);
        panel.add(speakerLabel);
        panel.add(dialogLabel);
        panel.add(characterSprite);
        panel.add(bgLabel);

        panel.setComponentZOrder(menuPopup,       0);
        panel.setComponentZOrder(toggleBtn,       1);
        panel.setComponentZOrder(hudLeft,         2);
        panel.setComponentZOrder(choicePanel,     3);
        panel.setComponentZOrder(speakerLabel,    4);
        panel.setComponentZOrder(dialogLabel,     5);
        panel.setComponentZOrder(characterSprite, 6);
        panel.setComponentZOrder(bgLabel,         7);

        // คลิกทั่วไปเดินเรื่อง / ปิด menu ถ้าเปิดอยู่
        MouseAdapter click = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (menuOpen) {
                    menuOpen = false;
                    menuPopup.setVisible(false);
                } else if (!choicePanel.isVisible()) {
                    advanceDialogue();
                }
            }
        };
        panel.addMouseListener(click);
        dialogLabel.addMouseListener(click);

        panel.addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent e) {
                startNewStory();
                updateStatus();
            }
        });

        return panel;
    }

    // ─── HUD update ─────────────────────────────────────────────────────────
    public void updateStatus() {
        if (logic == null) return;
        if (moneyLabel     != null) moneyLabel.setText("💰 " + logic.getMoney() + " บาท");
        if (affectionLabel != null) affectionLabel.setText("💝 ความชอบ " + logic.getCurrentAffection() + "/100");
        if (energyLabel    != null) energyLabel.setText("⚡ " + logic.getEnergy() + "/" + logic.getMaxEnergy());
        if (dateLabel      != null) dateLabel.setText("📅 " + gameDate.getDay() + " " + MONTH_SHORT[gameDate.getMonth() - 1]);
    }

    // ─── Helpers ────────────────────────────────────────────────────────────
    private JLabel makeHudLabel(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Tahoma", Font.BOLD, 15));
        lbl.setForeground(color);
        return lbl;
    }

    private JButton makeMenuButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Tahoma", Font.BOLD, 20));
        btn.setBackground(new Color(30, 30, 30));
        btn.setForeground(color);
        btn.setBorder(BorderFactory.createLineBorder(color, 2, true));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(240, 50));
        btn.setPreferredSize(new Dimension(240, 50));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(60, 60, 60)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(new Color(30, 30, 30)); }
        });
        return btn;
    }

    public void styleButton(JButton btn) {
        btn.setFont(new Font("Tahoma", Font.BOLD, 22));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(255, 105, 180));
        btn.setBorder(BorderFactory.createLineBorder(new Color(255, 105, 180), 2));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void updateImageLayer(JLabel label, String path, int w, int h) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage();
            BufferedImage bimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bimg.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(img, 0, 0, w, h, null);
            g2.dispose();
            label.setIcon(new ImageIcon(bimg));
        } catch (Exception e) {
            System.err.println("Load Error: " + path);
        }
    }

    // ─── เนื้อเรื่อง ─────────────────────────────────────────────────────────
    public void startNewStory() {
        String c = logic.getSelectedCharacter();
        currentStep = 0;
        if      ("มีน".equals(c))   currentStory = MeanStory.getStory();
        else if ("ลิลลี่".equals(c)) currentStory = LilliStory.getStory();
        else if ("พลอย".equals(c))  currentStory = PloyStory.getStory();
        advanceDialogue();
    }

    public void advanceDialogue() {
        if (currentStory == null || currentStep >= currentStory.size()) {
            cardLayout.show(mainContainer, "MENU");
            return;
        }
        Dialogue d = currentStory.get(currentStep);
        updateStatus();
        speakerLabel.setText(d.speaker);
        dialogLabel.setText("<html><div style='padding:15px;'>" + d.text + "</div></html>");

        if (d.imagePath != null && !d.imagePath.isEmpty()) {
            if (d.imagePath.contains("|")) {
                String[] paths = d.imagePath.split("\\|");
                updateImageLayer(bgLabel, paths[0], 1200, 800);
                updateImageLayer(characterSprite, paths[1], 1200, 800);
            } else if ("บรรยาย".equals(d.speaker)) {
                updateImageLayer(bgLabel, d.imagePath, 1200, 800);
                characterSprite.setIcon(null);
            } else {
                updateImageLayer(characterSprite, d.imagePath, 1200, 800);
            }
        }

        if (d.choices != null && d.choices.length > 0) {
            showChoices(d.choices, d.nextSteps, d.affectionGains);
        } else {
            currentStep++;
            choicePanel.setVisible(false);
        }
    }

    public void showChoices(String[] choices, int[] nextSteps, int[] affectionGains) {
        choicePanel.removeAll();
        choicePanel.setVisible(true);
        for (int i = 0; i < choices.length; i++) {
            JButton btn = new JButton(choices[i]);
            styleButton(btn);
            final int target = (i < nextSteps.length) ? nextSteps[i] : currentStep + 1;
            final int gain   = (affectionGains != null && i < affectionGains.length) ? affectionGains[i] : 0;
            btn.addActionListener(e -> {
                if (gain != 0) logic.addAffection(gain);
                updateStatus();
                currentStep = target;
                choicePanel.setVisible(false);
                advanceDialogue();
            });
            choicePanel.add(btn);
        }
        choicePanel.revalidate();
        choicePanel.repaint();
    }


    // ─── ปฏิทิน Popup ───────────────────────────────────────────────────────
    private void showCalendarPopup() {
        JDialog dialog = new JDialog(frame, "ปฏิทิน", true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        // panel หลัก — สีครีมอบอุ่น
        JPanel cal = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // พื้นหลัง + กรอบ
                g2.setColor(new Color(240, 220, 170));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(140, 90, 40));
                g2.setStroke(new BasicStroke(4));
                g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 20, 20);
            }
        };
        int calW = 480, calH = 400;
        cal.setPreferredSize(new Dimension(calW, calH));
        cal.setOpaque(false);

        // หัวเดือน
        String monthYearText = gameDate.getMonthName() + "  ปีที่ " + gameDate.getYear();
        JLabel monthLabel = new JLabel(monthYearText, SwingConstants.CENTER);
        monthLabel.setFont(new Font("Tahoma", Font.BOLD, 22));
        monthLabel.setForeground(new Color(100, 50, 10));
        monthLabel.setBounds(0, 10, calW, 36);
        cal.add(monthLabel);

        // หัวคอลัมน์วัน
        String[] dayNames = {"จ", "อ", "พ", "พฤ", "ศ", "ส", "อา"};
        Color[] dayColors = {
            new Color(60,60,60), new Color(60,60,60), new Color(60,60,60),
            new Color(60,60,60), new Color(60,60,60),
            new Color(160,80,0), new Color(180,40,40)
        };
        int cellW = 62, cellH = 54;
        int startX = 20, startY = 52;

        for (int d = 0; d < 7; d++) {
            JLabel h = new JLabel(dayNames[d], SwingConstants.CENTER);
            h.setFont(new Font("Tahoma", Font.BOLD, 15));
            h.setForeground(dayColors[d]);
            h.setBounds(startX + d * cellW, startY, cellW, 24);
            cal.add(h);
        }

        // เส้นคั่น
        int today = gameDate.getDay();

        // วันที่ 1-28 (4 สัปดาห์)
        for (int day = 1; day <= 28; day++) {
            final int dayNum = day;
            int week = (day - 1) / 7;       // แถว 0-3
            int dow  = (day - 1) % 7;       // คอลัมน์ 0-6

            int cx = startX + dow * cellW;
            int cy = startY + 28 + week * cellH;

            // cell panel
            JPanel cell = new JPanel(null) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // วันปัจจุบัน — ไฮไลต์
                    if (dayNum == today) {
                        g2.setColor(new Color(255, 220, 80, 180));
                        g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 8, 8);
                        g2.setColor(new Color(200, 130, 0));
                        g2.setStroke(new BasicStroke(2));
                        g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 8, 8);
                    }
                    // วันผ่านแล้ว — มืดลง
                    else if (dayNum < today) {
                        g2.setColor(new Color(0, 0, 0, 30));
                        g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 8, 8);
                    }
                }
            };
            cell.setOpaque(false);
            cell.setBounds(cx, cy, cellW, cellH);

            // ตัวเลขวัน
            JLabel numLbl = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            numLbl.setFont(new Font("Tahoma", day == today ? Font.BOLD : Font.PLAIN, 15));
            // สี: เสาร์=น้ำตาล อาทิตย์=แดง อื่น=ดำ; ถ้าผ่านแล้วจาง
            Color numColor;
            if (day < today) numColor = new Color(160, 140, 110);
            else if (dow == 5) numColor = new Color(160, 80, 0);
            else if (dow == 6) numColor = new Color(180, 40, 40);
            else               numColor = new Color(60, 40, 10);
            numLbl.setForeground(numColor);
            numLbl.setBounds(0, 2, cellW, 20);
            cell.add(numLbl);

            cal.add(cell);
        }

        // ปุ่มปิด
        JButton closeBtn = new JButton("✕  ปิด");
        closeBtn.setFont(new Font("Tahoma", Font.BOLD, 16));
        closeBtn.setBackground(new Color(180, 120, 50));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBorder(BorderFactory.createLineBorder(new Color(120, 70, 20), 2, true));
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setBounds(calW/2 - 70, calH - 52, 140, 38);
        closeBtn.addActionListener(e -> dialog.dispose());
        cal.add(closeBtn);

        dialog.add(cal);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    public void show() { frame.setVisible(true); }
}