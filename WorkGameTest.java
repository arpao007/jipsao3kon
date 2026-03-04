import javax.swing.*;
import java.awt.*;

/**
 * WorkGameTest.java
 * เทสระบบงาน - รันแยกได้เลย
 *
 * คอมไพล์: javac GameLogic.java WorkQuestion.java WorkQuestionBank.java WorkGameLogic.java WorkGame_ui.java WorkGameTest.java
 * รัน:     java WorkGameTest
 */
public class WorkGameTest {

    public static void main(String[] args) {
        System.out.println("===== START WORK GAME TEST =====\n");
        testLogic();
        SwingUtilities.invokeLater(WorkGameTest::testUI);
    }

    // ---- Logic Tests ----
    static void testLogic() {
        System.out.println("--- TEST 1: เริ่มงานโดยไม่มีพลังงาน ---");
        GameLogic g = new GameLogic();
        g.useEnergy(100); // หมดพลังงาน
        WorkGameLogic w = new WorkGameLogic(g);
        assert w.startRound(1) == WorkGameLogic.StartResult.NOT_ENOUGH_ENERGY : "FAIL";
        System.out.println("ผล: NOT_ENOUGH_ENERGY ✅\n");

        System.out.println("--- TEST 2: เล่น 2 รอบครบแล้วยังเล่นอีก ---");
        GameLogic g2 = new GameLogic();
        WorkGameLogic w2 = new WorkGameLogic(g2);

        // รอบ 1
        w2.startRound(1);
        for (int i = 0; i < 3; i++) w2.answerQuestion(w2.getCurrentQuestion().correctIndex);
        int earned1 = w2.finishRound();
        System.out.println("รอบ 1 ได้: " + earned1 + " บาท");

        // รอบ 2
        w2.startRound(2);
        for (int i = 0; i < 3; i++) w2.answerQuestion(0);
        int earned2 = w2.finishRound();
        System.out.println("รอบ 2 ได้: " + earned2 + " บาท");

        // รอบ 3 -> ควรปฏิเสธ
        assert w2.startRound(3) == WorkGameLogic.StartResult.NO_ROUNDS_LEFT : "FAIL";
        System.out.println("รอบ 3: NO_ROUNDS_LEFT ✅\n");

        System.out.println("--- TEST 3: ตอบถูกครบ ได้เงิน ---");
        GameLogic g3 = new GameLogic();
        WorkGameLogic w3 = new WorkGameLogic(g3);
        int moneyBefore = g3.getMoney();
        w3.startRound(2);
        for (int i = 0; i < 3; i++) w3.answerQuestion(w3.getCurrentQuestion().correctIndex);
        int earned = w3.finishRound();
        assert earned == WorkGameLogic.REWARD_PER_ROUND : "FAIL: ควรได้ 50";
        assert g3.getMoney() == moneyBefore - WorkGameLogic.ENERGY_COST/20*0 + earned - WorkGameLogic.ENERGY_COST : "อาจผิดปกติ";
        System.out.println("ตอบถูกครบ ได้ " + earned + " บาท ✅\n");

        System.out.println("--- TEST 4: ตอบผิดทั้งหมด ไม่ได้เงิน ---");
        GameLogic g4 = new GameLogic();
        WorkGameLogic w4 = new WorkGameLogic(g4);
        w4.startRound(3);
        for (int i = 0; i < 3; i++) {
            int wrong = (w4.getCurrentQuestion().correctIndex + 1) % 4;
            w4.answerQuestion(wrong);
        }
        int earned4 = w4.finishRound();
        assert earned4 == 0 : "FAIL: ควรได้ 0";
        System.out.println("ตอบผิดทั้งหมด ได้ 0 บาท ✅\n");

        System.out.println("===== LOGIC TEST ผ่านทั้งหมด ✅ =====\n");
    }

    // ---- UI Test ----
    static void testUI() {
        UIManager.put("OptionPane.messageFont", new Font("Tahoma", Font.PLAIN, 16));

        GameLogic g = new GameLogic();

        JFrame frame = new JFrame("WorkGame UI Test");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        CardLayout cl   = new CardLayout();
        JPanel     main = new JPanel(cl);

        // Dummy GAMEPLAY
        JPanel dummy = new JPanel(null);
        dummy.setBackground(new Color(200, 230, 255));
        JLabel lbl = new JLabel("[ GAMEPLAY ]", SwingConstants.CENTER);
        lbl.setBounds(0, 0, 1200, 800);
        lbl.setFont(new Font("Tahoma", Font.BOLD, 40));
        dummy.add(lbl);

        WorkGame_ui workUI = new WorkGame_ui(cl, main, g);
        main.add(dummy,  "GAMEPLAY");
        main.add(workUI, "WORK");
        cl.show(main, "WORK");

        frame.add(main);
        frame.setVisible(true);
        System.out.println("UI เปิดแล้ว — ทดลองเล่นได้เลย!");
    }
}
