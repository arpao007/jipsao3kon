import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MainMenu.java
 * หน้าเมนูหลัก "First Love ♡"
 * ใช้งานผ่าน CardLayout — ปุ่มจะสั่ง cardLayout.show(...)
 */
public class MainMenu extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel     mainContainer;

    // ── Particles ──────────────────────────────────────────────────────────────
    private final List<HeartParticle> particles = new ArrayList<>();
    private final Timer animTimer;
    private int W, H;

    // ── Menu buttons ──────────────────────────────────────────────────────────
    private final String[]   BTN_LABELS  = {"▶  เริ่มเกม", "◎  ดำเนินต่อ", "⚙  ตั้งค่า", "⚡  ผู้เล่นหลายคน"};
    private final String[]   BTN_CARDS   = {"NEW_GAME",   "GAMEPLAY",     "SETTINGS",  "LOBBY"};
    private final MenuBtn[]  menuBtns    = new MenuBtn[4];
    private int hoveredBtn = -1;

    // ── Fonts ──────────────────────────────────────────────────────────────────
    private Font titleFont = new Font("Tahoma", Font.BOLD, 62);
    private Font btnFont   = new Font("Tahoma", Font.BOLD, 18);
    private Font subFont   = new Font("Tahoma", Font.ITALIC, 13);

    // ────────────────────────────────────────────────────────────────────────────
    public MainMenu(CardLayout cardLayout, JPanel mainContainer) {
        this.cardLayout    = cardLayout;
        this.mainContainer = mainContainer;
        setBackground(new Color(0xFFF0F5));

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                W = getWidth(); H = getHeight();
                initParticles();
                layoutButtons();
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int prev = hoveredBtn;
                hoveredBtn = -1;
                for (int i = 0; i < menuBtns.length; i++) {
                    if (menuBtns[i] != null && menuBtns[i].contains(e.getX(), e.getY())) {
                        hoveredBtn = i; break;
                    }
                }
                if (hoveredBtn != prev) repaint();
                setCursor(hoveredBtn >= 0
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                for (int i = 0; i < menuBtns.length; i++) {
                    if (menuBtns[i] != null && menuBtns[i].contains(e.getX(), e.getY())) {
                        onButtonClick(i); break;
                    }
                }
            }
        });

        animTimer = new Timer(30, e -> {
            for (HeartParticle p : particles) p.update();
            repaint();
        });
        animTimer.start();
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Button click handler
    // ════════════════════════════════════════════════════════════════════════════
    private void onButtonClick(int idx) {
        String card = BTN_CARDS[idx];
        switch (card) {
            case "SETTINGS":
                JOptionPane.showMessageDialog(this,
                        "⚙ หน้าตั้งค่า — ยังไม่ได้เปิดใช้งาน",
                        "ตั้งค่า", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "GAMEPLAY":
                if (!SaveManager.hasSave()) {
                    JOptionPane.showMessageDialog(this,
                            "ไม่พบข้อมูล save กรุณาเริ่มเกมใหม่ก่อน",
                            "ไม่พบ Save", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                cardLayout.show(mainContainer, card);
                break;
            default:
                cardLayout.show(mainContainer, card);
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Layout
    // ════════════════════════════════════════════════════════════════════════════
    private void layoutButtons() {
        if (W == 0 || H == 0) return;
        double btnW = Math.min(W * 0.36, 400);
        double btnH = Math.max(44, H * 0.068);
        double gap  = Math.max(12, H * 0.018);
        double totalH = menuBtns.length * (btnH + gap) - gap;
        double startY = H * 0.52 - totalH / 2.0;
        double startX = W * 0.5 - btnW / 2.0;

        for (int i = 0; i < menuBtns.length; i++) {
            menuBtns[i] = new MenuBtn(startX, startY + i * (btnH + gap), btnW, btnH);
        }

        // scale fonts
        titleFont = new Font("Tahoma", Font.BOLD, (int) Math.min(72, Math.max(28, W / 14.0)));
        btnFont   = new Font("Tahoma", Font.BOLD, (int) Math.min(20, Math.max(13, W / 55.0)));
        subFont   = new Font("Tahoma", Font.ITALIC, (int) Math.min(14, Math.max(10, W / 90.0)));
    }

    private void initParticles() {
        particles.clear();
        int count = Math.max(20, (W * H) / 8000);
        for (int i = 0; i < count; i++) particles.add(new HeartParticle(W, H));
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Paint
    // ════════════════════════════════════════════════════════════════════════════
    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (W == 0) { W = getWidth(); H = getHeight(); initParticles(); layoutButtons(); }

        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background gradient
        GradientPaint bg = new GradientPaint(0, 0, new Color(0xFFF0F8), 0, H, new Color(0xFFD6EE));
        g.setPaint(bg);
        g.fillRect(0, 0, W, H);

        // Radial center glow
        RadialGradientPaint rg = new RadialGradientPaint(W / 2f, H * 0.4f, W * 0.4f,
                new float[]{0f, 1f},
                new Color[]{new Color(0xFF99BB, true), new Color(0xFFFFFF, true)});
        // note: Color with alpha
        g.setPaint(new RadialGradientPaint(W / 2f, H * 0.4f, W * 0.4f,
                new float[]{0f, 1f},
                new Color[]{new Color(255, 153, 187, 60), new Color(255, 255, 255, 0)}));
        g.fillRect(0, 0, W, H);

        // Particles
        for (HeartParticle p : particles) p.draw(g);

        drawTitle(g);
        drawButtons(g);
        drawFooter(g);
    }

    private void drawTitle(Graphics2D g) {
        long t = System.currentTimeMillis();
        double pulse = 0.92 + 0.08 * Math.sin(t / 1000.0);

        String title = "First Love";
        g.setFont(titleFont);
        FontMetrics fm = g.getFontMetrics();
        float tx = (W - fm.stringWidth(title)) / 2f;
        float ty = (float)(H * 0.29);

        // Glow
        for (int r = 14; r >= 2; r -= 2) {
            float a = (float)(0.025 * pulse * (15 - r) / 14.0);
            g.setColor(new Color(255, 100, 150, (int)(a * 255)));
            g.drawString(title, tx + r * 0.5f, ty + r * 0.5f);
            g.drawString(title, tx - r * 0.5f, ty - r * 0.5f);
        }

        // Shadow
        g.setColor(new Color(200, 100, 140, 80));
        g.drawString(title, tx + 3, ty + 4);

        // Gradient text
        GradientPaint gp = new GradientPaint(tx, ty - fm.getAscent(),
                new Color(0xFF6B9B), tx + fm.stringWidth(title), ty,
                new Color(0xFF9BB5));
        g.setPaint(gp);
        g.drawString(title, tx, ty);

        // Heart emoji
        String heart = "♡";
        g.setFont(titleFont.deriveFont(titleFont.getSize2D() * 0.55f));
        FontMetrics hm = g.getFontMetrics();
        float pulse2 = (float)(0.9 + 0.1 * Math.sin(t / 600.0));
        g.setColor(new Color(0xFF6B9B, true));
        g.setFont(titleFont.deriveFont(titleFont.getSize2D() * 0.6f * pulse2));
        g.drawString(heart, tx + fm.stringWidth(title) + 10, ty - fm.getAscent() * 0.2f);

        // Subtitle
        String sub = "— เรื่องราวของหัวใจดวงน้อย —";
        g.setFont(subFont);
        FontMetrics sfm = g.getFontMetrics();
        float sa = (float)(0.5 + 0.3 * Math.sin(t / 800.0));
        g.setColor(new Color(200, 100, 150, (int)(sa * 255)));
        g.drawString(sub, (W - sfm.stringWidth(sub)) / 2f, ty + fm.getDescent() + sfm.getAscent() + 8);

        // Decorative line
        float lineY = ty + fm.getDescent() + sfm.getHeight() + 16;
        int lineLen = (int) Math.min(fm.stringWidth(title) * 1.8, W * 0.65);
        float lx = (W - lineLen) / 2f;
        g.setPaint(new GradientPaint(lx, lineY, new Color(255,255,255,0),
                W / 2f, lineY, new Color(0xFF9BB5)));
        g.setStroke(new java.awt.BasicStroke(1f));
        g.draw(new Line2D.Float(lx, lineY, W / 2f, lineY));
        g.setPaint(new GradientPaint(W / 2f, lineY, new Color(0xFF9BB5),
                lx + lineLen, lineY, new Color(255,255,255,0)));
        g.draw(new Line2D.Float(W / 2f, lineY, lx + lineLen, lineY));
    }

    private void drawButtons(Graphics2D g) {
        for (int i = 0; i < menuBtns.length; i++) {
            if (menuBtns[i] == null) continue;
            boolean hov = (hoveredBtn == i);
            menuBtns[i].draw(g, BTN_LABELS[i], btnFont, hov);
        }
    }

    private void drawFooter(Graphics2D g) {
        long t = System.currentTimeMillis();
        String ver = "First Love v1.0  ♡  © 2025";
        g.setFont(subFont);
        FontMetrics fm = g.getFontMetrics();
        float a = (float)(0.35 + 0.1 * Math.sin(t / 1200.0));
        g.setColor(new Color(200, 130, 160, (int)(a * 255)));
        g.drawString(ver, (W - fm.stringWidth(ver)) / 2f, H - 14);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Inner — MenuBtn
    // ════════════════════════════════════════════════════════════════════════════
    static class MenuBtn {
        double x, y, w, h;
        MenuBtn(double x, double y, double w, double h) {
            this.x=x; this.y=y; this.w=w; this.h=h;
        }
        boolean contains(int mx, int my) {
            return mx >= x && mx <= x+w && my >= y && my <= y+h;
        }
        void draw(Graphics2D g, String label, Font font, boolean hovered) {
            Color bg1 = hovered ? new Color(0xFF6B9B) : new Color(0xFFB0CC);
            Color bg2 = hovered ? new Color(0xFF9BB5) : new Color(0xFFD6E8);
            GradientPaint gp = new GradientPaint((float)x,(float)y,bg1,
                    (float)(x+w),(float)(y+h),bg2);
            g.setPaint(gp);
            g.fill(new RoundRectangle2D.Double(x,y,w,h,20,20));

            // border
            g.setColor(hovered ? new Color(0xFF6B9B) : new Color(0xFFB0CC));
            g.setStroke(new java.awt.BasicStroke(hovered ? 2f : 1f));
            g.draw(new RoundRectangle2D.Double(x,y,w,h,20,20));

            // left accent
            g.setColor(new Color(255,255,255,120));
            g.fill(new RoundRectangle2D.Double(x+12, y+h*0.25, hovered?6:3, h*0.5, 3,3));

            // text
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            float tx = (float)(x + (w - fm.stringWidth(label)) / 2.0);
            float ty = (float)(y + (h + fm.getAscent()) / 2.0 - fm.getDescent());
            g.setColor(new Color(0,0,0,60));
            g.drawString(label, tx+2, ty+2);
            g.setColor(hovered ? Color.WHITE : new Color(0x8B2560));
            g.drawString(label, tx, ty);
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Inner — HeartParticle
    // ════════════════════════════════════════════════════════════════════════════
    static class HeartParticle {
        double x, y, vx, vy, size, alpha;
        int W, H;
        static final Random R = new Random();

        HeartParticle(int W, int H) {
            this.W = W; this.H = H; reset();
        }
        void reset() {
            x = R.nextDouble() * W;
            y = H + R.nextDouble() * 50;
            vx = (R.nextDouble() - 0.5) * 0.8;
            vy = -(0.5 + R.nextDouble() * 1.2);
            size = 8 + R.nextDouble() * 16;
            alpha = 0.2 + R.nextDouble() * 0.5;
        }
        void update() {
            x += vx; y += vy;
            alpha -= 0.003;
            if (y < -20 || alpha <= 0) reset();
        }
        void draw(Graphics2D g) {
            if (alpha <= 0) return;
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)alpha));
            g.setColor(new Color(0xFF6B9B));
            drawHeart(g, (int)x, (int)y, (int)size);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
        static void drawHeart(Graphics2D g, int cx, int cy, int s) {
            int[] xs = new int[20], ys = new int[20];
            for (int i = 0; i < 20; i++) {
                double t = -Math.PI + i * (2 * Math.PI / 20);
                xs[i] = cx + (int)(s * 0.5 * 16 * Math.pow(Math.sin(t), 3) / 16.0);
                ys[i] = cy - (int)(s * 0.5 * (13*Math.cos(t) - 5*Math.cos(2*t)
                        - 2*Math.cos(3*t) - Math.cos(4*t)) / 16.0);
            }
            g.fillPolygon(xs, ys, 20);
        }
    }
}
