package reference;

import org.openpatch.scratch.*;

public class WindowTransitionToStageSupplier {
  public WindowTransitionToStageSupplier() {
    Window window = new Window(600, 240);
    Stage first = new Stage();
    first.setColor(140);
    window.setStage(first);
    first.wait(1500);

    // Build the next stage only when the screen is black.
    // Further requests during the fade never call their supplier.
    window.transitionToStage(() -> {
      Stage next = new Stage();
      next.setColor(20);
      return next;
    }, 1000);
  }

  public static void main(String[] args) {
    new WindowTransitionToStageSupplier();
  }
}
