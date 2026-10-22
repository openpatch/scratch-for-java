package demos.saveSlots;

/** Only game state belongs in a save: sprites, windows and sounds stay in the game. */
public class SaveSlotProgress {
  public int version = 1;
  public int coins = 0;
  public double playerX = -180;
  public double playerY = -60;
  public double coinX = 120;
  public double coinY = 0;
}
