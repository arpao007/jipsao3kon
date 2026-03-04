import java.io.Serializable;

public class GameDate implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum TimeOfDay {
        MORNING("เช้า"),
        AFTERNOON("บ่าย"),
        EVENING("เย็น"),
        NIGHT("กลางคืน");

        public final String label;
        TimeOfDay(String label) { this.label = label; }
    }

    private int year = 1;
    private int month = 4;
    private int day = 1;
    private TimeOfDay timeOfDay = TimeOfDay.MORNING;

    public int getYear() { return year; }
    public int getMonth() { return month; }
    public int getDay() { return day; }
    public TimeOfDay getTimeOfDay() { return timeOfDay; }

    public String getMonthName() {
        String[] months = {
            "มกราคม","กุมภาพันธ์","มีนาคม","เมษายน",
            "พฤษภาคม","มิถุนายน","กรกฎาคม","สิงหาคม",
            "กันยายน","ตุลาคม","พฤศจิกายน","ธันวาคม"
        };
        return months[Math.max(0, Math.min(11, month - 1))];
    }

    public String getFullDateString() {
        return String.format("วันที่ %d %s ปีที่ %d — %s",
                day, getMonthName(), year, timeOfDay.label);
    }

    public void advanceTime() {
        switch (timeOfDay) {
            case MORNING -> timeOfDay = TimeOfDay.AFTERNOON;
            case AFTERNOON -> timeOfDay = TimeOfDay.EVENING;
            case EVENING -> timeOfDay = TimeOfDay.NIGHT;
            case NIGHT -> {
                timeOfDay = TimeOfDay.MORNING;
                advanceDay();
            }
        }
    }

    private void advanceDay() {
        day++;
        if (day > 30) {
            day = 1;
            month++;
            if (month > 12) {
                month = 1;
                year++;
            }
        }
    }

    public void reset() {
        year = 1;
        month = 4;
        day = 1;
        timeOfDay = TimeOfDay.MORNING;
    }
}