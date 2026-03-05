import java.time.LocalDate;

public class GameLogic {

    // =========================
    // ตัวละคร (เหลือคนเดียว)
    // =========================
    private static final String GIRL_NAME = "มีน";


    // =========================
    // ตัวละครที่เลือก (สำหรับ Save/Load)
    // =========================
    private String selectedCharacter = "DEFAULT";

    // =========================
    // ระบบวันในเกม
    // 1 ฤดู = 28 วัน (4 สัปดาห์)
    // 4 ฤดู = 1 ปี (112 วัน)
    // =========================
    private LocalDate gameStartDate = LocalDate.of(2026, 1, 1);
    private LocalDate gameDate = gameStartDate;

    // =========================
    // ระบบพลังงาน / เงิน
    // =========================
    private int energy = 100;
    private final int MAX_ENERGY = 100;

    private int money = 500;

    // =========================
    // ระบบความชอบ (คนเดียว)
    // =========================
    private int affection = 0;
    private final int MAX_AFFECTION = 100;

    // =========================
    // ระบบของขวัญ (จำกัดต่อวัน)
    // =========================
    private int giftQuotaToday = 3;
    private LocalDate lastGiftResetDate = gameDate;

    // =========================
    // ระบบงาน (จำกัดต่อวัน)
    // =========================
    private int workCountToday = 0;
    private final int MAX_WORK_PER_DAY = 5;
    private LocalDate lastWorkResetDate = gameDate;

    // =========================
    // ราคาของขวัญ
    // =========================
    private final int GIFT_CHEAP_COST = 50;      // +5
    private final int GIFT_NORMAL_COST = 150;    // +15
    private final int GIFT_EXPENSIVE_COST = 500; // +50

    // =========================
    // Constructor
    // =========================
    public GameLogic() {
        System.out.println("[GameLogic] ระบบเริ่มต้น - เงิน: " + money + " พลังงาน: " + energy);
    }

    // =====================================================
    // ระบบวัน/ฤดูกาล
    // =====================================================
    public LocalDate getGameDate() { return gameDate; }
    public void setGameDate(LocalDate d) {
        if (d == null) return;
        this.gameDate = d;
        // ปรับ last reset ให้ไม่เพี้ยน
        this.lastGiftResetDate = d;
        this.lastWorkResetDate = d;
    }

    private int getDayCount() {
        // นับจำนวนวันตั้งแต่เริ่มเกม
        return (int) (gameDate.toEpochDay() - gameStartDate.toEpochDay());
    }

    public int getYearIndex() {
        return (getDayCount() / 112) + 1;
    }

    public int getSeasonIndex() { // 0..3
        return (getDayCount() % 112) / 28;
    }

    public int getDayInSeason() { // 1..28
        return ((getDayCount() % 112) % 28) + 1;
    }

    public int getWeekInSeason() { // 1..4
        return ((getDayInSeason() - 1) / 7) + 1;
    }

    public int getDayOfWeekInSeason() { // 1..7
        return ((getDayInSeason() - 1) % 7) + 1;
    }

    /**
     * ข้ามวัน (ใช้กับปุ่มเข้านอน)
     * - เพิ่มวัน
     * - พลังงานเต็ม
     * - รีโควต้ารายวัน (ของขวัญ/งาน/อื่น ๆ)
     */
    public void nextDay() {
        gameDate = gameDate.plusDays(1);
        sleep();
        resetDailyLimits();
        System.out.println("[GameLogic] ข้ามวัน -> " + gameDate
                + " | " + " สัปดาห์ " + getWeekInSeason()
                + " วัน " + getDayInSeason() + "/28");
    }

    /**
     * รีเซ็ตโควต้ารายวันทั้งหมด
     */
    public void resetDailyLimits() {
        giftQuotaToday = 3;
        workCountToday = 0;
        lastGiftResetDate = gameDate;
        lastWorkResetDate = gameDate;

        // ให้มาเพิ่มรีเซ็ตตรงนี้ด้วย
        System.out.println("[GameLogic] รีเซ็ตโควต้ารายวัน: gift=3, work=0");
    }

    // =====================================================
    // ระบบพลังงาน
    // =====================================================
    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return MAX_ENERGY; }

    public boolean hasEnergy(int amount) { return energy >= amount; }

    public boolean useEnergy(int amount) {
        if (amount <= 0) return true;
        if (energy >= amount) {
            energy -= amount;
            if (energy < 0) energy = 0;
            System.out.println("[GameLogic] ใช้พลังงาน " + amount + " เหลือ: " + energy);
            return true;
        }
        System.out.println("[GameLogic] พลังงานไม่พอ! ต้องการ: " + amount + " มี: " + energy);
        return false;
    }

    public void restoreEnergy(int amount) {
        if (amount <= 0) return;
        energy += amount;
        if (energy > MAX_ENERGY) energy = MAX_ENERGY;
        System.out.println("[GameLogic] ฟื้นฟูพลังงาน +" + amount + " เป็น: " + energy);
    }

    public void sleep() {
        energy = MAX_ENERGY;
        System.out.println("[GameLogic] นอนพักผ่อน - พลังงานเต็ม: " + MAX_ENERGY);
    }

    /**
     * ทำกับข้าว: หักเงิน 50 + เพิ่มพลัง 50
     * @return true ถ้าทำได้, false ถ้าเงินไม่พอ
     */
    public boolean cookFood() {
        int cost = 50;
        int gain = 50;
        if (!spendMoney(cost)) return false;
        restoreEnergy(gain);
        System.out.println("[GameLogic] ทำกับข้าว - เงิน -" + cost + " พลัง +" + gain);
        return true;
    }

    // =====================================================
    // ระบบเงิน
    // =====================================================
    public int getMoney() { return money; }

    public boolean hasMoney(int amount) { return money >= amount; }

    public boolean spendMoney(int amount) {
        if (amount <= 0) return true;
        if (money >= amount) {
            money -= amount;
            System.out.println("[GameLogic] ใช้เงิน " + amount + " เหลือ: " + money);
            return true;
        }
        System.out.println("[GameLogic] เงินไม่พอ! ต้องการ: " + amount + " มี: " + money);
        return false;
    }

    public void addMoney(int amount) {
        if (amount <= 0) return;
        money += amount;
        System.out.println("[GameLogic] ได้รับเงิน +" + amount + " รวม: " + money);
    }

    // =====================================================
    // ระบบความชอบ (คนเดียว)
    // =====================================================
    public String getGirlName() { return GIRL_NAME; }


    public String getSelectedCharacter() { return selectedCharacter; }
    public void setSelectedCharacter(String name) {
        if (name == null ) return;
        this.selectedCharacter = name.trim();
    }

    public int getCurrentAffection() {
        return affection;
    }

    public void addAffection(int amount) {
        int old = affection;
        affection += amount;
        if (affection > MAX_AFFECTION) affection = MAX_AFFECTION;
        if (affection < 0) affection = 0;
        System.out.println("[GameLogic] ความชอบ " + GIRL_NAME + ": " + old + " -> " + affection);
    }

    public int getAffection(String characterName) {
        // กันโค้ดเก่าเรียกอยู่
        if (characterName == null) return 0;
        return characterName.equals(GIRL_NAME) ? affection : 0;
    }

    // =====================================================
    // ระบบของขวัญ
    // =====================================================
    private void checkGiftQuotaReset() {
        // ใช้ gameDate ไม่ใช้ LocalDate.now()
        if (!gameDate.equals(lastGiftResetDate)) {
            giftQuotaToday = 3;
            lastGiftResetDate = gameDate;
            System.out.println("[GameLogic] รีเซ็ตโควต้าของขวัญ: " + giftQuotaToday + " ครั้ง");
        }
    }

    public int getGiftQuota() {
        checkGiftQuotaReset();
        return giftQuotaToday;
    }

    /**
     * ใช้โควต้าของขวัญ 1 ครั้ง (สำหรับ ShopPanel)
     * @return true ถ้าใช้สำเร็จ, false ถ้าโควต้าหมดแล้ว
     */
    public boolean consumeGiftQuota() {
        checkGiftQuotaReset();
        if (giftQuotaToday <= 0) return false;
        giftQuotaToday--;
        return true;
    }

    /** ตั้งค่าความชอบตรงๆ (ใช้ตอนเริ่มเกม/โหลดเกม) */
    public void setCurrentAffection(int value) {
        affection = Math.max(0, Math.min(MAX_AFFECTION, value));
    }

    /**
     * ส่งของขวัญ - giftType: "cheap","normal","expensive"
     * คืนค่า: 0=สำเร็จ, 1=เงินไม่พอ, 2=โควต้าหมด, 3=ประเภทไม่ถูกต้อง
     */
    public int sendGift(String giftType) {
        checkGiftQuotaReset();

        if (giftQuotaToday <= 0) {
            System.out.println("[GameLogic] โควต้าของขวัญหมดแล้ววันนี้!");
            return 2;
        }

        int cost;
        int affectionGain;

        if (giftType == null) return 3;

        switch (giftType.toLowerCase()) {
                        { cost = GIFT_CHEAP_COST; affectionGain = 5; }
                        { cost = GIFT_NORMAL_COST; affectionGain = 15; }
                        { cost = GIFT_EXPENSIVE_COST; affectionGain = 50; }
                        {
                System.out.println("[GameLogic] ERROR: ประเภทของขวัญไม่ถูกต้อง: " + giftType);
                return 3;
            }

        if (!spendMoney(cost)) return 1;

        addAffection(affectionGain);
        giftQuotaToday--;

        System.out.println("[GameLogic] ส่งของขวัญ " + giftType + " สำเร็จ! ความชอบ+"
                + affectionGain + " โควต้าเหลือ: " + giftQuotaToday);
        return 0;
    }

    public int getGiftCheapCost() { return GIFT_CHEAP_COST; }
    public int getGiftNormalCost() { return GIFT_NORMAL_COST; }
    public int getGiftExpensiveCost() { return GIFT_EXPENSIVE_COST; }

    private void checkWorkReset() {
        // ใช้ gameDate ไม่ใช้ LocalDate.now()
        if (!gameDate.equals(lastWorkResetDate)) {
            workCountToday = 0;
            lastWorkResetDate = gameDate;
            System.out.println("[GameLogic] รีเซ็ตจำนวนครั้งทำงาน: " + workCountToday + "/" + MAX_WORK_PER_DAY);
        }
    }

    public int getWorkCountToday() {
        checkWorkReset();
        return workCountToday;
    }

    public int getMaxWorkPerDay() {
        return MAX_WORK_PER_DAY;
    }

    public int work() {
        checkWorkReset();

        if (workCountToday >= MAX_WORK_PER_DAY) {
            System.out.println("[GameLogic] ทำงานครบแล้ววันนี้! (" + workCountToday + "/" + MAX_WORK_PER_DAY + ")");
            return 2;
        }

        int energyCost = 20;
        int moneyGain = 100;

        if (!useEnergy(energyCost)) return 1;

        addMoney(moneyGain);
        workCountToday++;

        System.out.println("[GameLogic] ทำงานสำเร็จ! ได้เงิน +" + moneyGain
                + " ทำงานไปแล้ว: " + workCountToday + "/" + MAX_WORK_PER_DAY);
        return 0;
    }

    // =====================================================
    // ฟังก์ชันสำหรับ UI
    // =====================================================
    public String getStatusText() {
        checkGiftQuotaReset();
        checkWorkReset();

        return String.format(
            "📅 ปีที่ %d | %s | สัปดาห์ %d | วัน %d/28\n" +
            "💰 เงิน: %d บาท | ⚡ พลังงาน: %d/%d | 💝 ความชอบ: %d/100\n" +
            "🎁 ของขวัญวันนี้: %d/3 | 💼 ทำงาน: %d/%d",
            getYearIndex(), getWeekInSeason(), getDayInSeason(),
            money, energy, MAX_ENERGY, affection,
            giftQuotaToday, workCountToday, MAX_WORK_PER_DAY
        );
    }

    public void printDebugInfo() {
        System.out.println("\n========== DEBUG INFO ==========");
        System.out.println("วันในเกม: " + gameDate + " | ปีที่ " + getYearIndex()
                + " | " + " | วัน " + getDayInSeason() + "/28");
        System.out.println("เงิน: " + money);
        System.out.println("พลังงาน: " + energy + "/" + MAX_ENERGY);
        System.out.println("ความชอบ มีน: " + affection + "/" + MAX_AFFECTION);
        System.out.println("โควต้าของขวัญ: " + giftQuotaToday + "/3");
        System.out.println("ทำงาน: " + workCountToday + "/" + MAX_WORK_PER_DAY);
        System.out.println("================================\n");
    }

    // =====================================================
    // Setters สำหรับ Load Game
    // =====================================================
    public void setMoney(int amount) { this.money = amount; }
    public void setEnergy(int amount) { this.energy = Math.min(Math.max(amount, 0), MAX_ENERGY); }
    public void setAffection(int amount) {
        this.affection = Math.min(Math.max(amount, 0), MAX_AFFECTION);
    }
}