/**
 * WorkQuestion.java
 * คลาสเก็บข้อมูลโจทย์แต่ละข้อ
 */
public class WorkQuestion {
    public final String question;       // ข้อความโจทย์
    public final String[] choices;      // ตัวเลือก 4 ข้อ
    public final int correctIndex;      // index ที่ถูก (0-3)
    public final String explanation;    // คำอธิบายเฉลย

    public WorkQuestion(String question, String[] choices, int correctIndex, String explanation) {
        this.question     = question;
        this.choices      = choices;
        this.correctIndex = correctIndex;
        this.explanation  = explanation;
    }
}
