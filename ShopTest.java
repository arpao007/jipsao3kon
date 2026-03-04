import javax.swing.*;
import java.awt.*;

/**
 * ShopTest.java - ไฟล์เทสระบบร้านค้า
 * รันไฟล์นี้แยกได้เลย ไม่ต้องรัน Main.java
 *
 * คอมไพล์: javac GameLogic.java Shoplogic.java Shop_ui.java ShopTest.java
 * รัน:     java ShopTest
 */
public class ShopTest {

    public static void main(String[] args) {
        System.out.println("========== START SHOP TEST ==========\n");

        // ---- เทส Logic ----
        testShopLogic();

        // ---- เทส UI ----
        SwingUtilities.invokeLater(() -> testShopUI());
    }

    // =============================================
    // เทส Shoplogic (ไม่มี UI)
    // =============================================
    static void testShopLogic() {
        System.out.println("--- TEST 1: ซื้อโดยไม่ได้เลือกตัวละคร ---");
        GameLogic logic = new GameLogic();
        Shoplogic shop  = new Shoplogic(logic);

        Shoplogic.BuyResult r = shop.buyItem("flower");
        assert r == Shoplogic.BuyResult.NO_CHARACTER : "FAIL: ควรได้ NO_CHARACTER";
        System.out.println("ผล: " + r + " ✅\n");

        // ---
        System.out.println("--- TEST 2: ซื้อสินค้าปกติ ---");
        logic.setSelectedCharacter("มีน");
        int moneyBefore     = logic.getMoney();      // 500
        int affBefore       = logic.getCurrentAffection(); // 0
        int quotaBefore     = logic.getGiftQuota();  // 3

        r = shop.buyItem("flower"); // ราคา 150, +15 affection
        assert r == Shoplogic.BuyResult.SUCCESS : "FAIL: ควรได้ SUCCESS";
        assert logic.getMoney()             == moneyBefore - 150 : "FAIL: เงินไม่ลด";
        assert logic.getCurrentAffection()  == affBefore + 15    : "FAIL: affection ไม่เพิ่ม";
        assert logic.getGiftQuota()         == quotaBefore - 1   : "FAIL: โควต้าไม่ลด";
        System.out.println("ผล: " + r);
        System.out.println("เงิน: " + moneyBefore + " -> " + logic.getMoney() + " (ลด 150) ✅");
        System.out.println("ความชอบ: " + affBefore + " -> " + logic.getCurrentAffection() + " (+15) ✅");
        System.out.println("โควต้า: " + quotaBefore + " -> " + logic.getGiftQuota() + " ✅\n");

        // ---
        System.out.println("--- TEST 3: เงินไม่พอ ---");
        GameLogic logic2 = new GameLogic();
        logic2.setSelectedCharacter("พลอย");
        // ใช้เงินให้เหลือน้อย
        logic2.spendMoney(480); // เหลือ 20
        Shoplogic shop2 = new Shoplogic(logic2);
        r = shop2.buyItem("chocolate"); // ราคา 300
        assert r == Shoplogic.BuyResult.NOT_ENOUGH_MONEY : "FAIL: ควรได้ NOT_ENOUGH_MONEY";
        System.out.println("ผล: " + r + " ✅\n");

        // ---
        System.out.println("--- TEST 4: พลังงานไม่พอ ---");
        GameLogic logic3 = new GameLogic();
        logic3.setSelectedCharacter("มีน");
        logic3.useEnergy(98); // เหลือ 2 (ต้องการ 5)
        Shoplogic shop3 = new Shoplogic(logic3);
        r = shop3.buyItem("snack");
        assert r == Shoplogic.BuyResult.NOT_ENOUGH_ENERGY : "FAIL: ควรได้ NOT_ENOUGH_ENERGY";
        System.out.println("ผล: " + r + " ✅\n");

        // ---
        System.out.println("--- TEST 5: โควต้าของขวัญหมด ---");
        GameLogic logic4 = new GameLogic();
        logic4.setSelectedCharacter("มีน");
        Shoplogic shop4 = new Shoplogic(logic4);
        shop4.buyItem("snack");   // ครั้งที่ 1
        shop4.buyItem("snack");   // ครั้งที่ 2
        shop4.buyItem("snack");   // ครั้งที่ 3
        r = shop4.buyItem("snack"); // ครั้งที่ 4 -> ควรหมดโควต้า
        assert r == Shoplogic.BuyResult.GIFT_QUOTA_FULL : "FAIL: ควรได้ GIFT_QUOTA_FULL";
        System.out.println("ผล: " + r + " ✅\n");

        // ---
        System.out.println("--- TEST 6: สินค้าไม่มีในร้าน ---");
        GameLogic logic5 = new GameLogic();
        logic5.setSelectedCharacter("มีน");
        Shoplogic shop5 = new Shoplogic(logic5);
        r = shop5.buyItem("unknown_item");
        assert r == Shoplogic.BuyResult.INVALID_ITEM : "FAIL: ควรได้ INVALID_ITEM";
        System.out.println("ผล: " + r + " ✅\n");

        System.out.println("========== LOGIC TEST ผ่านทั้งหมด ✅ ==========\n");
    }

    // =============================================
    // เทส Shop UI (เปิดหน้าต่างจริง)
    // =============================================
    static void testShopUI() {
        UIManager.put("OptionPane.messageFont", new Font("Tahoma", Font.PLAIN, 16));
        UIManager.put("Button.font", new Font("Tahoma", Font.PLAIN, 14));

        GameLogic logic = new GameLogic();
        logic.setSelectedCharacter("มีน"); // เลือกตัวละครไว้ก่อน

        JFrame frame = new JFrame("Shop UI Test");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        CardLayout cardLayout    = new CardLayout();
        JPanel     mainContainer = new JPanel(cardLayout);

        // Dummy GAMEPLAY panel
        JPanel gamplayDummy = new JPanel(null);
        gamplayDummy.setBackground(new Color(200, 230, 255));
        JLabel lbl = new JLabel("[ GAMEPLAY PANEL ]", SwingConstants.CENTER);
        lbl.setBounds(0, 0, 1200, 800);
        lbl.setFont(new Font("Tahoma", Font.BOLD, 36));
        gamplayDummy.add(lbl);

        Shop_ui shopUI = new Shop_ui(cardLayout, mainContainer, logic);

        mainContainer.add(gamplayDummy, "GAMEPLAY");
        mainContainer.add(shopUI,       "SHOP");

        cardLayout.show(mainContainer, "SHOP"); // เปิดหน้าร้านตรงๆ

        frame.add(mainContainer);
        frame.setVisible(true);

        System.out.println("--- UI TEST: เปิดหน้าต่าง Shop แล้ว ---");
        System.out.println("ลองกดปุ่ม 'ซื้อ & มอบให้' ดูได้เลยครับ");
        System.out.println("กด '← กลับ' จะไปหน้า GAMEPLAY (dummy)");
    }
}