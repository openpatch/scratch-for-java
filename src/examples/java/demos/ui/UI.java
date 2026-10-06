package demos.ui;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.UISprite;
import org.openpatch.scratch.Stage;

public class UI extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private Panel panel;
  private Bar backgroundBar;
  private Bar bar;
  // scratch4j:end fields

  public UI() {
    super(800, 600);

    this.setCursor("demos/ui/crosshair_color_c.png");

    // scratch4j:begin setup (managed by the stage designer)
    panel = new Panel();
    panel.setWidth(600);
    panel.setHeight(480);
    this.add(panel);
    backgroundBar = new Bar();
    backgroundBar.switchCostume("bar-gray");
    backgroundBar.setPosition(0, 100);
    backgroundBar.setWidth(600);
    backgroundBar.setHeight(40);
    this.add(backgroundBar);
    bar = new Bar();
    bar.setPosition(0, 100);
    bar.setSize(70);
    bar.setWidth(100);
    bar.setHeight(40);
    this.add(bar);
    // scratch4j:end setup
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
