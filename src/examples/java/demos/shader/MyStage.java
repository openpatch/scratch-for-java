package demos.shader;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.timer.Timer;

public class MyStage extends Stage {
  public MyStage() {
    var shader = this.getShaders().add("blobby", "demos/shader/blobby.frag", "demos/shader/default.vert");
    shader.set("depth", 1.5);
    shader.set("rate", 1.5);
    shader = this.getShaders().add("glitch", "demos/shader/glitch.frag", "demos/shader/default.vert");
    shader.set("rate", 0.0001);
    shader = this.getShaders().add("light", "demos/shader/light.frag", "demos/shader/default.vert");
    shader = this.getShaders().add("pixel", "demos/shader/pixel.frag", "demos/shader/default.vert");
    this.getShaders().switchTo("pixel");
    this.add(new MySprite());
    this.add(new NormalSprite());

    for (int i = 0; i < 8; i++) {
      var sprite = new LightSprite();
      this.add(sprite);
      sprite.goToRandomPosition();
    }
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.A) {
      this.getShaders().next();
    }
    if (keyCode == KeyCode.S) {
      this.getShaders().reset();
    }
  }

  public void run() {
    this.display(
        "Press A for the next stage shader. Press S to remove the stage shader. Press N for the"
            + " next sprite shader. Press M to remove the sprite shader.");
    var shader = this.getShaders().getCurrent();
    if (shader != null) {
      shader.set("time", Timer.millis() / 1000.0);
      shader.set("resolution", (float) this.getWidth(), (float) this.getHeight());
      if ("light".equals(shader.getName())) {
        var lights = this.find(LightSprite.class);
        var lightPos = new double[lights.size() * 3 + 3];
        // light at mouse position
        lightPos[0] = this.getMouseX();
        lightPos[1] = this.getMouseY();
        lightPos[2] = 1;
        int i = 3;
        for (var light : lights) {
          var x = light.getX();
          var y = light.getY();
          lightPos[i] = x;
          lightPos[i + 1] = y;
          lightPos[i + 2] = 1;
          i += 3;
        }
        shader.set("lights", lightPos, 3);
      }
    }
  }
}
