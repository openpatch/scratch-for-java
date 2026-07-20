package org.openpatch.scratch;

/**
 * Key codes for keyboard input handling.
 *
 * <p>Use these constants to check which key was pressed or released.
 *
 * <pre>
 * {@code
 * // In whenKeyPressed:
 * if (keyCode == KeyCode.SPACE) { ... }
 *
 * // In isKeyPressed:
 * if (this.isKeyPressed(KeyCode.UP)) { ... }
 * }
 * </pre>
 */
public enum KeyCode {
  ENTER('\n'),
  BACKSPACE('\b'),
  TAB('\t'),
  CANCEL(0x03),
  CLEAR(0x0C),
  SHIFT(0x10),
  CTRL(0x11),
  ALT(0x12),
  PAUSE(0x13),
  CAPS_LOCK(0x14),
  ESCAPE(0x1B),
  SPACE(0x20),
  PAGE_UP(0x21),
  PAGE_DOWN(0x22),
  END(0x23),
  HOME(0x24),
  LEFT(0x25),
  UP(0x26),
  RIGHT(0x27),
  DOWN(0x28),
  COMMA(0x2C),
  MINUS(0x2D),
  PERIOD(0x2E),
  SLASH(0x2F),
  DIGIT_0(0x30),
  DIGIT_1(0x31),
  DIGIT_2(0x32),
  DIGIT_3(0x33),
  DIGIT_4(0x34),
  DIGIT_5(0x35),
  DIGIT_6(0x36),
  DIGIT_7(0x37),
  DIGIT_8(0x38),
  DIGIT_9(0x39),
  SEMICOLON(0x3B),
  EQUALS(0x3D),
  A(0x41),
  B(0x42),
  C(0x43),
  D(0x44),
  E(0x45),
  F(0x46),
  G(0x47),
  H(0x48),
  I(0x49),
  J(0x4A),
  K(0x4B),
  L(0x4C),
  M(0x4D),
  N(0x4E),
  O(0x4F),
  P(0x50),
  Q(0x51),
  R(0x52),
  S(0x53),
  T(0x54),
  U(0x55),
  V(0x56),
  W(0x57),
  X(0x58),
  Y(0x59),
  Z(0x5A),
  OPEN_BRACKET(0x5B),
  BACK_SLASH(0x5C),
  CLOSE_BRACKET(0x5D),
  NUMPAD_0(0x60),
  NUMPAD_1(0x61),
  NUMPAD_2(0x62),
  NUMPAD_3(0x63),
  NUMPAD_4(0x64),
  NUMPAD_5(0x65),
  NUMPAD_6(0x66),
  NUMPAD_7(0x67),
  NUMPAD_8(0x68),
  NUMPAD_9(0x69),
  MULTIPLY(0x6A),
  ADD(0x6B),
  SUBTRACT(0x6D),
  DECIMAL(0x6E),
  DIVIDE(0x6F),
  F1(0x70),
  F2(0x71),
  F3(0x72),
  F4(0x73),
  F5(0x74),
  F6(0x75),
  F7(0x76),
  F8(0x77),
  F9(0x78),
  F10(0x79),
  F11(0x7A),
  F12(0x7B),
  DELETE(0x7F),
  NUM_LOCK(0x90),
  SCROLL_LOCK(0x91),
  PRINT_SCREEN(0x9A),
  INSERT(0x9B),
  HELP(0x9C),
  META(0x9D),
  BACK_QUOTE(0xC0),
  QUOTE(0xDE),
  KP_UP(0xE0),
  KP_DOWN(0xE1),
  KP_LEFT(0xE2),
  KP_RIGHT(0xE3),
  WINDOWS(0x020C),
  CONTEXT_MENU(0x020D),
  UNKNOWN(0);

  private final int code;

  KeyCode(int code) {
    this.code = code;
  }

  public int getCode() {
    return this.code;
  }

  public static KeyCode fromCode(int code) {
    for (KeyCode keyCode : values()) {
      if (keyCode.code == code) {
        return keyCode;
      }
    }
    return UNKNOWN;
  }
}
