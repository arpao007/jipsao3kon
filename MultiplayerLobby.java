import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MultiplayerLobby.java
 * หน้า Lobby ผู้เล่นหลายคน
 */
public class MultiplayerLobby extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel     mainContainer;

    // ── Player slots ──────────────────────────────────────────────────────────
    private static final int MAX_PLAYERS = 4;
    private final PlayerSlot[] slots = new PlayerSlot[MAX_PLAYERS];
    private JLabel statusLabel;
    private JButton startBtn;

    // ────────────────────────────────────────────────────────────────────────────
    public MultiplayerLobby(CardLayout cardLayout, JPanel mainContainer) {
        this.cardLayout    = cardLayout;
        this.mainContainer = mainContainer;

        setLayout(new BorderLayout());
        setBackground(new Color(0xFFF0F5));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Header
    // ════════════════════════════════════════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel header = new GradPanel(new Color(0xFFD6E2), new Color(0xFFC0D8));
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 100));
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        JButton backBtn = flatBtn("← กลับเมนู", new Color(0xE8759A), Color.WHITE);
        backBtn.addActionListener(e -> cardLayout.show(mainContainer, "MENU"));
        backBtn.setPreferredSize(new Dimension(130, 36));

        JLabel title = new JLabel("⚡  ผู้เล่นหลายคน", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 28));
        title.setForeground(new Color(0x8B2560));

        JLabel sub = new JLabel("เพิ่มผู้เล่น 2–4 คนแล้วกดเริ่ม!", SwingConstants.CENTER);
        sub.setFont(new Font("Tahoma", Font.ITALIC, 14));
        sub.setForeground(new Color(0xC06090));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setOpaque(false);
        titleBox.add(title); titleBox.add(sub);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        left.setOpaque(false);
        left.add(backBtn);

        header.add(left, BorderLayout.WEST);
        header.add(titleBox, BorderLayout.CENTER);
        return header;
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Center — 4 Player Slots
    // ════════════════════════════════════════════════════════════════════════════
    private JPanel buildCenter() {
        JPanel grid = new JPanel(new GridLayout(1, 4, 16, 0));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(24, 24, 16, 24));

        Color[][] palette = {
            {new Color(0xFF9BB5), new Color(0xFFD6E2)},
            {new Color(0xB39DDB), new Color(0xE8DEF8)},
            {new Color(0x81D4FA), new Color(0xD4F1FF)},
            {new Color(0xA5D6A7), new Color(0xDCEEDC)},
        };
        String[] playerEmoji = {"🌸","💜","💙","💚"};

        for (int i = 0; i < MAX_PLAYERS; i++) {
            slots[i] = new PlayerSlot(i + 1, palette[i][0], palette[i][1], playerEmoji[i],
                    this::updateStatus);
            grid.add(slots[i]);
        }

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(grid, BorderLayout.CENTER);
        return wrap;
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Footer
    // ════════════════════════════════════════════════════════════════════════════
    private JPanel buildFooter() {
        JPanel footer = new GradPanel(new Color(0xFFE8F0), new Color(0xFFD0E4));
        footer.setLayout(new BorderLayout());
        footer.setPreferredSize(new Dimension(0, 72));
        footer.setBorder(new EmptyBorder(12, 24, 12, 24));

        statusLabel = new JLabel("เพิ่มผู้เล่นอย่างน้อย 2 คนเพื่อเริ่ม", SwingConstants.LEFT);
        statusLabel.setFont(new Font("Tahoma", Font.ITALIC, 14));
        statusLabel.setForeground(new Color(0xC070A0));

        startBtn = flatBtn("เริ่มเลย!  ▶", new Color(0xE8759A), Color.WHITE);
        startBtn.setPreferredSize(new Dimension(140, 46));
        startBtn.setFont(new Font("Tahoma", Font.BOLD, 16));
        startBtn.setEnabled(false);
        startBtn.addActionListener(e -> onStart());

        footer.add(statusLabel, BorderLayout.CENTER);
        footer.add(startBtn,    BorderLayout.EAST);
        return footer;
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Logic
    // ════════════════════════════════════════════════════════════════════════════
    private void updateStatus() {
        int active = 0;
        StringBuilder names = new StringBuilder();
        for (PlayerSlot s : slots) {
            if (s.isActive()) {
                active++;
                if (names.length() > 0) names.append(", ");
                names.append(s.getPlayerName());
            }
        }
        boolean canStart = active >= 2;
        startBtn.setEnabled(canStart);
        startBtn.setBackground(canStart ? new Color(0xE8759A) : new Color(0xD4A8B8));

        if (active == 0) {
            statusLabel.setText("เพิ่มผู้เล่นอย่างน้อย 2 คนเพื่อเริ่ม");
            statusLabel.setForeground(new Color(0xC070A0));
        } else if (active == 1) {
            statusLabel.setText("ต้องการผู้เล่นเพิ่มอีก 1 คน…");
            statusLabel.setForeground(new Color(0xE89040));
        } else {
            statusLabel.setText("พร้อมแล้ว! ผู้เล่น: " + names);
            statusLabel.setForeground(new Color(0x607040));
        }
    }

    private void onStart() {
        StringBuilder msg = new StringBuilder("เริ่มเกมผู้เล่นหลายคน!\n\nผู้เล่น:\n");
        int n = 0;
        for (PlayerSlot s : slots) {
            if (s.isActive()) {
                n++;
                msg.append("  ").append(n).append(". ").append(s.getPlayerName()).append("\n");
            }
        }
        JOptionPane.showMessageDialog(this, msg.toString(),
                "ผู้เล่นหลายคน — เริ่มเกม!", JOptionPane.INFORMATION_MESSAGE);
        // TODO: เชื่อมกับ MultiplayerGamePanel
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Inner — PlayerSlot
    // ════════════════════════════════════════════════════════════════════════════
    static class PlayerSlot extends JPanel {
        private final int playerNum;
        private final Color primary, secondary;
        private final String emoji;
        private final Runnable onChange;

        private boolean   active = false;
        private JTextField nameField;
        private JButton   joinBtn, removeBtn;
        private JLabel    statusLbl;

        PlayerSlot(int num, Color primary, Color secondary, String emoji, Runnable onChange) {
            this.playerNum = num; this.primary = primary;
            this.secondary = secondary; this.emoji = emoji;
            this.onChange  = onChange;
            setOpaque(false);
            setLayout(new BorderLayout(0, 8));
            setBorder(new EmptyBorder(8, 8, 8, 8));
            build();
        }

        private void build() {
            // Card background
            JPanel card = new JPanel(new BorderLayout(0, 10)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(0, 0,
                            active ? primary : new Color(0xF0E8EC),
                            0, getHeight(),
                            active ? secondary : new Color(0xE8E0E4));
                    g2.setPaint(gp);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                    g2.setColor(active ? primary : new Color(primary.getRed(),
                            primary.getGreen(), primary.getBlue(), 80));
                    g2.setStroke(new java.awt.BasicStroke(active ? 2.5f : 1f));
                    g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 20, 20));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            card.setOpaque(false);
            card.setBorder(new EmptyBorder(16, 12, 16, 12));

            // Emoji avatar
            JLabel avatarLbl = new JLabel(emoji, SwingConstants.CENTER);
            avatarLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            avatarLbl.setPreferredSize(new Dimension(0, 70));

            // Player number label
            JLabel numLbl = new JLabel("ผู้เล่น " + playerNum, SwingConstants.CENTER);
            numLbl.setFont(new Font("Tahoma", Font.BOLD, 15));
            numLbl.setForeground(new Color(0x8B2560));

            // Name field
            nameField = new JTextField("ผู้เล่น " + playerNum);
            nameField.setFont(new Font("Tahoma", Font.PLAIN, 14));
            nameField.setHorizontalAlignment(SwingConstants.CENTER);
            nameField.setEnabled(false);
            nameField.setBackground(new Color(0xFFF8FC));
            nameField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(primary.getRed(),
                            primary.getGreen(), primary.getBlue(), 120), 1, true),
                    new EmptyBorder(4, 8, 4, 8)));

            // Status label
            statusLbl = new JLabel("— ว่าง —", SwingConstants.CENTER);
            statusLbl.setFont(new Font("Tahoma", Font.ITALIC, 12));
            statusLbl.setForeground(new Color(0xB090A0));

            // Buttons
            joinBtn = flatBtn2("+ เข้าร่วม", primary, Color.WHITE);
            removeBtn = flatBtn2("✕ ออก", new Color(0xE05070), Color.WHITE);
            removeBtn.setVisible(false);

            joinBtn.addActionListener(e -> {
                active = true;
                nameField.setEnabled(true);
                nameField.requestFocus();
                nameField.selectAll();
                joinBtn.setVisible(false);
                removeBtn.setVisible(true);
                statusLbl.setText("✓ พร้อมแล้ว");
                statusLbl.setForeground(primary.darker().darker());
                card.repaint();
                onChange.run();
            });
            removeBtn.addActionListener(e -> {
                active = false;
                nameField.setEnabled(false);
                joinBtn.setVisible(true);
                removeBtn.setVisible(false);
                statusLbl.setText("— ว่าง —");
                statusLbl.setForeground(new Color(0xB090A0));
                card.repaint();
                onChange.run();
            });

            JPanel btnRow = new JPanel(new GridLayout(1, 1));
            btnRow.setOpaque(false);
            btnRow.add(joinBtn);
            btnRow.add(removeBtn);

            JPanel top = new JPanel(new GridLayout(3, 1, 0, 4));
            top.setOpaque(false);
            top.add(numLbl);
            top.add(nameField);
            top.add(statusLbl);

            card.add(avatarLbl, BorderLayout.NORTH);
            card.add(top,       BorderLayout.CENTER);
            card.add(btnRow,    BorderLayout.SOUTH);

            setLayout(new BorderLayout());
            add(card, BorderLayout.CENTER);
        }

        boolean isActive()       { return active; }
        String  getPlayerName()  { return nameField.getText().trim(); }

        private static JButton flatBtn2(String text, Color bg, Color fg) {
            JButton b = new JButton(text) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color c = getModel().isRollover() ? bg.brighter() : bg;
                    g2.setColor(c);
                    g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),16,16));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            b.setFont(new Font("Tahoma", Font.BOLD, 13));
            b.setForeground(fg);
            b.setBackground(bg);
            b.setOpaque(false);
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return b;
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Helpers
    // ════════════════════════════════════════════════════════════════════════════
    private static JButton flatBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = isEnabled()
                        ? (getModel().isPressed() ? bg.darker()
                            : getModel().isRollover() ? bg.brighter() : bg)
                        : new Color(0xD4A8B8);
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),20,20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Tahoma", Font.BOLD, 14));
        b.setForeground(fg);
        b.setBackground(bg);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Inner — GradPanel (gradient background)
    // ════════════════════════════════════════════════════════════════════════════
    static class GradPanel extends JPanel {
        private final Color c1, c2;
        GradPanel(Color c1, Color c2) { this.c1=c1; this.c2=c2; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0,0,c1,getWidth(),getHeight(),c2));
            g2.fillRect(0,0,getWidth(),getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
