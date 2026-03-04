public class DebugLogic {
    public static void main(String[] args) {
        System.out.println("========== ทดสอบ Logic ==========");
        
        GameLogic logic = new GameLogic();
        
        logic.setSelectedCharacter("มีน");
        logic.useEnergy(20);
        logic.addMoney(100);
        logic.sendGift("cheap");
        
        logic.printDebugInfo();
    }
}