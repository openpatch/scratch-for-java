package demos.tiled;

/** An Item pinned to the user interface layer, used for the inventory. */
public class UIItem extends Item {
  public UIItem() {
    this.setUI(true);

    // scratch4j:begin setup (managed by the stage designer)
    // scratch4j:end setup
  }
}
