import java.time.LocalDate;
import java.util.List;

/**
 * WorkGameLogic.java
 * จัดการระบบงาน: โควต้ารายวัน, สถานะเกม, รางวัล
 * เชื่อมกับ GameLogic และ WorkQuestionBank
 */
public class WorkGameLogic {

    // ---- ค่าคงที่ ----
    public static final int MAX_ROUNDS_PER_DAY = 2;   // ทำงานได้ 2 รอบ/วัน
    public static final int QUESTIONS_PER_ROUND = 3;  // 3 ข้อ/รอบ
    public static final int REWARD_PER_ROUND    = 50; // รางวัล 50 บาท/รอบ
    public static final int ENERGY_COST         = 20; // พลังงานที่ใช้/รอบ

    // ---- โควต้าวัน ----
    private int roundsPlayedToday = 0;
    private LocalDate lastResetDate = LocalDate.now();

    // ---- สถานะรอบปัจจุบัน ----
    private int currentJobType   = 0; // 1=LogicGate, 2=Math, 3=Physics
    private List<WorkQuestion> currentQuestions;
    private int currentQuestionIndex = 0;
    private int correctCount         = 0;
    private boolean roundActive      = false;

    // ---- GameLogic ----
    private final GameLogic gameLogic;

    // ============================================================
    public WorkGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    // ============================================================
    //  โควต้า
    // ============================================================
    private void checkReset() {
        LocalDate today = LocalDate.now();
        if (!today.equals(lastResetDate)) {
            roundsPlayedToday = 0;
            lastResetDate = today;
        }
    }

    public int getRoundsPlayedToday() {
        checkReset();
        return roundsPlayedToday;
    }

    public int getRoundsRemaining() {
        return MAX_ROUNDS_PER_DAY - getRoundsPlayedToday();
    }

    public boolean canWork() {
        checkReset();
        return roundsPlayedToday < MAX_ROUNDS_PER_DAY && gameLogic.hasEnergy(ENERGY_COST);
    }

    // ============================================================
    //  เริ่มรอบงาน
    // ============================================================
    public enum StartResult {
        OK, NO_ROUNDS_LEFT, NOT_ENOUGH_ENERGY, INVALID_JOB
    }

    /**
     * เริ่มรอบงาน
     * @param jobType 1=LogicGate, 2=Math, 3=Physics
     */
    public StartResult startRound(int jobType) {
        checkReset();
        if (jobType < 1 || jobType > 3) return StartResult.INVALID_JOB;
        if (roundsPlayedToday >= MAX_ROUNDS_PER_DAY) return StartResult.NO_ROUNDS_LEFT;
        if (!gameLogic.hasEnergy(ENERGY_COST)) return StartResult.NOT_ENOUGH_ENERGY;

        // หักพลังงาน
        gameLogic.useEnergy(ENERGY_COST);

        currentJobType        = jobType;
        currentQuestions      = WorkQuestionBank.getRandomQuestions(jobType);
        currentQuestionIndex  = 0;
        correctCount          = 0;
        roundActive           = true;

        System.out.println("[WorkGameLogic] เริ่มงาน " + getJobName(jobType) + " — " + currentQuestions.size() + " ข้อ");
        return StartResult.OK;
    }

    // ============================================================
    //  ตอบคำถาม
    // ============================================================
    public boolean isRoundActive()      { return roundActive; }
    public boolean hasNextQuestion()    { return currentQuestionIndex < currentQuestions.size(); }

    public WorkQuestion getCurrentQuestion() {
        if (!hasNextQuestion()) return null;
        return currentQuestions.get(currentQuestionIndex);
    }

    /**
     * ตอบคำถามข้อปัจจุบัน
     * @param choiceIndex 0-3
     * @return true=ถูก, false=ผิด
     */
    public boolean answerQuestion(int choiceIndex) {
        if (!roundActive || !hasNextQuestion()) return false;
        WorkQuestion q = currentQuestions.get(currentQuestionIndex);
        boolean correct = (choiceIndex == q.correctIndex);
        if (correct) correctCount++;
        currentQuestionIndex++;
        return correct;
    }

    /**
     * จบรอบ — ให้รางวัลถ้าตอบถูกครบ 3 ข้อ
     * @return เงินที่ได้รับ (0 ถ้าไม่ครบ)
     */
    public int finishRound() {
        roundActive = false;
        roundsPlayedToday++;

        int earned = 0;
        if (correctCount == QUESTIONS_PER_ROUND) {
            gameLogic.addMoney(REWARD_PER_ROUND);
            earned = REWARD_PER_ROUND;
            System.out.println("[WorkGameLogic] ตอบถูกครบ! ได้เงิน +" + REWARD_PER_ROUND);
        } else {
            System.out.println("[WorkGameLogic] ตอบถูก " + correctCount + "/" + QUESTIONS_PER_ROUND + " — ไม่ได้รับรางวัล");
        }
        return earned;
    }

    // ============================================================
    //  Helper
    // ============================================================
    public int getCurrentQuestionIndex() { return currentQuestionIndex; }
    public int getTotalQuestions()       { return currentQuestions != null ? currentQuestions.size() : 0; }
    public int getCorrectCount()         { return correctCount; }
    public int getCurrentJobType()       { return currentJobType; }

    public static String getJobName(int jobType) {
        switch (jobType) {
            case 1: return "Logic Gate";
            case 2: return "คณิตศาสตร์";
            case 3: return "ฟิสิกส์เวกเตอร์";
            default: return "ไม่ทราบ";
        }
    }

    public String getStatusText() {
        checkReset();
        return String.format("💼 งานวันนี้: %d/%d รอบ  |  ⚡ พลังงาน: %d/%d  |  💰 เงิน: %d บาท",
                roundsPlayedToday, MAX_ROUNDS_PER_DAY,
                gameLogic.getEnergy(), gameLogic.getMaxEnergy(),
                gameLogic.getMoney());
    }
}
