/**
 * GameLogic.java
 * เก็บ state หลักของเกม เช่น ชื่อผู้เล่น, สาวที่เลือก, คะแนน, ชีวิต
 */
public class GameLogic {

    // ── ข้อมูลผู้เล่น ──────────────────────────────────────────────────────────
    private String playerName  = "";
    private String chosenGirl  = "";   // ID: "SAKURA" / "HANA" / "YUKI"

    // ── สถิติ ──────────────────────────────────────────────────────────────────
    private int score     = 0;
    private int lives     = 3;
    private int day       = 1;         // วันในเกม
    private int affection = 0;         // ความสัมพันธ์ 0–100

    // ── สถานะเกม ──────────────────────────────────────────────────────────────
    private boolean gameStarted = false;
    private boolean gameOver    = false;

    // ════════════════════════════════════════════════════════════════════════════
    // Getters & Setters
    // ════════════════════════════════════════════════════════════════════════════

    public String getPlayerName()   { return playerName; }
    public String getChosenGirl()   { return chosenGirl; }
    public int    getScore()        { return score; }
    public int    getLives()        { return lives; }
    public int    getDay()          { return day; }
    public int    getAffection()    { return affection; }
    public boolean isGameStarted()  { return gameStarted; }
    public boolean isGameOver()     { return gameOver; }

    public void setPlayerName(String name)  { this.playerName = name; }
    public void setChosenGirl(String girlId){ this.chosenGirl = girlId; }

    // ════════════════════════════════════════════════════════════════════════════
    // Game actions
    // ════════════════════════════════════════════════════════════════════════════

    /** เริ่มเกมใหม่ — reset ทุกอย่าง */
    public void startNewGame(String playerName, String girlId) {
        this.playerName  = playerName;
        this.chosenGirl  = girlId;
        this.score       = 0;
        this.lives       = 3;
        this.day         = 1;
        this.affection   = 0;
        this.gameStarted = true;
        this.gameOver    = false;
        System.out.println("[GameLogic] เริ่มเกมใหม่ — ผู้เล่น: " + playerName
                + " | สาว: " + girlId);
    }

    /** เพิ่มคะแนน */
    public void addScore(int points) {
        if (!gameOver) {
            score += points;
            System.out.println("[GameLogic] คะแนน: " + score);
        }
    }

    /** ลดชีวิต — ถ้า lives = 0 → game over */
    public void loseLife() {
        if (!gameOver && lives > 0) {
            lives--;
            System.out.println("[GameLogic] ชีวิตเหลือ: " + lives);
            if (lives <= 0) triggerGameOver();
        }
    }

    /** เพิ่มความสัมพันธ์ (max 100) */
    public void increaseAffection(int amount) {
        affection = Math.min(100, affection + amount);
        System.out.println("[GameLogic] ความสัมพันธ์: " + affection);
    }

    /** ข้ามวัน */
    public void nextDay() {
        day++;
        System.out.println("[GameLogic] วันที่: " + day);
    }

    /** จบเกม */
    private void triggerGameOver() {
        gameOver = true;
        System.out.println("[GameLogic] GAME OVER — คะแนนสุดท้าย: " + score);
    }

    /** สรุปสถานะ */
    @Override
    public String toString() {
        return String.format("[GameLogic] ผู้เล่น=%s สาว=%s คะแนน=%d ชีวิต=%d วัน=%d ความสัมพันธ์=%d",
                playerName, chosenGirl, score, lives, day, affection);
    }
}
