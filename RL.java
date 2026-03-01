/**
 * RL.java — Responsive Layout helper
 * breakpoint:
 *   LARGE  : w >= 1400  (PC 27"+, Full HD monitor)
 *   MEDIUM : w >= 1000  (โน็ตบุค 13-16")
 *   SMALL  : w <  1000  (window เล็ก)
 */
public class RL {

    public enum Size { SMALL, MEDIUM, LARGE }

    public final int w, h;
    public final Size size;

    // typography
    public final int fontTitle;    // หัวข้อใหญ่
    public final int fontSubtitle; // subtitle
    public final int fontBody;     // ทั่วไป
    public final int fontSmall;    // เล็ก

    // buttons
    public final int btnW;
    public final int btnH;
    public final int btnFont;
    public final int btnGap;

    // spacing
    public final int padX;   // horizontal padding
    public final int padY;   // vertical padding from top
    public final int gap;    // ช่องว่างระหว่าง element

    public RL(int w, int h) {
        this.w = w;
        this.h = h;

        if (w >= 1400) {
            size       = Size.LARGE;
            fontTitle  = 58;
            fontSubtitle = 24;
            fontBody   = 18;
            fontSmall  = 14;
            btnW       = 300;
            btnH       = 66;
            btnFont    = 22;
            btnGap     = 82;
            padX       = 300;
            padY       = 60;
            gap        = 24;
        } else if (w >= 1000) {
            size       = Size.MEDIUM;
            fontTitle  = 42;
            fontSubtitle = 18;
            fontBody   = 15;
            fontSmall  = 12;
            btnW       = 240;
            btnH       = 54;
            btnFont    = 18;
            btnGap     = 66;
            padX       = 220;
            padY       = 40;
            gap        = 18;
        } else {
            size       = Size.SMALL;
            fontTitle  = 32;
            fontSubtitle = 15;
            fontBody   = 13;
            fontSmall  = 11;
            btnW       = 200;
            btnH       = 46;
            btnFont    = 15;
            btnGap     = 56;
            padX       = 160;
            padY       = 28;
            gap        = 14;
        }
    }

    /** center X สำหรับ element กว้าง ew */
    public int cx(int ew) { return (w - ew) / 2; }

    /** center Y สำหรับ element สูง eh */
    public int cy(int eh) { return (h - eh) / 2; }

    /** scale ค่า base (ออกแบบที่ 1200) ตาม w จริง */
    public int s(int base) {
        return (int)(base * w / 1200.0);
    }

    /** scale ค่า base (ออกแบบที่ 800) ตาม h จริง */
    public int sv(int base) {
        return (int)(base * h / 800.0);
    }
}
