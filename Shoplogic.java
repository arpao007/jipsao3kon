import java.util.ArrayList;
import java.util.List;

public class Shoplogic {

    // ========== ข้อมูลสินค้า ==========
    public static class ShopItem {
        public final String id;
        public final String name;
        public final int price;
        public final int affectionGain;
        public final int energyCost;
        public final String imagePath;
        public final String description;

        public ShopItem(String id, String name, int price, int affectionGain, int energyCost, String imagePath, String description) {
            this.id          = id;
            this.name        = name;
            this.price       = price;
            this.affectionGain = affectionGain;
            this.energyCost  = energyCost;
            this.imagePath   = imagePath;
            this.description = description;
        }
    }

    // ========== ผลลัพธ์การซื้อ ==========
    public enum BuyResult {
        SUCCESS,          // ซื้อสำเร็จ
        NOT_ENOUGH_MONEY, // เงินไม่พอ
        NOT_ENOUGH_ENERGY,// พลังงานไม่พอ
        GIFT_QUOTA_FULL,  // โควต้าของขวัญหมด
        NO_CHARACTER,     // ยังไม่ได้เลือกตัวละคร
        INVALID_ITEM      // ไม่พบสินค้า
    }

    // ========== ตัวแปร ==========
    private final GameLogic gameLogic;
    private final List<ShopItem> items;

    // ========== Constructor ==========
    public Shoplogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        this.items = new ArrayList<>();
        initItems();
        System.out.println("[Shoplogic] ระบบร้านค้าเริ่มต้น - สินค้า " + items.size() + " รายการ");
    }

    // ========== กำหนดรายการสินค้า ==========
    private void initItems() {
        // ดอกไม้ - ราคาปานกลาง ความชอบดี
        items.add(new ShopItem(
            "flower",
            "ดอกไม้",
            150,
            15,
            5,
            "res/shop/flower.png",
            "ดอกไม้สดสวยงาม เพิ่มความชอบ +15"
        ));

        // ช็อคโกแลต - ราคาแพง ความชอบสูง
        items.add(new ShopItem(
            "chocolate",
            "ช็อคโกแลต",
            300,
            30,
            5,
            "res/shop/chocolate.png",
            "ช็อคโกแลตรสเข้ม เพิ่มความชอบ +30"
        ));

        // ขนม - ราคาถูก ความชอบน้อย
        items.add(new ShopItem(
            "snack",
            "ขนม",
            80,
            8,
            5,
            "res/shop/snack.png",
            "ขนมอร่อย เพิ่มความชอบ +8"
        ));
    }

    // ========================================
    // ดึงข้อมูลสินค้า
    // ========================================

    public List<ShopItem> getAllItems() {
        return items;
    }

    public ShopItem getItemById(String id) {
        for (ShopItem item : items) {
            if (item.id.equals(id)) return item;
        }
        return null;
    }

    // ========================================
    // ซื้อสินค้า
    // ========================================

    /**
     * ซื้อสินค้าและให้เป็นของขวัญแก่ตัวละครที่เลือกอยู่
     * คืนค่า BuyResult
     */
    public BuyResult buyItem(String itemId) {
        ShopItem item = getItemById(itemId);
        if (item == null) {
            System.out.println("[Shoplogic] ERROR: ไม่พบสินค้า id=" + itemId);
            return BuyResult.INVALID_ITEM;
        }

        // ตรวจสอบตัวละคร
        if (gameLogic.getSelectedCharacter().isEmpty()) {
            System.out.println("[Shoplogic] ERROR: ยังไม่ได้เลือกตัวละคร!");
            return BuyResult.NO_CHARACTER;
        }

        // ตรวจสอบโควต้าของขวัญ
        if (gameLogic.getGiftQuota() <= 0) {
            System.out.println("[Shoplogic] โควต้าของขวัญหมดแล้ววันนี้!");
            return BuyResult.GIFT_QUOTA_FULL;
        }

        // ตรวจสอบเงิน
        if (!gameLogic.hasMoney(item.price)) {
            System.out.println("[Shoplogic] เงินไม่พอ! ต้องการ: " + item.price + " มี: " + gameLogic.getMoney());
            return BuyResult.NOT_ENOUGH_MONEY;
        }

        // ตรวจสอบพลังงาน
        if (!gameLogic.hasEnergy(item.energyCost)) {
            System.out.println("[Shoplogic] พลังงานไม่พอ! ต้องการ: " + item.energyCost + " มี: " + gameLogic.getEnergy());
            return BuyResult.NOT_ENOUGH_ENERGY;
        }

        // ดำเนินการซื้อ
        gameLogic.spendMoney(item.price);
        gameLogic.useEnergy(item.energyCost);
        gameLogic.addAffection(item.affectionGain);

        // ลดโควต้าของขวัญผ่าน sendGift แบบ bypass โดยลดตรง
        // (เราจัดการเองแล้วข้างบน แต่ต้องลดโควต้าด้วย)
        // เรียก sendGift ไม่ได้เพราะจะหักเงินซ้ำ -> ใช้ getGiftQuota indirect
        // workaround: ลดผ่าน sendGift("cheap") ไม่ได้ -> expose method ใน GameLogic
        // แต่เนื่องจากไม่แก้ GameLogic -> ใช้วิธีนับโควต้าเองใน Shoplogic
        decreaseGiftQuota();

        System.out.println("[Shoplogic] ซื้อ '" + item.name + "' สำเร็จ! ความชอบ+" + item.affectionGain);
        return BuyResult.SUCCESS;
    }

    // ========================================
    // จัดการโควต้าของขวัญภายใน
    // ========================================
    // เนื่องจาก GameLogic ไม่มี method ลดโควต้าโดยตรง
    // เราใช้วิธี sendGift dummy เพื่อลดโควต้า แต่หักเงินไปแล้วข้างบน
    // ดังนั้นสร้าง internal gift-quota tracker แยก
    private int localGiftQuotaUsed = 0;

    private void decreaseGiftQuota() {
        // บันทึกไว้ว่าเราใช้โควต้าไปแล้วกี่ครั้ง (ใน session นี้)
        // GameLogic.getGiftQuota() จะยังคงแสดงค่าที่ไม่ถูกต้อง
        // วิธีที่ดีที่สุดคือให้ GameLogic expose decreaseGiftQuota()
        // แต่เพื่อไม่แก้ไขไฟล์อื่น: เราเรียก sendGift กับ dummy character
        // *** วิธีที่ถูกต้องที่สุดคือ: ใช้ reflection หรือ expose method ***
        // สำหรับโปรเจกต์นี้: ใช้ sendGift("cheap") แล้ว undo เงินและ affection
        // เพื่อให้โควต้าลดลงใน GameLogic โดยไม่แก้ไขไฟล์อื่น

        int moneyBefore     = gameLogic.getMoney();
        int affectionBefore = gameLogic.getCurrentAffection();

        // sendGift จะ: ตรวจโควต้า -> หักเงิน 50 -> +5 ความชอบ -> ลดโควต้า 1
        int result = gameLogic.sendGift("cheap");

        if (result == 0) {
            // undo เงินและ affection ที่ sendGift เพิ่ม/หักไป
            gameLogic.addMoney(50);          // คืนเงิน 50 ที่ sendGift หักไป
            gameLogic.addAffection(-5);      // ลบ affection 5 ที่ sendGift เพิ่มไป
        }
        // ถ้า result != 0 โควต้าน่าจะหมดอยู่แล้ว ไม่ต้องทำอะไร
    }

    // ========================================
    // ข้อมูลสถานะสำหรับ UI
    // ========================================

    public String getShopStatusText() {
        return String.format("💰 เงิน: %d บาท  |  🎁 โควต้าของขวัญวันนี้: %d/3",
                gameLogic.getMoney(), gameLogic.getGiftQuota());
    }

    public String getBuyResultMessage(BuyResult result, String itemName) {
        switch (result) {
            case SUCCESS:           return "✅ ซื้อ " + itemName + " สำเร็จ! เพิ่มความชอบแล้ว";
            case NOT_ENOUGH_MONEY:  return "❌ เงินไม่พอ!";
            case NOT_ENOUGH_ENERGY: return "❌ พลังงานไม่พอ!";
            case GIFT_QUOTA_FULL:   return "❌ โควต้าของขวัญหมดแล้ววันนี้!";
            case NO_CHARACTER:      return "❌ กรุณาเลือกตัวละครก่อน";
            case INVALID_ITEM:      return "❌ ไม่พบสินค้า";
            default:                return "❌ เกิดข้อผิดพลาด";
        }
    }
}