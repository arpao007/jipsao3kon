import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import javax.swing.*;

public class GameplayPanel extends JPanel {

    // ===== required refs from main app =====
    private final CardLayout cardLayout;
    private final JPanel mainContainer;
    private final GameLogic logic;

    // ===== state =====
    private LocalDate gameDate = LocalDate.now();
    private JWindow hamburgerPopup;

    
    // ใช้ปิดเมนูเมื่อคลิกนอก popup (กัน memory leak)
    private transient AWTEventListener hamburgerOutsideListener;
// ===== simple UI =====
    private final JTextArea logArea = new JTextArea();

    public GameplayPanel(CardLayout cardLayout, JPanel mainContainer, GameLogic logic) {
        this.cardLayout = cardLayout;
        this.mainContainer = mainContainer;
        this.logic = logic;

        setLayout(new BorderLayout());
        setBackground(new Color(0xFFF7FB));

        // Top bar
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(0xFFE1F0));
        top.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel title = new JLabel("GAMEPLAY");
        title.setFont(new Font("Tahoma", Font.BOLD, 18));
        title.setForeground(new Color(0x5A3060));

        JButton hamburgerBtn = new JButton("☰");
        hamburgerBtn.setFont(new Font("Tahoma", Font.BOLD, 18));
        hamburgerBtn.setFocusPainted(false);
        hamburgerBtn.setBorderPainted(false);
        hamburgerBtn.setContentAreaFilled(false);
        hamburgerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        hamburgerBtn.addActionListener(e -> toggleHamburgerMenu(hamburgerBtn));

        top.add(title, BorderLayout.WEST);
        top.add(hamburgerBtn, BorderLayout.EAST);

        // Center (placeholder game view)
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JLabel hint = new JLabel("<html><div style='text-align:center'>"
                + "หน้านี้คือ GameplayPanel<br>"
                + "เมนู ☰ เลือก '🏠 กลับบ้าน' เพื่อเริ่มวันใหม่"
                + "</div></html>");
        hint.setFont(new Font("Tahoma", Font.PLAIN, 14));
        hint.setForeground(new Color(0x4A2060));
        center.add(hint);

        // Bottom log
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setText("LOG:\n");
        JScrollPane sp = new JScrollPane(logArea);
        sp.setPreferredSize(new Dimension(10, 160));

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(sp, BorderLayout.SOUTH);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Hamburger Menu Logic
    // ════════════════════════════════════════════════════════════════════════════

    private void toggleHamburgerMenu(JButton anchor) {
        if (hamburgerPopup != null && hamburgerPopup.isVisible()) {
            return;
        }
        showHamburgerMenu(anchor);
    }

    private void showHamburgerMenu(JComponent anchor) {
    private void openShopDialog(Window owner) {
        JDialog dialog = (owner instanceof JFrame)
                ? new JDialog((JFrame) owner, "🛍️ ร้านค้า", true)
                : new JDialog((Dialog) owner, "🛍️ ร้านค้า", true);

        Shoplogic shopLogic = new Shoplogic(logic);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(0xFFF0F5));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0xE91E8C));
        header.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        JLabel titleLbl = new JLabel("🛍️  ร้านค้า");
        titleLbl.setFont(new Font("Tahoma", Font.BOLD, 24));
        titleLbl.setForeground(Color.WHITE);
        JButton closeBtn = makeDialogCloseBtn(dialog::dispose);
        header.add(titleLbl, BorderLayout.WEST);
        header.add(closeBtn, BorderLayout.EAST);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 7));
        statusBar.setBackground(new Color(0xFCE4EC));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xF8BBD0)));
        JLabel[] statusLabels = {
                makeStatusLbl("💰", logic.getMoney() + " บาท", new Color(0x880E4F)),
                makeStatusLbl("⚡", logic.getEnergy() + "/" + logic.getMaxEnergy(), new Color(0x880E4F)),
                makeStatusLbl("💝", logic.getCurrentAffection() + "/100", new Color(0x880E4F)),
        };
        for (JLabel l : statusLabels) statusBar.add(l);

        JPanel grid = new JPanel(new GridLayout(2, 2, 14, 14));
        grid.setBackground(new Color(0xFFF0F5));
        grid.setBorder(BorderFactory.createEmptyBorder(18, 22, 14, 22));

        Object[][] shopItems = {
                {"🌹", "ดอกกุหลาบ", "สัญลักษณ์แห่งความรัก ❤️", 150, 15, 5, "rose"},
                {"🍫", "ช็อคโกแลต", "หวานละมุน ใจเขาอ่อน 💕", 80, 8, 2, "choco"},
                {"🍿", "ขนมกินเล่น", "กินด้วยกันสนุกๆ 😄", 30, 3, 1, "snack"},
                {"🌸", "ช่อดอกไม้", "สวยงาม หอมกรุ่น 🌺", 200, 25, 8, "bouquet"},
        };

        for (Object[] it : shopItems) {
            grid.add(makeShopCard(
                    (String) it[0], (String) it[1], (String) it[2],
                    (int) it[3], (int) it[4], (int) it[5], (String) it[6],
                    shopLogic, statusLabels
            ));
        }

        root.add(header, BorderLayout.NORTH);
        root.add(grid, BorderLayout.CENTER);
        root.add(statusBar, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setSize(640, 490);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);

        refreshUI();
    }

    private JPanel makeShopCard(String emoji, String name, String desc,
                                int price, int aff, int eng, String id,
                                Shoplogic shopLogic, JLabel[] statusLabels) {

        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xF8BBD0), 1, true),
                BorderFactory.createEmptyBorder(13, 13, 11, 13)
        ));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        top.setOpaque(false);
        JLabel em = new JLabel(emoji);
        em.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        JLabel nm = new JLabel(name);
        nm.setFont(new Font("Tahoma", Font.BOLD, 16));
        nm.setForeground(new Color(0x2D2D2D));
        top.add(em);
        top.add(nm);

        JLabel dl = new JLabel(desc, SwingConstants.CENTER);
        dl.setFont(new Font("Tahoma", Font.PLAIN, 12));
        dl.setForeground(new Color(0x888888));

        JPanel tags = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
        tags.setOpaque(false);
        tags.add(makeSmallTag("💝 +" + aff, new Color(0xE91E8C)));
        tags.add(makeSmallTag("⚡ -" + eng, new Color(0xFF8F00)));

        JPanel bot = new JPanel(new BorderLayout(6, 0));
        bot.setOpaque(false);
        JLabel pr = new JLabel("฿ " + price);
        pr.setFont(new Font("Tahoma", Font.BOLD, 20));
        pr.setForeground(new Color(0xFFB300));

        JButton buyBtn = new JButton("ซื้อ");
        buyBtn.setFont(new Font("Tahoma", Font.BOLD, 13));
        buyBtn.setBackground(new Color(0xE91E8C));
        buyBtn.setForeground(Color.WHITE);
        buyBtn.setFocusPainted(false);
        buyBtn.setBorderPainted(false);
        buyBtn.setPreferredSize(new Dimension(66, 30));
        buyBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buyBtn.addActionListener(e -> {
            Shoplogic.ShopItem target = null;
            for (Shoplogic.ShopItem it : shopLogic.getShopItems()) {
                if (it.id.equals(id)) { target = it; break; }
            }
            if (target == null) target = new Shoplogic.ShopItem(id, name, price, aff, eng);

            Shoplogic.Result r = shopLogic.buyItem(target);
            if (r == Shoplogic.Result.SUCCESS) {
                addLog("🛍️ ซื้อ " + name + " → 💝 +" + aff);
                spawnHearts(aff);
                JOptionPane.showMessageDialog(this,
                        "✅ ซื้อ " + name + " สำเร็จ!\n💝 ความสัมพันธ์ +" + aff,
                        "สำเร็จ 🎉", JOptionPane.INFORMATION_MESSAGE);
            } else if (r == Shoplogic.Result.NOT_ENOUGH_MONEY) {
                JOptionPane.showMessageDialog(this,
                        "❌ เงินไม่พอ!  ต้องการ ฿" + price + "  มี ฿" + logic.getMoney(),
                        "ไม่สำเร็จ", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "❌ พลังงานไม่พอ!  ต้องการ ⚡" + eng + "  มี ⚡" + logic.getEnergy(),
                        "ไม่สำเร็จ", JOptionPane.WARNING_MESSAGE);
            }

            statusLabels[0].setText("💰 " + logic.getMoney() + " บาท");
            statusLabels[1].setText("⚡ " + logic.getEnergy() + "/" + logic.getMaxEnergy());
            statusLabels[2].setText("💝 " + logic.getCurrentAffection() + "/100");
            refreshUI();
        });

        bot.add(pr, BorderLayout.WEST);
        bot.add(buyBtn, BorderLayout.EAST);

        JPanel mid = new JPanel();
        mid.setOpaque(false);
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
        dl.setAlignmentX(CENTER_ALIGNMENT);
        tags.setAlignmentX(CENTER_ALIGNMENT);
        mid.add(dl);
        mid.add(tags);

        card.add(top, BorderLayout.NORTH);
        card.add(mid, BorderLayout.CENTER);
        card.add(bot, BorderLayout.SOUTH);
        return card;
    }

    // ────────────────────────────────────────────────────────────
    //  Work Dialog
    // ────────────────────────────────────────────────────────────

    private void openWorkDialog(Window owner) {
        JDialog dialog = (owner instanceof JFrame)
                ? new JDialog((JFrame) owner, "💼 ทำงาน", true)
                : new JDialog((Dialog) owner, "💼 ทำงาน", true);

        WorkGameLogic workLogic = new WorkGameLogic(logic);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(0xF0F8FF));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x1565C0));
        header.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        JLabel titleLbl = new JLabel("💼  เลือกงานทำ");
        titleLbl.setFont(new Font("Tahoma", Font.BOLD, 24));
        titleLbl.setForeground(Color.WHITE);
        JButton closeBtn = makeDialogCloseBtn(dialog::dispose);
        header.add(titleLbl, BorderLayout.WEST);
        header.add(closeBtn, BorderLayout.EAST);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 7));
        statusBar.setBackground(new Color(0xE3F2FD));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xBBDEFB)));
        JLabel moneyStatusLbl = makeStatusLbl("💰", logic.getMoney() + " บาท", new Color(0x0D47A1));
        JLabel energyStatusLbl = makeStatusLbl("⚡", logic.getEnergy() + "/" + logic.getMaxEnergy(), new Color(0x0D47A1));
        JLabel roundsStatusLbl = makeStatusLbl("💼", "งานวันนี้: " + workLogic.getRoundsPlayedToday()
                + "/" + WorkGameLogic.MAX_ROUNDS_PER_DAY, new Color(0x0D47A1));
        statusBar.add(moneyStatusLbl);
        statusBar.add(energyStatusLbl);
        statusBar.add(roundsStatusLbl);

        JPanel jobList = new JPanel();
        jobList.setLayout(new BoxLayout(jobList, BoxLayout.Y_AXIS));
        jobList.setBackground(new Color(0xF0F8FF));
        jobList.setBorder(BorderFactory.createEmptyBorder(18, 36, 14, 36));

        Object[][] jobs = {
                {1, "🏪", "พนักงานพาร์ทไทม์", "ตอบคำถาม Logic Gate 3 ข้อ — ผ่านครบได้เงิน", 50, 20},
                {2, "📚", "พนักงานร้านหนังสือ", "ตอบโจทย์คณิตศาสตร์ 3 ข้อ — ผ่านครบได้เงิน", 50, 20},
                {3, "🏬", "พนักงานเซเว่น", "ตอบฟิสิกส์เวกเตอร์ 3 ข้อ — ผ่านครบได้เงิน", 50, 20},
        };

        for (Object[] j : jobs) {
            jobList.add(makeJobCard(
                    (int) j[0], (String) j[1], (String) j[2], (String) j[3],
                    (int) j[4], (int) j[5],
                    workLogic, dialog,
                    moneyStatusLbl, energyStatusLbl, roundsStatusLbl
            ));
            jobList.add(Box.createVerticalStrut(12));
        }

        root.add(header, BorderLayout.NORTH);
        root.add(jobList, BorderLayout.CENTER);
        root.add(statusBar, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setSize(560, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);

        refreshUI();
    }

    private JPanel makeJobCard(int jobType, String emoji, String name, String desc,
                               int pay, int eng, WorkGameLogic workLogic, JDialog parentDialog,
                               JLabel moneyLbl, JLabel energyLbl, JLabel roundsLbl) {

        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xBBDEFB), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel emLbl = new JLabel(emoji);
        emLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        emLbl.setPreferredSize(new Dimension(52, 52));

        JPanel mid = new JPanel();
        mid.setOpaque(false);
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Tahoma", Font.BOLD, 16));
        nameLbl.setForeground(new Color(0x0D47A1));
        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(new Font("Tahoma", Font.PLAIN, 12));
        descLbl.setForeground(new Color(0x666666));
        JPanel tags = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        tags.setOpaque(false);
        tags.add(makeSmallTag("💰 +" + pay + " บาท", new Color(0xF57F17)));
        tags.add(makeSmallTag("⚡ -" + eng, new Color(0x1565C0)));
        tags.add(makeSmallTag("📋 3 ข้อ", new Color(0x6A1B9A)));
        mid.add(nameLbl);
        mid.add(Box.createVerticalStrut(3));
        mid.add(descLbl);
        mid.add(tags);

        JButton startBtn = new JButton("เริ่มงาน");
        startBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        startBtn.setBackground(new Color(0x1565C0));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setBorderPainted(false);
        startBtn.setPreferredSize(new Dimension(88, 36));
        startBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startBtn.addActionListener(e -> {
            WorkGameLogic.StartResult r = workLogic.startRound(jobType);
            if (r == WorkGameLogic.StartResult.NO_ROUNDS_LEFT) {
                JOptionPane.showMessageDialog(parentDialog,
                        "ทำงานครบ " + WorkGameLogic.MAX_ROUNDS_PER_DAY + " รอบแล้ววันนี้!",
                        "หมดโควต้า", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (r == WorkGameLogic.StartResult.NOT_ENOUGH_ENERGY) {
                JOptionPane.showMessageDialog(parentDialog,
                        "พลังงานไม่พอ! ต้องการ ⚡" + WorkGameLogic.ENERGY_COST
                                + "  มี ⚡" + logic.getEnergy(),
                        "พลังงานไม่พอ", JOptionPane.WARNING_MESSAGE);
                return;
            }

            runQuizInDialog(workLogic, name, parentDialog);

            moneyLbl.setText("💰 " + logic.getMoney() + " บาท");
            energyLbl.setText("⚡ " + logic.getEnergy() + "/" + logic.getMaxEnergy());
            roundsLbl.setText("💼 งานวันนี้: " + workLogic.getRoundsPlayedToday()
                    + "/" + WorkGameLogic.MAX_ROUNDS_PER_DAY);
            refreshUI();
        });

        card.add(emLbl, BorderLayout.WEST);
        card.add(mid, BorderLayout.CENTER);
        card.add(startBtn, BorderLayout.EAST);
        return card;
    }

    private void runQuizInDialog(WorkGameLogic workLogic, String jobName, JDialog parent) {
        JDialog quiz = new JDialog(parent, jobName + " — แบบทดสอบ", true);
        quiz.setSize(500, 360);
        quiz.setLocationRelativeTo(parent);
        quiz.setResizable(false);

        final int[] correct = {0};
        final int total = workLogic.getTotalQuestions();

        JPanel qRoot = new JPanel(new BorderLayout(0, 10));
        qRoot.setBackground(new Color(0xF8F9FF));
        qRoot.setBorder(BorderFactory.createEmptyBorder(22, 26, 18, 26));

        JLabel progressLbl = new JLabel("", SwingConstants.CENTER);
        progressLbl.setFont(new Font("Tahoma", Font.BOLD, 13));
        progressLbl.setForeground(new Color(0x666666));

        JLabel qLbl = new JLabel("", SwingConstants.CENTER);
        qLbl.setFont(new Font("Tahoma", Font.BOLD, 17));
        qLbl.setForeground(new Color(0x0D47A1));

        JPanel choicesGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        choicesGrid.setOpaque(false);

        JLabel feedbackLbl = new JLabel(" ", SwingConstants.CENTER);
        feedbackLbl.setFont(new Font("Tahoma", Font.BOLD, 14));

        JButton nextBtn = new JButton("ถัดไป ▶");
        nextBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        nextBtn.setBackground(new Color(0x1565C0));
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFocusPainted(false);
        nextBtn.setBorderPainted(false);
        nextBtn.setVisible(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(progressLbl, BorderLayout.NORTH);
        top.add(qLbl, BorderLayout.CENTER);

        JPanel bot = new JPanel(new BorderLayout());
        bot.setOpaque(false);
        bot.add(feedbackLbl, BorderLayout.CENTER);
        bot.add(nextBtn, BorderLayout.EAST);

        qRoot.add(top, BorderLayout.NORTH);
        qRoot.add(choicesGrid, BorderLayout.CENTER);
        qRoot.add(bot, BorderLayout.SOUTH);
        quiz.setContentPane(qRoot);

        Runnable loadQ = new Runnable() {
            @Override public void run() {
                if (!workLogic.hasNextQuestion()) {
                    int earned = workLogic.finishRound();
                    String msg = earned > 0
                            ? "🎉 ผ่าน! ตอบถูก " + correct[0] + "/" + total + "\nได้รับเงิน +" + earned + " บาท"
                            : "😢 ไม่ผ่าน  ตอบถูก " + correct[0] + "/" + total + "\nไม่ได้รับเงิน";
                    if (earned > 0) addLog("💼 ทำงานสำเร็จ +" + earned + " บาท");
                    JOptionPane.showMessageDialog(quiz, msg,
                            earned > 0 ? "ผ่าน 🎉" : "ไม่ผ่าน", JOptionPane.INFORMATION_MESSAGE);
                    quiz.dispose();
                    return;
                }

                WorkQuestion q = workLogic.getCurrentQuestion();
                progressLbl.setText("ข้อที่ " + (workLogic.getCurrentQuestionIndex() + 1) + " / " + total
                        + "   |   " + WorkGameLogic.getJobName(workLogic.getCurrentJobType()));
                qLbl.setText("<html><div style='text-align:center'>"
                        + q.question.replace("\n", "<br>") + "</div></html>");

                choicesGrid.removeAll();
                feedbackLbl.setText(" ");
                nextBtn.setVisible(false);

                for (int i = 0; i < q.choices.length; i++) {
                    final int ci = i;
                    JButton cb = new JButton((char) ('A' + i) + ". " + q.choices[i]);
                    cb.setFont(new Font("Tahoma", Font.PLAIN, 14));
                    cb.setBackground(Color.WHITE);
                    cb.setFocusPainted(false);

                    Runnable self = this;
                    cb.addActionListener(ev -> {
                        for (Component c : choicesGrid.getComponents()) c.setEnabled(false);

                        boolean ok = workLogic.answerQuestion(ci);
                        if (ok) correct[0]++;

                        feedbackLbl.setText(ok ? "✅ ถูกต้อง!" : "❌ ผิด!");
                        feedbackLbl.setForeground(ok ? new Color(0x1B5E20) : new Color(0xB71C1C));

                        nextBtn.setVisible(true);
                        nextBtn.setText(workLogic.hasNextQuestion() ? "ถัดไป ▶" : "ดูผล 🏆");
                        for (ActionListener al : nextBtn.getActionListeners()) nextBtn.removeActionListener(al);
                        nextBtn.addActionListener(nev -> self.run());
                    });

                    choicesGrid.add(cb);
                }

                choicesGrid.revalidate();
                choicesGrid.repaint();
            }
        };

        loadQ.run();
        quiz.setVisible(true);
    }

    // ────────────────────────────────────────────────────────────
    //  Helper Methods
    // ────────────────────────────────────────────────────────────

    private JButton makeDialogCloseBtn(Runnable onClose) {
        JButton btn = new JButton("✕");
        btn.setFont(new Font("Arial", Font.BOLD, 17));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> onClose.run());
        return btn;
    }

    private JLabel makeStatusLbl(String icon, String text, Color color) {
        JLabel l = new JLabel(icon + " " + text);
        l.setFont(new Font("Tahoma", Font.BOLD, 14));
        l.setForeground(color);
        return l;
    }

    private JLabel makeSmallTag(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.BOLD, 12));
        l.setForeground(color);
        l.setOpaque(true);
        l.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 25));
        l.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
        return l;
    }

    private void addLog(String s) {
        logArea.append(s + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void refreshUI() {
        revalidate();
        repaint();
    }

    private void spawnHearts(int amount) {
        // เอฟเฟกต์หัวใจ (ถ้ามี)
    }
}