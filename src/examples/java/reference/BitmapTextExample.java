package reference;

import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.text.BitmapText;

/** @example.online false */
public class BitmapTextExample {
  public BitmapTextExample() {
    Window window = new Window(400, 240);
    Stage stage = new Stage();
    stage.setColor(30, 40, 60);
    BitmapText score = new BitmapText("src/examples/java/reference/assets/bitmap-font/%03d.png");
    score.showText("0123456789");
    score.setAlign(TextAlign.CENTER);
    score.setLetterSpacing(1);
    score.setSpaceWidth(4);
    score.setLineSpacing(2);
    score.setSize(300);
    score.setPosition(0, 20);
    score.setIsUI(true);
    stage.add(score);
    stage.display("Image glyphs: " + score.getWidth() + " x " + score.getHeight());
    window.setStage(stage);
  }
  public static void main(String[] args) { new BitmapTextExample(); }
}
