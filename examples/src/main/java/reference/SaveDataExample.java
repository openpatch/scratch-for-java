package reference;

import java.io.IOException;
import org.openpatch.scratch.save.SaveData;

public class SaveDataExample {
  public SaveDataExample() {
    SaveData<Progress> saves = new SaveData<>("org.openpatch.reference", "slot1", Progress.class);
    try {
      Progress progress = saves.loadOrDefault(() -> new Progress());
      progress.coins += 10;
      saves.save(progress);
      System.out.println("Coins: " + progress.coins);
    } catch (IOException error) {
      System.out.println("Could not save or load progress: " + error.getMessage());
    }
  }

  public static void main(String[] args) {
    new SaveDataExample();
  }
}

class Progress {
  public int version = 1;
  public int level = 1;
  public int coins = 0;

  public Progress() { }
}
