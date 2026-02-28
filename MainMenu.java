import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MainMenu.java
 * หน้าเมนูหลัก — Cozy Pastel Romance Style
 * ปุ่ม: New Game | Load Save | Multiplayer | Exit
 */
public class MainMenu extends JPanel {

    // ── Palette ──
    private static final Color BG_TOP        = new Color(0xF7D6E0);
    private static final Color BG_BOT        = new Color(0xD9AED0);
    private static final Color PINK_DEEP     = new Color(0xE8759A);
    private static final Color PINK_LIGHT    = new Color(0xF5A8C5);
    private static final Color LILAC         = new Color(0xC9A0DC);
    private static final Color LILAC_DARK    = new Color(0xA076BB);
    private static final Color BLUE_SOFT     = new Color(0xADD8E6);
    private static final Color TEXT_WHITE    = new Color(0xFFF5FA);
    private static final Color GOLD          = new Color(0xF0C060);

    private final CardLayout cardLayout;
    private final JPanel     mainContainer;

    // Floating petals
    private final List<Petal> petals = new ArrayList<>();
    private Timer animTimer;

    // Wiggle state for buttons
    private float wiggleTime = 0f;

    // ─────────────────────────────────────────────
    public MainMenu(CardLayout cardLayout, JPanel mainContainer) {
        this.cardLayout    = cardLayout;
        this.mainContainer = mainContainer;
        setLayout(null);
        setPreferredSize(new Dimension(1200, 800));
        initPetals();
        buildUI();
        startAnimation();
    }

    // ─────────────────────────────────────────────
    // Floating petal system
    // ─────────────────────────────────────────────
    private static class Petal {
        float x, y, size, speed, phase, rot, rotSpeed;
        Color color;
        Petal(int w) {
            Random r = new Random();
            x       = r.nextFloat() * w;
            y       = -r.nextFloat() * 800;
            size    = 6 + r.nextFloat() * 14;
            speed   = 0.6f + r.nextFloat() * 1.2f;
            phase   = r.nextFloat() * (float)(Math.PI * 2);
            rot     = r.nextFloat() * 360;
            rotSpeed= 0.5f + r.nextFloat() * 2f;
            Color[] cols = {
                new Color(0xF8BBD9), new Color(0xE8A0C0),
                new Color(0xD4B0E0), new Color(0xFFDDEE),
                new Color(0xC8A0D8)
            };
            color = cols[r.nextInt(cols.length)];
        }
        void update(float t) {
            y   += speed;
            x   += (float)(Math.sin(t * 0.03 + phase) * 0.8);
            rot += rotSpeed;
            if (y > 820) { y = -20; }
        }
    }

    private void initPetals() {
        for (int i = 0; i < 40; i++) petals.add(new Petal(1200));
    }

    private void startAnimation() {
        animTimer = new Timer(16, e -> {
            wiggleTime += 0.05f;
            for (Petal p : petals) p.update(wiggleTime);
            repaint();
        });
        animTimer.start();
    }

    // ─────────────────────────────────────────────
    // Background & petals paint
    // ─────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);

        // ── Gradient background ──
        GradientPaint gp = new GradientPaint(0, 0, BG_TOP, 0, 800, BG_BOT);
        g2.setPaint(gp);
        g2.fillRect(0, 0, 1200, 800);

        // ── Soft bokeh circles ──
        drawBokeh(g2);

        // ── Petals ──
        for (Petal p : petals) drawPetal(g2, p);

        // ── Decorative top ribbon ──
        drawRibbon(g2);
    }

    private void drawBokeh(Graphics2D g2) {
        int[][] bokeh = {
            {120, 200, 180}, {900, 100, 220}, {200, 600, 140},
            {1050, 500, 160}, {500, 700, 200}, {700, 150, 120}
        };
        for (int[] b : bokeh) {
            RadialGradientPaint rg = new RadialGradientPaint(
                b[0], b[1], b[2],
                new float[]{0f, 1f},
                new Color[]{new Color(255, 200, 230, 40), new Color(255, 200, 230, 0)}
            );
            g2.setPaint(rg);
            g2.fillOval(b[0]-b[2], b[1]-b[2], b[2]*2, b[2]*2);
        }
    }

    private void drawPetal(Graphics2D g2, Petal p) {
        Graphics2D pg = (Graphics2D) g2.create();
        pg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        pg.translate(p.x, p.y);
        pg.rotate(Math.toRadians(p.rot));
        pg.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), 200));
        // teardrop petal shape
        Path2D path = new Path2D.Float();
        path.moveTo(0, -p.size);
        path.curveTo(p.size*0.6f, -p.size*0.5f, p.size*0.6f, p.size*0.5f, 0, p.size*0.3f);
        path.curveTo(-p.size*0.6f, p.size*0.5f, -p.size*0.6f, -p.size*0.5f, 0, -p.size);
        pg.fill(path);
        pg.dispose();
    }

    private void drawRibbon(Graphics2D g2) {
        // soft wave at top
        Path2D wave = new Path2D.Float();
        wave.moveTo(0, 0);
        wave.curveTo(300, 30, 600, -10, 900, 25);
        wave.curveTo(1050, 38, 1150, 10, 1200, 20);
        wave.lineTo(1200, 0);
        wave.closePath();
        g2.setColor(new Color(0xF9C4DA));
        g2.fill(wave);

        // bottom decorative wave
        Path2D waveB = new Path2D.Float();
        waveB.moveTo(0, 800);
        waveB.curveTo(200, 765, 500, 785, 700, 770);
        waveB.curveTo(900, 755, 1100, 780, 1200, 760);
        waveB.lineTo(1200, 800);
        waveB.closePath();
        g2.setColor(new Color(0xC890C8, false));
        g2.fill(waveB);
    }

    // ─────────────────────────────────────────────
    // Build UI components
    // ─────────────────────────────────────────────
    private void buildUI() {

        // ── Title card ──
        JPanel titleCard = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Soft card bg
                g2.setColor(new Color(255, 240, 248, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                // Border
                g2.setStroke(new BasicStroke(3f));
                g2.setColor(new Color(0xE8759A, false));
                g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 38, 38);
                // Inner shimmer
                GradientPaint sh = new GradientPaint(0,0, new Color(255,255,255,80), getWidth(), getHeight(), new Color(255,200,230,0));
                g2.setPaint(sh);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            }
        };
        titleCard.setOpaque(false);
        titleCard.setBounds(300, 60, 600, 180);

        // Star decorations
        JLabel starL = makeStarLabel();
        starL.setBounds(20, 20, 40, 40);
        titleCard.add(starL);
        JLabel starR = makeStarLabel();
        starR.setBounds(540, 20, 40, 40);
        titleCard.add(starR);

        // Game title
        JLabel title1 = new JLabel("✿ First Love ✿", SwingConstants.CENTER);
        title1.setFont(loadFont(58));
        title1.setForeground(PINK_DEEP);
        title1.setBounds(0, 25, 600, 75);
        titleCard.add(title1);

        JLabel title2 = new JLabel("~ เกมสร้างความสัมพันธ์ ~", SwingConstants.CENTER);
        title2.setFont(new Font("Tahoma", Font.ITALIC, 18));
        title2.setForeground(LILAC_DARK);
        title2.setBounds(0, 105, 600, 35);
        titleCard.add(title2);

        add(titleCard);

        // ── Buttons ──
        int btnW  = 300;
        int btnH  = 72;
        int startX = (1200 - btnW) / 2;
        int startY = 290;
        int gap   = 90;

        add(makeMenuButton("🌸  New Game",   PINK_DEEP,   PINK_LIGHT, startX, startY,           btnW, btnH, () -> onNewGame()));
        add(makeMenuButton("💾  Load Save",  LILAC_DARK,  LILAC,      startX, startY + gap,     btnW, btnH, () -> onLoadSave()));
        add(makeMenuButton("🌐  Multiplayer",new Color(0x5B9AD5), BLUE_SOFT, startX, startY + gap*2, btnW, btnH, () -> onMultiplayer()));
        add(makeMenuButton("🚪  Exit",       new Color(0xB06090), new Color(0xE0A8C8), startX, startY + gap*3, btnW, btnH, () -> onExit()));

        // ── Bottom credits ──
        JLabel credit = new JLabel("♡  First Love Game  •  v1.0  ♡", SwingConstants.CENTER);
        credit.setFont(new Font("Tahoma", Font.ITALIC, 14));
        credit.setForeground(new Color(0x9060A0));
        credit.setBounds(0, 755, 1200, 30);
        add(credit);

    }

    // ─────────────────────────────────────────────
    // Custom rounded button
    // ─────────────────────────────────────────────
    private JPanel makeMenuButton(String text, Color colorDeep, Color colorLight,
                                   int x, int y, int w, int h, Runnable action) {
        JPanel btn = new JPanel(null) {
            private boolean hover  = false;
            private boolean press  = false;
            private float   pulse  = 0;
            private Timer   pt;

            {
                setOpaque(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        if (pt == null) {
                            pt = new Timer(16, ev -> { pulse += 0.15f; repaint(); });
                            pt.start();
                        }
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false; press = false;
                        if (pt != null) { pt.stop(); pt = null; }
                        pulse = 0; repaint();
                    }
                    @Override
                    public void mousePressed(MouseEvent e)  { press = true;  repaint(); }
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        press = false; repaint();
                        if (hover) action.run();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int oy = press ? 4 : hover ? -3 : 0;
                int px = press ? 0 : hover ? (int)(Math.sin(pulse)*2) : 0;

                // Shadow
                if (!press) {
                    g2.setColor(new Color(0, 0, 0, 30));
                    g2.fillRoundRect(px+5, oy+8, w, h, 36, 36);
                }

                // Main body gradient
                GradientPaint bodyGrad = new GradientPaint(
                    0, oy, hover ? colorLight.brighter() : colorLight,
                    0, oy + h, hover ? colorDeep : colorDeep.darker()
                );
                g2.setPaint(bodyGrad);
                g2.fillRoundRect(px, oy, w, h, 36, 36);

                // Inner shine
                g2.setColor(new Color(255, 255, 255, hover ? 80 : 50));
                g2.fillRoundRect(px+6, oy+5, w-12, h/2-4, 24, 24);

                // Border
                g2.setStroke(new BasicStroke(2.5f));
                g2.setColor(new Color(255, 255, 255, 160));
                g2.drawRoundRect(px+1, oy+1, w-2, h-2, 35, 35);

                // Sparkle dots on hover
                if (hover) {
                    g2.setColor(new Color(255, 240, 100, 180));
                    int[] sx = {px+15, px+w-20, px+w/2};
                    int[] sy = {oy+10, oy+12, oy+h-10};
                    int[] ss = {6, 5, 7};
                    for (int i = 0; i < 3; i++) {
                        drawSparkle(g2, sx[i], sy[i], ss[i]);
                    }
                }

                // Text
                g2.setFont(new Font("Tahoma", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                String label = text;
                int tx = px + (w - fm.stringWidth(label)) / 2;
                int ty = oy + (h + fm.getAscent() - fm.getDescent()) / 2;

                // Text shadow
                g2.setColor(new Color(0, 0, 0, 40));
                g2.drawString(label, tx+2, ty+2);
                // Text main
                g2.setColor(TEXT_WHITE);
                g2.drawString(label, tx, ty);
            }
        };
        btn.setBounds(x, y, w + 20, h + 20);
        return btn;
    }

    private void drawSparkle(Graphics2D g2, int cx, int cy, int r) {
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(cx-r, cy, cx+r, cy);
        g2.drawLine(cx, cy-r, cx, cy+r);
        int d = (int)(r * 0.7);
        g2.drawLine(cx-d, cy-d, cx+d, cy+d);
        g2.drawLine(cx-d, cy+d, cx+d, cy-d);
    }

    // ─────────────────────────────────────────────

    // ─────────────────────────────────────────────
    // Font helper
    // ─────────────────────────────────────────────
    private Font loadFont(float size) {
        // Try a round/cute fallback chain
        String[] candidates = {"Comic Sans MS", "Chalkboard SE", "Tahoma"};
        for (String name : candidates) {
            Font f = new Font(name, Font.BOLD, (int) size);
            if (!f.getFamily().equals("Dialog")) return f;
        }
        return new Font("Tahoma", Font.BOLD, (int) size);
    }

    private JLabel makeStarLabel() {
        JLabel lbl = new JLabel("✦");
        lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        lbl.setForeground(GOLD);
        return lbl;
    }

    // ─────────────────────────────────────────────
    // Button actions
    // ─────────────────────────────────────────────
    private void onNewGame() {
        int opt = JOptionPane.showConfirmDialog(this,
            "<html><div style='font-family:Tahoma;font-size:15px;text-align:center'>" +
            "🌸 เริ่มเกมใหม่?<br><br>" +
            "<span style='color:#888'>ข้อมูลปัจจุบันจะไม่หาย<br>ถ้ายังไม่ได้ save</span></div></html>",
            "New Game", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            cardLayout.show(mainContainer, "GAMEPLAY");
        }
    }

    private void onLoadSave() {
        if (!SaveManager.hasSave()) {
            JOptionPane.showMessageDialog(this,
                "<html><div style='font-family:Tahoma;font-size:15px;text-align:center'>" +
                "💾 ยังไม่มีไฟล์ save<br><br>" +
                "<span style='color:#888'>เล่น New Game ก่อนนะคะ ♡</span></div></html>",
                "Load Save", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        cardLayout.show(mainContainer, "GAMEPLAY");
    }

    private void onMultiplayer() {
        cardLayout.show(mainContainer, "LOBBY");
    }

    private void onExit() {
        int opt = JOptionPane.showConfirmDialog(this,
            "<html><div style='font-family:Tahoma;font-size:15px;text-align:center'>" +
            "🚪 ออกจากเกม?<br><br>" +
            "<span style='color:#888'>แน่ใจนะคะ ♡</span></div></html>",
            "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            if (animTimer != null) animTimer.stop();
            System.exit(0);
        }
    }

    // cleanup
    public void onHide() {
        if (animTimer != null) animTimer.stop();
    }
    public void onShow() {
        if (animTimer != null && !animTimer.isRunning()) animTimer.start();
    }
}