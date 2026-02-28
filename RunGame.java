import java.awt.*;
import javax.swing.*;

public class RunGame {

    public static final int W = 1200;
    public static final int H = 800;

    public static void main(String[] args) {
        setupLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("First Love ♡");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(W, H);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);

            try {
                ImageIcon icon = new ImageIcon("res/icon.png");
                frame.setIconImage(icon.getImage());
            } catch (Exception ignored) {}

            CardLayout cardLayout    = new CardLayout();
            JPanel     mainContainer = new JPanel(cardLayout);
            mainContainer.setPreferredSize(new Dimension(W, H));

            GameLogic logic    = new GameLogic();
            GameDate  gameDate = new GameDate();

            MainMenu menuPanel = new MainMenu(cardLayout, mainContainer);

            JPanel gameplayPlaceholder = buildGameplayPlaceholder(cardLayout, mainContainer, logic, gameDate);

            MultiplayerLobby lobbyPanel = new MultiplayerLobby(cardLayout, mainContainer);

            SettingPanel settingPanel = new SettingPanel(cardLayout, mainContainer);
            settingPanel.setGameFrame(frame);
            settingPanel.setSettingsListener(new SettingPanel.SettingsListener() {
                @Override
                public void onDisplayModeChanged(SettingPanel.DisplayMode mode, JFrame f) {
                    applyDisplayMode(mode, f);
                }
                @Override
                public void onVolumeChanged(int volume) {
                    // TODO: connect to AudioManager ในอนาคต
                    System.out.println("[RunGame] Volume: " + volume + "%");
                }
            });

            mainContainer.add(menuPanel,             "MENU");
            mainContainer.add(gameplayPlaceholder,   "GAMEPLAY");
            mainContainer.add(lobbyPanel,            "LOBBY");
            mainContainer.add(settingPanel,          "SETTINGS");

            cardLayout.show(mainContainer, "MENU");

            if (SaveManager.hasSave()) {
                System.out.println("[RunGame] พบไฟล์ save — สามารถ Load ได้จากเมนู");
            }

            frame.add(mainContainer);
            frame.pack();
            frame.setVisible(true);

            System.out.println("[RunGame] เกมเริ่มต้นแล้ว ♡");
        });
    }

    private static JPanel buildGameplayPlaceholder(CardLayout cl, JPanel container,
                                                    GameLogic logic, GameDate gameDate) {
        JPanel panel = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(0xF7D6E0),
                    0, H, new Color(0xD9AED0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, W, H);
            }
        };
        panel.setOpaque(false);

        JLabel lbl = new JLabel(
            "<html><div style='text-align:center;color:#9060A0'>" +
            "<span style='font-size:48px'>🌸</span><br><br>" +
            "<span style='font-size:22px;font-weight:bold'>Gameplay Area</span><br><br>" +
            "<span style='font-size:15px;color:#B080C0'>เชื่อม GameUI.java ที่นี่<br>" +
            "หรือ new GameUI(logic, gameDate) แล้ว add ลงมา</span>" +
            "</div></html>",
            SwingConstants.CENTER
        );
        lbl.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lbl.setBounds(0, 200, W, 300);
        panel.add(lbl);

        JButton backBtn = new JButton("← กลับเมนู");
        backBtn.setFont(new Font("Tahoma", Font.BOLD, 16));
        backBtn.setBackground(new Color(0xE8759A));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBorder(BorderFactory.createLineBorder(new Color(0xFFDDEE), 2, true));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setBounds(50, 720, 150, 44);
        backBtn.addActionListener(e -> cl.show(container, "MENU"));
        panel.add(backBtn);

        return panel;
    }


    public static void applyDisplayMode(SettingPanel.DisplayMode mode, JFrame frame) {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Dimension screen  = Toolkit.getDefaultToolkit().getScreenSize();

        // ออกจาก fullscreen ก่อนเสมอ
        if (gd.getFullScreenWindow() != null) {
            gd.setFullScreenWindow(null);
        }

        frame.setVisible(false);
        frame.dispose();

        switch (mode) {
            case WINDOWED:
                frame.setUndecorated(false);
                frame.setResizable(true);
                frame.setSize(W, H);
                frame.setLocationRelativeTo(null);
                frame.setMaximumSize(screen);
                break;

            case BORDERLESS:
                frame.setUndecorated(true);
                frame.setResizable(true);
                frame.setSize(W, H);
                frame.setLocationRelativeTo(null);
                frame.setMaximumSize(screen);
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
        System.out.println("[RunGame] Display mode: " + mode);
    }

    private static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException ignored) {}

        Font thaiFont = new Font("Tahoma", Font.PLAIN, 15);
        UIManager.put("OptionPane.messageFont",   new Font("Tahoma", Font.PLAIN, 16));
        UIManager.put("OptionPane.buttonFont",    new Font("Tahoma", Font.PLAIN, 14));
        UIManager.put("Button.font",              thaiFont);
        UIManager.put("Label.font",               thaiFont);
        UIManager.put("TextField.font",           thaiFont);
        UIManager.put("ComboBox.font",            thaiFont);
        UIManager.put("List.font",                thaiFont);
        UIManager.put("Panel.background",         new Color(0xF7D6E0));
        UIManager.put("OptionPane.background",    new Color(0xFFF0F5));
        UIManager.put("OptionPane.messageForeground", new Color(0x5A3060));
    }
}