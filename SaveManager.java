import java.io.*;
import java.util.Properties;

/**
 * SaveManager.java
 * บันทึกและโหลดข้อมูลเกมลงไฟล์ save.properties
 */
public class SaveManager {

    private static final String SAVE_FILE = "save.properties";

    // ────────────────────────────────────────────────
    // SAVE
    // ────────────────────────────────────────────────
    public static void save(GameLogic logic, GameDate gameDate) {
        Properties p = new Properties();

        // GameLogic
        p.setProperty("character",    logic.getSelectedCharacter());
        p.setProperty("money",        String.valueOf(logic.getMoney()));
        p.setProperty("energy",       String.valueOf(logic.getEnergy()));
        p.setProperty("affection",    String.valueOf(logic.getCurrentAffection()));
        p.setProperty("affectionMean",  String.valueOf(logic.getAffection("มีน")));
        p.setProperty("affectionPloy",  String.valueOf(logic.getAffection("พลอย")));
        p.setProperty("affectionLilli", String.valueOf(logic.getAffection("ลิลลี่")));

        // GameDate
        p.setProperty("year",  String.valueOf(gameDate.getYear()));
        p.setProperty("month", String.valueOf(gameDate.getMonth()));
        p.setProperty("day",   String.valueOf(gameDate.getDay()));

        try (OutputStream out = new FileOutputStream(SAVE_FILE)) {
            p.store(out, "FirstLove Save File");
            System.out.println("[SaveManager] บันทึกเกมสำเร็จ → " + SAVE_FILE);
        } catch (IOException e) {
            System.err.println("[SaveManager] บันทึกไม่สำเร็จ: " + e.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // LOAD
    // ────────────────────────────────────────────────
    public static boolean load(GameLogic logic, GameDate gameDate) {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            System.out.println("[SaveManager] ไม่พบไฟล์ save");
            return false;
        }

        Properties p = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            p.load(in);

            // GameLogic
            String character = p.getProperty("character", "");
            logic.setSelectedCharacter(character);
            logic.setMoney(parseInt(p, "money", 500));
            logic.setEnergy(parseInt(p, "energy", 100));

            // โหลด affection แยกตามตัวละคร
            loadAffection(logic, p, "มีน",   "affectionMean");
            loadAffection(logic, p, "พลอย",  "affectionPloy");
            loadAffection(logic, p, "ลิลลี่", "affectionLilli");

            // GameDate — ใช้ toAbsoluteDay / fromAbsoluteDay
            int year  = parseInt(p, "year",  1);
            int month = parseInt(p, "month", 1);
            int day   = parseInt(p, "day",   1);
            GameDate loaded = new GameDate(year, month, day);
            // copy ค่าเข้า gameDate ที่ส่งมา
            int abs = loaded.toAbsoluteDay();
            GameDate tmp = GameDate.fromAbsoluteDay(abs);
            // ใช้ reflection-free: loop nextDay
            // reset to day 1 ก่อน แล้ว nextDay ไปยัง abs
            // วิธีง่ายกว่า: เซ็ตผ่าน constructor แต่ gameDate ไม่มี setter
            // → เซ็ตผ่าน setDate method ที่เราจะ expose
            gameDate.setDate(year, month, day);

            System.out.println("[SaveManager] โหลดเกมสำเร็จ - " + character +
                    " วันที่ " + day + "/" + month + "/" + year);
            return true;

        } catch (IOException e) {
            System.err.println("[SaveManager] โหลดไม่สำเร็จ: " + e.getMessage());
            return false;
        }
    }

    // ────────────────────────────────────────────────
    // ตรวจว่ามี save file ไหม
    // ────────────────────────────────────────────────
    public static boolean hasSave() {
        return new File(SAVE_FILE).exists();
    }

    // ────────────────────────────────────────────────
    // Helpers
    // ────────────────────────────────────────────────
    private static int parseInt(Properties p, String key, int fallback) {
        try { return Integer.parseInt(p.getProperty(key, String.valueOf(fallback))); }
        catch (NumberFormatException e) { return fallback; }
    }

    private static void loadAffection(GameLogic logic, Properties p, String charName, String key) {
        String prev = logic.getSelectedCharacter();
        logic.setSelectedCharacter(charName);
        int val = parseInt(p, key, 0);
        logic.setAffection(val);
        logic.setSelectedCharacter(prev);
    }
}