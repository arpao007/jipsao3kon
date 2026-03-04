public class Dialogue {
    public String speaker;
    public String text;
    public String imagePath;
    public String[] choices;
    public int[] nextSteps;
    public int[] affectionGains; // เพิ่ม: เก็บแต้มความชอบของแต่ละตัวเลือก

    // Constructor ปกติ
    public Dialogue(String speaker, String text, String imagePath) {
        this.speaker = speaker;
        this.text = text;
        this.imagePath = imagePath;
        this.choices = null; 
        this.nextSteps = null;
        this.affectionGains = null;
    }

    // Constructor สำหรับ Choice (เพิ่ม parameter ตัวสุดท้าย)
    public Dialogue(String speaker, String text, String imagePath, String[] choices, int[] nextSteps, int[] affectionGains) {
        this.speaker = speaker;
        this.text = text;
        this.imagePath = imagePath;
        this.choices = choices;
        this.nextSteps = nextSteps;
        this.affectionGains = affectionGains; // บันทึกแต้ม
    }
}