import org.openpatch.scratch.*;

public class DodgeWindow extends Window {
  public DodgeWindow() {
    super(600, 400);
    this.setStage(new TitleStage("Dodge the Rocks"));

    // scratch4j:begin window (managed by the project settings)
    // scratch4j:end window
  }

  public static void main(String[] args) {
    // scratch4j:begin options (managed by the project settings)
    // scratch4j:end options
    new DodgeWindow();
  }
}
