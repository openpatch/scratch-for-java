package reference;
import org.openpatch.scratch.*;


public class StageWhenIReceive {
  public StageWhenIReceive() {
    class MyStage extends Stage {
      public MyStage() {
        super(600, 240);
      }

      // The stage hears its own messages too.
      public void whenIReceive(String message) {
        if (message.equals("darker")) {
          this.changeColor(10);
        }
      }
    }

    MyStage myStage = new MyStage();
    while (true) {
      myStage.broadcast("darker");
      myStage.wait(500);
    }
  }

  public static void main(String[] args) {
    new StageWhenIReceive();
  }
}
