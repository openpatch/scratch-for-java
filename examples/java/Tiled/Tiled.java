 

import org.openpatch.scratch.Window;

public class Tiled extends Window {
  public Tiled() {
    super(800, 600, "Tiled/assets");

    I18n.setup();

    this.setStage(new World());
  }

  public static void main(String[] args) {
    new Tiled();
  }
}
