import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class MainMenu extends JPanel {

    private static final Color BG_TOP     = new Color(0xF7D6E0);
    private static final Color BG_BOT     = new Color(0xD9AED0);
    private static final Color PINK_DEEP  = new Color(0xE8759A);
    private static final Color PINK_LIGHT = new Color(0xF5A8C5);
    private static final Color LILAC      = new Color(0xC9A0DC);
    private static final Color LILAC_DARK = new Color(0xA076BB);
    private static final Color BLUE_SOFT  = new Color(0xADD8E6);
    private static final Color TEXT_WHITE = new Color(0xFFF5FA);
    private static final Color GOLD       = new Color(0xF0C060);

    private final CardLayout cardLayout;
    private final JPanel     mainContainer;
    private JFrame           gameFrame;

    // ── MenuListener — ให้ RunGame hook การนำทางผ่านนี้ ──
    public interface MenuListener {
        void onNewGame();
        void onLoadGame();
        void onMultiplayer();
        void onSettings();
        void onExit();
    }
    private MenuListener menuListener;

    private final List<Petal> petals = new ArrayList<>();
    private Timer animTimer;
    private float wiggleTime = 0f;

    // RL คำนวณจากขนาดจริงของ panel
    private RL rl;

    // ──────────────────────────────────────────────
    public MainMenu(CardLayout cardLayout, JPanel mainContainer) {
        this(cardLayout, mainContainer, null);
    }

    public MainMenu(CardLayout cardLayout, JPanel mainContainer, JFrame frame) {
        this.cardLayout    = cardLayout;
        this.mainContainer = mainContainer;
        this.gameFrame     = frame;
        setLayout(null);
        initPetals();
        startAnimation();

        // rebuild UI เมื่อขนาดเปลี่ยน
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                rebuildUI();
            }
            @Override public void componentShown(ComponentEvent e) {
                rebuildUI();
            }
        });

        // catch เมื่อถูก add เข้า container (ได้ขนาดจริงครั้งแรก)
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0
                    && isShowing()) {
                rebuildUI();
            }
        });
    }

    // ──────────────────────────────────────────────
    // Rebuild UI ตาม RL ปัจจุบัน
    // ──────────────────────────────────────────────
    private void rebuildUI() {
        int w = getWidth();
        int h = getHeight();
        // ถ้ายังไม่มีขนาด รอ invokeLater รอบถัดไป
        if (w <= 0 || h <= 0) {
            javax.swing.SwingUtilities.invokeLater(this::rebuildUI);
            return;
        }
        rl = new RL(w, h);
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }

    // ──────────────────────────────────────────────
    // Petal animation
    // ──────────────────────────────────────────────
    private static class Petal {
        float x, y, size, speed, phase, rot, rotSpeed; Color color;
        Petal(int w) {
            Random r = new Random();
            x = r.nextFloat() * w; y = -r.nextFloat() * 800;
            size = 6 + r.nextFloat() * 14; speed = 0.6f + r.nextFloat() * 1.2f;
            phase = r.nextFloat() * (float)(Math.PI * 2);
            rot = r.nextFloat() * 360; rotSpeed = 0.5f + r.nextFloat() * 2f;
            Color[] c = { new Color(0xF8BBD9), new Color(0xE8A0C0),
                          new Color(0xD4B0E0), new Color(0xFFDDEE), new Color(0xC8A0D8) };
            color = c[r.nextInt(c.length)];
        }
        void update(float t) {
            y += speed; x += (float)(Math.sin(t * 0.03 + phase) * 0.8);
            rot += rotSpeed; if (y > 820) y = -20;
        }
    }

    private void initPetals() {
        for (int i = 0; i < 40; i++) petals.add(new Petal(1400));
    }

    private void startAnimation() {
        animTimer = new Timer(16, e -> {
            wiggleTime += 0.05f;
            for (Petal p : petals) p.update(wiggleTime);
            repaint();
        });
        animTimer.start();
    }

    // ──────────────────────────────────────────────
    // Paint background
    // ──────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setPaint(new GradientPaint(0, 0, BG_TOP, 0, h, BG_BOT));
        g2.fillRect(0, 0, w, h);

        // bokeh
        int[][] bokeh = { {w/10, h/4, 180}, {(int)(w*0.75), h/8, 220},
                          {w/6, (int)(h*0.75), 140}, {(int)(w*0.88), (int)(h*0.63), 160},
                          {w/2, (int)(h*0.88), 200}, {(int)(w*0.58), h/5, 120} };
        for (int[] b : bokeh) {
            g2.setPaint(new RadialGradientPaint(b[0], b[1], b[2],
                new float[]{0f, 1f},
                new Color[]{new Color(255,200,230,40), new Color(255,200,230,0)}));
            g2.fillOval(b[0]-b[2], b[1]-b[2], b[2]*2, b[2]*2);
        }

        for (Petal p : petals) drawPetal(g2, p, w);

        // ribbon top
        Path2D wave = new Path2D.Float();
        wave.moveTo(0, 0); wave.curveTo(w*0.25, h*0.04, w*0.5, -h*0.012, w*0.75, h*0.03);
        wave.curveTo(w*0.875, h*0.047, w*0.96, h*0.012, w, h*0.025);
        wave.lineTo(w, 0); wave.closePath();
        g2.setColor(new Color(0xF9C4DA)); g2.fill(wave);

        // ribbon bottom
        Path2D waveB = new Path2D.Float();
        waveB.moveTo(0, h); waveB.curveTo(w*0.17, h*0.956, w*0.42, h*0.981, w*0.58, h*0.963);
        waveB.curveTo(w*0.75, h*0.944, w*0.92, h*0.975, w, h*0.95);
        waveB.lineTo(w, h); waveB.closePath();
        g2.setColor(new Color(0xC890C8, false)); g2.fill(waveB);
    }

    private void drawPetal(Graphics2D g2, Petal p, int panelW) {
        float px = p.x * panelW / 1400f;
        Graphics2D pg = (Graphics2D) g2.create();
        pg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        pg.translate(px, p.y);
        pg.rotate(Math.toRadians(p.rot));
        pg.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), 200));
        Path2D path = new Path2D.Float();
        path.moveTo(0, -p.size);
        path.curveTo(p.size*0.6f, -p.size*0.5f, p.size*0.6f, p.size*0.5f, 0, p.size*0.3f);
        path.curveTo(-p.size*0.6f, p.size*0.5f, -p.size*0.6f, -p.size*0.5f, 0, -p.size);
        pg.fill(path); pg.dispose();
    }

    // ──────────────────────────────────────────────
    // Build UI (ใช้ rl ปัจจุบัน)
    // ──────────────────────────────────────────────
    private void buildUI() {
        if (rl == null) return;
        int w = rl.w, h = rl.h;

        // Title card
        int cardW = (int)(w * 0.5);
        int cardH = rl.size == RL.Size.LARGE ? 180 : rl.size == RL.Size.MEDIUM ? 150 : 120;
        int cardX = rl.cx(cardW);
        int cardY = rl.padY;

        JPanel titleCard = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 240, 248, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.setStroke(new BasicStroke(3f));
                g2.setColor(PINK_DEEP);
                g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 38, 38);
                g2.setPaint(new GradientPaint(0, 0, new Color(255,255,255,80),
                        getWidth(), getHeight(), new Color(255,200,230,0)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            }
        };
        titleCard.setOpaque(false);
        titleCard.setBounds(cardX, cardY, cardW, cardH);

        // stars
        JLabel starL = makeStarLabel(rl.fontBody);
        starL.setBounds(16, 14, 36, 36);
        titleCard.add(starL);
        JLabel starR = makeStarLabel(rl.fontBody);
        starR.setBounds(cardW - 52, 14, 36, 36);
        titleCard.add(starR);

        JLabel title1 = new JLabel("✿ First Love ✿", SwingConstants.CENTER);
        title1.setFont(loadFont(rl.fontTitle));
        title1.setForeground(PINK_DEEP);
        title1.setBounds(0, (int)(cardH * 0.1), cardW, (int)(cardH * 0.55));
        titleCard.add(title1);

        JLabel title2 = new JLabel("~ เกมสร้างความสัมพันธ์ ~", SwingConstants.CENTER);
        title2.setFont(new Font("Tahoma", Font.ITALIC, rl.fontSubtitle));
        title2.setForeground(LILAC_DARK);
        title2.setBounds(0, (int)(cardH * 0.68), cardW, (int)(cardH * 0.28));
        titleCard.add(title2);
        add(titleCard);

        // Buttons
        int btnStartY = cardY + cardH + rl.gap * 2;
        int btnX = rl.cx(rl.btnW);

        add(makeMenuButton("✦  New Game",    PINK_DEEP,   PINK_LIGHT,             btnX, btnStartY,                    rl.btnW, rl.btnH, rl.btnFont, () -> onNewGame()));
        add(makeMenuButton("✦  Load Save",   LILAC_DARK,  LILAC,                  btnX, btnStartY + rl.btnGap,        rl.btnW, rl.btnH, rl.btnFont, () -> onLoadSave()));
        add(makeMenuButton("✦  Multiplayer", new Color(0x5B9AD5), BLUE_SOFT,      btnX, btnStartY + rl.btnGap * 2,    rl.btnW, rl.btnH, rl.btnFont, () -> onMultiplayer()));
        add(makeMenuButton("✦  Settings",   new Color(0x7080B0), new Color(0xB0C4DE), btnX, btnStartY + rl.btnGap * 3, rl.btnW, rl.btnH, rl.btnFont, () -> onSettings()));
        add(makeMenuButton("✦  Exit",        new Color(0xB06090), new Color(0xE0A8C8), btnX, btnStartY + rl.btnGap * 4, rl.btnW, rl.btnH, rl.btnFont, () -> onExit()));

        // credit
        JLabel credit = new JLabel("♡  First Love Game  •  v1.0  ♡", SwingConstants.CENTER);
        credit.setFont(new Font("Tahoma", Font.ITALIC, rl.fontSmall + 1));
        credit.setForeground(new Color(0x9060A0));
        credit.setBounds(0, h - 34, w, 28);
        add(credit);
    }

    // ──────────────────────────────────────────────
    private JPanel makeMenuButton(String text, Color colorDeep, Color colorLight,
                                   int x, int y, int w, int h, int fontSize, Runnable action) {
        JPanel btn = new JPanel(null) {
            private boolean hover = false, press = false;
            private float pulse = 0;
            private Timer pt;
            {
                setOpaque(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) {
                        hover = true;
                        if (pt == null) { pt = new Timer(16, ev -> { pulse += 0.15f; repaint(); }); pt.start(); }
                    }
                    @Override public void mouseExited(MouseEvent e) {
                        hover = false; press = false;
                        if (pt != null) { pt.stop(); pt = null; } pulse = 0; repaint();
                    }
                    @Override public void mousePressed(MouseEvent e)  { press = true; repaint(); }
                    @Override public void mouseReleased(MouseEvent e) {
                        press = false; repaint(); if (hover) action.run();
                    }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int oy = press ? 4 : hover ? -3 : 0;
                int px = press ? 0 : hover ? (int)(Math.sin(pulse)*2) : 0;
                if (!press) { g2.setColor(new Color(0,0,0,30)); g2.fillRoundRect(px+5, oy+8, w, h, 36, 36); }
                g2.setPaint(new GradientPaint(0, oy, hover ? colorLight.brighter() : colorLight,
                        0, oy+h, hover ? colorDeep : colorDeep.darker()));
                g2.fillRoundRect(px, oy, w, h, 36, 36);
                g2.setColor(new Color(255,255,255, hover ? 80 : 50));
                g2.fillRoundRect(px+6, oy+5, w-12, h/2-4, 24, 24);
                g2.setStroke(new BasicStroke(2.5f));
                g2.setColor(new Color(255,255,255,160));
                g2.drawRoundRect(px+1, oy+1, w-2, h-2, 35, 35);
                if (hover) {
                    g2.setColor(new Color(255,240,100,180));
                    int[] sx={px+15,px+w-20,px+w/2}, sy={oy+10,oy+12,oy+h-10}, ss={6,5,7};
                    for (int i=0;i<3;i++) drawSparkle(g2,sx[i],sy[i],ss[i]);
                }
                g2.setFont(new Font("Tahoma", Font.BOLD, fontSize));
                FontMetrics fm = g2.getFontMetrics();
                int tx = px + (w - fm.stringWidth(text)) / 2;
                int ty = oy + (h + fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(new Color(0,0,0,40)); g2.drawString(text, tx+2, ty+2);
                g2.setColor(TEXT_WHITE); g2.drawString(text, tx, ty);
            }
        };
        btn.setBounds(x, y, w + 20, h + 20);
        return btn;
    }

    private void drawSparkle(Graphics2D g2, int cx, int cy, int r) {
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(cx-r,cy,cx+r,cy); g2.drawLine(cx,cy-r,cx,cy+r);
        int d = (int)(r*0.7);
        g2.drawLine(cx-d,cy-d,cx+d,cy+d); g2.drawLine(cx-d,cy+d,cx+d,cy-d);
    }

    private Font loadFont(float size) {
        for (String name : new String[]{"Comic Sans MS","Chalkboard SE","Tahoma"}) {
            Font f = new Font(name, Font.BOLD, (int)size);
            if (!f.getFamily().equals("Dialog")) return f;
        }
        return new Font("Tahoma", Font.BOLD, (int)size);
    }

    private JLabel makeStarLabel(int size) {
        JLabel lbl = new JLabel("✦");
        lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, size));
        lbl.setForeground(GOLD);
        return lbl;
    }

    private Component getDialogParent() { return gameFrame != null ? gameFrame : this; }

    public void setMenuListener(MenuListener listener) { this.menuListener = listener; }

    private void onNewGame() {
        if (menuListener != null) {
            menuListener.onNewGame();
            return;
        }
        // fallback เดิม (ถ้าไม่มี listener)
        int opt = JOptionPane.showConfirmDialog(getDialogParent(),
            "<html><div style='font-family:Tahoma;font-size:15px;text-align:center'>" +
            "เริ่มเกมใหม่?<br><br><span style='color:#888'>ข้อมูลปัจจุบันจะไม่หาย<br>ถ้ายังไม่ได้ save</span></div></html>",
            "New Game", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) cardLayout.show(mainContainer, "GAMEPLAY");
    }

    private void onLoadSave() {
        if (menuListener != null) {
            menuListener.onLoadGame();
            return;
        }
        // fallback เดิม
        if (!SaveManager.hasSave()) {
            JOptionPane.showMessageDialog(getDialogParent(),
                "<html><div style='font-family:Tahoma;font-size:15px;text-align:center'>" +
                "ยังไม่มีไฟล์ save<br><br><span style='color:#888'>เล่น New Game ก่อนนะคะ</span></div></html>",
                "Load Save", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        cardLayout.show(mainContainer, "GAMEPLAY");
    }

    private void onMultiplayer() {
        if (menuListener != null) menuListener.onMultiplayer();
        else cardLayout.show(mainContainer, "LOBBY");
    }
    private void onSettings() {
        if (menuListener != null) menuListener.onSettings();
        else cardLayout.show(mainContainer, "SETTINGS");
    }
    private void onExit() {
        if (menuListener != null) menuListener.onExit();
        else { if (animTimer != null) animTimer.stop(); System.exit(0); }
    }

    public void onHide() { if (animTimer != null) animTimer.stop(); }
    public void onShow() { if (animTimer != null && !animTimer.isRunning()) animTimer.start(); }
}
