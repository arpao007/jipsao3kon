import java.util.ArrayList;
import java.util.List;

public class LilliStory {
    public static List<Dialogue> getStory() {
        List<Dialogue> list = new ArrayList<>();
        list.add(new Dialogue("บรรยาย", "ช่วงเย็นหลังเลิกเรียน แถวตึกศิลปะเงียบกว่าปกติ\n", "res/Library.jpg"));
        list.add(new Dialogue("บรรยาย", "เห็นผู้หญิงคนหนึ่งนั่งวาดรูปอยู่ใต้ต้นไม้ เธอดูตั้งใจมากจนไม่ได้สนใจรอบข้าง\n", "res/LibraryGate.jpg"));
        list.add(new Dialogue("บรรยาย", "ด้วยความเผลอสนใจ เลยแอบมองภาพในสมุดของเธอจากด้านหลัง\n", "res/LibraryGate.jpg|res/LilliArt222.png"));
        list.add(new Dialogue("บรรยาย", "เป็นภาพวิวแสงเย็นกับต้นไม้ โทนสีอบอุ่นสบายตา\n", "res/LibraryGate.jpg|res/Lilli001.png"));
        list.add(new Dialogue("คุณ", "“ชอบวาดรูปสไตล์นี้เหรอ เราว่าสวยนะ”", "res/LibraryGate.jpg|res/Lilli001.png"));
        return list;
    }
}