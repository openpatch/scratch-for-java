---
name: UI
---

# UI

An example which changes the mouse pointer and demonstrates the 9-slice scaling of sprites.

## Run it here

The panel and the bars are stretched from small pictures without distorting their
corners, and the mouse pointer is a crosshair. Click the stage and hold the left or
right arrow key to shrink or grow the green bar.

<!-- demo: ui -->
:::onlineide{height="560px" libraries="scratch" speed="-1"}

@file dest="bar_round_gloss_large.png" src="/examples/ui/bar_round_gloss_large.png"
@file dest="bar_round_gloss_large_gray.png" src="/examples/ui/bar_round_gloss_large_gray.png"
@file dest="crosshair_color_c.png" src="/examples/ui/crosshair_color_c.png"
@file dest="metalPanel_greenCorner.png" src="/examples/ui/metalPanel_greenCorner.png"

```java UI.java

void main() {
  new UI();
}

class UI extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private Panel panel;
  private Bar backgroundBar;
  private Bar bar;
  // scratch4j:end fields

  public UI() {
    super(800, 600);

    this.setCursor("crosshair_color_c.png");

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
}

class Panel extends UISprite {
  public Panel() {
    super();
    this.addCostume("metal-panel-green-corner", "metalPanel_greenCorner.png");
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
    this.addCostume("bar", "bar_round_gloss_large.png");
    this.addCostume("bar-gray", "bar_round_gloss_large_gray.png");
    this.setNineSlice(12, 24, 12, 24);
  }

  @Override
  public void run() {
    // Logic for progress bar can be added here
  }
}
```

:::

To run it on your own computer, put the pictures of the
[source code](https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/ui) next to the program.

## Source Code:

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/ui

