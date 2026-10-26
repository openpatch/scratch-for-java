package reference;
import org.openpatch.scratch.*;


public class StageWhenIReceive {
  public StageWhenIReceive() {
    class MyStage extends Stage {
      public MyStage() {
      }

      // The stage hears its own messages too.
      public void whenIReceive(String message) {
        if (message.equals("darker")) {
          Color color = this.getColor();
          this.setColor(color.withHue(color.getHue() + 10));
        }
      }
    }

    MyStage myStage = new MyStage();
    while (true) {
      myStage.broadcast("darker");
      myStage.waitSeconds(0.5);
    }
  }

  public static void main(String[] args) {
    new StageWhenIReceive();
  }
}
