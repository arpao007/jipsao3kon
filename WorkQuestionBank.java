import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * WorkQuestionBank.java
 * คลังโจทย์สำหรับงาน 3 ประเภท
 * - งาน 1: Logic Gate  (12 ข้อ: NOT×2, AND×2, NAND×2, NOR×2, XOR×2, XNOR×2)
 * - งาน 2: คณิตศาสตร์ (10 ข้อ: +, -, ×, /)
 * - งาน 3: ฟิสิกส์เวกเตอร์ (10 ข้อ)
 */
public class WorkQuestionBank {

    // ============================================================
    //  งาน 1 : Logic Gate
    // ============================================================
    public static List<WorkQuestion> getLogicGateQuestions() {
        List<WorkQuestion> list = new ArrayList<>();

        // ---- NOT (2 ข้อ) ----
        list.add(new WorkQuestion(
            "NOT 1 = ?",
            new String[]{"1", "0", "2", "-1"},
            1, "NOT 1 = 0  (กลับค่า)"
        ));
        list.add(new WorkQuestion(
            "NOT 0 = ?",
            new String[]{"0", "-1", "1", "2"},
            2, "NOT 0 = 1  (กลับค่า)"
        ));

        // ---- AND (2 ข้อ) ----
        list.add(new WorkQuestion(
            "1 AND 0 = ?",
            new String[]{"1", "0", "3", "-1"},
            1, "AND ได้ 1 ก็ต่อเมื่อ input ทั้งคู่เป็น 1 → 1 AND 0 = 0"
        ));
        list.add(new WorkQuestion(
            "1 AND 1 = ?",
            new String[]{"0", "-1", "2", "1"},
            3, "AND ได้ 1 ก็ต่อเมื่อ input ทั้งคู่เป็น 1 → 1 AND 1 = 1"
        ));

        // ---- NAND (2 ข้อ) ----
        list.add(new WorkQuestion(
            "1 NAND 1 = ?",
            new String[]{"1", "0", "-1", "2"},
            1, "NAND = NOT(AND) → NOT(1) = 0"
        ));
        list.add(new WorkQuestion(
            "0 NAND 0 = ?",
            new String[]{"0", "-1", "2", "1"},
            3, "NAND = NOT(AND) → NOT(0) = 1"
        ));

        // ---- NOR (2 ข้อ) ----
        list.add(new WorkQuestion(
            "0 NOR 0 = ?",
            new String[]{"0", "2", "1", "-1"},
            2, "NOR = NOT(OR) → NOT(0 OR 0) = NOT(0) = 1"
        ));
        list.add(new WorkQuestion(
            "1 NOR 0 = ?",
            new String[]{"1", "0", "-1", "2"},
            1, "NOR = NOT(OR) → NOT(1 OR 0) = NOT(1) = 0"
        ));

        // ---- XOR (2 ข้อ) ----
        list.add(new WorkQuestion(
            "1 XOR 1 = ?",
            new String[]{"1", "2", "0", "-1"},
            2, "XOR ได้ 1 เมื่อ input ต่างกัน → 1 XOR 1 = 0"
        ));
        list.add(new WorkQuestion(
            "1 XOR 0 = ?",
            new String[]{"0", "1", "-1", "2"},
            1, "XOR ได้ 1 เมื่อ input ต่างกัน → 1 XOR 0 = 1"
        ));

        // ---- XNOR (2 ข้อ) ----
        list.add(new WorkQuestion(
            "1 XNOR 1 = ?",
            new String[]{"0", "-1", "2", "1"},
            3, "XNOR = NOT(XOR) → NOT(0) = 1  (input เหมือนกันได้ 1)"
        ));
        list.add(new WorkQuestion(
            "0 XNOR 1 = ?",
            new String[]{"1", "0", "2", "-1"},
            1, "XNOR = NOT(XOR) → NOT(1) = 0  (input ต่างกันได้ 0)"
        ));

        return list;
    }

    // ============================================================
    //  งาน 2 : คณิตศาสตร์
    // ============================================================
    public static List<WorkQuestion> getMathQuestions() {
        List<WorkQuestion> list = new ArrayList<>();

        // บวก
        list.add(new WorkQuestion("47 + 38 = ?",
            new String[]{"84", "85", "86", "87"}, 1, "47 + 38 = 85"));
        list.add(new WorkQuestion("123 + 456 = ?",
            new String[]{"578", "579", "580", "569"}, 1, "123 + 456 = 579"));

        // ลบ
        list.add(new WorkQuestion("200 - 73 = ?",
            new String[]{"126", "127", "128", "137"}, 1, "200 - 73 = 127"));
        list.add(new WorkQuestion("500 - 189 = ?",
            new String[]{"310", "311", "312", "321"}, 1, "500 - 189 = 311"));

        // คูณ
        list.add(new WorkQuestion("13 × 7 = ?",
            new String[]{"89", "90", "91", "92"}, 2, "13 × 7 = 91"));
        list.add(new WorkQuestion("24 × 6 = ?",
            new String[]{"142", "143", "144", "145"}, 2, "24 × 6 = 144"));

        // หาร
        list.add(new WorkQuestion("144 ÷ 12 = ?",
            new String[]{"11", "12", "13", "14"}, 1, "144 ÷ 12 = 12"));
        list.add(new WorkQuestion("256 ÷ 8 = ?",
            new String[]{"30", "31", "32", "33"}, 2, "256 ÷ 8 = 32"));

        // ผสม
        list.add(new WorkQuestion("(5 + 3) × 4 = ?",
            new String[]{"28", "30", "32", "34"}, 2, "(5+3)×4 = 8×4 = 32"));
        list.add(new WorkQuestion("100 - (6 × 7) = ?",
            new String[]{"56", "57", "58", "59"}, 2, "100 - 42 = 58"));

        return list;
    }

    // ============================================================
    //  งาน 3 : ฟิสิกส์เวกเตอร์
    // ============================================================
    public static List<WorkQuestion> getPhysicsQuestions() {
        List<WorkQuestion> list = new ArrayList<>();

        list.add(new WorkQuestion(
            "เวกเตอร์ A = (3, 4) ขนาดของ A คือ?",
            new String[]{"3", "4", "5", "7"},
            2, "|A| = √(3²+4²) = √25 = 5"
        ));
        list.add(new WorkQuestion(
            "เวกเตอร์ A = (1, 0) และ B = (0, 1)\nA + B = ?",
            new String[]{"(1,1)", "(0,0)", "(2,1)", "(1,2)"},
            0, "บวกทีละส่วน: (1+0, 0+1) = (1,1)"
        ));
        list.add(new WorkQuestion(
            "เวกเตอร์ A = (6, 4) และ B = (2, 1)\nA - B = ?",
            new String[]{"(8,5)", "(4,3)", "(3,3)", "(4,4)"},
            1, "ลบทีละส่วน: (6-2, 4-1) = (4,3)"
        ));
        list.add(new WorkQuestion(
            "2 × เวกเตอร์ (3, 5) = ?",
            new String[]{"(5,7)", "(6,10)", "(3,10)", "(6,5)"},
            1, "คูณสเกลาร์: 2×(3,5) = (6,10)"
        ));
        list.add(new WorkQuestion(
            "Dot product: (2,3)·(4,1) = ?",
            new String[]{"10", "11", "12", "14"}, // จริง 8+3=11
            1, "(2×4)+(3×1) = 8+3 = 11"
        ));
        list.add(new WorkQuestion(
            "เวกเตอร์ (0, -5) ชี้ไปทิศใด?",
            new String[]{"ทิศเหนือ", "ทิศใต้", "ทิศตะวันออก", "ทิศตะวันตก"},
            1, "แกน Y ลบ = ทิศใต้"
        ));
        list.add(new WorkQuestion(
            "ขนาดของเวกเตอร์ (0, 7) คือ?",
            new String[]{"0", "49", "7", "√7"},
            2, "|v| = √(0²+7²) = 7"
        ));
        list.add(new WorkQuestion(
            "เวกเตอร์ใดเป็น unit vector?",
            new String[]{"(1,1)", "(0,2)", "(1,0)", "(2,0)"},
            2, "unit vector มีขนาด 1: |(1,0)| = 1 ✓"
        ));
        list.add(new WorkQuestion(
            "Cross product ของ 2D เวกเตอร์ A=(3,2), B=(1,4)\n|A×B| = ?",
            new String[]{"10", "11", "12", "14"},  // 3×4-2×1=10
            0, "|A×B| = |3×4 - 2×1| = |12-2| = 10"
        ));
        list.add(new WorkQuestion(
            "มุมระหว่างเวกเตอร์ (1,0) และ (0,1) คือ?",
            new String[]{"0°", "45°", "90°", "180°"},
            2, "เวกเตอร์ตั้งฉากกัน มุม = 90°"
        ));

        return list;
    }

    // ============================================================
    //  สุ่ม 3 ข้อจากคลัง
    // ============================================================
    public static List<WorkQuestion> getRandomQuestions(int jobType) {
        List<WorkQuestion> pool;
        switch (jobType) {
            case 1: pool = getLogicGateQuestions(); break;
            case 2: pool = getMathQuestions();      break;
            case 3: pool = getPhysicsQuestions();   break;
            default: return new ArrayList<>();
        }
        Collections.shuffle(pool);
        return pool.subList(0, Math.min(3, pool.size()));
    }
}
