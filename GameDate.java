/**
 * GameDate.java
 * ติดตามวัน เดือน ปี และช่วงเวลาในเกม
 */
public class GameDate {

    public enum TimeOfDay {
        MORNING("เช้า", "🌅"),
        AFTERNOON("บ่าย", "☀️"),
        EVENING("เย็น", "🌆"),
        NIGHT("กลางคืน", "🌙");

        public final String label;
        public final String emoji;
        TimeOfDay(String label, String emoji) {
            this.label = label; this.emoji = emoji;
        }
    }

    public enum Season {
        SPRING("ฤดูใบไม้ผลิ", "🌸"),
        SUMMER("ฤดูร้อน",     "☀️"),
        AUTUMN("ฤดูใบไม้ร่วง","🍂"),
        WINTER("ฤดูหนาว",     "❄️");

        public final String label;
        public final String emoji;
        Season(String label, String emoji) {
            this.label = label; this.emoji = emoji;
        }
    }

    // ── State ──────────────────────────────────────────────────────────────────
    private int       year      = 1;
    private int       month     = 4;   // เมษายน — ต้นปีการศึกษา
    private int       day       = 1;
    private TimeOfDay timeOfDay = TimeOfDay.MORNING;

    // ════════════════════════════════════════════════════════════════════════════
    // Getters
    // ════════════════════════════════════════════════════════════════════════════

    public int       getYear()      { return year; }
    public int       getMonth()     { return month; }
    public int       getDay()       { return day; }
    public TimeOfDay getTimeOfDay() { return timeOfDay; }

    public Season getSeason() {
        if (month >= 3 && month <= 5)  return Season.SPRING;
        if (month >= 6 && month <= 8)  return Season.SUMMER;
        if (month >= 9 && month <= 11) return Season.AUTUMN;
        return Season.WINTER;
    }

    /** ชื่อเดือนภาษาไทย */
    public String getMonthName() {
        String[] months = {
            "มกราคม","กุมภาพันธ์","มีนาคม","เมษายน",
            "พฤษภาคม","มิถุนายน","กรกฎาคม","สิงหาคม",
            "กันยายน","ตุลาคม","พฤศจิกายน","ธันวาคม"
        };
        return months[Math.max(0, Math.min(11, month - 1))];
    }

    /** แสดงผลเต็ม เช่น "วันที่ 1 เมษายน ปีที่ 1 — เช้า 🌅" */
    public String getFullDateString() {
        return String.format("วันที่ %d %s ปีที่ %d — %s %s",
                day, getMonthName(), year, timeOfDay.label, timeOfDay.emoji);
    }

    /** แสดงสั้น เช่น "1/4 ปี1 เช้า" */
    public String getShortDateString() {
        return String.format("%d/%d ปี%d %s", day, month, year, timeOfDay.label);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Advance time
    // ════════════════════════════════════════════════════════════════════════════

    /** ข้ามไปช่วงเวลาถัดไป */
    public void advanceTime() {
        switch (timeOfDay) {
            case MORNING:   timeOfDay = TimeOfDay.AFTERNOON; break;
            case AFTERNOON: timeOfDay = TimeOfDay.EVENING;   break;
            case EVENING:   timeOfDay = TimeOfDay.NIGHT;     break;
            case NIGHT:
                timeOfDay = TimeOfDay.MORNING;
                advanceDay();
                break;
        }
        System.out.println("[GameDate] " + getFullDateString());
    }

    /** ข้ามวัน */
    public void advanceDay() {
        day++;
        int daysInMonth = getDaysInMonth(month);
        if (day > daysInMonth) {
            day = 1;
            month++;
            if (month > 12) { month = 1; year++; }
        }
    }

    private int getDaysInMonth(int m) {
        if (m == 2) return 28;
        if (m == 4 || m == 6 || m == 9 || m == 11) return 30;
        return 31;
    }

    /** reset กลับต้น */
    public void reset() {
        year = 1; month = 4; day = 1;
        timeOfDay = TimeOfDay.MORNING;
    }

    @Override
    public String toString() { return getFullDateString(); }
}
