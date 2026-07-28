package reference.AnimatedSpritePlayAnimation;

import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class MyWindow extends Window {
  public MyWindow() {
    Stage myStage = new MyStage();
    this.setStage(myStage);
  }

  public static void main(String[] args) {
    new MyWindow();
  }
}
