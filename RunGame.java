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

            // ── 1. เตรียมระบบหน้าจอ (CardLayout) ──────────────────────────────────
            CardLayout cardLayout    = new CardLayout();
            JPanel     mainContainer = new JPanel(cardLayout);

            // ── 2. สร้าง State หลักของเกม ───────────────────────────────────────
            GameLogic logic    = new GameLogic();
            GameDate  gameDate = new GameDate();

            // ── 3. สร้างหน้าจอต่างๆ ─────────────────────────────────────────────
            
            // หน้าเมนูหลัก
            MainMenu mainMenu = new MainMenu(cardLayout, mainContainer);
            
            // หน้าเล่นเกม
            GameplayPanel gameplayPanel = new GameplayPanel(cardLayout, mainContainer, logic, gameDate);
            
            // หน้าตั้งค่าเกมใหม่
            setupgame setupGamePanel = new setupgame(cardLayout, mainContainer, (playerName, chosenGirl) -> {
                // แก้ไขจุด Error: ส่ง parameter ให้ครบตามที่ GameLogic.java ต้องการ
                logic.setPlayerName(playerName);
                logic.setChosenGirl(chosenGirl.id);
                logic.startNewGame(playerName, chosenGirl.id); 
                
                // ส่งข้อมูลไปอัปเดต UI ในหน้า Gameplay
                gameplayPanel.onGameStart(playerName, chosenGirl);
                
                // สลับหน้าจอไปที่หน้าเล่นเกม
                cardLayout.show(mainContainer, "GAMEPLAY");
            });

            // หน้า Lobby (ถ้ามีไฟล์ MultiplayerLobby.java อยู่ในโฟลเดอร์เดียวกัน)
            try {
                MultiplayerLobby lobby = new MultiplayerLobby(cardLayout, mainContainer);
                mainContainer.add(lobby, "LOBBY");
            } catch (Exception e) {
                System.out.println("ระบบ Multiplayer ยังไม่พร้อมใช้งาน");
            }

            // ── 4. เพิ่มหน้าจอลงใน Container ──────────────────────────────
            mainContainer.add(mainMenu,       "MENU");
            mainContainer.add(setupGamePanel, "NEW_GAME");
            mainContainer.add(gameplayPanel,  "GAMEPLAY");

            cardLayout.show(mainContainer, "MENU");

            frame.add(mainContainer);
            frame.pack();
            frame.setVisible(true);
        });
    }

    private static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        Font thaiFont = new Font("Tahoma", Font.PLAIN, 15);
        UIManager.put("Label.font", thaiFont);
        UIManager.put("Button.font", thaiFont);
        UIManager.put("Panel.background", new Color(0xF7D6E0));
    }
}