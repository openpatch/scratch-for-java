package Tiled;

import org.openpatch.scratch.*;

public class Item extends Sprite {
  private String name;

  // Constructor for displaying the inventory
  public Item() {
    this.addCostume("scroll-water", "Tiled/assets/ScrollWater.png");
    this.addCostume("scroll-fire", "Tiled/assets/ScrollFire.png");
    this.hide();
  }

  // Constructor for displaying an item on the map
  public Item(String name, double x, double y) {
    this();

    this.show();
    this.name = name;

    this.setX(x);
    this.setY(y);

    this.run();

    this.switchCostume(name);
  }

  public void collect() {
    GameState.get().items.add(name);
    // auto-save when an item is collected
    GameState.save();
    this.hide();
    this.getStage().display(I18n.get("item-found", I18n.get(name)), 2000);

    if (name.equals("scroll-fire")) {
      this.getStage().display(I18n.get("tutorial-scroll-fire"), 2000);
    }
  }

  public String getName() {
    return name;
  }
}
