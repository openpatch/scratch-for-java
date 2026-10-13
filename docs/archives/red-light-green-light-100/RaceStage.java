import org.openpatch.scratch.*;

public class RaceStage extends Stage {

  // begin stage designer
  private Referee referee;
  // end stage designer

  public RaceStage() {

    // begin stage designer
    this.addBackdrop("background");
    referee = new Referee();
    this.add(referee);
    // end stage designer
    this.add(new Racer("bee", 60, 2.2));
    this.add(new Racer("ladybug", 0, 1.6));
    this.add(new Racer("snail", -60, 1.0));
  }

  public static void main(String[] args) {
    Window myWindow = new Window(600, 340);
    myWindow.setStage(new RaceStage());
  }
}
