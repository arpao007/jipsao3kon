import java.awt.*;
import java.awt.event.*;
import javax.swing.*;A

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

            GameLogic logic = new GameLogic();

            // ── Panels ──
            MainMenu         menuPanel    = new MainMenu(cardLayout, mainContainer, frame);
            MultiplayerLobby lobbyPanel   = new MultiplayerLobby(cardLayout, mainContainer);
            SettingPanel     settingPanel = new SettingPanel(cardLayout, mainContainer);
            GameStoryUI      storyUI      = new GameStoryUI(cardLayout, mainContainer, logic);
            JPanel           nameScreen   = buildNameScreen(cardLayout, mainContainer, storyUI);

            // ✅ เพิ่ม 2 panel ใหม่
            HomePanel        homePanel     = new HomePanel(cardLayout, mainContainer, logic);
            CalendarPanel    calendarPanel = new CalendarPanel(cardLayout, mainContainer, logic);

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
                @Override public void onMultiplayer() { cardLayout.show(mainContainer, "LOBBY"); }
                @Override public void onSettings()    { cardLayout.show(mainContainer, "SETTINGS"); }
                @Override public void onExit()        { System.exit(0); }
            });

            // ── Add panels ──
            mainContainer.add(menuPanel,     "MENU");
            mainContainer.add(nameScreen,    "NAME_INPUT");
            mainContainer.add(storyUI,       "STORY");

            // ✅ เพิ่มจริง
            mainContainer.add(homePanel,     "HOME");
            mainContainer.add(calendarPanel, "CALENDAR");

            mainContainer.add(lobbyPanel,    "LOBBY");
            mainContainer.add(settingPanel,  "SETTINGS");

            cardLayout.show(mainContainer, "MENU");

            frame.add(mainContainer);
            frame.setSize(W, H);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            System.out.println("[RunGame] เกมเริ่มต้นแล้ว ♡");
        });
    }

    // ===== หน้าตั้งชื่อ =====
    private static JPanel buildNameScreen(CardLayout cl, JPanel container, GameStoryUI storyUI) {
        JPanel panel = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(0xF7D6E0), 0, getHeight(), new Color(0xD9AED0)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);

        panel.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { layoutNameCard(panel, cl, container, storyUI); }
            @Override public void componentShown(ComponentEvent e)   { layoutNameCard(panel, cl, container, storyUI); }
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

        JPanel card = new JPanel(null);
        card.setBackground(new Color(255, 240, 248, 225));
        card.setBounds(cardX, cardY, cardW, cardH);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xE8759A), 2, true));
        panel.add(card);

        JLabel title = new JLabel("ตั้งชื่อตัวละครของคุณ", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 22));
        title.setForeground(new Color(0xA076BB));
        title.setBounds(0, 30, cardW, 32);
        card.add(title);

        JTextField nameField = new JTextField("ผู้เล่น", SwingConstants.CENTER);
        nameField.setFont(new Font("Tahoma", Font.PLAIN, 19));
        nameField.setBounds(cardW/2 - 160, 90, 320, 46);
        card.add(nameField);

        JButton start = new JButton("เริ่มเรื่องราว ♡");
        start.setFont(new Font("Tahoma", Font.BOLD, 18));
        start.setBounds(cardW/2 - 150, 165, 300, 52);
        start.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) name = "ผู้เล่น";
            if (name.length() > 10) name = name.substring(0, 10);
            storyUI.startGame(name);
            cl.show(container, "STORY");
        });
        card.add(start);

        JLabel back = new JLabel("← ย้อนกลับเมนู", SwingConstants.CENTER);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.setBounds(0, 240, cardW, 26);
        back.addMouseListener(new MouseAdapter() {
            @Override public void mouseReleased(MouseEvent e) { cl.show(container, "MENU"); }
        });
        card.add(back);

        panel.revalidate();
        panel.repaint();
    }

    // ===== Display Mode =====
    public static void applyDisplayMode(SettingPanel.DisplayMode mode, JFrame frame) {
        applyDisplayMode(mode, frame, null);
    }

public static void applyDisplayMode(SettingPanel.DisplayMode mode, JFrame frame,
                                        SettingPanel.Resolution res) {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Dimension screen  = Toolkit.getDefaultToolkit().getScreenSize();

        if (gd.getFullScreenWindow() != null) gd.setFullScreenWindow(null);
        
        frame.setVisible(false);
        frame.dispose(); // ต้องเรียก dispose ก่อนเปลี่ยน setUndecorated

        switch (mode) {
            case WINDOWED:
                frame.setUndecorated(false);
                frame.setResizable(true);
                if (res != null) frame.setSize(res.w, res.h);
                else             frame.setSize(W, H);
                frame.setLocationRelativeTo(null);
                break; // ต้องมี break เพื่อไม่ให้ไหลไป case ถัดไป

            case BORDERLESS:
                frame.setUndecorated(true);
                frame.setResizable(true);
                if (res != null) frame.setSize(res.w, res.h);
                else             frame.setSize(screen);
                frame.setLocationRelativeTo(null);
                break;

            case FULLSCREEN:
                frame.setUndecorated(true);
                frame.setResizable(false);
                if (gd.isFullScreenSupported()) {
                    gd.setFullScreenWindow(frame);
                } else {
                    frame.setSize(screen);
                    frame.setLocationRelativeTo(null);
                }
                break;
        }
        frame.setVisible(true);
    }
    
    // ===== Look & Feel =====
    private static void setupLookAndFeel() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (ReflectiveOperationException | UnsupportedLookAndFeelException ignored) {}

        Font thaiFont = new Font("Tahoma", Font.PLAIN, 15);
        UIManager.put("OptionPane.messageFont",       new Font("Tahoma", Font.PLAIN, 16));
        UIManager.put("OptionPane.buttonFont",        new Font("Tahoma", Font.PLAIN, 14));
        UIManager.put("Button.font",                  thaiFont);
        UIManager.put("Label.font",                   thaiFont);
        UIManager.put("TextField.font",               thaiFont);
        UIManager.put("Panel.background",             new Color(0xF7D6E0));
        UIManager.put("OptionPane.background",        new Color(0xFFF0F5));
        UIManager.put("OptionPane.messageForeground", new Color(0x5A3060));
    }
}