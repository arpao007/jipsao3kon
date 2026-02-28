import java.time.LocalDate;

public class GameLogic {
    // ========== ข้อมูลตัวละคร ==========
    private String selectedCharacter = "";
    
    // ========== ระบบพลังงาน ==========
    private int energy = 100;
    private final int MAX_ENERGY = 100;
    
    // ========== ระบบเงิน ==========
    private int money = 500; // เริ่มต้น 500 บาท
    
    // ========== ระบบความชอบ (แยกตามตัวละคร) ==========
    private int affectionMean = 0;
    private int affectionPloy = 0;
    private int affectionLilli = 0;
    private final int MAX_AFFECTION = 100;
    
    // ========== ระบบของขวัญ (จำกัดต่อวัน) ==========
    private int giftQuotaToday = 3; // ส่งได้ 3 ครั้ง/วัน
    private LocalDate lastGiftResetDate = LocalDate.now();
    
    // ========== ระบบงาน (จำกัดต่อวัน) ==========
    private int workCountToday = 0;
    private final int MAX_WORK_PER_DAY = 5; // ทำงานได้ 5 ครั้ง/วัน
    private LocalDate lastWorkResetDate = LocalDate.now();
    
    // ========== ราคาของขวัญ ==========
    private final int GIFT_CHEAP_COST = 50;      // ของถูก +5 ความชอบ
    private final int GIFT_NORMAL_COST = 150;    // ของปกติ +15 ความชอบ
    private final int GIFT_EXPENSIVE_COST = 500; // ของแพง +50 ความชอบ
    
    // ========== Constructor ==========
    public GameLogic() {
        System.out.println("[GameLogic] ระบบเริ่มต้น - เงิน: " + money + " พลังงาน: " + energy);
    }
    
    // ========================================
    // ระบบตัวละคร
    // ========================================
    
    public void setSelectedCharacter(String name) {
        this.selectedCharacter = name;
        System.out.println("[GameLogic] เลือกตัวละคร: " + name);
    }
    
    public String getSelectedCharacter() {
        return selectedCharacter;
    }
    
    // ========================================
    // ระบบพลังงาน
    // ========================================
    
    public int getEnergy() {
        return energy;
    }
    
    public int getMaxEnergy() {
        return MAX_ENERGY;
    }
    
    public boolean hasEnergy(int amount) {
        return energy >= amount;
    }
    
    /**
     * ใช้พลังงาน - คืนค่า true ถ้าพลังงานพอ
     */
    public boolean useEnergy(int amount) {
        if (energy >= amount) {
            energy -= amount;
            if (energy < 0) energy = 0;
            System.out.println("[GameLogic] ใช้พลังงาน " + amount + " เหลือ: " + energy);
            return true;
        }
        System.out.println("[GameLogic] พลังงานไม่พอ! ต้องการ: " + amount + " มี: " + energy);
        return false;
    }
    
    /**
     * เพิ่มพลังงาน
     */
    public void restoreEnergy(int amount) {
        energy += amount;
        if (energy > MAX_ENERGY) energy = MAX_ENERGY;
        System.out.println("[GameLogic] ฟื้นฟูพลังงาน +" + amount + " เป็น: " + energy);
    }
    
    /**
     * นอนพักผ่อน - รีพลังงานเต็ม
     */
    public void sleep() {
        energy = MAX_ENERGY;
        System.out.println("[GameLogic] นอนพักผ่อน - พลังงานเต็ม: " + MAX_ENERGY);
    }
    
    // ========================================
    // ระบบเงิน
    // ========================================
    
    public int getMoney() {
        return money;
    }
    
    public boolean hasMoney(int amount) {
        return money >= amount;
    }
    
    /**
     * ใช้เงิน - คืนค่า true ถ้าเงินพอ
     */
    public boolean spendMoney(int amount) {
        if (money >= amount) {
            money -= amount;
            System.out.println("[GameLogic] ใช้เงิน " + amount + " เหลือ: " + money);
            return true;
        }
        System.out.println("[GameLogic] เงินไม่พอ! ต้องการ: " + amount + " มี: " + money);
        return false;
    }
    
    /**
     * เพิ่มเงิน
     */
    public void addMoney(int amount) {
        money += amount;
        System.out.println("[GameLogic] ได้รับเงิน +" + amount + " รวม: " + money);
    }
    
    // ========================================
    // ระบบความชอบ
    // ========================================
    
    /**
     * ดูค่าความชอบของตัวละครที่เลือกอยู่
     */
    public int getCurrentAffection() {
        switch (selectedCharacter) {
            case "มีน": return affectionMean;
            case "พลอย": return affectionPloy;
            case "พี่ลิลลี่": return affectionLilli;
            default: return 0;
        }
    }
    
    /**
     * เพิ่มค่าความชอบให้ตัวละครที่เลือกอยู่
     */
    public void addAffection(int amount) {
        String oldChar = selectedCharacter;
        int oldValue = getCurrentAffection();
        
        switch (selectedCharacter) {
            case "มีน": 
                affectionMean += amount;
                if (affectionMean > MAX_AFFECTION) affectionMean = MAX_AFFECTION;
                if (affectionMean < 0) affectionMean = 0;
                break;
            case "พลอย": 
                affectionPloy += amount;
                if (affectionPloy > MAX_AFFECTION) affectionPloy = MAX_AFFECTION;
                if (affectionPloy < 0) affectionPloy = 0;
                break;
            case "พี่ลิลลี่": 
                affectionLilli += amount;
                if (affectionLilli > MAX_AFFECTION) affectionLilli = MAX_AFFECTION;
                if (affectionLilli < 0) affectionLilli = 0;
                break;
        }
        
        System.out.println("[GameLogic] ความชอบ " + oldChar + ": " + oldValue + " -> " + getCurrentAffection());
    }
    
    /**
     * ดูค่าความชอบของตัวละครที่ระบุ
     */
    public int getAffection(String characterName) {
        switch (characterName) {
            case "มีน": return affectionMean;
            case "พลอย": return affectionPloy;
            case "พี่ลิลลี่": return affectionLilli;
            default: return 0;
        }
    }
    
    // ========================================
    // ระบบของขวัญ
    // ========================================
    
    /**
     * รีเซ็ตโควต้าของขวัญถ้าเป็นวันใหม่
     */
    private void checkGiftQuotaReset() {
        LocalDate today = LocalDate.now();
        if (!today.equals(lastGiftResetDate)) {
            giftQuotaToday = 3;
            lastGiftResetDate = today;
            System.out.println("[GameLogic] รีเซ็ตโควต้าของขวัญ: " + giftQuotaToday + " ครั้ง");
        }
    }
    
    public int getGiftQuota() {

        return giftQuotaToday;
    }
    
    /**
     * ส่งของขวัญ - ระบุประเภท: "cheap", "normal", "expensive"
     * คืนค่า: 0=สำเร็จ, 1=เงินไม่พอ, 2=โควต้าหมด, 3=ไม่มีตัวละครที่เลือก
     */
    public int sendGift(String giftType) {

        
        if (selectedCharacter.isEmpty()) {
            System.out.println("[GameLogic] ERROR: ยังไม่ได้เลือกตัวละคร!");
            return 3;
        }
        
        if (giftQuotaToday <= 0) {
            System.out.println("[GameLogic] โควต้าของขวัญหมดแล้ววันนี้!");
            return 2;
        }
        
        int cost = 0;
        int affectionGain = 0;
        
        switch (giftType.toLowerCase()) {
            case "cheap":
                cost = GIFT_CHEAP_COST;
                affectionGain = 5;
                break;
            case "normal":
                cost = GIFT_NORMAL_COST;
                affectionGain = 15;
                break;
            case "expensive":
                cost = GIFT_EXPENSIVE_COST;
                affectionGain = 50;
                break;
            default:
                System.out.println("[GameLogic] ERROR: ประเภทของขวัญไม่ถูกต้อง: " + giftType);
                return 3;
        }
        
        if (!spendMoney(cost)) {
            return 1; // เงินไม่พอ
        }
        
        addAffection(affectionGain);
        giftQuotaToday--;
        
        System.out.println("[GameLogic] ส่งของขวัญ " + giftType + " สำเร็จ! ความชอบ+" + affectionGain + " โควต้าเหลือ: " + giftQuotaToday);
        return 0; // สำเร็จ
    }
    
    public int getGiftCheapCost() { return GIFT_CHEAP_COST; }
    public int getGiftNormalCost() { return GIFT_NORMAL_COST; }
    public int getGiftExpensiveCost() { return GIFT_EXPENSIVE_COST; }
    
    // ========================================
    // ระบบงาน
    // ========================================
    
    /**
     * รีเซ็ตจำนวนครั้งทำงานถ้าเป็นวันใหม่
     */
    private void checkWorkReset() {
        LocalDate today = LocalDate.now();
        if (!today.equals(lastWorkResetDate)) {
            workCountToday = 0;
            lastWorkResetDate = today;
            System.out.println("[GameLogic] รีเซ็ตจำนวนครั้งทำงาน: " + workCountToday + "/" + MAX_WORK_PER_DAY);
        }
    }
    
    public int getWorkCountToday() {

        return workCountToday;
    }
    
    public int getMaxWorkPerDay() {
        return MAX_WORK_PER_DAY;
    }
    
    /**
     * ทำงาน - ใช้พลังงาน 20 ได้เงิน 100
     * คืนค่า: 0=สำเร็จ, 1=พลังงานไม่พอ, 2=ทำงานครบวันนี้แล้ว
     */
    public int work() {

        
        if (workCountToday >= MAX_WORK_PER_DAY) {
            System.out.println("[GameLogic] ทำงานครบแล้ววันนี้! (" + workCountToday + "/" + MAX_WORK_PER_DAY + ")");
            return 2;
        }
        
        int energyCost = 20;
        int moneyGain = 100;
        
        if (!useEnergy(energyCost)) {
            return 1; // พลังงานไม่พอ
        }
        
        addMoney(moneyGain);
        workCountToday++;
        
        System.out.println("[GameLogic] ทำงานสำเร็จ! ได้เงิน +" + moneyGain + " ทำงานไปแล้ว: " + workCountToday + "/" + MAX_WORK_PER_DAY);
        return 0; // สำเร็จ
    }
    
    // ========================================
    // ฟังก์ชันสำหรับ UI แสดงข้อมูล
    // ========================================
    
    /**
     * ดึงข้อมูลสถานะทั้งหมดเป็น String
     */
    public String getStatusText() {


        
        return String.format(
            "💰 เงิน: %d บาท | ⚡ พลังงาน: %d/%d | 💝 ความชอบ: %d/100\n🎁 ของขวัญวันนี้: %d/3 | 💼 ทำงาน: %d/5",
            money, energy, MAX_ENERGY, getCurrentAffection(), 
            giftQuotaToday, workCountToday
        );
    }
    
    /**
     * Debug - แสดงข้อมูลทั้งหมด
     */
    public void printDebugInfo() {
        System.out.println("\n========== DEBUG INFO ==========");
        System.out.println("ตัวละครที่เลือก: " + selectedCharacter);
        System.out.println("เงิน: " + money);
        System.out.println("พลังงาน: " + energy + "/" + MAX_ENERGY);
        System.out.println("ความชอบ มีน: " + affectionMean);
        System.out.println("ความชอบ พลอย: " + affectionPloy);
        System.out.println("ความชอบ ลิลลี่: " + affectionLilli);
        System.out.println("โควต้าของขวัญ: " + giftQuotaToday + "/3");
        System.out.println("ทำงาน: " + workCountToday + "/" + MAX_WORK_PER_DAY);
        System.out.println("================================\n");
    }

    // ── Setters สำหรับ Load Game ──
    public void setMoney(int amount)   { this.money = amount; }
    public void setEnergy(int amount)  { this.energy = Math.min(amount, MAX_ENERGY); }
    public void setAffection(int amount) {
        if      ("มีน".equals(selectedCharacter))   affectionMean  = amount;
        else if ("พลอย".equals(selectedCharacter))  affectionPloy  = amount;
        else if ("ลิลลี่".equals(selectedCharacter)) affectionLilli = amount;
    }
}