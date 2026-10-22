package spike;

import static org.lwjgl.glfw.GLFW.*;

/**
 * GLFW keys to the AWT virtual-key values {@code KeyCode} uses. GLFW names a
 * key by where it sits on a US keyboard, so a German Z (where the US Y is)
 * arrives as GLFW_KEY_Y. Printable keys therefore go by the layout's name
 * for the key ({@code glfwGetKeyName}); the rest by a table.
 */
final class KeyMapping {

  private KeyMapping() {}

  static int toVirtualKey(int key, String layoutName) {
    if (layoutName != null && layoutName.length() == 1) {
      char c = Character.toUpperCase(layoutName.charAt(0));
      if ((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
        return c;
      }
    }
    return switch (key) {
      case GLFW_KEY_SPACE -> 0x20;
      case GLFW_KEY_ENTER, GLFW_KEY_KP_ENTER -> '\n';
      case GLFW_KEY_BACKSPACE -> '\b';
      case GLFW_KEY_TAB -> '\t';
      case GLFW_KEY_ESCAPE -> 0x1B;
      case GLFW_KEY_LEFT -> 0x25;
      case GLFW_KEY_UP -> 0x26;
      case GLFW_KEY_RIGHT -> 0x27;
      case GLFW_KEY_DOWN -> 0x28;
      case GLFW_KEY_LEFT_SHIFT, GLFW_KEY_RIGHT_SHIFT -> 0x10;
      case GLFW_KEY_LEFT_CONTROL, GLFW_KEY_RIGHT_CONTROL -> 0x11;
      case GLFW_KEY_LEFT_ALT, GLFW_KEY_RIGHT_ALT -> 0x12;
      case GLFW_KEY_F12 -> 0x7B;
      default -> key >= GLFW_KEY_A && key <= GLFW_KEY_Z || key >= GLFW_KEY_0 && key <= GLFW_KEY_9
          ? key // GLFW uses ASCII there too, but by US position
          : -1;
    };
  }
}
