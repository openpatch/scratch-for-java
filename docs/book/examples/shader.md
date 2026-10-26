---
name: Shader
---

# Shader

Shaders on the stage and on a sprite. A shader is a small program the graphics
card runs for every pixel, so it can change how something is drawn: blur it,
turn it into dots, light it up or make it wobble.

![shader example](/assets/shader.gif)

The stage has four shaders and the bouncing cat three, each in a `.frag` file
of its own. They are added with a name, and some get values to work with:

```java
var shader = this.getShaders().add("pixelate", "pixelate.frag");
shader.set("pixels", 20.0, 10.0);
```

`set` changes a `uniform` of the shader, a value it reads but the program
decides. That is how the halftone shader follows the mouse, and how the light
shader knows where the lights are: the stage hands it their positions in every
`run()`.

## Run it here

Press **A** for the next stage shader and **S** to take the stage shader off.
**N** and **M** do the same for the cat. Click into the stage first, so it gets
the keys.

<!-- demo: shader -->
:::onlineide{height="560px" libraries="scratch"}

@file dest="blobby.frag" src="/examples/shader/blobby.frag"
@file dest="cat.png" src="/examples/shader/cat.png"
@file dest="glitch.frag" src="/examples/shader/glitch.frag"
@file dest="halftone.frag" src="/examples/shader/halftone.frag"
@file dest="light.frag" src="/examples/shader/light.frag"
@file dest="light.png" src="/examples/shader/light.png"
@file dest="neon.frag" src="/examples/shader/neon.frag"
@file dest="pixel.frag" src="/examples/shader/pixel.frag"
@file dest="pixelate.frag" src="/examples/shader/pixelate.frag"

```java ShaderExample.java

void main() {
  // begin project settings
  Window.useFullScreen();
  // end project settings
  new ShaderExample();
}

class ShaderExample extends Window {
  public ShaderExample() {
    super(800, 400);

    // begin project settings
    this.setStage(new MyStage());
    // end project settings
  }
}

class MyStage extends Stage {

  // begin stage designer
  private MySprite mySprite;
  private NormalSprite normalSprite;
  // end stage designer

  public MyStage() {
    var shader = this.getShaders().add("blobby", "blobby.frag");
    shader.set("depth", 1.5);
    shader.set("rate", 1.5);
    shader = this.getShaders().add("glitch", "glitch.frag");
    shader.set("rate", 0.0001);
    shader = this.getShaders().add("light", "light.frag");
    shader = this.getShaders().add("pixel", "pixel.frag");
    this.getShaders().switchTo("pixel");
    // begin stage designer
    mySprite = new MySprite();
    this.add(mySprite);
    normalSprite = new NormalSprite();
    this.add(normalSprite);
    // end stage designer

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

class MySprite extends Sprite {
  public MySprite() {
    // begin sprite editor
    this.addCostume("cat", "cat.png");
    // end sprite editor
    var shader = this.getShaders().add("halftone", "halftone.frag");
    shader = this.getShaders().add("pixelate", "pixelate.frag");
    shader.set("pixels", 20.0, 10.0);
    shader = this.getShaders().add("neon", "neon.frag");
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

class NormalSprite extends Sprite {
  public NormalSprite() {
    // begin sprite editor
    this.addCostume("cat", "cat.png");
    // end sprite editor
  }

  public void run() {
    this.ifOnEdgeBounce();
    this.move(5);
  }
}

class LightSprite extends Sprite {
  public LightSprite() {
    // begin sprite editor
    this.addCostume("light", "light.png");
    // end sprite editor
  }
}
```

:::

## Source Code

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/shader
