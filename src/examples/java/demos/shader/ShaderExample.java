package demos.shader;

import org.openpatch.scratch.Window;

public class ShaderExample extends Window {
  public ShaderExample() {
    super(800, 400);

    // begin project settings
    this.setStage(new MyStage());
    // end project settings
  }

  public static void main(String[] args) {
    // begin project settings
    Window.useFullScreen();
    // end project settings
    new ShaderExample();
  }
}
