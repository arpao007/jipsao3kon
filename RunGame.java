import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class RunGame {

    public static final int W = 1200;
    public static final int H = 800;

    public static void main(String[] args) {
        setupLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("First Love ♡");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(true);
            frame.setMinimumSize(new Dimension(800, 540));

            try { frame.setIconImage(new ImageIcon("res/icon.png").getImage()); }
            catch (Exception ignored) {}

            CardLayout cardLayout    = new CardLayout();
            JPanel     mainContainer = new JPanel(cardLayout);
            mainContainer.setPreferredSize(new Dimension(W, H));

            GameLogic logic    = new GameLogic();

            // ── Panels ──
            MainMenu         menuPanel    = new MainMenu(cardLayout, mainContainer, frame);
            MultiplayerLobby lobbyPanel   = new MultiplayerLobby(cardLayout, mainContainer);
            SettingPanel     settingPanel = new SettingPanel(cardLayout, mainContainer);
            GameStoryUI      storyUI      = new GameStoryUI(cardLayout, mainContainer, logic);
            JPanel           nameScreen   = buildNameScreen(cardLayout, mainContainer, storyUI);

            // ── Settings listener ──
            settingPanel.setGameFrame(frame);
            settingPanel.setSettingsListener(new SettingPanel.SettingsListener() {
                @Override public void onDisplayModeChanged(SettingPanel.DisplayMode mode, JFrame f) {
                    applyDisplayMode(mode, f, settingPanel.getCurrentResolution());
                }
                @Override public void onResolutionChanged(SettingPanel.Resolution res, JFrame f) {
                    SettingPanel.DisplayMode mode = settingPanel.getCurrentMode();
                    if (mode != SettingPanel.DisplayMode.FULLSCREEN) {
                        applyDisplayMode(mode, f, res);
                    }
                }
                @Override public void onVolumeChanged(int volume) {
                    System.out.println("[RunGame] Volume: " + volume + "%");
                }
            });

            // ── MainMenu listener ──
            menuPanel.setMenuListener(new MainMenu.MenuListener() {
                @Override public void onNewGame() {
                    // ไปหน้าตั้งชื่อก่อน
                    cardLayout.show(mainContainer, "NAME_INPUT");
                }
                @Override public void onLoadGame() {
                    if (SaveManager.hasSave()) {
                        storyUI.startGame("ผู้เล่น");
                        cardLayout.show(mainContainer, "STORY");
                    } else {
                        JOptionPane.showMessageDialog(frame,
                            "ไม่พบไฟล์ Save ค่ะ", "Load Game",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                @Override public void onMultiplayer() {
                    cardLayout.show(mainContainer, "LOBBY");
                }
                @Override public void onSettings() {
                    cardLayout.show(mainContainer, "SETTINGS");
                }
                @Override public void onExit() {
                    System.exit(0);
                }
            });

            // ── Add panels ──
            mainContainer.add(menuPanel,    "MENU");
            mainContainer.add(nameScreen,   "NAME_INPUT");
            mainContainer.add(storyUI,      "STORY");
            mainContainer.add(lobbyPanel,   "LOBBY");
            mainContainer.add(settingPanel, "SETTINGS");
            cardLayout.show(mainContainer, "MENU");

            frame.add(mainContainer);
            frame.setSize(W, H);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            if (SaveManager.hasSave())
                System.out.println("[RunGame] พบไฟล์ save");
            System.out.println("[RunGame] เกมเริ่มต้นแล้ว ♡");
        });
    }

    // ════════════════════════════════════════════════
    //  หน้าตั้งชื่อพระเอกก่อนเริ่มเกม
    // ════════════════════════════════════════════════
    private static JPanel buildNameScreen(CardLayout cl, JPanel container, GameStoryUI storyUI) {
        JPanel panel = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(0xF7D6E0), 0, getHeight(), new Color(0xD9AED0)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new java.awt.RadialGradientPaint(
                    getWidth() / 2f, getHeight() / 2f, getHeight() / 2f,
                    new float[]{0, 1},
                    new Color[]{new Color(255, 200, 230, 50), new Color(255, 200, 230, 0)}));
                g2.fillOval(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);

        panel.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                layoutNameCard(panel, cl, container, storyUI);
            }
            @Override public void componentShown(ComponentEvent e) {
                layoutNameCard(panel, cl, container, storyUI);
            }
        });
        panel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0
                    && panel.isShowing()) {
                layoutNameCard(panel, cl, container, storyUI);
            }
        });

        return panel;
    }

    private static void layoutNameCard(JPanel panel, CardLayout cl,
                                        JPanel container, GameStoryUI storyUI) {
        int w = panel.getWidth(), h = panel.getHeight();
        if (w <= 0 || h <= 0) {
            SwingUtilities.invokeLater(() -> layoutNameCard(panel, cl, container, storyUI));
            return;
        }
        panel.removeAll();

        int cardW = Math.min(500, (int)(w * 0.5));
        int cardH = 370;
        int cardX = (w - cardW) / 2;
        int cardY = (h - cardH) / 2;

        // ── Card ──
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 240, 248, 225));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 36, 36);
                g2.setStroke(new BasicStroke(2.5f));
                g2.setColor(new Color(0xE8759A));
                g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 34, 34);
                // shimmer
                g2.setPaint(new GradientPaint(0, 0, new Color(255,255,255,60),
                    getWidth(), getHeight(), new Color(255,200,230,0)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 36, 36);
            }
        };
        card.setOpaque(false);
        card.setBounds(cardX, cardY, cardW, cardH);
        panel.add(card);

        // ── Heart icon ──
        JLabel heart = new JLabel("♡", SwingConstants.CENTER);
        heart.setFont(new Font("Tahoma", Font.BOLD, 42));
        heart.setForeground(new Color(0xE8759A));
        heart.setBounds(0, 16, cardW, 52);
        card.add(heart);

        // ── Title ──
        JLabel title = new JLabel("ตั้งชื่อตัวละครของคุณ", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 22));
        title.setForeground(new Color(0xA076BB));
        title.setBounds(0, 70, cardW, 32);
        card.add(title);

        JLabel sub = new JLabel("ชื่อนี้จะปรากฏตลอดเนื้อเรื่องทั้ง 50 บท", SwingConstants.CENTER);
        sub.setFont(new Font("Tahoma", Font.ITALIC, 13));
        sub.setForeground(new Color(0xB090C0));
        sub.setBounds(0, 104, cardW, 20);
        card.add(sub);

        // ── Text field ──
        JTextField nameField = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 248, 253));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                super.paintComponent(g);
            }
        };
        nameField.setFont(new Font("Tahoma", Font.PLAIN, 19));
        nameField.setForeground(new Color(0x5A3060));
        nameField.setCaretColor(new Color(0xE8759A));
        nameField.setOpaque(false);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0A0C8), 2, true),
            BorderFactory.createEmptyBorder(7, 16, 7, 16)
        ));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setText("ผู้เล่น");
        nameField.selectAll();
        int tfW = (int)(cardW * 0.72);
        nameField.setBounds((cardW - tfW) / 2, 140, tfW, 48);
        card.add(nameField);

        JLabel hint = new JLabel("กรอกชื่อ 1-10 ตัวอักษร", SwingConstants.CENTER);
        hint.setFont(new Font("Tahoma", Font.PLAIN, 12));
        hint.setForeground(new Color(0xC0A0D0));
        hint.setBounds(0, 194, cardW, 18);
        card.add(hint);

        // ── Start button ──
        JPanel startBtn = new JPanel(null) {
            boolean hov = false, prs = false;
            {
                setOpaque(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e)  { hov=true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)   { hov=false; prs=false; repaint(); }
                    @Override public void mousePressed(MouseEvent e)  { prs=true;  repaint(); }
                    @Override public void mouseReleased(MouseEvent e) {
                        prs = false; repaint();
                        if (!hov) return;
                        String name = nameField.getText().trim();
                        if (name.isEmpty()) name = "ผู้เล่น";
                        if (name.length() > 10) name = name.substring(0, 10);
                        storyUI.startGame(name);
                        cl.show(container, "STORY");
                    }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int oy = prs ? 3 : 0;
                if (!prs) {
                    g2.setColor(new Color(0,0,0,25));
                    g2.fillRoundRect(4, oy+6, getWidth(), getHeight(), 22, 22);
                }
                Color top = hov ? new Color(0xF5A8C5) : new Color(0xF0C0D8);
                Color bot = hov ? new Color(0xE8759A) : new Color(0xD06088);
                g2.setPaint(new GradientPaint(0, oy, top, 0, oy+getHeight(), bot));
                g2.fillRoundRect(0, oy, getWidth(), getHeight(), 22, 22);
                g2.setColor(new Color(255,255,255,70));
                g2.fillRoundRect(6, oy+5, getWidth()-12, getHeight()/2-4, 14, 14);
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(new Color(255,255,255,150));
                g2.drawRoundRect(1, oy+1, getWidth()-2, getHeight()-2, 21, 21);
                g2.setFont(new Font("Tahoma", Font.BOLD, 18));
                g2.setColor(new Color(0xFFF5FA));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "เริ่มเรื่องราว  ♡";
                g2.drawString(txt,
                    (getWidth()-fm.stringWidth(txt))/2,
                    oy+(getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        int sbW = (int)(cardW * 0.62);
        startBtn.setBounds((cardW - sbW) / 2, 226, sbW, 52);
        card.add(startBtn);

        // Enter key shortcut
        nameField.addActionListener(e -> startBtn.dispatchEvent(
            new MouseEvent(startBtn, MouseEvent.MOUSE_RELEASED, 0,
                MouseEvent.BUTTON1_DOWN_MASK, startBtn.getWidth()/2, startBtn.getHeight()/2, 1, false)));

        // ── Back link ──
        JLabel backLbl = new JLabel("← ย้อนกลับเมนู", SwingConstants.CENTER);
        backLbl.setFont(new Font("Tahoma", Font.PLAIN, 13));
        backLbl.setForeground(new Color(0xB090C0));
        backLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLbl.setBounds(0, 297, cardW, 22);
        backLbl.addMouseListener(new MouseAdapter() {
            @Override public void mouseReleased(MouseEvent e) { cl.show(container, "MENU"); }
            @Override public void mouseEntered(MouseEvent e)  { backLbl.setForeground(new Color(0xE8759A)); }
            @Override public void mouseExited(MouseEvent e)   { backLbl.setForeground(new Color(0xB090C0)); }
        });
        card.add(backLbl);

        panel.revalidate();
        panel.repaint();

        // focus ที่ text field
        SwingUtilities.invokeLater(() -> {
            nameField.requestFocusInWindow();
            nameField.selectAll();
        });
    }

    // ════════════════════════════════════════════════
    //  Display Mode
    // ════════════════════════════════════════════════
    public static void applyDisplayMode(SettingPanel.DisplayMode mode, JFrame frame) {
        applyDisplayMode(mode, frame, null);
    }

    public static void applyDisplayMode(SettingPanel.DisplayMode mode, JFrame frame,
                                         SettingPanel.Resolution res) {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Dimension screen  = Toolkit.getDefaultToolkit().getScreenSize();

        if (gd.getFullScreenWindow() != null) gd.setFullScreenWindow(null);
        frame.setVisible(false);
        frame.dispose();

        switch (mode) {
            case WINDOWED -> {
                frame.setUndecorated(false);
                frame.setResizable(true);
                if (res != null) frame.setSize(res.w, res.h);
                else             frame.setSize(W, H);
                frame.setLocationRelativeTo(null);
            }
            case BORDERLESS -> {
                frame.setUndecorated(true);
                frame.setResizable(true);
                if (res != null) frame.setSize(res.w, res.h);
                else             frame.setSize(screen);
                frame.setLocationRelativeTo(null);
            }
            case FULLSCREEN -> {
                frame.setUndecorated(true);
                frame.setResizable(false);
                if (gd.isFullScreenSupported()) gd.setFullScreenWindow(frame);
                else { frame.setSize(screen); frame.setLocationRelativeTo(null); }
            }
        }
        frame.setVisible(true);
        System.out.println("[RunGame] Display mode: " + mode
            + (res != null ? "  " + res.label : ""));
    }

    // ════════════════════════════════════════════════
    //  Look & Feel
    // ════════════════════════════════════════════════
    private static void setupLookAndFeel() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (ReflectiveOperationException | UnsupportedLookAndFeelException ignored) {}

        Font thaiFont = new Font("Tahoma", Font.PLAIN, 15);
        UIManager.put("OptionPane.messageFont",       new Font("Tahoma", Font.PLAIN, 16));
        UIManager.put("OptionPane.buttonFont",        new Font("Tahoma", Font.PLAIN, 14));
        UIManager.put("Button.font",                  thaiFont);
        UIManager.put("Label.font",                   thaiFont);
        UIManager.put("TextField.font",               thaiFont);
        UIManager.put("ComboBox.font",                thaiFont);
        UIManager.put("List.font",                    thaiFont);
        UIManager.put("Panel.background",             new Color(0xF7D6E0));
        UIManager.put("OptionPane.background",        new Color(0xFFF0F5));
        UIManager.put("OptionPane.messageForeground", new Color(0x5A3060));
    }
}