package demos.stressTest;

import org.openpatch.scratch.*;

public class StressTest extends Stage {

  // begin stage designer
  // end stage designer

  private static int dinos = 180;
  private static int knights = 180;
  private static int ninjas = 180;

  public StressTest() {
    // begin stage designer
    this.addBackdrop("outback", "demos/stressTest/assets/outback.png");
    // end stage designer

    for (int i = 0; i < dinos; i++) {
      this.add(new Dino());
    }
    for (int i = 0; i < knights; i++) {
      this.add(new Knight());
    }
    for (int i = 0; i < ninjas; i++) {
      this.add(new Ninja());
    }
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.SPACE) {
      var figures = this.find(Figure.class);
      for (var figure : figures) {
        ((Figure) figure).state = FigureState.RUN;
      }
    }
  }

  public static void main(String[] args) {
    Window myWindow = new Window(1200, 800, "demos/stressTest/assets");
    myWindow.setStage(new StressTest());
  }
}
