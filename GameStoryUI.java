import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;

/**
 * GameStoryUI.java
 * — UI ล่างจอ 40% : location bar + story box + 2x2 choice grid
 * — บน 60% ว่างไว้สำหรับรูปตัวละคร
 */
public class GameStoryUI extends JPanel {

    // ── สี ──
    private static final Color BG_TOP      = new Color(0xF7D6E0);
    private static final Color BG_BOT      = new Color(0xD9AED0);
    private static final Color PINK_DEEP   = new Color(0xE8759A);
    private static final Color PINK_LIGHT  = new Color(0xF5A8C5);
    private static final Color LILAC_DARK  = new Color(0xA076BB);
    private static final Color LILAC       = new Color(0xC9A0DC);
    private static final Color TEXT_DARK   = new Color(0x4A2060);
    private static final Color TEXT_WHITE  = new Color(0xFFF5FA);
    private static final Color BAD_C       = new Color(0xAAAAAA);
    // กรอบ choice สีเดียวกันทุกปุ่ม
    private static final Color CHOICE_BORDER = new Color(0xE8759A);

    // ── State ──
    private final CardLayout cardLayout;
    private final JPanel     mainContainer;
    private final GameLogic  logic;

    private String playerName = "ผู้เล่น";
    private static final String GIRL_NAME = "มีน";
    private int currentChapter = 0;
    private List<StoryData.Chapter> chapters;

    private int affection = 0;
    private static final int MAX_AFFECTION = 100;

    private enum UIState { STORY, CHOICES, REACTION, ENDING }
    private UIState uiState = UIState.STORY;
    private String  reactionText = "";
    private int     lastAffectionDelta = 0;

    // typewriter
    private String targetText = "";
    private int    charIndex  = 0;
    private Timer  typeTimer;

    // components
    private JPanel  infoBar;
    private JPanel  affectionBar;
    private JLabel  locationLbl;
    private JPanel  storyBox;
    private JLabel  storyText;
    private JButton nextBtn;
    private JPanel  choicePanel;
    private JPanel  reactionBox;
    private JLabel  reactionText2;
    private JLabel  deltaLbl;
    private JButton continueBtn;
    private JPanel  endingPanel;
    private JPanel  hamburgerBtn;  // ปุ่ม 3 ขีด
    private JPanel  menuOverlay;   // backdrop + popup
    private boolean menuOpen = false;

    private RL rl;

    // ──────────────────────────────────────────────
    public GameStoryUI(CardLayout cardLayout, JPanel mainContainer, GameLogic logic) {
        this.cardLayout    = cardLayout;
        this.mainContainer = mainContainer;
        this.logic         = logic;
        this.chapters      = StoryData.getAll();
        setLayout(null);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { rebuildUI(); }
            @Override public void componentShown(ComponentEvent e)   { rebuildUI(); }
        });
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing())
                rebuildUI();
        });
    }

    public void startGame(String name) {
        this.playerName     = name;
        this.currentChapter = 0;
        this.affection      = 0;
        this.uiState        = UIState.STORY;
        rebuildUI();
    }

    public void resumeGame(String name, int chapterIdx, int aff) {
        this.playerName     = name;
        this.currentChapter = chapterIdx;
        this.affection      = aff;
        this.uiState        = UIState.STORY;
        rebuildUI();
    }

    // ──────────────────────────────────────────────
    private void rebuildUI() {
        int w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) { SwingUtilities.invokeLater(this::rebuildUI); return; }
        rl = new RL(w, h);
        removeAll();
        buildUI();
        showCurrentState();
        revalidate(); repaint();
    }

    // ──────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int pw = getWidth(), ph = getHeight();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, BG_TOP, 0, ph, BG_BOT));
        g2.fillRect(0, 0, pw, ph);
        // bokeh บน 60%
        g2.setPaint(new RadialGradientPaint(pw/2f, ph*0.3f, ph/4f,
            new float[]{0,1}, new Color[]{new Color(255,200,230,30), new Color(255,200,230,0)}));
        g2.fillOval(pw/4, 0, pw/2, (int)(ph*0.6));
        // เส้นแบ่ง 60/40 บางๆ
        int divY = (int)(ph * 0.60);
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
            0, new float[]{10, 7}, 0));
        g2.setColor(new Color(0xE8759A));
        g2.drawLine(0, divY, pw, divY);
    }

    // ══════════════════════════════════════════════
    //  buildUI — ส่วนล่าง 40%
    // ══════════════════════════════════════════════
    private void buildUI() {
        int w = rl.w, h = rl.h;
        int pad = Math.max(8, w / 90);

        int topArea = (int)(h * 0.60);   // เริ่มต้น UI ล่างที่ 60%
        int botH    = h - topArea;        // ความสูงรวม 40%

        // ── Info bar: location + affection (~10% ของ botH) ──
        int barH = Math.max(30, (int)(botH * 0.10));
        infoBar = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 240, 248, 215));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(CHOICE_BORDER);
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 13, 13);
            }
        };
        infoBar.setOpaque(false);
        infoBar.setBounds(pad, topArea + pad, w - pad*2, barH);
        add(infoBar);

        locationLbl = new JLabel("", SwingConstants.LEFT);
        locationLbl.setFont(new Font("Tahoma", Font.ITALIC, rl.fontSmall));
        locationLbl.setForeground(new Color(0xA070C0));
        locationLbl.setBounds(12, 0, (int)((w-pad*2)*0.48), barH);
        infoBar.add(locationLbl);

        int abarW = (int)((w-pad*2) * 0.44);
        buildAffectionBar(infoBar, (w-pad*2) - abarW - 8, 0, abarW, barH);

        // ── Story box (~32% ของ botH) ──
        int storyY = topArea + pad + barH + 4;
        int storyH = (int)(botH * 0.32);
        int uiW    = w - pad*2;

        storyBox = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 245, 252, 228));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(new Color(0xE0A0C8));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 17, 17);
            }
        };
        storyBox.setOpaque(false);
        storyBox.setBounds(pad, storyY, uiW, storyH);
        add(storyBox);

        storyText = new JLabel();
        storyText.setFont(new Font("Tahoma", Font.PLAIN, rl.fontBody));
        storyText.setForeground(TEXT_DARK);
        storyText.setVerticalAlignment(SwingConstants.TOP);
        storyText.setBounds(12, 8, uiW - 24, storyH - 16);
        storyBox.add(storyText);

        // ── Next button ──
        int nextY = storyY + storyH + 3;
        int nextH = Math.max(24, (int)(botH * 0.07));
        nextBtn = makeRoundBtn("แตะเพื่อดำเนินต่อ ▶", PINK_DEEP, PINK_LIGHT,
            w/2 - 100, nextY, 200, nextH);
        nextBtn.addActionListener(e -> onNextClick());
        add(nextBtn);

        // ── Choice panel 2×2 ──
        int choiceY = nextY + nextH + 4;
        int choiceH = h - choiceY - pad;
        choicePanel = new JPanel(null);
        choicePanel.setOpaque(false);
        choicePanel.setBounds(pad, choiceY, uiW, choiceH);
        add(choicePanel);

        // ── Reaction box — ครอบคลุม choiceH เต็มๆ ──
        // continueBtn อยู่ข้างในที่ย่อหน้าจากขอบล่าง 10px
        int contBtnH = Math.max(30, (int)(choiceH * 0.22));
        int contBtnY = choiceH - contBtnH - 8;   // relative ภายใน reactionBox

        reactionBox = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 248, 240, 232));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(new Color(0xE0A060));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 17, 17);
            }
        };
        reactionBox.setOpaque(false);
        reactionBox.setBounds(pad, choiceY, uiW, choiceH);   // เต็ม choiceH
        add(reactionBox);

        reactionText2 = new JLabel();
        reactionText2.setFont(new Font("Tahoma", Font.PLAIN, rl.fontBody));
        reactionText2.setForeground(new Color(0x7050A0));
        reactionText2.setVerticalAlignment(SwingConstants.TOP);
        reactionText2.setBounds(12, 8, uiW - 24, contBtnY - 12);
        reactionBox.add(reactionText2);

        deltaLbl = new JLabel("", SwingConstants.RIGHT);
        deltaLbl.setFont(new Font("Tahoma", Font.BOLD, rl.fontBody + 1));
        deltaLbl.setBounds(uiW - 110, 6, 96, 24);
        reactionBox.add(deltaLbl);

        // continueBtn อยู่ใต้สุดของ reactionBox
        continueBtn = makeRoundBtn("ดำเนินต่อ ▶", LILAC_DARK, LILAC,
            uiW/2 - 85, contBtnY, 170, contBtnH);
        continueBtn.addActionListener(e -> onContinueClick());
        reactionBox.add(continueBtn);

        // ── Ending panel ──
        endingPanel = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 240, 250, 242));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.setStroke(new BasicStroke(2.5f));
                g2.setColor(PINK_DEEP);
                g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 22, 22);
            }
        };
        endingPanel.setOpaque(false);
        endingPanel.setBounds(pad, storyY, uiW, h - storyY - pad);
        add(endingPanel);

        // ── Hamburger button — มุมขวาบนของ infoBar ──
        int hbSize = barH - 6;
        hamburgerBtn = new JPanel(null) {
            boolean hov = false;
            { setOpaque(false); setCursor(new Cursor(Cursor.HAND_CURSOR));
              addMouseListener(new MouseAdapter() {
                  @Override public void mouseEntered(MouseEvent e){ hov=true; repaint(); }
                  @Override public void mouseExited(MouseEvent e) { hov=false; repaint(); }
                  @Override public void mouseReleased(MouseEvent e){ toggleMenu(); }
              });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // circle bg on hover
                if (hov) {
                    g2.setColor(new Color(0xE8759A, false));
                    g2.fillOval(0, 0, getWidth(), getHeight());
                }
                // 3 ขีด
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(hov ? PINK_DEEP : new Color(0xA076BB));
                int lw = (int)(getWidth() * 0.58);
                int lx = (getWidth() - lw) / 2;
                int[] ly = { (int)(getHeight()*0.28), (int)(getHeight()*0.50), (int)(getHeight()*0.72) };
                for (int ly1 : ly) g2.drawLine(lx, ly1, lx+lw, ly1);
            }
        };
        // วางไว้ใน infoBar ด้านขวาสุด (ก่อน affection bar จะเริ่ม)
        hamburgerBtn.setBounds((w - pad*2) - hbSize - 4, (barH - hbSize)/2, hbSize, hbSize);
        infoBar.add(hamburgerBtn);

        // ── Menu overlay (เต็มจอ, แสดงเมื่อ menuOpen) ──
        menuOverlay = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                // backdrop ดำ 50% ทับทั้งจอ
                Graphics2D g2 = (Graphics2D)g;
                g2.setColor(new Color(0, 0, 0, 130));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        menuOverlay.setOpaque(false);
        menuOverlay.setBounds(0, 0, w, h);
        menuOverlay.setVisible(false);
        // กดพื้นหลังเพื่อปิด
        menuOverlay.addMouseListener(new MouseAdapter() {
            @Override public void mouseReleased(MouseEvent e) {
                // ปิดเฉพาะถ้าคลิกนอก popup
                Component hit = menuOverlay.getComponentAt(e.getPoint());
                if (hit == menuOverlay) closeMenu();
            }
        });
        buildMenuPopup(menuOverlay, w, h);
        add(menuOverlay);
        setComponentZOrder(menuOverlay, 0);  // อยู่บนสุด
    }

    // ──────────────────────────────────────────────
    private void buildAffectionBar(JPanel parent, int x, int y, int w, int h) {
        affectionBar = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int bh = 10, by = (getHeight()-bh)/2;
                g2.setFont(new Font("Tahoma", Font.BOLD, rl!=null?rl.fontSmall:11));
                g2.setColor(LILAC_DARK);
                String lbl = "ความชอบ  ";
                FontMetrics fm = g2.getFontMetrics();
                int lw = fm.stringWidth(lbl);
                g2.drawString(lbl, 0, getHeight()/2 + fm.getAscent()/2 - 2);
                int tx = lw+4, tw = getWidth()-tx-34;
                g2.setColor(new Color(0xE0C0D8));
                g2.fillRoundRect(tx, by, tw, bh, bh, bh);
                int fw = (int)(tw*(affection/(double)MAX_AFFECTION));
                if (fw > 0) {
                    g2.setPaint(new GradientPaint(tx,0,PINK_LIGHT,tx+fw,0,PINK_DEEP));
                    g2.fillRoundRect(tx, by, fw, bh, bh, bh);
                }
                g2.setColor(TEXT_DARK);
                String pct = affection + "%";
                g2.drawString(pct, tx+tw+5, getHeight()/2 + fm.getAscent()/2 - 2);
            }
        };
        affectionBar.setOpaque(false);
        affectionBar.setBounds(x, y, w, h);
        parent.add(affectionBar);
    }

    // ──────────────────────────────────────────────
    private void showCurrentState() {
        if (rl == null) return;
        boolean isStory    = uiState == UIState.STORY;
        boolean isChoices  = uiState == UIState.CHOICES;
        boolean isReaction = uiState == UIState.REACTION;
        boolean isEnding   = uiState == UIState.ENDING;

        infoBar.setVisible(!isEnding);
        storyBox.setVisible(isStory || isChoices);
        nextBtn.setVisible(isStory);

        // ล้าง choicePanel ทุกครั้งที่ไม่ใช่ CHOICES ป้องกันปุ่มเก่าค้างและกดซ้ำ
        if (!isChoices) {
            choicePanel.removeAll();
            choicePanel.revalidate();
            choicePanel.repaint();
        }
        choicePanel.setVisible(isChoices);

        // reactionBox ต้องอยู่บนสุด — raise to front
        reactionBox.setVisible(isReaction);
        if (isReaction) setComponentZOrder(reactionBox, 0);

        endingPanel.setVisible(isEnding);

        if (isStory || isChoices) {
            StoryData.Chapter ch = chapters.get(currentChapter);
            locationLbl.setText("[ " + ch.location + " ]");
            if (isStory) {
                updateStoryText(ch.story);
            }
            if (isChoices) {
                if (typeTimer != null) typeTimer.stop();
                String full = ch.story.replace("{player}", playerName)
                                      .replace("{girl}", GIRL_NAME)
                                      .replace("\n","<br>");
                storyText.setText("<html><body style='width:100%'>" + full + "</body></html>");
                buildChoiceButtons(ch);
            }
        }
        if (isReaction) buildReactionView();
        if (isEnding)   buildEndingView();
        if (affectionBar != null) affectionBar.repaint();
        // ปิด menu overlay ทุกครั้งที่ state เปลี่ยน
        if (menuOverlay != null && menuOpen) closeMenu();
    }

    private void updateStoryText(String raw) {
        String text = raw.replace("{player}", playerName).replace("{girl}", GIRL_NAME);
        targetText  = text;
        charIndex   = 0;
        storyText.setText("");
        if (typeTimer != null) typeTimer.stop();
        typeTimer = new Timer(16, e -> {
            if (charIndex < targetText.length()) {
                charIndex = Math.min(charIndex + 3, targetText.length());
                storyText.setText("<html><body style='width:100%'>" +
                    targetText.substring(0, charIndex).replace("\n","<br>") + "</body></html>");
            } else { ((Timer)e.getSource()).stop(); }
        });
        typeTimer.start();
    }

    // ══════════════════════════════════════════════
    //  Choice buttons — 2×2 grid
    //  กรอบสีเดียวกัน ไม่มี type label
    // ══════════════════════════════════════════════
    private void buildChoiceButtons(StoryData.Chapter ch) {
        choicePanel.removeAll();
        List<StoryData.Choice> list = ch.choices;
        int cpW = choicePanel.getWidth();
        int cpH = choicePanel.getHeight();
        if (cpW <= 0) cpW = rl.w - 20;
        if (cpH <= 0) cpH = 120;

        int gap  = 6;
        int btnW = (cpW - gap) / 2;
        int btnH = (cpH - gap) / 2;

        for (int i = 0; i < Math.min(list.size(), 4); i++) {
            StoryData.Choice choice = list.get(i);
            int col = i % 2;
            int row = i / 2;
            int bx  = col * (btnW + gap);
            int by  = row * (btnH + gap);
            final int idx = i;

            JPanel btn = new JPanel(null) {
                boolean hov = false;
                {
                    setOpaque(false);
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    addMouseListener(new MouseAdapter() {
                        @Override public void mouseEntered(MouseEvent e) { hov=true;  repaint(); }
                        @Override public void mouseExited(MouseEvent e)  { hov=false; repaint(); }
                        @Override public void mouseReleased(MouseEvent e){ onChoiceSelected(ch, idx); }
                    });
                }
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D)g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // พื้นหลัง
                    g2.setColor(hov ? new Color(0xF9D0E4) : new Color(255, 245, 252, 228));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    // กรอบสีเดียวกันทุกปุ่ม
                    g2.setStroke(new BasicStroke(2f));
                    g2.setColor(hov ? PINK_DEEP : CHOICE_BORDER);
                    g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 15, 15);
                    // ข้อความ (ไม่มี prefix [Romantic]/[Good] ฯลฯ)
                    g2.setFont(new Font("Tahoma", Font.PLAIN, rl!=null?rl.fontBody:13));
                    g2.setColor(TEXT_DARK);
                    String ct = choice.text
                        .replace("{player}", playerName)
                        .replace("{girl}", GIRL_NAME);
                    FontMetrics fm = g2.getFontMetrics();
                    // word-wrap ภาษาไทย (pixel-based)
                    java.util.List<String> lines = wrapText(ct, fm, getWidth()-20);
                    int totalH = lines.size() * fm.getHeight();
                    int ty = (getHeight() - totalH) / 2 + fm.getAscent();
                    for (String ln : lines) {
                        int lx = (getWidth() - fm.stringWidth(ln)) / 2;
                        g2.drawString(ln, lx, ty);
                        ty += fm.getHeight();
                    }
                }
            };
            btn.setBounds(bx, by, btnW, btnH);
            choicePanel.add(btn);
        }
        choicePanel.revalidate();
        choicePanel.repaint();
    }

    /** wrap ข้อความภาษาไทย (pixel-based ไม่ใช้ space) */
    private java.util.List<String> wrapText(String text, FontMetrics fm, int maxW) {
        java.util.List<String> lines = new java.util.ArrayList<>();
        if (text == null || text.isEmpty()) return lines;
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            cur.append(text.charAt(i));
            if (fm.stringWidth(cur.toString()) > maxW) {
                if (cur.length() > 1) {
                    lines.add(cur.substring(0, cur.length()-1));
                    cur = new StringBuilder(String.valueOf(text.charAt(i)));
                }
            }
        }
        if (cur.length() > 0) lines.add(cur.toString());
        return lines;
    }

    // flag ป้องกันกดซ้ำ
    private boolean choiceProcessing = false;

    // ──────────────────────────────────────────────
    private void onChoiceSelected(StoryData.Chapter ch, int idx) {
        if (choiceProcessing) return;   // กัน double-click
        choiceProcessing = true;

        // ซ่อน choicePanel ทันที ป้องกันกดซ้ำ
        if (choicePanel != null) {
            choicePanel.setVisible(false);
            choicePanel.removeAll();
        }

        StoryData.Choice choice = ch.choices.get(idx);
        int delta = 0;
        switch (choice.type) {
            case ROMANTIC: delta = (affection >= 40) ? 5 : 1; break;
            case GOOD:     delta = 3;  break;
            case NORMAL:   delta = 2;  break;
            case BAD:      delta = -2; break;
        }
        lastAffectionDelta = delta;
        affection = Math.max(0, Math.min(MAX_AFFECTION, affection + delta));
        if (affectionBar != null) affectionBar.repaint();

        if (choice.reaction == null) {
            choiceProcessing = false;
            advanceChapter();
        } else {
            reactionText = choice.reaction
                .replace("{player}", playerName).replace("{girl}", GIRL_NAME);
            uiState = UIState.REACTION;
            showCurrentState();
            // reset flag หลัง UI เปลี่ยนแล้ว
            choiceProcessing = false;
        }
    }

    private void buildReactionView() {
        String delta = lastAffectionDelta >= 0
            ? "+" + lastAffectionDelta + " ความชอบ"
            : lastAffectionDelta + " ความชอบ";
        deltaLbl.setText(delta);
        deltaLbl.setForeground(lastAffectionDelta >= 0 ? new Color(0x4CAF50) : BAD_C);
        reactionText2.setText("<html><body style='width:100%'>[ " + GIRL_NAME + " ]: " +
            reactionText.replace("\n","<br>") + "</body></html>");
    }

    private void buildEndingView() {
        endingPanel.removeAll();
        int ew = endingPanel.getWidth();
        int eh = endingPanel.getHeight();
        if (ew <= 0) ew = rl.w - 20;
        if (eh <= 0) eh = (int)(rl.h * 0.38);

        boolean success = affection >= 60;
        String emoji = success ? "♡" : "...";
        String title = success ? "สำเร็จแล้ว!" : "ยังไม่พร้อม...";
        String ending = success
            ? "มีนหยุดนิ่งสักครู่ ก่อนจะยิ้มอย่างอบอุ่น\n\n\"หนูก็ชอบ " + playerName
              + " เหมือนกันนะคะ...\"\n\n「 จบบริบูรณ์ — First Love 」\nความชอบ: " + affection + " / 100"
            : "มีนยิ้มเบาๆ\n\n\"ขอเวลาคิดก่อนนะคะ...\"\n\n「 จบ — ลองใหม่อีกครั้งนะ 」\nความชอบ: "
              + affection + " / 100";

        JLabel emojiLbl = new JLabel(emoji, SwingConstants.CENTER);
        emojiLbl.setFont(new Font("Tahoma", Font.BOLD, rl.fontTitle + 6));
        emojiLbl.setForeground(success ? PINK_DEEP : BAD_C);
        emojiLbl.setBounds(0, 10, ew, 48);
        endingPanel.add(emojiLbl);

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Tahoma", Font.BOLD, rl.fontTitle));
        titleLbl.setForeground(success ? PINK_DEEP : BAD_C);
        titleLbl.setBounds(0, 60, ew, 36);
        endingPanel.add(titleLbl);

        JLabel endLbl = new JLabel();
        endLbl.setText("<html><div style='text-align:center;width:100%;'>" +
            ending.replace("\n","<br>") + "</div></html>");
        endLbl.setFont(new Font("Tahoma", Font.PLAIN, rl.fontBody));
        endLbl.setForeground(TEXT_DARK);
        endLbl.setHorizontalAlignment(SwingConstants.CENTER);
        endLbl.setVerticalAlignment(SwingConstants.TOP);
        endLbl.setBounds(20, 104, ew-40, eh-170);
        endingPanel.add(endLbl);

        JPanel menuBtn = makeRoundBtnPanel("← กลับเมนูหลัก", LILAC_DARK, LILAC,
            () -> cardLayout.show(mainContainer, "MENU"));
        menuBtn.setBounds(ew/2-110, eh-60, 220, 40);
        endingPanel.add(menuBtn);

        JPanel restartBtn = makeRoundBtnPanel("เล่นใหม่", PINK_DEEP, PINK_LIGHT,
            () -> startGame(playerName));
        restartBtn.setBounds(ew/2-65, eh-12, 130, 30);
        endingPanel.add(restartBtn);

        endingPanel.revalidate();
        endingPanel.repaint();
    }

    // ──────────────────────────────────────────────
    private void onNextClick() {
        if (charIndex < targetText.length()) {
            if (typeTimer != null) typeTimer.stop();
            charIndex = targetText.length();
            storyText.setText("<html><body style='width:100%'>" +
                targetText.replace("\n","<br>") + "</body></html>");
            return;
        }
        uiState = UIState.CHOICES;
        showCurrentState();
    }

    private void onContinueClick() { advanceChapter(); }

    private void advanceChapter() {
        // reset state บทใหม่
        choiceProcessing = false;
        charIndex = 0;
        targetText = "";
        if (typeTimer != null) { typeTimer.stop(); typeTimer = null; }

        currentChapter++;
        uiState = (currentChapter >= chapters.size()) ? UIState.ENDING : UIState.STORY;
        showCurrentState();
    }

    public int getCurrentChapter() { return currentChapter; }
    public int getAffection()      { return affection; }
    public String getPlayerName()  { return playerName; }

    // ══════════════════════════════════════════════
    //  Hamburger menu
    // ══════════════════════════════════════════════
    private void toggleMenu() {
        menuOpen = !menuOpen;
        if (menuOverlay != null) {
            menuOverlay.setVisible(menuOpen);
            setComponentZOrder(menuOverlay, 0);
            revalidate(); repaint();
        }
    }

    private void closeMenu() {
        menuOpen = false;
        if (menuOverlay != null) {
            menuOverlay.setVisible(false);
            revalidate(); repaint();
        }
    }

    private void buildMenuPopup(JPanel overlay, int pw, int ph) {
        // popup card — อยู่มุมขวา ใต้ infoBar
        int topArea = (int)(ph * 0.60);
        int pad     = Math.max(8, pw / 90);
        int barH    = Math.max(30, (int)((ph - topArea) * 0.10));
        int popW    = Math.max(200, Math.min(260, (int)(pw * 0.22)));
        int itemH   = Math.max(44, (int)((ph - topArea) * 0.13));
        int popH    = itemH * 4 + 20;
        int popX    = pw - pad - popW;
        int popY    = topArea + pad + barH + 4;

        JPanel popup = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // shadow
                g2.setColor(new Color(0,0,0,40));
                g2.fillRoundRect(4, 6, getWidth(), getHeight(), 22, 22);
                // card
                g2.setColor(new Color(255, 245, 252, 248));
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-6, 22, 22);
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(new Color(0xE8759A));
                g2.drawRoundRect(1, 1, getWidth()-6, getHeight()-8, 21, 21);
            }
        };
        popup.setOpaque(false);
        popup.setBounds(popX, popY, popW, popH);
        overlay.add(popup);

        // รายการเมนู
        String[] labels  = { "🏠  กลับบ้าน", "💼  ทำงาน", "🛒  ร้านค้า", "🚪  ออกเกม" };
        Color[]  colors  = {
            new Color(0xA076BB), new Color(0x5B9AD5),
            new Color(0x4CAF50), new Color(0xE8759A)
        };
        Runnable[] actions = {
            () -> { closeMenu(); /* TODO: ไปหน้ากลับบ้าน */ },
            () -> { closeMenu(); /* TODO: ไปหน้าทำงาน  */ },
            () -> { closeMenu(); /* TODO: ไปหน้าร้านค้า */ },
            () -> { closeMenu(); cardLayout.show(mainContainer, "MENU"); }
        };

        for (int i = 0; i < labels.length; i++) {
            final int fi = i;
            final Color fc = colors[i];
            final String fl = labels[i];

            JPanel item = new JPanel(null) {
                boolean hov = false;
                { setOpaque(false); setCursor(new Cursor(Cursor.HAND_CURSOR));
                  addMouseListener(new MouseAdapter() {
                      @Override public void mouseEntered(MouseEvent e){ hov=true; repaint(); }
                      @Override public void mouseExited(MouseEvent e) { hov=false; repaint(); }
                      @Override public void mouseReleased(MouseEvent e){ actions[fi].run(); }
                  });
                }
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D)g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (hov) {
                        g2.setColor(new Color(fc.getRed(), fc.getGreen(), fc.getBlue(), 28));
                        g2.fillRoundRect(4, 2, getWidth()-8, getHeight()-4, 14, 14);
                    }
                    // divider (ยกเว้นอันแรก)
                    if (fi > 0) {
                        g2.setColor(new Color(0xE8759A, false));
                        g2.setStroke(new BasicStroke(0.8f));
                        g2.drawLine(14, 0, getWidth()-14, 0);
                    }
                    g2.setFont(new Font("Tahoma", Font.BOLD, rl!=null ? rl.fontBody : 14));
                    g2.setColor(hov ? fc : new Color(0x5A3060));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(fl, 18, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                }
            };
            item.setBounds(0, 10 + i * itemH, popW - 4, itemH);
            popup.add(item);
        }
    }

    // ──────────────────────────────────────────────
    private JButton makeRoundBtn(String text, Color cd, Color cl,
                                  int x, int y, int w, int h) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,cl,0,getHeight(),cd));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setFont(new Font("Tahoma",Font.BOLD,rl!=null?rl.fontSmall+1:12));
                g2.setColor(TEXT_WHITE);
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBounds(x,y,w,h);
        return btn;
    }

    private JPanel makeRoundBtnPanel(String text, Color cd, Color cl, Runnable action) {
        return new JPanel(null) {
            boolean hov=false;
            { setOpaque(false); setCursor(new Cursor(Cursor.HAND_CURSOR));
              addMouseListener(new MouseAdapter(){
                  @Override public void mouseEntered(MouseEvent e){hov=true;repaint();}
                  @Override public void mouseExited(MouseEvent e){hov=false;repaint();}
                  @Override public void mouseReleased(MouseEvent e){action.run();}
              });
            }
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,hov?cl.brighter():cl,0,getHeight(),hov?cd:cd.darker()));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setFont(new Font("Tahoma",Font.BOLD,rl!=null?rl.fontSmall+2:13));
                g2.setColor(TEXT_WHITE);
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(text,
                    (getWidth()-fm.stringWidth(text))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
    }
}