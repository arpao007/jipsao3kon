import java.util.ArrayList;
import java.util.List;

public class Shoplogic {
    public static class ShopItem {
        public String id, name;
        public int price, affectionGain, energyCost;
        public ShopItem(String id, String name, int price, int aff, int energy) {
            this.id = id; this.name = name; this.price = price; this.affectionGain = aff; this.energyCost = energy;
        }
    }

    public static class JobItem {
        public String name;
        public int salary, energyCost;
        public JobItem(String name, int salary, int energy) {
            this.name = name; this.salary = salary; this.energyCost = energy;
        }
    }

    public enum Result { SUCCESS, NOT_ENOUGH_MONEY, NOT_ENOUGH_ENERGY }

    private final GameLogic gameLogic;
    private final List<ShopItem> shopItems = new ArrayList<>();
    private final List<JobItem> jobItems = new ArrayList<>();

    public Shoplogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        // สินค้า
        shopItems.add(new ShopItem("rose", "ดอกกุหลาบ", 150, 15, 5));
        shopItems.add(new ShopItem("choco", "ช็อคโกแลต", 80, 8, 2));
        shopItems.add(new ShopItem("snack", "ขนมกินเล่น", 30, 3, 1));
        // งาน
        jobItems.add(new JobItem("พนักงานพาร์ทไทม์", 300, 20));
        jobItems.add(new JobItem("พนักงานร้านหนังสือ", 450, 30));
        jobItems.add(new JobItem("พนักงานเซเว่น", 400, 25));
    }

    public List<ShopItem> getShopItems() { return shopItems; }
    public List<JobItem> getJobItems() { return jobItems; }

    public Result buyItem(ShopItem item) {
        if (gameLogic.getMoney() < item.price) return Result.NOT_ENOUGH_MONEY;
        if (gameLogic.getEnergy() < item.energyCost) return Result.NOT_ENOUGH_ENERGY;
        gameLogic.addMoney(-item.price);
        gameLogic.addEnergy(-item.energyCost);
        gameLogic.addAffection(item.affectionGain);
        return Result.SUCCESS;
    }

    public Result doJob(JobItem job) {
        if (gameLogic.getEnergy() < job.energyCost) return Result.NOT_ENOUGH_ENERGY;
        gameLogic.addMoney(job.salary);
        gameLogic.addEnergy(-job.energyCost);
        return Result.SUCCESS;
    }
}