import javax.swing.*;
import java.awt.*;

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

            // ── 1. เตรียมระบบหน้าจอ (CardLayout) ──────────────────────────────────
            CardLayout cardLayout    = new CardLayout();
            JPanel     mainContainer = new JPanel(cardLayout);
            mainContainer.setPreferredSize(new Dimension(W, H));

            // ── 2. สร้าง State หลักของเกม ───────────────────────────────────────
            GameLogic logic    = new GameLogic();
            GameDate  gameDate = new GameDate();

            // ── 3. สร้างหน้าจอต่างๆ ─────────────────────────────────────────────
            
            // หน้าเมนูหลัก
            MainMenu mainMenu = new MainMenu(cardLayout, mainContainer);
            
            // หน้าเล่นเกม (สร้างไว้ก่อนแต่ยังไม่มีข้อมูลตัวละคร)
            GameplayPanel gameplayPanel = new GameplayPanel(cardLayout, mainContainer, logic, gameDate);
            
            // หน้าตั้งค่าเกมใหม่ (จุดเชื่อมต่อสำคัญ)
            setupgame setupGamePanel = new setupgame(cardLayout, mainContainer, (playerName, chosenGirl) -> {
                // เมธอดนี้จะทำงานเมื่อกดปุ่ม "เริ่มการเดินทาง" ในหน้า setupgame
                
                // อัปเดตข้อมูลลงใน GameLogic
                logic.setPlayerName(playerName);
                logic.setChosenGirl(chosenGirl.id);
                logic.startNewGame(); // รีเซ็ตค่าพลังต่างๆ
                
                // ส่งข้อมูลไปอัปเดต UI ในหน้า GameplayPanel
                gameplayPanel.onGameStart(playerName, chosenGirl);
                
                // สลับหน้าจอไปที่หน้าเล่นเกมจริง
                cardLayout.show(mainContainer, "GAMEPLAY");
                
                System.out.println("[RunGame] ข้อมูลถูกส่งไปยัง Gameplay เรียบร้อย: " + playerName);
            });

            // หน้า Lobby (ถ้ามี)
            MultiplayerLobby lobby = new MultiplayerLobby(cardLayout, mainContainer);

            // ── 4. เพิ่มหน้าจอทั้งหมดลงใน Container ──────────────────────────────
            mainContainer.add(mainMenu,      "MENU");
            mainContainer.add(setupGamePanel, "NEW_GAME");
            mainContainer.add(gameplayPanel, "GAMEPLAY");
            mainContainer.add(lobby,         "LOBBY");

            // เริ่มต้นที่หน้าเมนู
            cardLayout.show(mainContainer, "MENU");

            frame.add(mainContainer);
            frame.pack();
            frame.setVisible(true);
            System.out.println("[RunGame] ระบบรวมไฟล์เสร็จสมบูรณ์ ♡");
        });
    }

    private static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        
        Font thaiFont = new Font("Tahoma", Font.PLAIN, 15);
        UIManager.put("Label.font", thaiFont);
        UIManager.put("Button.font", thaiFont);
        UIManager.put("TextField.font", thaiFont);
        UIManager.put("Panel.background", new Color(0xF7D6E0));
    }
}