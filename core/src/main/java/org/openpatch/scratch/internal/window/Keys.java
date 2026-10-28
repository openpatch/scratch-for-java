package org.openpatch.scratch.internal.window;

import static org.openpatch.scratch.internal.platform.InputCodes.*;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.internal.platform.Display;

/**
 * GLFW keys to {@link KeyCode}. GLFW names keys by where they sit on a US keyboard, so a German Z
 * (where the US Y is) arrives as KEY_Y: letters and digits therefore go by the name the
 * keyboard layout gives the key ({@link Display#keyName}); everything else by place.
 */
final class Keys {

  private Keys() {}

  static KeyCode map(int key, int scancode, Display display) {
    if (key >= KEY_A && key <= KEY_Z || key >= KEY_0 && key <= KEY_9) {
      String name = display.keyName(key, scancode);
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
      return key <= KEY_9 ? KeyCode.valueOf("DIGIT_" + (char) key) : KeyCode.valueOf(String.valueOf((char) key));
    }
    return switch (key) {
      case KEY_SPACE -> KeyCode.SPACE;
      case KEY_ENTER, KEY_KP_ENTER -> KeyCode.ENTER;
      case KEY_BACKSPACE -> KeyCode.BACKSPACE;
      case KEY_TAB -> KeyCode.TAB;
      case KEY_ESCAPE -> KeyCode.ESCAPE;
      case KEY_LEFT -> KeyCode.LEFT;
      case KEY_RIGHT -> KeyCode.RIGHT;
      case KEY_UP -> KeyCode.UP;
      case KEY_DOWN -> KeyCode.DOWN;
      case KEY_LEFT_SHIFT, KEY_RIGHT_SHIFT -> KeyCode.SHIFT;
      case KEY_LEFT_CONTROL, KEY_RIGHT_CONTROL -> KeyCode.CTRL;
      case KEY_LEFT_ALT, KEY_RIGHT_ALT -> KeyCode.ALT;
      case KEY_LEFT_SUPER, KEY_RIGHT_SUPER -> KeyCode.META;
      case KEY_PAUSE -> KeyCode.PAUSE;
      case KEY_CAPS_LOCK -> KeyCode.CAPS_LOCK;
      case KEY_PAGE_UP -> KeyCode.PAGE_UP;
      case KEY_PAGE_DOWN -> KeyCode.PAGE_DOWN;
      case KEY_END -> KeyCode.END;
      case KEY_HOME -> KeyCode.HOME;
      case KEY_INSERT -> KeyCode.INSERT;
      case KEY_DELETE -> KeyCode.DELETE;
      case KEY_COMMA -> KeyCode.COMMA;
      case KEY_MINUS -> KeyCode.MINUS;
      case KEY_PERIOD -> KeyCode.PERIOD;
      case KEY_SLASH -> KeyCode.SLASH;
      case KEY_SEMICOLON -> KeyCode.SEMICOLON;
      case KEY_EQUAL -> KeyCode.EQUALS;
      case KEY_LEFT_BRACKET -> KeyCode.OPEN_BRACKET;
      case KEY_BACKSLASH -> KeyCode.BACK_SLASH;
      case KEY_RIGHT_BRACKET -> KeyCode.CLOSE_BRACKET;
      case KEY_GRAVE_ACCENT -> KeyCode.BACK_QUOTE;
      case KEY_APOSTROPHE -> KeyCode.QUOTE;
      case KEY_NUM_LOCK -> KeyCode.NUM_LOCK;
      case KEY_SCROLL_LOCK -> KeyCode.SCROLL_LOCK;
      case KEY_PRINT_SCREEN -> KeyCode.PRINT_SCREEN;
      case KEY_MENU -> KeyCode.CONTEXT_MENU;
      case KEY_KP_0 -> KeyCode.NUMPAD_0;
      case KEY_KP_1 -> KeyCode.NUMPAD_1;
      case KEY_KP_2 -> KeyCode.NUMPAD_2;
      case KEY_KP_3 -> KeyCode.NUMPAD_3;
      case KEY_KP_4 -> KeyCode.NUMPAD_4;
      case KEY_KP_5 -> KeyCode.NUMPAD_5;
      case KEY_KP_6 -> KeyCode.NUMPAD_6;
      case KEY_KP_7 -> KeyCode.NUMPAD_7;
      case KEY_KP_8 -> KeyCode.NUMPAD_8;
      case KEY_KP_9 -> KeyCode.NUMPAD_9;
      case KEY_KP_MULTIPLY -> KeyCode.MULTIPLY;
      case KEY_KP_ADD -> KeyCode.ADD;
      case KEY_KP_SUBTRACT -> KeyCode.SUBTRACT;
      case KEY_KP_DECIMAL -> KeyCode.DECIMAL;
      case KEY_KP_DIVIDE -> KeyCode.DIVIDE;
      default -> {
        if (key >= KEY_F1 && key <= KEY_F12) {
          yield KeyCode.valueOf("F" + (key - KEY_F1 + 1));
        }
        yield KeyCode.UNKNOWN;
      }
    };
  }
}
