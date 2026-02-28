import java.util.Scanner;

class Character {
    String name;
    int affinity;

    public Character(String name) {
        this.name = name;
        this.affinity = 0;
    }

    public void addAffinity(int points) {
        this.affinity += points;
        System.out.println(">> [ความสัมพันธ์กับ " + name + " เพิ่มขึ้น: +" + points + " (รวม: " + affinity + ")]");
    }
}

public class HighSchoolLife {
    private static Scanner scanner = new Scanner(System.in);
    private static Character rin = new Character("ริน (Rin)");

    public static void main(String[] args) {
        startGame();
        chapter1_TheFirstMeet();
        chapter2_LateNightChat();
        chapter3_TheRainyDay();
        finalResult();
    }

    public static void startGame() {
        System.out.println("==============================================");
        System.out.println("   Visual Novel: บันทึกรัก ม.4 (First Semester)   ");
        System.out.println("==============================================");
        System.out.println("คุณคือ 'ธันว์' เด็กหนุ่มที่เพิ่งย้ายเข้ามาเรียน ม.4 ที่นี่...");
        pause(1000);
    }

    // --- บทที่ 1: การพบกันครั้งแรก ---
    public static void chapter1_TheFirstMeet() {
        System.out.println("\n[บทที่ 1: ห้องเรียน 4/1 ตอนเช้า]");
        System.out.println("คุณเดินเข้ามาในห้องเรียนใหม่ และพบว่าที่นั่งของคุณอยู่ข้างเด็กสาวคนหนึ่ง");
        System.out.println("เธอกำลังตั้งใจอ่านหนังสือวิชาเคมีจนไม่ทันสังเกตเห็นคุณ...");
        System.out.println("1. ทักทายด้วยรอยยิ้ม: 'สวัสดีครับ ผมธันว์นะ ฝากเนื้อฝากตัวด้วย'");
        System.out.println("2. แอบดูชื่อที่ปกหนังสือ: (รินลดา... ชื่อน่ารักจัง)");
        System.out.println("3. นั่งลงเงียบๆ ไม่กล้าทัก");

        int choice = getChoice(3);
        if (choice == 1) {
            System.out.println("\nริน: (สะดุ้งเล็กน้อย) 'อ๊ะ! สวัสดีค่ะ... ฉันรินนะ ฝากตัวด้วยเหมือนกัน'");
            rin.addAffinity(10);
        } else if (choice == 2) {
            System.out.println("\nริน: (เงยหน้าขึ้นมาสบตาพอดี) 'เอ๋... มีอะไรติดที่หน้าฉันเหรอคะ?'");
            System.out.println("คุณ: 'ปะ... เปล่าครับ แค่เห็นชื่อที่ปกหนังสือ ลายมือสวยดีนะครับ'");
            System.out.println("ริน: (หน้าแดงนิดๆ) 'ขอบคุณค่ะ...'");
            rin.addAffinity(15);
        } else {
            System.out.println("\nบรรยากาศเงียบกริบจนน่าอึดอัด... คุณเสียโอกาสเริ่มบทสนทนาไปแล้ว");
            rin.addAffinity(0);
        }
    }

    // --- บทที่ 2: แชทตอนดึก ---
    public static void chapter2_LateNightChat() {
        System.out.println("\n[บทที่ 2: ห้องนอนของคุณ - 21:00 น.]");
        System.out.println("ตึ๊ด! (เสียงแจ้งเตือน LINE ดังขึ้น)");
        System.out.println("ริน: 'ธันว์... หลับหรือยัง? พอดีฉันติดโจทย์เลขที่อาจารย์สั่งน่ะ'");
        System.out.println("คุณจะตอบว่าอะไร?");
        System.out.println("1. 'ยังครับ มีอะไรให้ช่วยบอกมาได้เลย!'");
        System.out.println("2. 'กำลังจะหลับเลย แต่ถ้าเป็นริน ผมช่วยได้เสมอครับ'");
        System.out.println("3. 'ส่งโจทย์มาสิ เดี๋ยวดูให้'");

        int choice = getChoice(3);
        if (choice == 1) {
            System.out.println("\nริน: 'ขอบคุณนะ! นายนี่พึ่งพาได้จริงๆ'");
            rin.addAffinity(5);
        } else if (choice == 2) {
            System.out.println("\nริน: 'พิมพ์อะไรน่ะ! พะ... พักผ่อนเถอะ ถ้าลำบากก็ไม่ต้องช่วยก็ได้ (แต่ขอบคุณนะ)'");
            rin.addAffinity(15);
        } else {
            System.out.println("\nริน: 'อื้ม ส่งไปแล้วนะ...'");
            rin.addAffinity(2);
        }
        pause(1000);
        System.out.println("\n(คุณช่วยรินทำโจทย์จนถึง 4 ทุ่ม)");
        System.out.println("ริน: 'เสร็จแล้ว! ขอบคุณมากนะธันว์ พรุ่งนี้ฉันจะเอาขนมไปให้ที่โรงเรียนนะ'");
    }

    // --- บทที่ 3: วันที่ฝนตก ---
    public static void chapter3_TheRainyDay() {
        System.out.println("\n[บทที่ 3: หน้าโรงเรียน หลังเลิกเรียน]");
        System.out.println("ฝนตกลงมาอย่างหนัก และรินยืนอยู่คนเดียวที่ป้ายรถเมล์โดยไม่มีร่ม");
        System.out.println("คุณมีร่มเพียงคันเดียวในมือ...");
        System.out.println("1. เดินเข้าไปชวนกลับด้วยกัน: 'ริน กลับด้วยกันไหม? ร่มผมกว้างนะ'");
        System.out.println("2. ยื่นร่มให้เธอแล้วตัวเองวิ่งฝ่าฝนกลับ: 'เอาไปใช้เถอะ เดี๋ยวผมวิ่งกลับเอง'");
        System.out.println("3. ยืนรอเป็นเพื่อนจนกว่าฝนจะซา");

        int choice = getChoice(3);
        if (choice == 1) {
            System.out.println("\nริน: 'จะ... จะดีเหรอ? งั้นรบกวนด้วยนะ...'");
            System.out.println("(คุณและรินเดินเบียดกันใต้ร่มคันเล็ก หัวใจคุณเต้นแรงจนแทบหลุดออกมา)");
            rin.addAffinity(20);
        } else if (choice == 2) {
            System.out.println("\nริน: 'เดี๋ยวสิธันว์! นายจะเปียกนะ...' (เธอมองตามคุณด้วยความเป็นห่วง)");
            rin.addAffinity(10);
        } else {
            System.out.println("\nริน: 'ขอบคุณที่อยู่เป็นเพื่อนนะธันว์ แต่นายไม่เปียกเหรอ?'");
            rin.addAffinity(5);
        }
    }

    // --- สรุปผล ---
    public static void finalResult() {
        System.out.println("\n==============================================");
        System.out.println("               - จบช่วงอาทิตย์แรก -              ");
        System.out.println("คะแนนความสัมพันธ์ทั้งหมด: " + rin.affinity);
        
        if (rin.affinity >= 45) {
            System.out.println("Ending: [รักครั้งแรกเริ่มผลิบาน]");
            System.out.println("รินเริ่มแอบมองคุณบ่อยๆ ในห้องเรียน และความสัมพันธ์ของคุณใกล้ชิดเกินกว่าเพื่อนทั่วไปแล้ว!");
        } else if (rin.affinity >= 20) {
            System.out.println("Ending: [เพื่อนสนิทคิดไม่ซื่อ]");
            System.out.println("คุณและรินเป็นเพื่อนที่ดีต่อกัน แต่เธอยังไม่แน่ใจในความรู้สึกที่มีให้คุณ...");
        } else {
            System.out.println("Ending: [คนรู้จักที่เดินผ่านกัน]");
            System.out.println("รินจำคุณได้ในฐานะเพื่อนร่วมชั้นคนหนึ่งเท่านั้น พยายามใหม่นะ!");
        }
        System.out.println("==============================================");
    }

    // --- Helper Methods ---
    private static int getChoice(int max) {
        int choice = 0;
        while (choice < 1 || choice > max) {
            System.out.print("เลือกคำตอบ (1-" + max + "): ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            } else {
                scanner.next();
            }
        }
        return choice;
    }

    private static void pause(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }
}