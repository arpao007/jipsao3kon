import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * WorkGame_ui.java
 * หน้าจอเกมทำงาน 3 ประเภท
 * Flow: เลือกงาน → ถาม 3 ข้อ → แสดงผล → กลับ
 */
public class WorkGame_ui extends JPanel {

    private final WorkGameLogic workLogic;
    private final CardLayout    cardLayout;
    private final JPanel        mainContainer;

    // ---- Sub-panels ----
    private JPanel selectPanel;   // เลือกงาน
    private JPanel questionPanel; // ถาม-ตอบ
    private JPanel resultPanel;   // ผลลัพธ์

    private CardLayout innerLayout;
    private JPanel     innerContainer;

    // ---- Question Panel widgets ----
    private JLabel  qStatusLabel;   // "ข้อ 1/3  |  ถูก: 0"
    private JLabel  qJobLabel;      // ชื่องาน
    private JLabel  qTextLabel;     // โจทย์
    private JButton[] choiceBtns;
    private JLabel  qFeedbackLabel; // ถูก/ผิด + อธิบาย

    // ---- Result Panel widgets ----
    private JLabel resultTitle;
    private JLabel resultDetail;
    private JLabel resultReward;

    // ---- Status bar ----
    private JLabel statusLabel;

    // ============================================================
    public WorkGame_ui(CardLayout cardLayout, JPanel mainContainer, GameLogic gameLogic) {
        this.cardLayout     = cardLayout;
        this.mainContainer  = mainContainer;
        this.workLogic      = new WorkGameLogic(gameLogic);

        setLayout(null);
        setBackground(new Color(255, 240, 248));

        buildStatusBar();
        buildInnerContainer();
    }

    // ============================================================
    //  Status Bar (บนสุด)
    // ============================================================
    private void buildStatusBar() {
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(200, 60, 130));
        statusLabel.setBounds(0, 0, 1200, 48);
        refreshStatus();
        add(statusLabel);
    }

    // ============================================================
    //  Inner Container (สลับหน้าย่อย)
    // ============================================================
    private void buildInnerContainer() {
        innerLayout    = new CardLayout();
        innerContainer = new JPanel(innerLayout);
        innerContainer.setBounds(0, 48, 1200, 752);
        innerContainer.setOpaque(false);

        selectPanel   = buildSelectPanel();
        questionPanel = buildQuestionPanel();
        resultPanel   = buildResultPanel();

        innerContainer.add(selectPanel,   "SELECT");
        innerContainer.add(questionPanel, "QUESTION");
        innerContainer.add(resultPanel,   "RESULT");

        innerLayout.show(innerContainer, "SELECT");
        add(innerContainer);
    }

    // ============================================================
    //  หน้าเลือกงาน
    // ============================================================
    private JPanel buildSelectPanel() {
        JPanel p = new JPanel(null);
        p.setOpaque(false);

        JLabel title = new JLabel("เลือกประเภทงาน", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 46));
        title.setForeground(new Color(210, 60, 120));
        title.setBounds(0, 30, 1200, 70);
        p.add(title);

        JLabel sub = new JLabel("ทำงาน 3 ข้อ ตอบถูกครบได้ 50 บาท  |  วันนี้เหลืออีก ? รอบ", SwingConstants.CENTER);
        sub.setFont(new Font("Tahoma", Font.PLAIN, 20));
        sub.setForeground(new Color(120, 120, 120));
        sub.setBounds(0, 105, 1200, 32);
        p.add(sub);

        // เก็บ ref เพื่ออัปเดต
        p.setName("selectPanel");

        // cards งาน 3 ใบ
        String[] jobNames = {"Logic Gate", "คณิตศาสตร์", "ฟิสิกส์เวกเตอร์"};
        String[] jobDescs = {
            "NOT / AND / NAND / NOR\nXOR / XNOR  (12 แบบ สุ่ม 3 ข้อ)",
            "+ − × ÷  (10 แบบ สุ่ม 3 ข้อ)",
            "เวกเตอร์ 2D  (10 แบบ สุ่ม 3 ข้อ)"
        };
        String[] jobIcons = {"⚙️", "🔢", "📐"};
        int cardW = 290, cardH = 360;
        int totalW = 3 * cardW + 2 * 50;
        int startX = (1200 - totalW) / 2;

        for (int i = 0; i < 3; i++) {
            final int jobType = i + 1;
            JPanel card = makeJobCard(jobIcons[i], jobNames[i], jobDescs[i], jobType);
            card.setBounds(startX + i * (cardW + 50), 160, cardW, cardH);
            p.add(card);
        }

        // ปุ่มกลับ
        JButton backBtn = makeBtn("← กลับ");
        backBtn.setBounds(50, 680, 170, 50);
        backBtn.addActionListener(e -> cardLayout.show(mainContainer, "GAMEPLAY"));
        p.add(backBtn);

        return p;
    }

    private JPanel makeJobCard(String icon, String name, String desc, int jobType) {
        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 105, 180), 3, true),
                new EmptyBorder(10, 10, 10, 10)));

        JLabel iconLbl = new JLabel(icon, SwingConstants.CENTER);
        iconLbl.setFont(new Font("Tahoma", Font.PLAIN, 60));
        iconLbl.setBounds(0, 20, 290, 80);
        card.add(iconLbl);

        JLabel nameLbl = new JLabel(name, SwingConstants.CENTER);
        nameLbl.setFont(new Font("Tahoma", Font.BOLD, 26));
        nameLbl.setForeground(new Color(200, 60, 110));
        nameLbl.setBounds(0, 110, 290, 40);
        card.add(nameLbl);

        JLabel descLbl = new JLabel("<html><div style='text-align:center;color:#888'>" +
                desc.replace("\n", "<br>") + "</div></html>", SwingConstants.CENTER);
        descLbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
        descLbl.setBounds(10, 155, 270, 80);
        card.add(descLbl);

        JLabel rewardLbl = new JLabel("💰 รางวัล: 50 บาท / รอบ", SwingConstants.CENTER);
        rewardLbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
        rewardLbl.setForeground(new Color(30, 140, 30));
        rewardLbl.setBounds(0, 240, 290, 28);
        card.add(rewardLbl);

        JButton startBtn = makeBtn("เลือกงานนี้");
        startBtn.setBounds(30, 283, 230, 50);
        startBtn.addActionListener(e -> handleStartRound(jobType));
        card.add(startBtn);

        return card;
    }

    // ============================================================
    //  หน้าถาม-ตอบ
    // ============================================================
    private JPanel buildQuestionPanel() {
        JPanel p = new JPanel(null);
        p.setOpaque(false);

        qJobLabel = new JLabel("", SwingConstants.CENTER);
        qJobLabel.setFont(new Font("Tahoma", Font.BOLD, 32));
        qJobLabel.setForeground(new Color(200, 60, 120));
        qJobLabel.setBounds(0, 20, 1200, 50);
        p.add(qJobLabel);

        qStatusLabel = new JLabel("", SwingConstants.CENTER);
        qStatusLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
        qStatusLabel.setForeground(new Color(100, 100, 100));
        qStatusLabel.setBounds(0, 75, 1200, 32);
        p.add(qStatusLabel);

        // กล่องโจทย์
        qTextLabel = new JLabel("", SwingConstants.CENTER);
        qTextLabel.setFont(new Font("Tahoma", Font.BOLD, 26));
        qTextLabel.setForeground(new Color(40, 40, 40));
        qTextLabel.setOpaque(true);
        qTextLabel.setBackground(Color.WHITE);
        qTextLabel.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 105, 180), 3, true),
                new EmptyBorder(18, 20, 18, 20)));
        qTextLabel.setBounds(150, 120, 900, 110);
        p.add(qTextLabel);

        // ปุ่มตัวเลือก 4 ข้อ (2×2)
        choiceBtns = new JButton[4];
        String[] prefixes = {"A", "B", "C", "D"};
        int bW = 420, bH = 70;
        int[][] pos = {{100, 260}, {680, 260}, {100, 350}, {680, 350}};
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            choiceBtns[i] = makeBtn("");
            choiceBtns[i].setFont(new Font("Tahoma", Font.BOLD, 20));
            choiceBtns[i].setBounds(pos[i][0], pos[i][1], bW, bH);
            choiceBtns[i].addActionListener(e -> handleAnswer(idx));
            p.add(choiceBtns[i]);
        }

        // label feedback
        qFeedbackLabel = new JLabel("", SwingConstants.CENTER);
        qFeedbackLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        qFeedbackLabel.setBounds(100, 440, 1000, 220);
        qFeedbackLabel.setOpaque(true);
        qFeedbackLabel.setBackground(new Color(255, 250, 255));
        qFeedbackLabel.setBorder(new LineBorder(new Color(200, 150, 200), 2, true));
        qFeedbackLabel.setVisible(false);
        p.add(qFeedbackLabel);

        // ปุ่มถัดไป (ซ่อนไว้ก่อน)
        JButton nextBtn = makeBtn("ถัดไป →");
        nextBtn.setName("nextBtn");
        nextBtn.setBounds(500, 680, 200, 52);
        nextBtn.setVisible(false);
        nextBtn.addActionListener(e -> handleNext(nextBtn));
        p.add(nextBtn);

        return p;
    }

    // ============================================================
    //  หน้าผลลัพธ์
    // ============================================================
    private JPanel buildResultPanel() {
        JPanel p = new JPanel(null);
        p.setOpaque(false);

        resultTitle = new JLabel("", SwingConstants.CENTER);
        resultTitle.setFont(new Font("Tahoma", Font.BOLD, 56));
        resultTitle.setBounds(0, 160, 1200, 90);
        p.add(resultTitle);

        resultDetail = new JLabel("", SwingConstants.CENTER);
        resultDetail.setFont(new Font("Tahoma", Font.PLAIN, 28));
        resultDetail.setForeground(new Color(80, 80, 80));
        resultDetail.setBounds(0, 270, 1200, 50);
        p.add(resultDetail);

        resultReward = new JLabel("", SwingConstants.CENTER);
        resultReward.setFont(new Font("Tahoma", Font.BOLD, 36));
        resultReward.setBounds(0, 340, 1200, 60);
        p.add(resultReward);

        JButton againBtn = makeBtn("ทำงานอีกรอบ");
        againBtn.setBounds(350, 460, 220, 58);
        againBtn.addActionListener(e -> {
            innerLayout.show(innerContainer, "SELECT");
            refreshStatus();
        });
        p.add(againBtn);

        JButton homeBtn = makeBtn("กลับ Gameplay");
        homeBtn.setBounds(630, 460, 220, 58);
        homeBtn.addActionListener(e -> cardLayout.show(mainContainer, "GAMEPLAY"));
        p.add(homeBtn);

        return p;
    }

    // ============================================================
    //  Handlers
    // ============================================================
    private void handleStartRound(int jobType) {
        WorkGameLogic.StartResult r = workLogic.startRound(jobType);

        switch (r) {
            case NO_ROUNDS_LEFT:
                JOptionPane.showMessageDialog(this,
                    "❌ ทำงานครบ " + WorkGameLogic.MAX_ROUNDS_PER_DAY + " รอบแล้ววันนี้!\nมาใหม่พรุ่งนี้นะครับ",
                    "หมดโควต้า", JOptionPane.WARNING_MESSAGE);
                return;
            case NOT_ENOUGH_ENERGY:
                JOptionPane.showMessageDialog(this,
                    "❌ พลังงานไม่พอ! ต้องการ " + WorkGameLogic.ENERGY_COST + "\nลองนอนพักก่อนนะครับ",
                    "พลังงานไม่พอ", JOptionPane.WARNING_MESSAGE);
                return;
            case OK:
                qJobLabel.setText(WorkGameLogic.getJobName(jobType));
                refreshStatus();
                loadCurrentQuestion();
                innerLayout.show(innerContainer, "QUESTION");
                break;
            default:
                break;
        }
    }

    private void loadCurrentQuestion() {
        WorkQuestion q = workLogic.getCurrentQuestion();
        if (q == null) return;

        int qIdx   = workLogic.getCurrentQuestionIndex() + 1;
        int total  = workLogic.getTotalQuestions();
        int correct= workLogic.getCorrectCount();

        qStatusLabel.setText(String.format("ข้อที่ %d / %d   |   ✅ ถูก: %d", qIdx, total, correct));
        qTextLabel.setText("<html><div style='text-align:center'>" + q.question.replace("\n","<br>") + "</div></html>");

        String[] prefixes = {"A) ", "B) ", "C) ", "D) "};
        for (int i = 0; i < 4; i++) {
            choiceBtns[i].setText(prefixes[i] + q.choices[i]);
            choiceBtns[i].setEnabled(true);
            choiceBtns[i].setBackground(Color.WHITE);
            choiceBtns[i].setForeground(new Color(255, 105, 180));
        }

        qFeedbackLabel.setVisible(false);

        // ซ่อนปุ่มถัดไป
        findNextBtn().setVisible(false);
    }

    private void handleAnswer(int choiceIdx) {
        WorkQuestion q    = workLogic.getCurrentQuestion();
        boolean     correct = workLogic.answerQuestion(choiceIdx);

        // ไฮไลต์ปุ่ม
        for (int i = 0; i < 4; i++) {
            choiceBtns[i].setEnabled(false);
            if (i == q.correctIndex) {
                choiceBtns[i].setBackground(new Color(144, 238, 144)); // เขียว
                choiceBtns[i].setForeground(new Color(0, 100, 0));
            } else if (i == choiceIdx && !correct) {
                choiceBtns[i].setBackground(new Color(255, 160, 160)); // แดง
                choiceBtns[i].setForeground(new Color(150, 0, 0));
            }
        }

        // feedback
        String fb = correct
            ? "<html><div style='text-align:center;color:green;font-size:18px'>✅ ถูกต้อง!<br><br>" + q.explanation + "</div></html>"
            : "<html><div style='text-align:center;color:red;font-size:18px'>❌ ผิด!<br><br>เฉลย: " + q.explanation + "</div></html>";
        qFeedbackLabel.setText(fb);
        qFeedbackLabel.setVisible(true);

        // แสดงปุ่มถัดไป
        JButton nb = findNextBtn();
        nb.setText(workLogic.hasNextQuestion() ? "ถัดไป →" : "ดูผลลัพธ์ →");
        nb.setVisible(true);
    }

    private void handleNext(JButton nextBtn) {
        if (workLogic.hasNextQuestion()) {
            loadCurrentQuestion();
        } else {
            // จบรอบ
            int earned = workLogic.finishRound();
            refreshStatus();
            showResult(earned);
        }
    }

    private void showResult(int earned) {
        int correct = workLogic.getCorrectCount();
        int total   = workLogic.getTotalQuestions();

        if (earned > 0) {
            resultTitle.setText("🎉 ยอดเยี่ยม!");
            resultTitle.setForeground(new Color(30, 150, 30));
        } else {
            resultTitle.setText("😅 พยายามต่อไป!");
            resultTitle.setForeground(new Color(200, 80, 80));
        }

        resultDetail.setText(String.format("ตอบถูก %d / %d ข้อ", correct, total));
        resultReward.setText(earned > 0
            ? "💰 ได้รับ " + earned + " บาท!"
            : "ตอบถูกครบ 3 ข้อถึงจะได้รับเงินนะครับ");
        resultReward.setForeground(earned > 0 ? new Color(30, 130, 30) : new Color(150, 80, 80));

        innerLayout.show(innerContainer, "RESULT");
    }

    // ============================================================
    //  Helper
    // ============================================================
    private JButton findNextBtn() {
        for (Component c : questionPanel.getComponents()) {
            if (c instanceof JButton && "nextBtn".equals(c.getName())) return (JButton) c;
        }
        return new JButton(); // fallback
    }

    public void refreshStatus() {
        if (statusLabel != null) statusLabel.setText(workLogic.getStatusText());
    }

    private JButton makeBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Tahoma", Font.BOLD, 20));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(255, 105, 180));
        btn.setBorder(new LineBorder(new Color(255, 105, 180), 2, true));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
