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
        R_1280x720  ("1280 x 720",   1280, 720),
        R_1366x768  ("1366 x 768",   1366, 768),
        R_1440x900  ("1440 x 900",   1440, 900),
        R_1600x900  ("1600 x 900",   1600, 900),
        R_1920x1080 ("1920 x 1080",  1920, 1080),
        R_2560x1440 ("2560 x 1440",  2560, 1440),
        R_2560x1600 ("2560 x 1600",  2560, 1600);

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
    private RL rl;

    // ──────────────────────────────────────────────
    public SettingPanel(CardLayout cardLayout, JPanel mainContainer) {
        this.cardLayout    = cardLayout;
        this.mainContainer = mainContainer;
        setLayout(null);
        initPetals();
        startAnimation();

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { rebuildUI(); }
            @Override public void componentShown(ComponentEvent e)   { rebuildUI(); }
        });
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0
                    && isShowing()) rebuildUI();
        });
    }

    public void setGameFrame(JFrame frame)             { this.gameFrame = frame; }
    public void setSettingsListener(SettingsListener l){ this.settingsListener = l; }
    public DisplayMode getCurrentMode()                { return currentMode; }
    public Resolution  getCurrentResolution()          { return currentResolution; }
    public int getSoundVolume()                        { return soundVolume; }

    private void rebuildUI() {
        int w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) { javax.swing.SwingUtilities.invokeLater(this::rebuildUI); return; }
        rl = new RL(w, h);
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }

    // ── Petal ──
    private static class Petal {
        float x, y, size, speed, phase, rot, rotSpeed; Color color;
        Petal(int w) {
            Random r = new Random();
            x=r.nextFloat()*w; y=-r.nextFloat()*800;
            size=6+r.nextFloat()*14; speed=0.6f+r.nextFloat()*1.2f;
            phase=r.nextFloat()*(float)(Math.PI*2);
            rot=r.nextFloat()*360; rotSpeed=0.5f+r.nextFloat()*2f;
            Color[] c={new Color(0xF8BBD9),new Color(0xE8A0C0),new Color(0xD4B0E0),
                       new Color(0xFFDDEE),new Color(0xC8A0D8)};
            color=c[r.nextInt(c.length)];
        }
        void update(float t){ y+=speed; x+=(float)(Math.sin(t*0.03+phase)*0.8); rot+=rotSpeed; if(y>820)y=-20; }
    }
    private void initPetals(){ for(int i=0;i<40;i++) petals.add(new Petal(1400)); }
    private void startAnimation(){
        animTimer=new Timer(16,e->{animTime+=0.05f;for(Petal p:petals)p.update(animTime);repaint();});
        animTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int pw=getWidth(), ph=getHeight();
        Graphics2D g2=(Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0,0,BG_TOP,0,ph,BG_BOT));
        g2.fillRect(0,0,pw,ph);
        int[][]bk={{pw/10,ph/4,180},{(int)(pw*.75),ph/8,220},{pw/6,(int)(ph*.75),140},
                   {(int)(pw*.88),(int)(ph*.63),160},{pw/2,(int)(ph*.88),200},{(int)(pw*.58),ph/5,120}};
        for(int[]b:bk){
            g2.setPaint(new RadialGradientPaint(b[0],b[1],b[2],new float[]{0f,1f},
                new Color[]{new Color(255,200,230,40),new Color(255,200,230,0)}));
            g2.fillOval(b[0]-b[2],b[1]-b[2],b[2]*2,b[2]*2);
        }
        for(Petal p:petals){
            float px2=p.x*pw/1400f;
            Graphics2D pg=(Graphics2D)g2.create();
            pg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            pg.translate(px2,p.y); pg.rotate(Math.toRadians(p.rot));
            pg.setColor(new Color(p.color.getRed(),p.color.getGreen(),p.color.getBlue(),200));
            Path2D path=new Path2D.Float();
            path.moveTo(0,-p.size);
            path.curveTo(p.size*.6f,-p.size*.5f,p.size*.6f,p.size*.5f,0,p.size*.3f);
            path.curveTo(-p.size*.6f,p.size*.5f,-p.size*.6f,-p.size*.5f,0,-p.size);
            pg.fill(path); pg.dispose();
        }
        Path2D wave=new Path2D.Float();
        wave.moveTo(0,0); wave.curveTo(pw*.25,ph*.04,pw*.5,-ph*.012,pw*.75,ph*.03);
        wave.curveTo(pw*.875,ph*.047,pw*.96,ph*.012,pw,ph*.025);
        wave.lineTo(pw,0); wave.closePath();
        g2.setColor(new Color(0xF9C4DA)); g2.fill(wave);
    }

    // ── Build UI ──
    private void buildUI() {
        if (rl == null) return;
        int w = rl.w, h = rl.h;

        // card = 62% wide, 86% tall, centered
        int cardW = (int)(w * 0.62);
        int cardH = (int)(h * 0.86);
        int cardX = (w - cardW) / 2;
        int cardY = (int)(h * 0.07);

        // Title above card
        JLabel titleLbl = new JLabel("Settings", SwingConstants.CENTER);
        titleLbl.setFont(new Font("Tahoma", Font.BOLD, rl.fontTitle));
        titleLbl.setForeground(PINK_DEEP);
        titleLbl.setBounds(0, (int)(h*0.01), w, cardY - 4);
        add(titleLbl);

        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,240,248,210));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),40,40);
                g2.setStroke(new BasicStroke(2.5f)); g2.setColor(PINK_DEEP);
                g2.drawRoundRect(2,2,getWidth()-4,getHeight()-4,38,38);
                g2.setPaint(new GradientPaint(0,0,new Color(255,255,255,70),
                        getWidth(),getHeight(),new Color(255,200,230,0)));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),40,40);
            }
        };
        card.setOpaque(false);
        card.setBounds(cardX, cardY, cardW, cardH);
        add(card);

        int pad  = (int)(cardW * 0.053);
        int iw   = cardW - pad * 2;
        int curY = (int)(cardH * 0.04);

        // ── Display Mode ──
        addSectionLabel(card, "Display Mode", pad, curY, rl.fontBody + 2);
        addSepLine(card, pad, curY + 28, iw);
        curY += 36;

        boolean[][] selState = {
            {currentMode == DisplayMode.WINDOWED},
            {currentMode == DisplayMode.BORDERLESS},
            {currentMode == DisplayMode.FULLSCREEN}
        };
        String[]      modeLabels = {"Windowed","Borderless","Fullscreen"};
        DisplayMode[] modes      = {DisplayMode.WINDOWED,DisplayMode.BORDERLESS,DisplayMode.FULLSCREEN};

        JLabel modeDesc = new JLabel(getModeDesc(currentMode), SwingConstants.CENTER);
        modeDesc.setFont(new Font("Tahoma", Font.ITALIC, rl.fontSmall+1));
        modeDesc.setForeground(new Color(0x9060A0));

        int mbtnH  = (int)(cardH * 0.09);
        int mbtnW  = (iw - 20) / 3;
        JPanel[] toggles = new JPanel[3];
        for(int i=0;i<3;i++){
            final int idx=i;
            toggles[i] = makeToggle(modeLabels[i], selState[i], rl.fontSmall+2, () -> {
                currentMode = modes[idx];
                for(int j=0;j<3;j++) selState[j][0]=(modes[j]==currentMode);
                for(JPanel t:toggles) t.repaint();
                modeDesc.setText(getModeDesc(currentMode));
                if(settingsListener!=null&&gameFrame!=null)
                    settingsListener.onDisplayModeChanged(currentMode, gameFrame);
            });
            toggles[i].setBounds(pad + i*(mbtnW+10), curY, mbtnW, mbtnH);
            card.add(toggles[i]);
        }
        curY += mbtnH + 4;
        modeDesc.setBounds(pad, curY, iw, 20);
        card.add(modeDesc);
        curY += 26;

        // ── Sound Volume ──
        addSectionLabel(card, "Sound Volume", pad, curY, rl.fontBody+2);
        addSepLine(card, pad, curY+28, iw);
        curY += 36;

        JLabel volNum = new JLabel(soundVolume+"%", SwingConstants.CENTER);
        volNum.setFont(new Font("Tahoma", Font.BOLD, rl.fontTitle-10));
        volNum.setForeground(PINK_DEEP);
        volNum.setBounds(cardW/2-50, curY, 100, 34);
        card.add(volNum);
        curY += 38;

        JSlider slider = new JSlider(0, 100, soundVolume){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                int th=10,ty=getHeight()/2-th/2;
                g2.setColor(new Color(0xE0C0D8));
                g2.fillRoundRect(14,ty,getWidth()-28,th,th,th);
                int fw=(int)((getValue()/100.0)*(getWidth()-28));
                if(fw>0){ g2.setPaint(new GradientPaint(14,0,PINK_LIGHT,14+fw,0,PINK_DEEP));
                          g2.fillRoundRect(14,ty,fw,th,th,th); }
                int tx2=Math.max(0,Math.min(14+fw-14,getWidth()-28));
                g2.setColor(Color.WHITE); g2.fillOval(tx2,getHeight()/2-14,28,28);
                g2.setStroke(new BasicStroke(2.5f)); g2.setColor(PINK_DEEP);
                g2.drawOval(tx2,getHeight()/2-14,28,28);
                g2.fillOval(tx2+9,getHeight()/2-5,10,10);
            }
        };
        slider.setOpaque(false); slider.setFocusable(false);
        slider.setBounds(pad, curY, iw, 44);
        slider.addChangeListener(e -> {
            soundVolume=slider.getValue(); volNum.setText(soundVolume+"%");
            if(settingsListener!=null) settingsListener.onVolumeChanged(soundVolume);
        });
        card.add(slider);
        curY += 46;

        JLabel l0=new JLabel("0%");   l0.setFont(new Font("Tahoma",Font.PLAIN,rl.fontSmall));
        l0.setForeground(new Color(0xB090C0)); l0.setBounds(pad,curY,30,16); card.add(l0);
        JLabel l100=new JLabel("100%"); l100.setFont(new Font("Tahoma",Font.PLAIN,rl.fontSmall));
        l100.setForeground(new Color(0xB090C0)); l100.setBounds(pad+iw-42,curY,42,16); card.add(l100);
        curY += 20;

        int sbW=(int)(iw*.22), sbH=34;
        card.add(makeSmallBtn("Mute", ()->slider.setValue(0),   pad,         curY, sbW, sbH));
        card.add(makeSmallBtn("Max",  ()->slider.setValue(100), pad+iw-sbW,  curY, sbW, sbH));
        curY += sbH + 12;

        // ── Window Size ──
        addSectionLabel(card, "Window Size", pad, curY, rl.fontBody+2);
        addSepLine(card, pad, curY+28, iw);
        curY += 36;

        JLabel resDesc = new JLabel(currentResolution.label + "  (Windowed / Borderless)", SwingConstants.CENTER);
        resDesc.setFont(new Font("Tahoma", Font.ITALIC, rl.fontSmall+1));
        resDesc.setForeground(new Color(0x9060A0));
        resDesc.setBounds(pad, curY, iw, 20);
        card.add(resDesc);
        curY += 26;

        Resolution[] resList = Resolution.values();
        int rbW  = (iw - 30) / 4;
        int rbH  = (int)(cardH * 0.075);
        JPanel[] rToggles = new JPanel[resList.length];
        boolean[][] rSel  = new boolean[resList.length][1];
        for (int i=0;i<resList.length;i++) rSel[i][0]=(resList[i]==currentResolution);

        for (int i=0;i<4&&i<resList.length;i++){
            final int idx=i;
            rToggles[i]=makeToggle(resList[i].label, rSel[i], rl.fontSmall, ()->{
                currentResolution=resList[idx];
                for(int j=0;j<resList.length;j++) rSel[j][0]=(j==idx);
                for(JPanel t:rToggles) if(t!=null) t.repaint();
                resDesc.setText(currentResolution.label+"  (Windowed / Borderless)");
                if(settingsListener!=null&&gameFrame!=null)
                    settingsListener.onResolutionChanged(currentResolution,gameFrame);
            });
            rToggles[i].setBounds(pad+i*(rbW+10), curY, rbW, rbH);
            card.add(rToggles[i]);
        }
        int row2Y = curY + rbH + 8;
        int r2total = 3*rbW + 2*10;
        int row2X   = pad + (iw - r2total) / 2;
        for (int i=4;i<resList.length;i++){
            final int idx=i;
            rToggles[i]=makeToggle(resList[i].label, rSel[i], rl.fontSmall, ()->{
                currentResolution=resList[idx];
                for(int j=0;j<resList.length;j++) rSel[j][0]=(j==idx);
                for(JPanel t:rToggles) if(t!=null) t.repaint();
                resDesc.setText(currentResolution.label+"  (Windowed / Borderless)");
                if(settingsListener!=null&&gameFrame!=null)
                    settingsListener.onResolutionChanged(currentResolution,gameFrame);
            });
            rToggles[i].setBounds(row2X+(i-4)*(rbW+10), row2Y, rbW, rbH);
            card.add(rToggles[i]);
        }

        // ── Back ──
        int backY  = row2Y + rbH + 10;
        int backBW = (int)(iw * 0.5);
        int backBH = (int)(cardH * 0.09);
        JPanel back = makeMenuBtn("← Back", LILAC_DARK, LILAC,
                pad+(iw-backBW)/2, backY, backBW, backBH,
                () -> cardLayout.show(mainContainer,"MENU"));
        card.add(back);

        // Credit
        JLabel credit=new JLabel("≡  First Love Game  •  v1.0  ≡", SwingConstants.CENTER);
        credit.setFont(new Font("Tahoma",Font.ITALIC,rl.fontSmall));
        credit.setForeground(new Color(0x9060A0));
        credit.setBounds(0,h-26,w,22);
        add(credit);
    }

    // ── helpers ──
    private JPanel makeToggle(String label, boolean[] selRef, int fontSize, Runnable action){
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
                g2.setFont(new Font("Tahoma",Font.BOLD,fontSize));
                g2.setColor(sel?TEXT_WHITE:new Color(0x7050A0));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(label,(getWidth()-fm.stringWidth(label))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
    }

    private JPanel makeSmallBtn(String label,Runnable action,int x,int y,int w,int h){
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
                g2.setFont(new Font("Tahoma",Font.BOLD,rl!=null?rl.fontSmall+1:13));
                g2.setColor(new Color(0x8050A0));
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
                g2.setFont(new Font("Tahoma",Font.BOLD,rl!=null?rl.fontBody:18));
                FontMetrics fm=g2.getFontMetrics();
                int tx=px+(w-fm.stringWidth(text))/2,ty=oy+(h+fm.getAscent()-fm.getDescent())/2;
                g2.setColor(new Color(0,0,0,40));g2.drawString(text,tx+2,ty+2);
                g2.setColor(TEXT_WHITE);g2.drawString(text,tx,ty);
            }
        };
        btn.setBounds(x,y,w+10,h+10); return btn;
    }

    private void addSectionLabel(JPanel parent,String text,int x,int y,int fontSize){
        JLabel lbl=new JLabel("◆  "+text);
        lbl.setFont(new Font("Tahoma",Font.BOLD,fontSize));
        lbl.setForeground(LILAC_DARK);
        lbl.setBounds(x,y,500,28);
        parent.add(lbl);
    }
    private void addSepLine(JPanel parent,int x,int y,int w){
        JPanel line=new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g;
                g2.setPaint(new GradientPaint(0,0,new Color(0xE8A0C8),getWidth(),0,new Color(0xF7D6E0)));
                g2.fillRect(0,2,getWidth(),2);
            }
        };
        line.setOpaque(false); line.setBounds(x,y,w,6); parent.add(line);
    }
    private String getModeDesc(DisplayMode m){
        switch(m){
            case WINDOWED:   return "มีกรอบหน้าต่าง — ปรับขนาดได้ สูงสุดตามจอ";
            case BORDERLESS: return "ไม่มีกรอบ — ปรับขนาดได้ สูงสุดตามจอ";
            case FULLSCREEN: return "เต็มจอ — UI ทุกอย่างขยายตามขนาดจอ";
            default:         return "";
        }
    }

    public void onHide(){ if(animTimer!=null)animTimer.stop(); }
    public void onShow(){ if(animTimer!=null&&!animTimer.isRunning())animTimer.start(); }
}