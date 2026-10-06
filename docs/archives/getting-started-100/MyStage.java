import org.openpatch.scratch.*;

public class MyStage extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private Bunny bunny;
  // scratch4j:end fields

  public MyStage() {
    // scratch4j:begin setup (managed by the stage designer)
    this.addBackdrop("background");
    bunny = new Bunny();
    this.add(bunny);
    // scratch4j:end setup
  }

  public static void main(String[] args) {
    new MyStage();
  }
}
