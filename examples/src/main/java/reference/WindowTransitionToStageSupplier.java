package reference;

import org.openpatch.scratch.*;

public class WindowTransitionToStageSupplier {
  public WindowTransitionToStageSupplier() {
    Window window = new Window(600, 240);
    Stage first = new Stage();
    first.setColor(Color.fromHSB(55, 100, 100));
    window.setStage(first);
    first.waitSeconds(1.5);

    // Build the next stage only when the screen is black.
    // Further requests during the fade never call their supplier.
    window.transitionToStage(() -> {
      Stage next = new Stage();
      next.setColor(Color.fromHSB(8, 100, 100));
      return next;
    }, 1);
  }

  public static void main(String[] args) {
    new WindowTransitionToStageSupplier();
  }
}
