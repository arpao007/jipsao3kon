import java.util.*;

public class Shoplogic {

    public enum Result { SUCCESS, NOT_ENOUGH_MONEY, NOT_ENOUGH_ENERGY, INVALID_ITEM }

    public static class ShopItem {
        public final String id;
        public final String name;
        public final int price;
        public final int affection;
        public final int energyCost;

        public ShopItem(String id, String name, int price, int affection, int energyCost) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.affection = affection;
            this.energyCost = energyCost;
        }
    }

    private final GameLogic logic;
    private final List<ShopItem> items = new ArrayList<>();

    public Shoplogic(GameLogic logic) {
        this.logic = logic;

        // รายการพื้นฐาน (ShopPanel / GameplayPanel จะหาเจอจาก id)
        items.add(new ShopItem("rose",    "ดอกกุหลาบ", 150, 15, 5));
        items.add(new ShopItem("choco",   "ช็อคโกแลต",  80,  8, 2));
        items.add(new ShopItem("snack",   "ขนมกินเล่น", 30,  3, 1));
        items.add(new ShopItem("bouquet", "ช่อดอกไม้", 200, 25, 8));
    }

    public List<ShopItem> getShopItems() {
        return Collections.unmodifiableList(items);
    }

    public Result buyItem(ShopItem item) {
        if (item == null) return Result.INVALID_ITEM;

        if (logic.getMoney() < item.price) return Result.NOT_ENOUGH_MONEY;
        if (logic.getEnergy() < item.energyCost) return Result.NOT_ENOUGH_ENERGY;

        logic.setMoney(logic.getMoney() - item.price);
        logic.useEnergy(item.energyCost);

        // รองรับ GameLogic แบบเหลือคนเดียว (มีน)
        logic.setAffection(logic.getCurrentAffection() + item.affection);

        return Result.SUCCESS;
    }
}