---
name: Save Slots
---

# Coin Quest: three save slots

Collect coins with the arrow keys. The alien, coins, backdrop and sound are
built into Scratch for Java. Press **1**, **2** or **3** to select a slot,
**S** to save, **L** to load, and **R** to start a fresh round.

Selecting a slot does not overwrite or load it. An empty slot loads a fresh
round; a damaged save reports an error and leaves the current round intact.
Save different scores to all three slots, reload this page, then load them again.

`SaveData` stores public data fields as JSON. On desktop the files are in your
user-data folder, independently of the game installation. In the browser,
IndexedDB keeps one record per game ID and slot, scoped to this site's origin
and your browser profile. Browser storage can be cleared or evicted, and these
saves are not automatically synchronized between computers or with desktop saves.

Save after a checkpoint or an explicit player action. The game pauses movement
during saving and loading, and reports success after the write has committed.

<!-- demo: saveSlots -->
:::onlineide{height="640px" libraries="scratch"}

```java SaveSlotGame.java

void main() {
  Window window = new Window(640, 480);
  window.setStage(new SaveSlotStage());
}

/** An ordinary Java entry point; the same stage runs in the browser. */
class SaveSlotGame {
}

/** Collect coins, then save and restore the score and both sprite positions. */
class SaveSlotStage extends Stage {
  private Sprite player = new Sprite("player", "alienGreen_stand");
  private Sprite coin = new Sprite("coin", "coinGold");
  private Text score = new Text();
  private Text slots = new Text();
  private Text message = new Text();
  private int coins = 0;
  private int selectedSlot = 1;
  private boolean busy = false;

  public SaveSlotStage() {
    this.addBackdrop("background");
    this.addSound("handleCoins");
    this.player.setSize(22);
    this.coin.setSize(65);
    this.add(this.player);
    this.add(this.coin);
    this.addLabel("Coin Quest: three save slots", 208, 25);
    this.score.setPosition(0, 178);
    this.score.setTextSize(20);
    this.add(this.score);
    this.slots.setPosition(0, 147);
    this.slots.setTextSize(20);
    this.add(this.slots);
    this.message.setPosition(0, -154);
    this.message.setTextSize(16);
    this.add(this.message);
    this.addLabel("Arrow keys: move    1 / 2 / 3: select slot", -190, 16);
    this.addLabel("S: save    L: load    R: fresh round", -215, 16);
    this.apply(new SaveSlotProgress());
    this.message.showText("Collect 10 coins. Save whenever you want!");
    this.updateLabels();
  }

  private void addLabel(String label, double y, int size) {
    Text text = new Text();
    text.setPosition(0, y);
    text.setTextSize(size);
    text.showText(label);
    this.add(text);
  }

  public void run() {
    if (this.busy) return;
    if (this.player.isKeyPressed(KeyCode.LEFT)) this.player.changeX(-3);
    if (this.player.isKeyPressed(KeyCode.RIGHT)) this.player.changeX(3);
    if (this.player.isKeyPressed(KeyCode.UP)) this.player.changeY(3);
    if (this.player.isKeyPressed(KeyCode.DOWN)) this.player.changeY(-3);
    this.player.setX(Math.max(-280, Math.min(280, this.player.getX())));
    this.player.setY(Math.max(-120, Math.min(105, this.player.getY())));
    double dx = this.player.getX() - this.coin.getX();
    double dy = this.player.getY() - this.coin.getY();
    if (dx * dx + dy * dy < 30 * 30) {
      this.coins++;
      this.playSound("handleCoins");
      this.coin.setPosition(Random.random(-250, 250), Random.random(-110, 90));
      this.updateLabels();
      if (this.coins == 10) this.message.showText("10 coins! Save your progress or keep collecting.");
    }
  }

  public void whenKeyPressed(KeyCode key) {
    if (this.busy) return;
    if (key == KeyCode.DIGIT_1 || key == KeyCode.NUMPAD_1) this.selectSlot(1);
    else if (key == KeyCode.DIGIT_2 || key == KeyCode.NUMPAD_2) this.selectSlot(2);
    else if (key == KeyCode.DIGIT_3 || key == KeyCode.NUMPAD_3) this.selectSlot(3);
    else if (key == KeyCode.S) this.saveSelectedSlot();
    else if (key == KeyCode.L) this.loadSelectedSlot();
    else if (key == KeyCode.R) {
      this.apply(new SaveSlotProgress());
      this.message.showText("Fresh round. Saved slots stay as they were.");
    }
  }

  /** Choosing a slot does not load it or overwrite it. */
  public void selectSlot(int slot) {
    if (this.busy || slot < 1 || slot > 3) return;
    this.selectedSlot = slot;
    this.updateLabels();
    this.message.showText("Slot " + slot + " selected. S to save, L to load.");
  }

  private SaveData<SaveSlotProgress> selectedSave() {
    return new SaveData<>("org.openpatch.coinquest", "slot" + this.selectedSlot, SaveSlotProgress.class);
  }

  public void saveSelectedSlot() {
    if (this.busy) return;
    this.busy = true;
    this.message.showText("Saving slot " + this.selectedSlot + "...");
    try {
      this.selectedSave().save(this.snapshot());
      this.message.showText("Saved " + this.coins + " coins to slot " + this.selectedSlot + ".");
    } catch (IOException error) {
      this.message.showText("Save failed. See the console for details.");
      System.out.println(error.getMessage());
    } finally {
      this.busy = false;
    }
  }

  public void loadSelectedSlot() {
    if (this.busy) return;
    this.busy = true;
    this.message.showText("Loading slot " + this.selectedSlot + "...");
    try {
      SaveSlotProgress loaded = this.selectedSave().loadOrDefault(() -> new SaveSlotProgress());
      if (loaded.version != 1 || loaded.coins < 0) {
        this.message.showText("This save version is not supported. Progress stays as it was.");
      } else {
        this.apply(loaded);
        this.message.showText("Loaded slot " + this.selectedSlot + ": " + this.coins + " coins.");
      }
    } catch (IOException error) {
      this.message.showText("Load failed. Your current round stays as it was.");
      System.out.println(error.getMessage());
    } finally {
      this.busy = false;
    }
  }

  public SaveSlotProgress snapshot() {
    SaveSlotProgress data = new SaveSlotProgress();
    data.coins = this.coins;
    data.playerX = this.player.getX();
    data.playerY = this.player.getY();
    data.coinX = this.coin.getX();
    data.coinY = this.coin.getY();
    return data;
  }

  private void apply(SaveSlotProgress data) {
    this.coins = data.coins;
    this.player.setPosition(data.playerX, data.playerY);
    this.coin.setPosition(data.coinX, data.coinY);
    this.updateLabels();
  }

  private void updateLabels() {
    this.score.showText("Coins: " + this.coins + " / 10");
    this.slots.showText((this.selectedSlot == 1 ? "[ Slot 1 ]" : "Slot 1") + "     "
        + (this.selectedSlot == 2 ? "[ Slot 2 ]" : "Slot 2") + "     "
        + (this.selectedSlot == 3 ? "[ Slot 3 ]" : "Slot 3"));
  }
}

/** Only game state belongs in a save: sprites, windows and sounds stay in the game. */
class SaveSlotProgress {
  public int version = 1;
  public int coins = 0;
  public double playerX = -180;
  public double playerY = -60;
  public double coinX = 120;
  public double coinY = 0;
}
```

:::
