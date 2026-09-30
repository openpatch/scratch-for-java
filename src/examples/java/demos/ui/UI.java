package demos.ui;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.UISprite;
import org.openpatch.scratch.Stage;

public class UI extends Stage {

  private Bar bar;

  public UI() {
    super(800, 600);

    this.setCursor("demos/ui/crosshair_color_c.png");

    var panel = new Panel();
    panel.setWidth(600);
    panel.setHeight(480);
    this.add(panel);

    var backgroundBar = new Bar();
    backgroundBar.setWidth(600);
    backgroundBar.setHeight(40);
    backgroundBar.setY(100);
    backgroundBar.switchCostume("bar-gray");
    this.add(backgroundBar);

    bar = new Bar();
    bar.setWidth(100);
    bar.setHeight(40);
    bar.setY(100);
    bar.setSize(70);
    this.add(bar);
  }

  public void run() {
    if (this.isKeyPressed(KeyCode.LEFT)) {
      bar.changeWidth(-1);
    } else if (this.isKeyPressed(KeyCode.RIGHT)) {
      bar.changeWidth(+1);
    }
  }

  public static void main(String[] args) {
    new UI();
  }
}

class Panel extends UISprite {
  public Panel() {
    super();
    this.addCostume("metal-panel-green-corner", "demos/ui/metalPanel_greenCorner.png");
    this.setNineSlice(30, 25, 30, 70);
  }

  @Override
  public void run() {
    // Logic for button interaction can be added here
  }
}

class Bar extends UISprite {
  public Bar() {
    super();
    this.addCostume("bar", "demos/ui/bar_round_gloss_large.png");
    this.addCostume("bar-gray", "demos/ui/bar_round_gloss_large_gray.png");
    this.setNineSlice(12, 24, 12, 24);
  }

  @Override
  public void run() {
    // Logic for progress bar can be added here
  }
}
