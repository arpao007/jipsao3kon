import java.util.ArrayList;
import java.util.List;

public class MeanStory {
    public static List<Dialogue> getStory() {
        List<Dialogue> list = new ArrayList<>();

        // Index 0
        list.add(new Dialogue("บรรยาย", "วันแรก ๆ ของการเปิดเทอม เราแวะเข้าห้องสมุดเพื่อหาที่เงียบ ๆ", "res/BGLibrary.jpg")); 
        // Index 1
        list.add(new Dialogue("บรรยาย", "ในห้องสมุดเงียบๆ มีผู้หญิงคนหนึ่งกำลังเอื้อมหยิบหนังสือไม่ถึง", "res/unnamed.jpg")); 
        // Index 2
        list.add(new Dialogue("บรรยาย", "เธอดูตั้งใจมาก จนเผลอยิ้มออกมา", "res/unnamed.jpg"));
        
        // Index 3: จุดตัวเลือกแรก (เพิ่มคะแนนความชอบ)
        list.add(new Dialogue("คุณ", "“เข้าไปช่วยดีไหมนะ...”", "res/unnamed.jpg",
            new String[]{"เข้าไปช่วยหยิบให้", "ยิ้มให้แล้วเดินผ่าน", "ยืนดูห่างๆ"},
            new int[]{4, 17, 17},           // เลือกข้อแรกไป Index 4, ข้ออื่นไป Bad End (17)
            new int[]{20, -10, 0}           // << เพิ่ม: ข้อแรก +20, ข้อสอง -10, ข้อสาม 0
        ));

        // Index 4 (รูทช่วยหยิบหนังสือ)
        list.add(new Dialogue("คุณ", "“ นี่ครับหนังสือที่คุณต้องการ ”", "res/BGLibrary.jpg|res/Givebook.png"));
        // Index 5
        list.add(new Dialogue("มีน", "“ขอบคุณนะคะ”", "res/BGLibrary.jpg|res/Mean2.png"));
        // Index 6
        list.add(new Dialogue("บรรยาย", " นั่นคือจุดเริ่มต้นของความสัมพันธ์เรา ", "res/BGLibrary.jpg"));
        // Index 7
        list.add(new Dialogue("มีน", "“วันนี้มาอ่านหนังสืออีกแล้วหรอ ดีเลย...จะได้ไม่เหงา”", "res/BGLibrary.jpg|res/Mean2.png")); 
        // Index 8
        list.add(new Dialogue("คุณ", "“ เจอกันอีกแล้วนะ ”", "res/BGLibrary.jpg|res/Givebook.png"));
        
        // Index 9:
        list.add(new Dialogue("มีน", "“ช่วงนี้เรามานั่งอ่านหนังสือด้วยกันบ่อยเนอะ”", "res/BGLibrary.jpg|res/Mean2.png"));
        
        // Index 10: คำถามสารภาพรัก (เพิ่มคะแนนความชอบ)
        list.add(new Dialogue("คุณ", "“ เราก็รู้สึกเหมือนกันนะ ”", "res/BGLibrary.jpg|res/Givebook.png",
            new String[]{
                "งั้นต่อไป...ขออยู่ข้างๆแบบนี้ทุกวันได้ไหม",
                "เราก็ดีใจนะ ที่มีเธอเป็นเพื่อนอ่านหนังสือ",
                "ขอโทษนะ หลังจากนี้เราอาจไม่ได้มาบ่อยๆแล้ว"
            },
            new int[]{11, 14, 17},          // ไป Good End (11), Friend End (14), Bad End (17)
            new int[]{50, 10, -30}          // << เพิ่ม: ให้คะแนนตามระดับความสัมพันธ์
        ));

        // --- Good End (เริ่มที่ Index 11) ---
        list.add(new Dialogue("มีน", "“เล่มนี้สนุกมาก แต่อยากอ่านไปพร้อมกันมากกว่า”", "res/BGLibrary.jpg|res/Mean2.png")); 
        list.add(new Dialogue("บรรยาย", "มีนยื่นหนังสือที่มีลายมือเล็กๆ เขียนคั่นไว้ แล้วเธอก็นั่งข้างเราเหมือนทุกวัน", "res/BGLibrary.jpg|res/Mean2.png")); 
        list.add(new Dialogue("SYSTEM", "--- HAPPY ENDING ---", null));

        // --- Friend End (เริ่มที่ Index 14) ---
        list.add(new Dialogue("มีน", "“ขอบคุณนะที่ชอบมาอ่านด้วยกัน อยู่ด้วยแล้วสบายใจดี”", "res/BGLibrary.jpg|res/Mean2.png")); 
        list.add(new Dialogue("บรรยาย", "ความสัมพันธ์ของเรา กลายเป็นมุมสงบ ๆ ในห้องสมุดเสมอ", "res/BGLibrary.jpg")); 
        list.add(new Dialogue("SYSTEM", "--- FRIEND ENDING ---", null));

        // --- Bad End (เริ่มที่ Index 17) ---
        list.add(new Dialogue("บรรยาย", "เธอก้มหน้าอ่านหนังสือเงียบ ๆ เหมือนกำลังรอใครบางคนที่ไม่ได้กลับมาอีก", "res/empty_chair.jpg")); 
        list.add(new Dialogue("บรรยาย", "มุมประจำยังเหมือนเดิม แต่ที่นั่งข้างมีนว่างเปล่าเสมอ...", "res/empty_chair.jpg")); 
        list.add(new Dialogue("SYSTEM", "--- BAD ENDING ---", null));

        return list;
    }
}