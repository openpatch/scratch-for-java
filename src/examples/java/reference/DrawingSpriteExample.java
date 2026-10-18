package reference;

import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.drawing.*;

/** A road made from projected 2D points. Keep projection and physics in the game. */
public class DrawingSpriteExample {
  public DrawingSpriteExample() {
    Window window = new Window(400, 240);
    Stage stage = new Stage();
    stage.add(new Road());
    window.setStage(stage);
  }

  static class Road extends DrawingSprite {
    @Override public void draw(Drawing drawing) {
      drawing.clip(0, 0, 320, 180);
      drawing.rectangle(0, 0, 320, 180, new Color(90, 170, 220));
      drawing.polygon(new double[] {-160, 160, 30, -30},
          new double[] {-90, -90, 30, 30}, new Color(70, 70, 70));
      drawing.image("slimeGreen", 0, -50, 40, 30);
      drawing.noClip();
    }
  }

  public static void main(String[] args) { new DrawingSpriteExample(); }
}
