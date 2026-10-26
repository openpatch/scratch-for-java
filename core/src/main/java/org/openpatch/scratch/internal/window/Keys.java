package org.openpatch.scratch.internal.window;

import static org.lwjgl.glfw.GLFW.*;

import org.openpatch.scratch.KeyCode;

/**
 * GLFW keys to {@link KeyCode}. GLFW names keys by where they sit on a US keyboard, so a German Z
 * (where the US Y is) arrives as GLFW_KEY_Y: letters and digits therefore go by the name the
 * keyboard layout gives the key ({@code glfwGetKeyName}); everything else by place.
 */
final class Keys {

  private Keys() {}

  static KeyCode map(int key, int scancode) {
    if (key >= GLFW_KEY_A && key <= GLFW_KEY_Z || key >= GLFW_KEY_0 && key <= GLFW_KEY_9) {
      String name = glfwGetKeyName(key, scancode);
      if (name != null && name.length() == 1) {
        char c = Character.toUpperCase(name.charAt(0));
        if (c >= 'A' && c <= 'Z') {
          return KeyCode.valueOf(String.valueOf(c));
        }
        if (c >= '0' && c <= '9') {
          return KeyCode.valueOf("DIGIT_" + c);
        }
      }
      // a layout that has no letter there (Cyrillic, …): the US place
      return key <= GLFW_KEY_9 ? KeyCode.valueOf("DIGIT_" + (char) key) : KeyCode.valueOf(String.valueOf((char) key));
    }
    return switch (key) {
      case GLFW_KEY_SPACE -> KeyCode.SPACE;
      case GLFW_KEY_ENTER, GLFW_KEY_KP_ENTER -> KeyCode.ENTER;
      case GLFW_KEY_BACKSPACE -> KeyCode.BACKSPACE;
      case GLFW_KEY_TAB -> KeyCode.TAB;
      case GLFW_KEY_ESCAPE -> KeyCode.ESCAPE;
      case GLFW_KEY_LEFT -> KeyCode.LEFT;
      case GLFW_KEY_RIGHT -> KeyCode.RIGHT;
      case GLFW_KEY_UP -> KeyCode.UP;
      case GLFW_KEY_DOWN -> KeyCode.DOWN;
      case GLFW_KEY_LEFT_SHIFT, GLFW_KEY_RIGHT_SHIFT -> KeyCode.SHIFT;
      case GLFW_KEY_LEFT_CONTROL, GLFW_KEY_RIGHT_CONTROL -> KeyCode.CTRL;
      case GLFW_KEY_LEFT_ALT, GLFW_KEY_RIGHT_ALT -> KeyCode.ALT;
      case GLFW_KEY_LEFT_SUPER, GLFW_KEY_RIGHT_SUPER -> KeyCode.META;
      case GLFW_KEY_PAUSE -> KeyCode.PAUSE;
      case GLFW_KEY_CAPS_LOCK -> KeyCode.CAPS_LOCK;
      case GLFW_KEY_PAGE_UP -> KeyCode.PAGE_UP;
      case GLFW_KEY_PAGE_DOWN -> KeyCode.PAGE_DOWN;
      case GLFW_KEY_END -> KeyCode.END;
      case GLFW_KEY_HOME -> KeyCode.HOME;
      case GLFW_KEY_INSERT -> KeyCode.INSERT;
      case GLFW_KEY_DELETE -> KeyCode.DELETE;
      case GLFW_KEY_COMMA -> KeyCode.COMMA;
      case GLFW_KEY_MINUS -> KeyCode.MINUS;
      case GLFW_KEY_PERIOD -> KeyCode.PERIOD;
      case GLFW_KEY_SLASH -> KeyCode.SLASH;
      case GLFW_KEY_SEMICOLON -> KeyCode.SEMICOLON;
      case GLFW_KEY_EQUAL -> KeyCode.EQUALS;
      case GLFW_KEY_LEFT_BRACKET -> KeyCode.OPEN_BRACKET;
      case GLFW_KEY_BACKSLASH -> KeyCode.BACK_SLASH;
      case GLFW_KEY_RIGHT_BRACKET -> KeyCode.CLOSE_BRACKET;
      case GLFW_KEY_GRAVE_ACCENT -> KeyCode.BACK_QUOTE;
      case GLFW_KEY_APOSTROPHE -> KeyCode.QUOTE;
      case GLFW_KEY_NUM_LOCK -> KeyCode.NUM_LOCK;
      case GLFW_KEY_SCROLL_LOCK -> KeyCode.SCROLL_LOCK;
      case GLFW_KEY_PRINT_SCREEN -> KeyCode.PRINT_SCREEN;
      case GLFW_KEY_MENU -> KeyCode.CONTEXT_MENU;
      case GLFW_KEY_KP_0 -> KeyCode.NUMPAD_0;
      case GLFW_KEY_KP_1 -> KeyCode.NUMPAD_1;
      case GLFW_KEY_KP_2 -> KeyCode.NUMPAD_2;
      case GLFW_KEY_KP_3 -> KeyCode.NUMPAD_3;
      case GLFW_KEY_KP_4 -> KeyCode.NUMPAD_4;
      case GLFW_KEY_KP_5 -> KeyCode.NUMPAD_5;
      case GLFW_KEY_KP_6 -> KeyCode.NUMPAD_6;
      case GLFW_KEY_KP_7 -> KeyCode.NUMPAD_7;
      case GLFW_KEY_KP_8 -> KeyCode.NUMPAD_8;
      case GLFW_KEY_KP_9 -> KeyCode.NUMPAD_9;
      case GLFW_KEY_KP_MULTIPLY -> KeyCode.MULTIPLY;
      case GLFW_KEY_KP_ADD -> KeyCode.ADD;
      case GLFW_KEY_KP_SUBTRACT -> KeyCode.SUBTRACT;
      case GLFW_KEY_KP_DECIMAL -> KeyCode.DECIMAL;
      case GLFW_KEY_KP_DIVIDE -> KeyCode.DIVIDE;
      default -> {
        if (key >= GLFW_KEY_F1 && key <= GLFW_KEY_F12) {
          yield KeyCode.valueOf("F" + (key - GLFW_KEY_F1 + 1));
        }
        yield KeyCode.UNKNOWN;
      }
    };
  }
}
