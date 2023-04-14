package org.openpatch.scratch.internal;

import org.openpatch.scratch.Stage;

public interface Drawable {
  public void draw();

  public void addedToStage(Stage stage);

  public void removedFromStage(Stage stage);
}
