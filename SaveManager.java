import java.io.*;
import java.util.Properties;
import java.time.LocalDate;

/**
 * SaveManager
 * - เซฟ/โหลดสถานะเกมลงไฟล์ properties (save.properties)
 * - ใช้ LocalDate (gameDate) ตามที่ GameLogic ใช้อยู่
 */
public class SaveManager {

    private static final String SAVE_FILE = "save.properties";

    public static boolean hasSave() {
        return new File(SAVE_FILE).exists();
    }

    // ──────────────────────────────────────────────
    // SAVE
    // ──────────────────────────────────────────────
    public static void save(GameLogic logic, LocalDate date) {
        Properties p = new Properties();

        // core
        p.setProperty("character",  logic.getSelectedCharacter());

        // stats
        p.setProperty("money",      String.valueOf(logic.getMoney()));
        p.setProperty("energy",     String.valueOf(logic.getEnergy()));
        p.setProperty("affection",  String.valueOf(logic.getCurrentAffection()));

        // date (LocalDate)
        p.setProperty("year",  String.valueOf(date.getYear()));
        p.setProperty("month", String.valueOf(date.getMonthValue()));
        p.setProperty("day",   String.valueOf(date.getDayOfMonth()));

        try (OutputStream out = new FileOutputStream(SAVE_FILE)) {
            p.store(out, "FirstLove Save File");
            System.out.println("[SaveManager] บันทึกเกมสำเร็จ → " + SAVE_FILE);
        } catch (IOException e) {
            System.err.println("[SaveManager] บันทึกไม่สำเร็จ: " + e.getMessage());
        }
    }

    // ──────────────────────────────────────────────
    // LOAD
    // ──────────────────────────────────────────────
    public static boolean load(GameLogic logic) {
        File f = new File(SAVE_FILE);
        if (!f.exists()) return false;

        Properties p = new Properties();
        try (InputStream in = new FileInputStream(f)) {
            p.load(in);
        } catch (IOException e) {
            System.err.println("[SaveManager] โหลดไม่สำเร็จ: " + e.getMessage());
            return false;
        }

        try {
            logic.setSelectedCharacter(p.getProperty("character", "มีน"));

            logic.setMoney(Integer.parseInt(p.getProperty("money", "500")));
            logic.setEnergy(Integer.parseInt(p.getProperty("energy", "100")));

            int aff = Integer.parseInt(p.getProperty("affection", "0"));
            try {
                logic.setCurrentAffection(aff);
            } catch (Throwable t) {
                logic.setAffection(aff);
            }

            int y = Integer.parseInt(p.getProperty("year",  String.valueOf(LocalDate.now().getYear())));
            int m = Integer.parseInt(p.getProperty("month", String.valueOf(LocalDate.now().getMonthValue())));
            int d = Integer.parseInt(p.getProperty("day",   String.valueOf(LocalDate.now().getDayOfMonth())));
            logic.setGameDate(LocalDate.of(y, m, d));

            System.out.println("[SaveManager] โหลดเกมสำเร็จ ← " + SAVE_FILE);
            return true;
        } catch (Exception ex) {
            System.err.println("[SaveManager] โหลดไม่สำเร็จ: " + ex.getMessage());
            return false;
        }
    }

    public static void deleteSave() {
        File f = new File(SAVE_FILE);
        if (f.exists() && f.delete()) {
            System.out.println("[SaveManager] ลบเซฟแล้ว");
        }
    }

    /** helper: สลับตัวละครที่เลือกแบบชั่วคราว */
    public static void swapCharacter(GameLogic logic, String charName, Runnable action) {
        String prev = logic.getSelectedCharacter();
        logic.setSelectedCharacter(charName);
        try { action.run(); }
        finally { logic.setSelectedCharacter(prev); }
    }
}
