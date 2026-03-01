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
            GameDate  gameDate = new GameDate();

            MainMenu         menuPanel    = new MainMenu(cardLayout, mainContainer, frame);
            JPanel           gameplay    = buildGameplayPlaceholder(cardLayout, mainContainer, logic, gameDate);
            MultiplayerLobby lobbyPanel  = new MultiplayerLobby(cardLayout, mainContainer);
            SettingPanel     settingPanel = new SettingPanel(cardLayout, mainContainer);

            settingPanel.setGameFrame(frame);
            settingPanel.setSettingsListener(new SettingPanel.SettingsListener() {
                @Override public void onDisplayModeChanged(SettingPanel.DisplayMode mode, JFrame f) {
                    applyDisplayMode(mode, f, settingPanel.getCurrentResolution());
                }
                @Override public void onResolutionChanged(SettingPanel.Resolution res, JFrame f) {
                    // เปลี่ยน resolution เฉพาะ WINDOWED / BORDERLESS
                    SettingPanel.DisplayMode mode = settingPanel.getCurrentMode();
                    if (mode != SettingPanel.DisplayMode.FULLSCREEN) {
                        applyDisplayMode(mode, f, res);
                    }
                }
                @Override public void onVolumeChanged(int volume) {
                    System.out.println("[RunGame] Volume: " + volume + "%");
                }
            });

            mainContainer.add(menuPanel,    "MENU");
            mainContainer.add(gameplay,     "GAMEPLAY");
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

    // ────────────────────────────────────────────────

    private static JPanel buildGameplayPlaceholder(CardLayout cl, JPanel container,
                                                    GameLogic logic, GameDate gameDate) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(0xF7D6E0),
                        0, getHeight(), new Color(0xD9AED0)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);

        JLabel lbl = new JLabel(
            "<html><div style='text-align:center;color:#9060A0'>" +
            "<span style='font-size:48px'>🌸</span><br><br>" +
            "<span style='font-size:22px;font-weight:bold'>Gameplay Area</span><br><br>" +
            "<span style='font-size:15px;color:#B080C0'>เชื่อม GameUI.java ที่นี่</span>" +
            "</div></html>", SwingConstants.CENTER);
        lbl.setFont(new Font("Tahoma", Font.PLAIN, 18));
        panel.add(lbl, BorderLayout.CENTER);

        JButton backBtn = new JButton("← กลับเมนู");
        backBtn.setFont(new Font("Tahoma", Font.BOLD, 16));
        backBtn.setBackground(new Color(0xE8759A));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBorder(BorderFactory.createLineBorder(new Color(0xFFDDEE), 2, true));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> cl.show(container, "MENU"));
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        south.setOpaque(false);
        south.add(backBtn);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    // ────────────────────────────────────────────────

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
            case WINDOWED:
                frame.setUndecorated(false);
                frame.setResizable(true);
                if (res != null) frame.setSize(res.w, res.h);
                else             frame.setSize(W, H);
                frame.setLocationRelativeTo(null);
                break;
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
                if (gd.isFullScreenSupported()) gd.setFullScreenWindow(frame);
                else { frame.setSize(screen); frame.setLocationRelativeTo(null); }
                break;
        }
        frame.setVisible(true);
        System.out.println("[RunGame] Display mode: " + mode
            + (res != null ? "  " + res.label : ""));
    }

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