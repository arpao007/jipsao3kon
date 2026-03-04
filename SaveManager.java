import java.io.*;

public class SaveManager {

    private static final String SAVE_FILE = "save.dat";

    public static void save(GameLogic logic, GameDate date) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {

            out.writeObject(logic);
            out.writeObject(date);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasSave() {
        return new File(SAVE_FILE).exists();
    }
}