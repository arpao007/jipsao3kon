import javax.swing.*;
import java.awt.*;

/**
 * QuizRunnerPanel.java
 * UI สำหรับตอบคำถาม 3 ข้อในงาน
 */
public class QuizRunnerPanel extends JPanel {

    private static final Color BG     = new Color(0xF8F9FF);
    private static final Color ACCENT = new Color(0x1565C0);

    private final WorkGameLogic logic;
    private final String        jobName;
    private final Runnable      onFinish;

    private JLabel   questionLabel;
    private JLabel   progressLabel;
    private JPanel   choicesPanel;
    private JLabel   feedbackLabel;
    private JButton  nextBtn;

    private Boolean lastCorrect = null;

    public QuizRunnerPanel(WorkGameLogic logic, String jobName, Runnable onFinish) {
        this.logic    = logic;
        this.jobName  = jobName;
        this.onFinish = onFinish;

        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));

        buildUI();
        showCurrentQuestion();
    }

    private void buildUI() {
        // progress top
        progressLabel = new JLabel("", SwingConstants.CENTER);
        progressLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        progressLabel.setForeground(new Color(0x666666));
        progressLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // question
        questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        questionLabel.setForeground(new Color(0x0D47A1));
        questionLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 16, 0));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(progressLabel, BorderLayout.NORTH);
        top.add(questionLabel, BorderLayout.CENTER);

        // choices
        choicesPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        choicesPanel.setOpaque(false);

        // feedback
        feedbackLabel = new JLabel("", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
        feedbackLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // next
        nextBtn = new JButton("ถัดไป ▶");
        nextBtn.setFont(new Font("Tahoma", Font.BOLD, 15));
        nextBtn.setBackground(ACCENT);
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFocusPainted(false);
        nextBtn.setVisible(false);
        nextBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nextBtn.addActionListener(e -> onNext());

        JPanel bot = new JPanel(new BorderLayout());
        bot.setOpaque(false);
        bot.add(feedbackLabel, BorderLayout.CENTER);
        bot.add(nextBtn,       BorderLayout.EAST);

        add(top,          BorderLayout.NORTH);
        add(choicesPanel, BorderLayout.CENTER);
        add(bot,          BorderLayout.SOUTH);
    }

    private void showCurrentQuestion() {
        if (!logic.hasNextQuestion()) {
            showResult();
            return;
        }

        WorkQuestion q = logic.getCurrentQuestion();
        int idx = logic.getCurrentQuestionIndex();
        int total = logic.getTotalQuestions();

        progressLabel.setText("ข้อที่ " + (idx + 1) + " / " + total
            + "  |  " + WorkGameLogic.getJobName(logic.getCurrentJobType()));

        // HTML for multi-line question
        questionLabel.setText("<html><div style='text-align:center'>"
            + q.question.replace("\n","<br>") + "</div></html>");

        choicesPanel.removeAll();
        for (int i = 0; i < q.choices.length; i++) {
            final int ci = i;
            JButton btn = new JButton((char)('A' + i) + ". " + q.choices[i]);
            btn.setFont(new Font("Tahoma", Font.PLAIN, 15));
            btn.setBackground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> answer(ci));
            choicesPanel.add(btn);
        }

        feedbackLabel.setText("");
        nextBtn.setVisible(false);
        lastCorrect = null;

        revalidate();
        repaint();
    }

    private void answer(int choiceIndex) {
        // ล็อกปุ่มทุกปุ่ม
        for (Component c : choicesPanel.getComponents()) c.setEnabled(false);

        boolean correct = logic.answerQuestion(choiceIndex);
        lastCorrect = correct;

        WorkQuestion q = logic.getCurrentQuestion() == null
            ? null
            : null; // already advanced, get explanation from bank

        if (correct) {
            feedbackLabel.setText("✅ ถูกต้อง!");
            feedbackLabel.setForeground(new Color(0x1B5E20));
        } else {
            feedbackLabel.setText("❌ ผิด — เฉลย: " + getLastExplanation());
            feedbackLabel.setForeground(new Color(0xB71C1C));
        }

        nextBtn.setVisible(true);

        // ถ้าข้อสุดท้าย เปลี่ยนปุ่มเป็น "ดูผล"
        if (!logic.hasNextQuestion()) {
            nextBtn.setText("ดูผล 🏆");
        } else {
            nextBtn.setText("ถัดไป ▶");
        }

        revalidate();
        repaint();
    }

    private String getLastExplanation() {
        // ดึงจากคำถามข้อก่อนหน้า (index ถูก advance ไปแล้ว)
        // เราไม่มี reference โดยตรง — แค่แสดง index คำตอบที่ถูก
        return "(ดูในเฉลย)";
    }

    private void onNext() {
        if (!logic.hasNextQuestion()) {
            showResult();
        } else {
            showCurrentQuestion();
        }
    }

    private void showResult() {
        int earned = logic.finishRound();
        int correct = logic.getCorrectCount();
        int total   = logic.getTotalQuestions();

        String msg;
        Color  color;
        if (earned > 0) {
            msg   = "🎉 ผ่าน! ตอบถูก " + correct + "/" + total + "\nได้รับเงิน +" + earned + " บาท";
            color = new Color(0x1B5E20);
        } else {
            msg   = "😢 ไม่ผ่าน  ตอบถูก " + correct + "/" + total + "\nไม่ได้รับเงิน";
            color = new Color(0xB71C1C);
        }

        removeAll();
        setLayout(new BorderLayout());

        JLabel result = new JLabel("<html><div style='text-align:center;font-size:16pt'>"
            + msg.replace("\n","<br>") + "</div></html>", SwingConstants.CENTER);
        result.setFont(new Font("Tahoma", Font.BOLD, 18));
        result.setForeground(color);

        JButton doneBtn = new JButton("เสร็จสิ้น ✓");
        doneBtn.setFont(new Font("Tahoma", Font.BOLD, 16));
        doneBtn.setBackground(ACCENT);
        doneBtn.setForeground(Color.WHITE);
        doneBtn.setFocusPainted(false);
        doneBtn.setPreferredSize(new Dimension(140, 44));
        doneBtn.addActionListener(e -> onFinish.run());

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bot.setOpaque(false);
        bot.add(doneBtn);

        add(result, BorderLayout.CENTER);
        add(bot,    BorderLayout.SOUTH);
        revalidate();
        repaint();
    }
}