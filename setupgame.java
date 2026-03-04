import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;


public class setupgame extends JPanel {
    
private float alpha = 1.0f;
    // ── ข้อมูลสาวแต่ละคน ──────────────────────────────────────────────────────
    public static class GirlData {
        public final String id, name, nickname, description, trait1, trait2, trait3;
        public final Color primary, secondary, accent;
        public final String emoji;

        public GirlData(String id, String name, String nickname, String desc,
                        String t1, String t2, String t3,
                        Color primary, Color secondary, Color accent, String emoji) {
            this.id = id; this.name = name; this.nickname = nickname;
            this.description = desc;
            this.trait1 = t1; this.trait2 = t2; this.trait3 = t3;
            this.primary = primary; this.secondary = secondary;
            this.accent = accent; this.emoji = emoji;
        }
    }

    // ── ข้อมูลสาวทั้ง 3 ──────────────────────────────────────────────────────
    private static final GirlData[] GIRLS = {
        new GirlData(
            "SAKURA", "ซากุระ", "นิดหน่อย",
            "สาวเพื่อน幼馴染ที่รู้จักกันมาตั้งแต่เด็ก\nอบอุ่น ใจดี และมักจะเอาขนมมาฝากเสมอ",
            "🍱 ทำอาหารเก่ง", "🌸 อ่อนโยน", "😊 ร่าเริง",
            new Color(0xFF9BB5), new Color(0xFFD6E2), new Color(0xFF6B9B),
            "🌸"
        ),
        new GirlData(
            "HANA", "ฮานะ", "หนานะ",
            "หัวหน้าชั้นที่เก่งรอบด้าน ดูเข้มแข็งภายนอก\nแต่ข้างในเป็นคนขี้อาย และชอบอ่านนิยาย",
            "📚 เรียนเก่ง", "🎭 ลึกลับ", "💜 สุขุม",
            new Color(0xB39DDB), new Color(0xE8DEF8), new Color(0x7C4DFF),
            "🌺"
        ),
        new GirlData(
            "YUKI", "ยูกิ", "ยูยู่",
            "สาวนักกีฬาที่กล้าหาญและพูดตรงๆ\nดูแข็งแกร่ง แต่จะอ่อนแอมากเมื่อเผชิญกับแมลง",
            "⚡ กล้าหาญ", "🎯 มุ่งมั่น", "💙 ซื่อสัตย์",
            new Color(0x81D4FA), new Color(0xD4F1FF), new Color(0x0288D1),
            "❄️"
        )
    };

    // ── State ──────────────────────────────────────────────────────────────────
    private int selectedGirl = -1;
    private int hoveredGirl  = -1;
    private JTextField nameField;
    private JLabel statusLabel;
    private JButton confirmBtn;
    private GirlCard[] girlCards;

    // ── Constructor ───────────────────────────────────────────────────────────
    public setupgame(CardLayout cardLayout, JPanel mainContainer,
                     BiConsumer<String, GirlData> onConfirm) {
        setLayout(new BorderLayout());
        setBackground(new Color(0xFFF0F5));

        add(buildHeader(cardLayout, mainContainer), BorderLayout.NORTH);
        add(buildCenter(),                          BorderLayout.CENTER);
        add(buildFooter(cardLayout, mainContainer, onConfirm), BorderLayout.SOUTH);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Header
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildHeader(CardLayout cl, JPanel container) {
        JPanel header = new PetalBackgroundPanel(new Color(0xFFD6E2), new Color(0xFFC0D8));
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 110));
        header.setBorder(new EmptyBorder(16, 28, 16, 28));

        JButton backBtn = createFlatButton("← ย้อนกลับ",
                new Color(0xE8759A), Color.WHITE, new Font("Tahoma", Font.BOLD, 14));
        backBtn.setPreferredSize(new Dimension(130, 36));
        backBtn.addActionListener(e -> cl.show(container, "MENU"));

        JLabel title = new JLabel("✨  เริ่มต้นรักครั้งใหม่  ✨", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 30));
        title.setForeground(new Color(0x8B2560));

        JLabel subtitle = new JLabel("ตั้งชื่อตัวเองแล้วเลือกคนที่ใช่สักคน…", SwingConstants.CENTER);
        subtitle.setFont(new Font("Tahoma", Font.ITALIC, 15));
        subtitle.setForeground(new Color(0xC06090));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(subtitle);

        JPanel leftBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        leftBox.setOpaque(false);
        leftBox.add(backBtn);

        header.add(leftBox,  BorderLayout.WEST);
        header.add(titleBox, BorderLayout.CENTER);
        return header;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Center
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setOpaque(false);
        center.add(buildNameSection(), BorderLayout.NORTH);
        center.add(buildGirlSection(), BorderLayout.CENTER);
        return center;
    }

    private JPanel buildNameSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 14));
        panel.setOpaque(false);

        JLabel lbl = new JLabel("ชื่อของคุณ :");
        lbl.setFont(new Font("Tahoma", Font.BOLD, 17));
        lbl.setForeground(new Color(0x7B3060));

        nameField = new JTextField(18) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xFFF0F8));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        nameField.setFont(new Font("Tahoma", Font.PLAIN, 17));
        nameField.setForeground(new Color(0x5A2040));
        nameField.setCaretColor(new Color(0xE8759A));
        nameField.setOpaque(false);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(20, new Color(0xFFB0CC), 2),
                new EmptyBorder(6, 14, 6, 14)));
        nameField.setPreferredSize(new Dimension(240, 40));
        nameField.setToolTipText("ใส่ชื่อตัวเอง 1–12 ตัวอักษร");

        JLabel hint = new JLabel("(1–12 ตัวอักษร)");
        hint.setFont(new Font("Tahoma", Font.ITALIC, 12));
        hint.setForeground(new Color(0xC090B0));

        panel.add(lbl);
        panel.add(nameField);
        panel.add(hint);
        return panel;
    }

    private JPanel buildGirlSection() {
        JLabel sectionTitle = new JLabel("เลือกหัวใจของคุณ…", SwingConstants.CENTER);
        sectionTitle.setFont(new Font("Tahoma", Font.BOLD, 17));
        sectionTitle.setForeground(new Color(0x9B4070));
        sectionTitle.setBorder(new EmptyBorder(4, 0, 10, 0));

        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsRow.setOpaque(false);
        cardsRow.setBorder(new EmptyBorder(0, 30, 0, 30));

        girlCards = new GirlCard[GIRLS.length];
        for (int i = 0; i < GIRLS.length; i++) {
            final int idx = i;
            girlCards[i] = new GirlCard(GIRLS[i]);
            girlCards[i].addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { selectGirl(idx); }
                @Override public void mouseEntered(MouseEvent e) {
                    hoveredGirl = idx; girlCards[idx].setHovered(true); updateStatus();
                }
                @Override public void mouseExited(MouseEvent e) {
                    hoveredGirl = -1; girlCards[idx].setHovered(false); updateStatus();
                }
            });
            cardsRow.add(girlCards[i]);
        }

        JPanel section = new JPanel(new BorderLayout(0, 0));
        section.setOpaque(false);
        section.setBorder(new EmptyBorder(0, 0, 10, 0));
        section.add(sectionTitle, BorderLayout.NORTH);
        section.add(cardsRow,     BorderLayout.CENTER);
        return section;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Footer
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildFooter(CardLayout cl, JPanel container,
                                BiConsumer<String, GirlData> onConfirm) {
        JPanel footer = new PetalBackgroundPanel(new Color(0xFFE8F0), new Color(0xFFD0E4));
        footer.setLayout(new BorderLayout(0, 0));
        footer.setPreferredSize(new Dimension(0, 80));
        footer.setBorder(new EmptyBorder(12, 30, 12, 30));

        statusLabel = new JLabel("← คลิกเลือกสาวที่ชอบ แล้วกดยืนยัน", SwingConstants.LEFT);
        statusLabel.setFont(new Font("Tahoma", Font.ITALIC, 14));
        statusLabel.setForeground(new Color(0xC070A0));

        confirmBtn = createFlatButton("ยืนยัน  ♡", new Color(0xE8759A), Color.WHITE,
                new Font("Tahoma", Font.BOLD, 17));
        confirmBtn.setPreferredSize(new Dimension(160, 48));
        confirmBtn.setEnabled(false);
        confirmBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                shake(nameField);
                statusLabel.setText("⚠ กรุณาใส่ชื่อของคุณก่อน!");
                statusLabel.setForeground(new Color(0xE84070));
                return;
            }
            if (name.length() > 12) {
                shake(nameField);
                statusLabel.setText("⚠ ชื่อยาวเกินไป (สูงสุด 12 ตัวอักษร)");
                statusLabel.setForeground(new Color(0xE84070));
                return;
            }
            GirlData chosen = GIRLS[selectedGirl];
            playConfirmAnimation(() -> onConfirm.accept(name, chosen));
        });

        footer.add(statusLabel, BorderLayout.CENTER);
        footer.add(confirmBtn,  BorderLayout.EAST);
        return footer;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Logic helpers
    // ═══════════════════════════════════════════════════════════════════════════
    private void selectGirl(int idx) {
        selectedGirl = idx;
        for (int i = 0; i < girlCards.length; i++) girlCards[i].setSelected(i == idx);
        updateStatus();
        checkCanConfirm();
    }

    private void updateStatus() {
        if (selectedGirl >= 0) {
            GirlData g = GIRLS[selectedGirl];
            statusLabel.setText("คุณเลือก " + g.emoji + " " + g.name
                    + " (" + g.nickname + ")  —  " + g.trait1 + "  " + g.trait2 + "  " + g.trait3);
            statusLabel.setForeground(g.primary.darker());
        } else if (hoveredGirl >= 0) {
            GirlData g = GIRLS[hoveredGirl];
            statusLabel.setText("✦ " + g.name + " — " + g.description.replace('\n', ' '));
            statusLabel.setForeground(new Color(0xA06080));
        } else {
            statusLabel.setText("← คลิกเลือกสาวที่ชอบ แล้วกดยืนยัน");
            statusLabel.setForeground(new Color(0xC070A0));
        }
    }

    private void checkCanConfirm() {
        boolean ok = selectedGirl >= 0;
        confirmBtn.setEnabled(ok);
        confirmBtn.setBackground(ok ? new Color(0xE8759A) : new Color(0xD4A8B8));
    }

    private void shake(JComponent comp) {
        Point orig = comp.getLocation();
        Timer t = new Timer(30, null);
        int[] steps = {-8, 8, -6, 6, -4, 4, -2, 2, 0};
        final int[] i = {0};
        t.addActionListener(e -> {
            if (i[0] >= steps.length) { t.stop(); comp.setLocation(orig); return; }
            comp.setLocation(orig.x + steps[i[0]], orig.y);
            i[0]++;
        });
        t.start();
    }

    private void playConfirmAnimation(Runnable callback) {
        JPanel overlay = new JPanel() {
            float alpha = 0f;
            { setOpaque(false); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.setColor(new Color(0xFFD6E2));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                        Math.min(1f, alpha * 3)));
                g2.setColor(new Color(0x8B2560));
                g2.setFont(new Font("Tahoma", Font.BOLD, 32));
                String msg = "ยินดีต้อนรับ  " + GIRLS[selectedGirl].emoji;
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2,
                        getHeight() / 2 - 16);
                g2.setFont(new Font("Tahoma", Font.ITALIC, 18));
                g2.setColor(new Color(0xC06090));
                String sub = nameField.getText().trim() + "  ♡  " + GIRLS[selectedGirl].name;
                fm = g2.getFontMetrics();
                g2.drawString(sub, (getWidth() - fm.stringWidth(sub)) / 2,
                        getHeight() / 2 + 24);
                g2.dispose();
            }
        };
        overlay.setBounds(0, 0, getWidth(), getHeight());
        setLayout(null);
        add(overlay, 0);

        Timer[] ref = {null};
        float[] a = {0f};
        ref[0] = new Timer(16, e -> {
            a[0] += 0.04f;
            overlay.alpha = Math.min(1f, a[0]);
            overlay.repaint();
            if (a[0] >= 1.2f) {
                ref[0].stop();
                SwingUtilities.invokeLater(callback);
            }
        });
        ref[0].start();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Utility
    // ═══════════════════════════════════════════════════════════════════════════
    private static JButton createFlatButton(String text, Color bg, Color fg, Font font) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = isEnabled()
                        ? (getModel().isPressed() ? bg.darker()
                            : getModel().isRollover() ? bg.brighter() : bg)
                        : new Color(0xD4A8B8);
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(font);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Inner class — GirlCard
    // ═══════════════════════════════════════════════════════════════════════════
    static class GirlCard extends JPanel {
        private final GirlData data;
        private boolean selected = false;
        private boolean hovered  = false;
        private float   glowAnim = 0f;
        private Timer   glowTimer;

        GirlCard(GirlData data) {
            this.data = data;
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(280, 380));
            setLayout(new BorderLayout(0, 0));
            buildContent();
            startGlowTimer();
        }

        private void buildContent() {
            AvatarPanel avatar = new AvatarPanel(data);
            avatar.setPreferredSize(new Dimension(0, 200));

            JPanel info = new JPanel();
            info.setOpaque(false);
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setBorder(new EmptyBorder(10, 16, 14, 16));

            JLabel nameLbl = new JLabel(data.emoji + "  " + data.name, SwingConstants.CENTER);
            nameLbl.setFont(new Font("Tahoma", Font.BOLD, 20));
            nameLbl.setForeground(data.primary.darker().darker());
            nameLbl.setAlignmentX(CENTER_ALIGNMENT);

            JLabel nickLbl = new JLabel("\"" + data.nickname + "\"", SwingConstants.CENTER);
            nickLbl.setFont(new Font("Tahoma", Font.ITALIC, 13));
            nickLbl.setForeground(data.accent.darker());
            nickLbl.setAlignmentX(CENTER_ALIGNMENT);

            JTextArea desc = new JTextArea(data.description);
            desc.setFont(new Font("Tahoma", Font.PLAIN, 12));
            desc.setForeground(new Color(0x705060));
            desc.setOpaque(false);
            desc.setEditable(false);
            desc.setFocusable(false);
            desc.setWrapStyleWord(true);
            desc.setLineWrap(true);
            desc.setAlignmentX(CENTER_ALIGNMENT);
            desc.setMaximumSize(new Dimension(230, 60));

            JPanel traits = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            traits.setOpaque(false);
            for (String t : new String[]{data.trait1, data.trait2, data.trait3}) {
                JLabel tl = new JLabel(t);
                tl.setFont(new Font("Tahoma", Font.PLAIN, 11));
                tl.setForeground(data.accent.darker());
                tl.setBackground(new Color(data.secondary.getRed(),
                        data.secondary.getGreen(), data.secondary.getBlue(), 180));
                tl.setOpaque(true);
                tl.setBorder(new EmptyBorder(3, 8, 3, 8));
                tl.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                traits.add(tl);
            }

            info.add(nameLbl);
            info.add(Box.createVerticalStrut(2));
            info.add(nickLbl);
            info.add(Box.createVerticalStrut(8));
            info.add(desc);
            info.add(Box.createVerticalStrut(8));
            info.add(traits);

            add(avatar, BorderLayout.NORTH);
            add(info,   BorderLayout.CENTER);
        }

        private void startGlowTimer() {
            glowTimer = new Timer(40, e -> {
                float target = (selected || hovered) ? 1f : 0f;
                glowAnim += (target - glowAnim) * 0.12f;
                repaint();
            });
            glowTimer.start();
        }

        void setSelected(boolean s) { this.selected = s; repaint(); }
        void setHovered(boolean h)  { this.hovered  = h; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            RoundRectangle2D rr = new RoundRectangle2D.Float(3, 3, w - 6, h - 6, 24, 24);

            GradientPaint bg = new GradientPaint(0, 0, new Color(0xFFF8FC), 0, h, data.secondary);
            g2.setPaint(bg);
            g2.fill(rr);

            if (glowAnim > 0.02f) {
                int glow = (int) (glowAnim * 20);
                for (int r = glow; r >= 1; r -= 2) {
                    float a = 0.04f * glowAnim * (glow - r + 1f) / glow;
                    g2.setColor(new Color(data.primary.getRed(), data.primary.getGreen(),
                            data.primary.getBlue(), (int)(a * 255)));
                    g2.setStroke(new BasicStroke(r * 2));
                    g2.draw(rr);
                }
            }

            Color borderColor = selected ? data.accent
                    : hovered  ? data.primary
                    : new Color(data.primary.getRed(), data.primary.getGreen(),
                                data.primary.getBlue(), 80);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(selected ? 3f : 1.5f));
            g2.draw(rr);

            if (selected) {
                g2.setColor(data.accent);
                g2.fill(new Ellipse2D.Float(w - 38, 10, 28, 28));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Tahoma", Font.BOLD, 16));
                g2.drawString("✓", w - 30, 30);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Inner class — AvatarPanel
    // ═══════════════════════════════════════════════════════════════════════════
    static class AvatarPanel extends JPanel {
        private final GirlData data;
        private final BufferedImage cache;

        AvatarPanel(GirlData data) {
            this.data = data;
            setOpaque(false);
            cache = renderAvatar(data, 280, 200);
        }

        private static BufferedImage renderAvatar(GirlData data, int w, int h) {
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint bg = new GradientPaint(0, 0, data.secondary, 0, h, data.primary);
            g.setPaint(bg);
            g.fillRoundRect(0, 0, w, h, 24, 24);

            g.setColor(new Color(255, 255, 255, 40));
            g.fillOval(-20, -20, 120, 120);
            g.fillOval(w - 60, h - 60, 100, 100);

            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
            FontMetrics fm = g.getFontMetrics();
            String em = data.emoji;
            int ew = fm.stringWidth(em);
            g.drawString(em, (w - ew) / 2, h / 2 + 30);

            GradientPaint shadow = new GradientPaint(0, h * 0.7f,
                    new Color(0, 0, 0, 0), 0, h, new Color(0, 0, 0, 30));
            g.setPaint(shadow);
            g.fillRoundRect(0, 0, w, h, 24, 24);

            g.setColor(new Color(255, 255, 255, 80));
            g.setStroke(new BasicStroke(1.5f));
            g.drawLine(20, h - 1, w - 20, h - 1);

            g.dispose();
            return img;
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (cache != null) g.drawImage(cache, 0, 0, getWidth(), getHeight(), null);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Inner class — PetalBackgroundPanel
    // ═══════════════════════════════════════════════════════════════════════════
    static class PetalBackgroundPanel extends JPanel {
        private final Color c1, c2;
        PetalBackgroundPanel(Color c1, Color c2) {
            this.c1 = c1; this.c2 = c2;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Inner class — RoundedBorder
    // ═══════════════════════════════════════════════════════════════════════════
    static class RoundedBorder implements Border {
        private final int   radius;
        private final Color color;
        private final int   thickness;

        RoundedBorder(int radius, Color color, int thickness) {
            this.radius = radius; this.color = color; this.thickness = thickness;
        }
        @Override public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
        @Override public boolean isBorderOpaque() { return false; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Float(x + thickness / 2f, y + thickness / 2f,
                    w - thickness, h - thickness, radius, radius));
            g2.dispose();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // main() — ทดสอบเดี่ยว
    // ═══════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("First Love ♡ — New Game Setup");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            CardLayout cl  = new CardLayout();
            JPanel     con = new JPanel(cl);

            JPanel menu = new JPanel();
            menu.setBackground(new Color(0xFFD6E2));
            JLabel ml = new JLabel("[ MENU PLACEHOLDER ]");
            ml.setFont(new Font("Tahoma", Font.BOLD, 24));
            menu.add(ml);

            // ✅ ใช้ setupgame แทน NewGameSetup
            setupgame setup = new setupgame(cl, con, (name, girl) -> {
                JOptionPane.showMessageDialog(null,
                        "ยินดีต้อนรับ  " + name + "  ♡\n"
                        + "คุณเลือก: " + girl.name + " (" + girl.nickname + ")\n"
                        + "ID: " + girl.id,
                        "เริ่มเกม!", JOptionPane.INFORMATION_MESSAGE);
                cl.show(con, "MENU");
            });

            con.add(menu,  "MENU");
            con.add(setup, "NEW_GAME");
            cl.show(con, "NEW_GAME");

            frame.add(con);
            frame.setVisible(true);
        });
    }
}
