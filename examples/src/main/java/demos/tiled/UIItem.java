package demos.tiled;

import org.openpatch.scratch.Layer;

/** An Item pinned to the user interface layer, used for the inventory. */
public class UIItem extends Item {
  public UIItem() {
    this.setLayer(Layer.UI);

    // begin sprite editor
    // end sprite editor
  }
}
