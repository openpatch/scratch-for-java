import org.openpatch.scratch.*;

public class DodgeWindow extends Window {
  public DodgeWindow() {
    super(600, 400);
    this.setStage(new TitleStage("Dodge the Rocks"));
  }

  public static void main(String[] args) {
    new DodgeWindow();
  }
}
