package Shader;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.timer.Timer;

public class MyStage extends Stage {
  public MyStage() {
    var shader = this.addShader("blobby", "Shader/blobby.glsl");
    shader.set("depth", 1.5);
    shader.set("rate", 1.5);
    shader = this.addShader("glitch", "Shader/glitch.glsl");
    shader.set("rate", 0.0001);
    shader = this.addShader("light", "Shader/light.glsl");
    this.switchShader("light");

    this.add(new MySprite());
    this.add(new NormalSprite());

    for (int i = 0; i < 8; i++) {
      var sprite = new LightSprite();
      this.add(sprite);
      sprite.goToRandomPosition();
    }
  }

  public void whenKeyPressed(int keyCode) {
    if (keyCode == KeyCode.VK_A) {
      this.nextShader();
    }
    if (keyCode == KeyCode.VK_S) {
      this.resetShader();
    }
  }

  public void run() {
    this.display(
        "Press A for the next stage shader. Press S to remove the stage shader. Press N for the"
            + " next sprite shader. Press M to remove the sprite shader.");
    var shader = this.getCurrentShader();
    if (shader != null) {
      shader.set("time", Timer.millis() / 1000.0);
      shader.set("resolution", (float) this.getWidth(), (float) this.getHeight());
      if ("light".equals(shader.getName())) {
        var lights = this.findSpritesOf(LightSprite.class);
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
