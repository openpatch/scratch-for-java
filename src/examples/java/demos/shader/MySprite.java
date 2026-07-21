package demos.shader;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Operators;
import org.openpatch.scratch.Sprite;

public class MySprite extends Sprite {
  public MySprite() {
    this.addCostume("cat", "demos/shader/cat.png");
    var shader = this.getShaders().add("halftone", "demos/shader/halftone.frag", "demos/shader/default.vert");
    shader = this.getShaders().add("pixelate", "demos/shader/pixelate.frag", "demos/shader/default.vert");
    shader.set("pixels", 20.0, 10.0);
    shader = this.getShaders().add("neon", "demos/shader/neon.frag", "demos/shader/default.vert");
    shader.set("brt", 0.4);
    shader.set("rad", 1);
    this.getShaders().switchTo("halftone");
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.N) {
      this.getShaders().next();
    }
    if (keyCode == KeyCode.M) {
      this.getShaders().reset();
    }
  }

  public void run() {
    if ("halftone".equals(this.getShaders().getCurrentName())) {
      var shader = this.getShaders().get("halftone");
      shader.set("pixelsPerRow", Operators.round(Operators.absOf(this.getMouseX())));
    } else if ("pixelate".equals(this.getShaders().getCurrentName())) {
      var shader = this.getShaders().get("pixelate");
      shader.set("pixels", Operators.absOf(this.getX()), Operators.absOf(this.getX()));
    }
    this.ifOnEdgeBounce();
    this.move(2);
    this.say("Shaders are cool!");
  }
}
