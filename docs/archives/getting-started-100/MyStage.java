import org.openpatch.scratch.*;

public class MyStage extends Stage {
  public MyStage() {
    this.addBackdrop("background");
    this.add(new Bunny());
  }

  public static void main(String[] args) {
    new MyStage();
  }
}
