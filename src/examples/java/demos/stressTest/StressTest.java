package demos.stressTest;

import org.openpatch.scratch.*;

public class StressTest extends Stage {

  private static int dinos = 180;
  private static int knights = 180;
  private static int ninjas = 180;

  public StressTest() {
    this.addBackdrop("outback", "demos/stressTest/assets/outback.png");

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

  public void whenKeyPressed(int keyCode) {
    if (keyCode == KeyCode.VK_SPACE) {
      var characters = this.find(Character.class);
      for (var character : characters) {
        ((Character) character).state = CharacterState.RUN;
      }
    }
  }

  public static void main(String[] args) {
    Window myWindow = new Window(1200, 800, "demos/stressTest/assets");
    myWindow.setStage(new StressTest());
  }
}
