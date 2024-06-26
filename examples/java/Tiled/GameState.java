package Tiled;

import java.util.*;
import org.openpatch.scratch.extensions.fs.File;

public class GameState {
  public List<String> items = new ArrayList<>();
  public double playerX = Double.MAX_VALUE;
  public double playerY = Double.MAX_VALUE;
  public String map = "Level1";
  public String locale = "de";
  private static GameState instance;

  public static void save() {
    File.save("Tiled/save.json", GameState.get());
  }

  public static void load() {
    instance = File.load("Tiled/save.json", GameState.class);
  }

  public static GameState get() {
    if (instance == null) {
      instance = new GameState();
    }
    return instance;
  }
}
