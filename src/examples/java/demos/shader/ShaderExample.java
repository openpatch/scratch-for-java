package demos.shader;

import org.openpatch.scratch.Window;

public class ShaderExample extends Window {
  public ShaderExample() {
    super(800, 400);
    this.setStage(new MyStage());
  }

  public static void main(String[] args) {
    Window.useFullScreen();
    new ShaderExample();
  }
}
