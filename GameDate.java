public class GameDate {

    // ─────────────────────────────────────────
    // ค่าคงที่
    // ─────────────────────────────────────────
    public static final int DAYS_PER_WEEK  = 7;
    public static final int WEEKS_PER_MONTH = 4;
    public static final int MONTHS_PER_YEAR = 12;
    public static final int DAYS_PER_MONTH = DAYS_PER_WEEK * WEEKS_PER_MONTH; // 28

    public static final String[] DAY_NAMES = {
        "จันทร์", "อังคาร", "พุธ", "พฤหัส", "ศุกร์", "เสาร์", "อาทิตย์"
    };

    public static final String[] MONTH_NAMES = {
        "มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน",
        "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม",
        "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"
    };

    // ─────────────────────────────────────────
    // State
    // ─────────────────────────────────────────
    private int year;   // เริ่มต้นที่ 1
    private int month;  // 1-12
    private int day;    // 1-28

    // ─────────────────────────────────────────
    // Listener
    // ─────────────────────────────────────────
    public interface OnDayChangeListener {
        void onDayChanged(GameDate date, boolean isMonday);
    }

    private OnDayChangeListener listener;

    // ─────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────
    public GameDate() {
        this.year  = 1;
        this.month = 1;
        this.day   = 1;
    }

    public GameDate(int year, int month, int day) {
        this.year  = year;
        this.month = month;
        this.day   = day;
    }

    // ─────────────────────────────────────────
    // Listener
    // ─────────────────────────────────────────
    public void setOnDayChangeListener(OnDayChangeListener listener) {
        this.listener = listener;
    }

    // ─────────────────────────────────────────
    // ข้ามวัน (เรียกเมื่อนอนหลับ ฯลฯ)
    // ─────────────────────────────────────────
    // ── Setter สำหรับ Load Game ──
    public void setDate(int year, int month, int day) {
        this.year  = year;
        this.month = month;
        this.day   = day;
    }

    public void nextDay() {
        day++;

        if (day > DAYS_PER_MONTH) {
            day = 1;
            month++;
            if (month > MONTHS_PER_YEAR) {
                month = 1;
                year++;
            }
        }

        boolean monday = isMonday();
        if (listener != null) {
            listener.onDayChanged(this, monday);
        }
    }

    // ─────────────────────────────────────────
    // Getters
    // ─────────────────────────────────────────
    public int getYear()  { return year;  }
    public int getMonth() { return month; }
    public int getDay()   { return day;   }

    /** วันที่ 1-7 ของสัปดาห์ปัจจุบัน */
    public int getDayOfWeek() {
        return ((day - 1) % DAYS_PER_WEEK) + 1; // 1=จันทร์ ... 7=อาทิตย์
    }

    /** สัปดาห์ที่ 1-4 ของเดือน */
    public int getWeekOfMonth() {
        return ((day - 1) / DAYS_PER_WEEK) + 1;
    }

    public String getDayName() {
        return DAY_NAMES[getDayOfWeek() - 1];
    }

    public String getMonthName() {
        return MONTH_NAMES[month - 1];
    }

    public boolean isMonday() {
        return getDayOfWeek() == 1;
    }

    // ─────────────────────────────────────────
    // แปลงเป็น int เดียว (สำหรับ save/load)
    // ─────────────────────────────────────────
    public int toAbsoluteDay() {
        int totalMonths = (year - 1) * MONTHS_PER_YEAR + (month - 1);
        return totalMonths * DAYS_PER_MONTH + (day - 1);
    }

    public static GameDate fromAbsoluteDay(int absDay) {
        int totalDays  = absDay;
        int y = totalDays / (MONTHS_PER_YEAR * DAYS_PER_MONTH) + 1;
        totalDays %= (MONTHS_PER_YEAR * DAYS_PER_MONTH);
        int m = totalDays / DAYS_PER_MONTH + 1;
        int d = totalDays % DAYS_PER_MONTH + 1;
        return new GameDate(y, m, d);
    }

    // ─────────────────────────────────────────
    // แสดงผล
    // ─────────────────────────────────────────
    @Override
    public String toString() {
        return String.format("ปีที่ %d  %s  สัปดาห์ที่ %d  วัน%s",
            year, getMonthName(), getWeekOfMonth(), getDayName());
    }

    public String toShortString() {
        return String.format("ปี%d %s วัน%s",
            year, getMonthName(), getDayName());
    }
}