package org.openpatch.scratch.internal;

import org.openpatch.scratch.Stage;

public interface Drawable {

  public float getX();

  public float getY();

  public void draw();

  public void addedToStage(Stage stage);

  public void removedFromStage(Stage stage);
}
