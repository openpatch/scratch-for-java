package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.gamepad.Gamepad;
import org.openpatch.scratch.gamepad.GamepadButton;
import org.openpatch.scratch.gamepad.GamepadListener;
import org.openpatch.scratch.gamepad.GamepadStick;


/**
 * A controller is a desktop thing: the Online IDE has no gamepad classes yet.
 *
 * @example.online false
 */
public class GamepadExample {
  public GamepadExample() {
    Window myWindow = new Window(480, 360);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new GamepadSprite();
    myStage.add(mySprite);
  }

  public static void main(String[] args) {
    new GamepadExample();
  }
}

class GamepadSprite extends Sprite implements GamepadListener {
  @Override
  public void run() {
    // The first connected controller's left stick: x −1 to 1, y up as on the stage.
    this.changePosition(Gamepad.getStick(GamepadStick.LEFT).multiply(5));
    if (Gamepad.isButtonPressed(GamepadButton.START)) {
      this.setDirection(90);
    }
  }

  @Override
  public void whenGamepadButtonPressed(GamepadButton button) {
    if (button == GamepadButton.A) {
      this.changeY(20); // jump
    }
  }
}
