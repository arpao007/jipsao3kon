import java.awt.Font;
import javax.swing.UIManager; // เพิ่มตัวนี้เข้าไปครับ

public class Main {
    public static void main(String[] args) {
        // ตั้งค่าฟอนต์ภาษาไทยให้ระบบ UI ทั้งหมด
        UIManager.put("OptionPane.messageFont", new Font("Tahoma", Font.PLAIN, 16));
        UIManager.put("Button.font", new Font("Tahoma", Font.PLAIN, 14));
        
        GameLogic logic = new GameLogic();
        GameUI ui = new GameUI(logic);
        ui.show();
    }
}