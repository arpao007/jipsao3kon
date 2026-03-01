import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;


public class SettingPanel extends JPanel {

    private static final Color BG_TOP     = new Color(0xF7D6E0);
    private static final Color BG_BOT     = new Color(0xD9AED0);
    private static final Color PINK_DEEP  = new Color(0xE8759A);
    private static final Color PINK_LIGHT = new Color(0xF5A8C5);
    private static final Color LILAC_DARK = new Color(0xA076BB);
    private static final Color LILAC      = new Color(0xC9A0DC);
    private static final Color TEXT_WHITE = new Color(0xFFF5FA);

    public enum DisplayMode { WINDOWED, BORDERLESS, FULLSCREEN }
    private DisplayMode currentMode = DisplayMode.WINDOWED;

    public enum Resolution {
        R_1280x720  ("1280 × 720",   1280, 720),
        R_1366x768  ("1366 × 768",   1366, 768),
        R_1440x900  ("1440 × 900",   1440, 900),
        R_1600x900  ("1600 × 900",   1600, 900),
        R_1920x1080 ("1920 × 1080",  1920, 1080),
        R_2560x1440 ("2560 × 1440",  2560, 1440),
        R_2560x1600 ("2560 × 1600",  2560, 1600);

        public final String label;
        public final int w, h;
        Resolution(String label, int w, int h) { this.label = label; this.w = w; this.h = h; }
        @Override public String toString() { return label; }
    }
    private Resolution currentResolution = Resolution.R_1920x1080;

    private int soundVolume = 70;

    private final CardLayout cardLayout;
    private final JPanel     mainContainer;
    private JFrame           gameFrame;

    public interface SettingsListener {
        void onDisplayModeChanged(DisplayMode mode, JFrame frame);
        void onResolutionChanged(Resolution res, JFrame frame);
        void onVolumeChanged(int volume);
    }
    private SettingsListener settingsListener;

    private final List<Petal> petals = new ArrayList<>();
    private Timer animTimer;
    private float animTime = 0f;

    public SettingPanel(CardLayout cardLayout, JPanel mainContainer) {
        this.cardLayout    = cardLayout;
        this.mainContainer = mainContainer;
        setLayout(null);
        setPreferredSize(new Dimension(1200, 800));
        initPetals();
        buildUI();
        startAnimation();
    }

    public void setGameFrame(JFrame frame) { this.gameFrame = frame; }
    public void setSettingsListener(SettingsListener l) { this.settingsListener = l; }
    public DisplayMode getCurrentMode() { return currentMode; }
    public Resolution getCurrentResolution() { return currentResolution; }
    public int getSoundVolume() { return soundVolume; }

    private static class Petal {
        float x, y, size, speed, phase, rot, rotSpeed; Color color;
        Petal(int w) {
            Random r = new Random();
            x=r.nextFloat()*w; y=-r.nextFloat()*800;
            size=6+r.nextFloat()*14; speed=0.6f+r.nextFloat()*1.2f;
            phase=r.nextFloat()*(float)(Math.PI*2);
            rot=r.nextFloat()*360; rotSpeed=0.5f+r.nextFloat()*2f;
            Color[] c={new Color(0xF8BBD9),new Color(0xE8A0C0),new Color(0xD4B0E0),new Color(0xFFDDEE),new Color(0xC8A0D8)};
            color=c[r.nextInt(c.length)];
        }
        void update(float t){y+=speed;x+=(float)(Math.sin(t*0.03+phase)*0.8);rot+=rotSpeed;if(y>820)y=-20;}
    }
    private void initPetals(){for(int i=0;i<40;i++)petals.add(new Petal(1200));}
    private void startAnimation(){
        animTimer=new Timer(16,e->{animTime+=0.05f;for(Petal p:petals)p.update(animTime);repaint();});
        animTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0,0,BG_TOP,0,800,BG_BOT));
        g2.fillRect(0,0,1200,800);
        int[][]bk={{120,200,180},{900,100,220},{200,600,140},{1050,500,160},{500,700,200},{700,150,120}};
        for(int[]b:bk){
            g2.setPaint(new RadialGradientPaint(b[0],b[1],b[2],new float[]{0f,1f},
                new Color[]{new Color(255,200,230,40),new Color(255,200,230,0)}));
            g2.fillOval(b[0]-b[2],b[1]-b[2],b[2]*2,b[2]*2);
        }
        for(Petal p:petals){
            Graphics2D pg=(Graphics2D)g2.create();
            pg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            pg.translate(p.x,p.y);pg.rotate(Math.toRadians(p.rot));
            pg.setColor(new Color(p.color.getRed(),p.color.getGreen(),p.color.getBlue(),200));
            Path2D path=new Path2D.Float();
            path.moveTo(0,-p.size);
            path.curveTo(p.size*0.6f,-p.size*0.5f,p.size*0.6f,p.size*0.5f,0,p.size*0.3f);
            path.curveTo(-p.size*0.6f,p.size*0.5f,-p.size*0.6f,-p.size*0.5f,0,-p.size);
            pg.fill(path);pg.dispose();
        }
        Path2D wave=new Path2D.Float();
        wave.moveTo(0,0);wave.curveTo(300,30,600,-10,900,25);wave.curveTo(1050,38,1150,10,1200,20);
        wave.lineTo(1200,0);wave.closePath();
        g2.setColor(new Color(0xF9C4DA));g2.fill(wave);
    }

    private void buildUI() {

        // Title
        JLabel titleLbl = new JLabel("Settings", SwingConstants.CENTER);
        titleLbl.setFont(new Font("Tahoma", Font.BOLD, 42));
        titleLbl.setForeground(PINK_DEEP);
        titleLbl.setBounds(0, 35, 1200, 60);
        add(titleLbl);

        // Card bg
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,240,248,210));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),40,40);
                g2.setStroke(new BasicStroke(2.5f));
                g2.setColor(new Color(0xE8759A,false));
                g2.drawRoundRect(2,2,getWidth()-4,getHeight()-4,38,38);
                g2.setPaint(new GradientPaint(0,0,new Color(255,255,255,70),getWidth(),getHeight(),new Color(255,200,230,0)));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),40,40);
            }
        };
        card.setOpaque(false);
        card.setBounds(220, 100, 760, 650);
        add(card);

        addSectionTitle(card, "◆  Display Mode", 40, 28);
        addSepLine(card, 40, 64, 620);

        boolean[][] selState = {
            {currentMode == DisplayMode.WINDOWED},
            {currentMode == DisplayMode.BORDERLESS},
            {currentMode == DisplayMode.FULLSCREEN}
        };
        String[] modeLabels = {"Windowed", "Borderless", "Fullscreen"};
        DisplayMode[] modes = {DisplayMode.WINDOWED, DisplayMode.BORDERLESS, DisplayMode.FULLSCREEN};

        JLabel modeDesc = new JLabel(getModeDesc(currentMode), SwingConstants.CENTER);
        modeDesc.setFont(new Font("Tahoma", Font.ITALIC, 13));
        modeDesc.setForeground(new Color(0x9060A0));
        modeDesc.setBounds(30, 148, 640, 22);
        card.add(modeDesc);

        int bw=170, bh=52, gap=15, totalW=bw*3+gap*2;
        int bx=(700-totalW)/2;
        JPanel[] toggles = new JPanel[3];
        for(int i=0;i<3;i++){
            final int idx=i;
            toggles[i] = makeToggle(modeLabels[i], selState[i], () -> {
                currentMode = modes[idx];
                for(int j=0;j<3;j++) selState[j][0]=(modes[j]==currentMode);
                for(JPanel t:toggles) t.repaint();
                modeDesc.setText(getModeDesc(currentMode));
                if(settingsListener!=null && gameFrame!=null)
                    settingsListener.onDisplayModeChanged(currentMode, gameFrame);
            });
            toggles[i].setBounds(bx+i*(bw+gap), 82, bw, bh);
            card.add(toggles[i]);
        }

        addSectionTitle(card, "◆  Sound Volume", 40, 195);
        addSepLine(card, 40, 231, 620);

        JLabel volNum = new JLabel(soundVolume+"%", SwingConstants.CENTER);
        volNum.setFont(new Font("Tahoma", Font.BOLD, 30));
        volNum.setForeground(PINK_DEEP);
        volNum.setBounds(280, 244, 140, 42);
        card.add(volNum);

        JSlider slider = new JSlider(0, 100, soundVolume){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                int th=10, ty=getHeight()/2-th/2;
                g2.setColor(new Color(0xE0C0D8));
                g2.fillRoundRect(14,ty,getWidth()-28,th,th,th);
                int fw=(int)((getValue()/100.0)*(getWidth()-28));
                if(fw>0){
                    g2.setPaint(new GradientPaint(14,0,PINK_LIGHT,14+fw,0,PINK_DEEP));
                    g2.fillRoundRect(14,ty,fw,th,th,th);
                }
                int tx2=14+fw-14;
                tx2=Math.max(0,Math.min(tx2,getWidth()-28));
                g2.setColor(Color.WHITE); g2.fillOval(tx2,getHeight()/2-14,28,28);
                g2.setStroke(new BasicStroke(2.5f)); g2.setColor(PINK_DEEP);
                g2.drawOval(tx2,getHeight()/2-14,28,28);
                g2.fillOval(tx2+9,getHeight()/2-5,10,10);
            }
        };
        slider.setOpaque(false);
        slider.setFocusable(false);
        slider.setBounds(50, 294, 600, 50);
        slider.addChangeListener(e -> {
            soundVolume=slider.getValue();
            volNum.setText(soundVolume+"%");
            if(settingsListener!=null) settingsListener.onVolumeChanged(soundVolume);
        });
        card.add(slider);

        JLabel l0=new JLabel("0%"); l0.setFont(new Font("Tahoma",Font.PLAIN,12));
        l0.setForeground(new Color(0xB090C0)); l0.setBounds(46,346,30,18); card.add(l0);
        JLabel l100=new JLabel("100%"); l100.setFont(new Font("Tahoma",Font.PLAIN,12));
        l100.setForeground(new Color(0xB090C0)); l100.setBounds(617,346,38,18); card.add(l100);

        card.add(makeSmallBtn("Mute", ()->{slider.setValue(0);}, 50, 374, 120, 38));
        card.add(makeSmallBtn("Max",  ()->{slider.setValue(100);}, 530, 374, 120, 38));

        // ── Resolution section ──
        addSectionTitle(card, "◆  Window Size", 40, 400);
        addSepLine(card, 40, 436, 680);

        JLabel resDesc = new JLabel(currentResolution.label + "  (เฉพาะ Windowed / Borderless)", SwingConstants.CENTER);
        resDesc.setFont(new Font("Tahoma", Font.ITALIC, 13));
        resDesc.setForeground(new Color(0x9060A0));
        resDesc.setBounds(30, 446, 700, 22);
        card.add(resDesc);

        Resolution[] resList = Resolution.values();
        int rbw = 148, rbh = 40, rbgap = 10;
        int totalRW = rbw * 4 + rbgap * 3;
        int rbx = (760 - totalRW) / 2;
        JPanel[] rToggles = new JPanel[resList.length];
        boolean[][] rSel = new boolean[resList.length][1];
        for (int i = 0; i < resList.length; i++) rSel[i][0] = (resList[i] == currentResolution);

        // row 1 (4 ตัว)
        for (int i = 0; i < 4 && i < resList.length; i++) {
            final int idx = i;
            rToggles[i] = makeToggle(resList[i].label, rSel[i], () -> {
                currentResolution = resList[idx];
                for (int j = 0; j < resList.length; j++) rSel[j][0] = (j == idx);
                for (JPanel t : rToggles) if (t != null) t.repaint();
                resDesc.setText(currentResolution.label + "  (เฉพาะ Windowed / Borderless)");
                if (settingsListener != null && gameFrame != null)
                    settingsListener.onResolutionChanged(currentResolution, gameFrame);
            });
            rToggles[i].setBounds(rbx + i * (rbw + rbgap), 474, rbw, rbh);
            card.add(rToggles[i]);
        }
        // row 2 (ที่เหลือ)
        int row2x = rbx + (rbw + rbgap); // center เหลือ 3 ตัว
        for (int i = 4; i < resList.length; i++) {
            final int idx = i;
            rToggles[i] = makeToggle(resList[i].label, rSel[i], () -> {
                currentResolution = resList[idx];
                for (int j = 0; j < resList.length; j++) rSel[j][0] = (j == idx);
                for (JPanel t : rToggles) if (t != null) t.repaint();
                resDesc.setText(currentResolution.label + "  (เฉพาะ Windowed / Borderless)");
                if (settingsListener != null && gameFrame != null)
                    settingsListener.onResolutionChanged(currentResolution, gameFrame);
            });
            rToggles[i].setBounds(row2x + (i - 4) * (rbw + rbgap), 524, rbw, rbh);
            card.add(rToggles[i]);
        }

        JPanel back = makeMenuBtn("← กลับเมนู", LILAC_DARK, LILAC, 230, 580, 300, 52, ()->{
            cardLayout.show(mainContainer,"MENU");
        });
        card.add(back);

        JLabel credit=new JLabel("♡  First Love Game  •  v1.0  ♡",SwingConstants.CENTER);
        credit.setFont(new Font("Tahoma",Font.ITALIC,14));
        credit.setForeground(new Color(0x9060A0));
        credit.setBounds(0,755,1200,30);
        add(credit);
    }

    private JPanel makeToggle(String label, boolean[] selRef, Runnable action) {
        return new JPanel(null){
            boolean hover=false;
            { setOpaque(false); setCursor(new Cursor(Cursor.HAND_CURSOR));
              addMouseListener(new MouseAdapter(){
                  @Override public void mouseEntered(MouseEvent e){hover=true;repaint();}
                  @Override public void mouseExited(MouseEvent e){hover=false;repaint();}
                  @Override public void mouseReleased(MouseEvent e){action.run();}
              });
            }
            @Override protected void paintComponent(Graphics g){
                boolean sel=selRef[0];
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill=sel?PINK_DEEP:hover?new Color(0xF0C8DC):new Color(0xF8E8F0);
                g2.setColor(fill); g2.fillRoundRect(0,0,getWidth(),getHeight(),24,24);
                if(sel){g2.setColor(new Color(255,255,255,60));g2.fillRoundRect(4,4,getWidth()-8,getHeight()/2-4,16,16);}
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(sel?PINK_DEEP:new Color(0xD0A0C0));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,23,23);
                g2.setFont(new Font("Tahoma",Font.BOLD,15));
                g2.setColor(sel?TEXT_WHITE:new Color(0x7050A0));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(label,(getWidth()-fm.stringWidth(label))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
    }

    private JPanel makeSmallBtn(String label, Runnable action, int x, int y, int w, int h){
        JPanel p=new JPanel(null){
            boolean hov=false;
            {setOpaque(false);setCursor(new Cursor(Cursor.HAND_CURSOR));
             addMouseListener(new MouseAdapter(){
                 @Override public void mouseEntered(MouseEvent e){hov=true;repaint();}
                 @Override public void mouseExited(MouseEvent e){hov=false;repaint();}
                 @Override public void mouseReleased(MouseEvent e){action.run();}
             });
            }
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov?new Color(0xF0C8DC):new Color(0xF8E8F0));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),16,16);
                g2.setStroke(new BasicStroke(1.5f));g2.setColor(new Color(0xD0A0C0));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,15,15);
                g2.setFont(new Font("Tahoma",Font.BOLD,13));g2.setColor(new Color(0x8050A0));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(label,(getWidth()-fm.stringWidth(label))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        p.setBounds(x,y,w,h); return p;
    }

    private JPanel makeMenuBtn(String text,Color cd,Color cl,int x,int y,int w,int h,Runnable action){
        JPanel btn=new JPanel(null){
            boolean hov=false,prs=false;float pls=0;Timer pt;
            {setOpaque(false);setCursor(new Cursor(Cursor.HAND_CURSOR));
             addMouseListener(new MouseAdapter(){
                 @Override public void mouseEntered(MouseEvent e){hov=true;if(pt==null){pt=new Timer(16,ev->{pls+=0.15f;repaint();});pt.start();}}
                 @Override public void mouseExited(MouseEvent e){hov=false;prs=false;if(pt!=null){pt.stop();pt=null;}pls=0;repaint();}
                 @Override public void mousePressed(MouseEvent e){prs=true;repaint();}
                 @Override public void mouseReleased(MouseEvent e){prs=false;repaint();if(hov)action.run();}
             });
            }
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                int oy=prs?4:hov?-3:0,px=prs?0:hov?(int)(Math.sin(pls)*2):0;
                if(!prs){g2.setColor(new Color(0,0,0,30));g2.fillRoundRect(px+5,oy+8,w,h,36,36);}
                g2.setPaint(new GradientPaint(0,oy,hov?cl.brighter():cl,0,oy+h,hov?cd:cd.darker()));
                g2.fillRoundRect(px,oy,w,h,36,36);
                g2.setColor(new Color(255,255,255,hov?80:50));g2.fillRoundRect(px+6,oy+5,w-12,h/2-4,24,24);
                g2.setStroke(new BasicStroke(2.5f));g2.setColor(new Color(255,255,255,160));
                g2.drawRoundRect(px+1,oy+1,w-2,h-2,35,35);
                g2.setFont(new Font("Tahoma",Font.BOLD,18));FontMetrics fm=g2.getFontMetrics();
                int tx=px+(w-fm.stringWidth(text))/2,ty=oy+(h+fm.getAscent()-fm.getDescent())/2;
                g2.setColor(new Color(0,0,0,40));g2.drawString(text,tx+2,ty+2);
                g2.setColor(TEXT_WHITE);g2.drawString(text,tx,ty);
            }
        };
        btn.setBounds(x,y,w+20,h+20);return btn;
    }

    private void addSectionTitle(JPanel parent, String text, int x, int y){
        JLabel lbl=new JLabel(text);
        lbl.setFont(new Font("Tahoma",Font.BOLD,20));
        lbl.setForeground(LILAC_DARK);
        lbl.setBounds(x,y,620,32);
        parent.add(lbl);
    }
    private void addSepLine(JPanel parent, int x, int y, int w){
        JPanel line=new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g;
                g2.setPaint(new GradientPaint(0,0,new Color(0xE8A0C8),getWidth(),0,new Color(0xF7D6E0)));
                g2.fillRect(0,2,getWidth(),2);
            }
        };
        line.setOpaque(false);line.setBounds(x,y,w,6);parent.add(line);
    }
    private String getModeDesc(DisplayMode m){
        switch(m){
            case WINDOWED:   return "มีกรอบหน้าต่าง — ปรับขนาดได้ สูงสุดตามจอ";
            case BORDERLESS: return "ไม่มีกรอบ — ปรับขนาดได้ สูงสุดตามจอ";
            case FULLSCREEN: return "เต็มจอ — UI ทุกอย่างขยายตามขนาดจอ";
            default:         return "";
        }
    }

    public void onHide(){if(animTimer!=null)animTimer.stop();}
    public void onShow(){if(animTimer!=null&&!animTimer.isRunning())animTimer.start();}
}