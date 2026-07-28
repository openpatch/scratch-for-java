package reference;
import org.openpatch.scratch.*;

public class StageRun {
  class CustomStage extends Stage {
    public CustomStage() {
      super(600, 240);
      this.setColor(255, 0, 0);
    }

    public void run() {
      this.changeColor(0.5);
    }
  }

  public StageRun() {
    Stage myStage = new CustomStage();
  }

  public static void main(String[] args) {
    new StageRun();
  }
}
