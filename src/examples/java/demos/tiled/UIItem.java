package demos.tiled;

/** An Item pinned to the user interface layer, used for the inventory. */
public class UIItem extends Item {
  public UIItem() {
    this.setUI(true);
  }
}
