package demos.shader;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Operators;
import org.openpatch.scratch.Sprite;

public class MySprite extends Sprite {
  public MySprite() {
    this.addCostume("cat", "demos/shader/cat.png");
    var shader = this.addShader("halftone", "demos/shader/halftone.frag", "demos/shader/default.vert");
    shader = this.addShader("pixelate", "demos/shader/pixelate.frag", "demos/shader/default.vert");
    shader.set("pixels", 20.0, 10.0);
    shader = this.addShader("neon", "demos/shader/neon.frag", "demos/shader/default.vert");
    shader.set("brt", 0.4);
    shader.set("rad", 1);
    this.switchShader("halftone");
  }

  public void whenKeyPressed(int keyCode) {
    if (keyCode == KeyCode.VK_N) {
      this.nextShader();
    }
    if (keyCode == KeyCode.VK_M) {
      this.resetShader();
    }
  }

  public void run() {
    if ("halftone".equals(this.getCurrentShaderName())) {
      var shader = this.getShader("halftone");
      shader.set("pixelsPerRow", Operators.round(Operators.absOf(this.getMouseX())));
    } else if ("pixelate".equals(this.getCurrentShaderName())) {
      var shader = this.getShader("pixelate");
      shader.set("pixels", Operators.absOf(this.getX()), Operators.absOf(this.getX()));
    }
    this.ifOnEdgeBounce();
    this.move(2);
    this.say("Shaders are cool!");
  }
}
