package demos.saveSlots;

import org.openpatch.scratch.Window;

/** An ordinary Java entry point; the same stage runs in the browser. */
public class SaveSlotGame {
  public static void main(String[] args) {
    Window window = new Window(640, 480);
    window.setStage(new SaveSlotStage());
  }
}
