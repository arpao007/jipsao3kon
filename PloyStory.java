import java.util.ArrayList;
import java.util.List;

public class PloyStory {
    public static List<Dialogue> getStory() {
        List<Dialogue> list = new ArrayList<>();
        list.add(new Dialogue("พลอย", "เฮ้! มาทำงานกลุ่มด้วยกันไหม?", "res/Emean.png"));
        list.add(new Dialogue("คุณ", "ได้เลยครับ", null));
        list.add(new Dialogue("พลอย", "ดีจัง! งั้นเริ่มกันเลยนะ", "res/Emean.png"));
        return list;
    }
}