package demos.tiled;

import java.io.IOException;
import org.openpatch.scratch.save.SaveData;

public class GameState {
  public String[] items = new String[0];
  public double playerX = Double.MAX_VALUE;
  public double playerY = Double.MAX_VALUE;
  public String map = "Level1";
  public String locale = "de";
  private static GameState instance;
  private static SaveData<GameState> saves = new SaveData<>("demos.tiled", "slot1", GameState.class);

  public static void save() {
    try {
      saves.save(get());
    } catch (IOException error) {
      // without a save the game keeps running, it just starts over next time
    }
  }

  public static void load() {
    try {
      instance = saves.loadOrDefault(() -> new GameState());
    } catch (IOException error) {
      instance = new GameState();
    }
  }

  public static GameState get() {
    if (instance == null) {
      instance = new GameState();
    }
    return instance;
  }

  public static void addItem(String name) {
    var items = get().items;
    var grown = new String[items.length + 1];
    for (int i = 0; i < items.length; i++) {
      grown[i] = items[i];
    }
    grown[items.length] = name;
    get().items = grown;
  }

  public static boolean hasItem(String name) {
    for (var item : get().items) {
      if (item.equals(name)) {
        return true;
      }
    }
    return false;
  }
}
