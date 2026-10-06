package demos.shader;

import org.openpatch.scratch.Window;

public class ShaderExample extends Window {
  public ShaderExample() {
    super(800, 400);

    // scratch4j:begin window (managed by the project settings)
    this.setStage(new MyStage());
    // scratch4j:end window
  }

  public static void main(String[] args) {
    // scratch4j:begin options (managed by the project settings)
    Window.useFullScreen();
    // scratch4j:end options
    new ShaderExample();
  }
}
