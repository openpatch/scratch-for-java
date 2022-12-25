package org.openpatch.scratch;

/**
 * Copied from java.awt.event.KeyEvent for easy access.
 */
public final class KeyCode {
    /** The virtual key Enter, which will always map to '\n'. */
    public static final int VK_ENTER = '\n';

    /** The virtual key Backspace, which will always map to '\b'. */
    public static final int VK_BACK_SPACE = '\b';

    /** The virtual key Tab, which will always map to '\t'. */
    public static final int VK_TAB = '\t';

    /** The virtual key Cancel. */
    public static final int VK_CANCEL = 3;

    /** The virtual key VK_CLEAR. */
    public static final int VK_CLEAR = 12;

    /** The virtual key VK_SHIFT. */
    public static final int VK_SHIFT = 16;

    /** The virtual key VK_CONTROL. */
    public static final int VK_CONTROL = 17;

    /** The virtual key VK_ALT. */
    public static final int VK_ALT = 18;

    /** The virtual key VK_PAUSE. */
    public static final int VK_PAUSE = 19;

    /** The virtual key VK_CAPS_LOCK. */
    public static final int VK_CAPS_LOCK = 20;

    /** The virtual key VK_ESCAPE. */
    public static final int VK_ESCAPE = 27;

    /** The virtual key VK_SPACE. */
    public static final int VK_SPACE = ' ';

    /** The virtual key VK_PAGE_UP. */
    public static final int VK_PAGE_UP = 33;

    /** The virtual key VK_PAGE_DOWN. */
    public static final int VK_PAGE_DOWN = 34;

    /** The virtual key VK_END. */
    public static final int VK_END = 35;

    /** The virtual key VK_HOME. */
    public static final int VK_HOME = 36;

    /**
     * The virtual key for the non-numpad VK_LEFT.
     *
     * @see #VK_KP_LEFT
     */
    public static final int VK_LEFT = 37;

    /**
     * The virtual key for the non-numpad VK_UP.
     *
     * @see #VK_KP_UP
     */
    public static final int VK_UP = 38;

    /**
     * The virtual key for the non-numpad VK_RIGHT.
     *
     * @see #VK_KP_RIGHT
     */
    public static final int VK_RIGHT = 39;

    /**
     * The virtual key for the non-numpad VK_DOWN.
     *
     * @see #VK_KP_DOWN
     */
    public static final int VK_DOWN = 40;

    /** The virtual key VK_COMMA. */
    public static final int VK_COMMA = ',';

    /**
     * The virtual key VK_MINUS.
     *
     * @since 1.2
     */
    public static final int VK_MINUS = '-';

    /** The virtual key VK_PERIOD. */
    public static final int VK_PERIOD = '.';

    /** The virtual key VK_SLASH. */
    public static final int VK_SLASH = '/';

    /** The virtual key VK_0. */
    public static final int VK_0 = '0';

    /** The virtual key VK_1. */
    public static final int VK_1 = '1';

    /** The virtual key VK_2. */
    public static final int VK_2 = '2';

    /** The virtual key VK_3. */
    public static final int VK_3 = '3';

    /** The virtual key VK_4. */
    public static final int VK_4 = '4';

    /** The virtual key VK_5. */
    public static final int VK_5 = '5';

    /** The virtual key VK_6. */
    public static final int VK_6 = '6';

    /** The virtual key VK_7. */
    public static final int VK_7 = '7';

    /** The virtual key VK_8. */
    public static final int VK_8 = '8';

    /** The virtual key VK_9. */
    public static final int VK_9 = '9';

    /** The virtual key VK_SEMICOLON. */
    public static final int VK_SEMICOLON = ';';

    /** The virtual key VK_EQUALS. */
    public static final int VK_EQUALS = '=';

    /** The virtual key VK_A. */
    public static final int VK_A = 'A';

    /** The virtual key VK_B. */
    public static final int VK_B = 'B';

    /** The virtual key VK_C. */
    public static final int VK_C = 'C';

    /** The virtual key VK_D. */
    public static final int VK_D = 'D';

    /** The virtual key VK_E. */
    public static final int VK_E = 'E';

    /** The virtual key VK_F. */
    public static final int VK_F = 'F';

    /** The virtual key VK_G. */
    public static final int VK_G = 'G';

    /** The virtual key VK_H. */
    public static final int VK_H = 'H';

    /** The virtual key VK_I. */
    public static final int VK_I = 'I';

    /** The virtual key VK_J. */
    public static final int VK_J = 'J';

    /** The virtual key VK_K. */
    public static final int VK_K = 'K';

    /** The virtual key VK_L. */
    public static final int VK_L = 'L';

    /** The virtual key VK_M. */
    public static final int VK_M = 'M';

    /** The virtual key VK_N. */
    public static final int VK_N = 'N';

    /** The virtual key VK_O. */
    public static final int VK_O = 'O';

    /** The virtual key VK_P. */
    public static final int VK_P = 'P';

    /** The virtual key VK_Q. */
    public static final int VK_Q = 'Q';

    /** The virtual key VK_R. */
    public static final int VK_R = 'R';

    /** The virtual key VK_S. */
    public static final int VK_S = 'S';

    /** The virtual key VK_T. */
    public static final int VK_T = 'T';

    /** The virtual key VK_U. */
    public static final int VK_U = 'U';

    /** The virtual key VK_V. */
    public static final int VK_V = 'V';

    /** The virtual key VK_W. */
    public static final int VK_W = 'W';

    /** The virtual key VK_X. */
    public static final int VK_X = 'X';

    /** The virtual key VK_Y. */
    public static final int VK_Y = 'Y';

    /** The virtual key VK_Z. */
    public static final int VK_Z = 'Z';

    /** The virtual key VK_OPEN_BRACKET. */
    public static final int VK_OPEN_BRACKET = '[';

    /** The virtual key VK_BACK_SLASH. */
    public static final int VK_BACK_SLASH = '\\';

    /** The virtual key VK_CLOSE_BRACKET. */
    public static final int VK_CLOSE_BRACKET = ']';

    /** The virtual key VK_NUMPAD0. */
    public static final int VK_NUMPAD0 = 96;

    /** The virtual key VK_NUMPAD1. */
    public static final int VK_NUMPAD1 = 97;

    /** The virtual key VK_NUMPAD2. */
    public static final int VK_NUMPAD2 = 98;

    /** The virtual key VK_NUMPAD3. */
    public static final int VK_NUMPAD3 = 99;

    /** The virtual key VK_NUMPAD4. */
    public static final int VK_NUMPAD4 = 100;

    /** The virtual key VK_NUMPAD5. */
    public static final int VK_NUMPAD5 = 101;

    /** The virtual key VK_NUMPAD6. */
    public static final int VK_NUMPAD6 = 102;

    /** The virtual key VK_NUMPAD7. */
    public static final int VK_NUMPAD7 = 103;

    /** The virtual key VK_NUMPAD8. */
    public static final int VK_NUMPAD8 = 104;

    /** The virtual key VK_NUMPAD9. */
    public static final int VK_NUMPAD9 = 105;

    /** The virtual key VK_MULTIPLY. */
    public static final int VK_MULTIPLY = 106;

    /** The virtual key VK_ADD. */
    public static final int VK_ADD = 107;

    /**
     * The virtual key VK_SEPARATOR, handily mispelled for those who can't
     * figure it out.
     *
     * @deprecated use {@link #VK_SEPARATOR}
     */
    public static final int VK_SEPARATER = 108;

    /**
     * The virtual key VK_SEPARATOR.
     *
     * @since 1.4
     */
    public static final int VK_SEPARATOR = 108;

    /** The virtual key VK_SUBTRACT. */
    public static final int VK_SUBTRACT = 109;

    /** The virtual key VK_DECIMAL. */
    public static final int VK_DECIMAL = 110;

    /** The virtual key VK_DIVIDE. */
    public static final int VK_DIVIDE = 111;

    /** The virtual key VK_DELETE. */
    public static final int VK_DELETE = 127;

    /** The virtual key VK_NUM_LOCK. */
    public static final int VK_NUM_LOCK = 144;

    /** The virtual key VK_SCROLL_LOCK. */
    public static final int VK_SCROLL_LOCK = 145;

    /** The virtual key VK_F1. */
    public static final int VK_F1 = 112;

    /** The virtual key VK_F2. */
    public static final int VK_F2 = 113;

    /** The virtual key VK_F3. */
    public static final int VK_F3 = 114;

    /** The virtual key VK_F4. */
    public static final int VK_F4 = 115;

    /** The virtual key VK_F5. */
    public static final int VK_F5 = 116;

    /** The virtual key VK_F6. */
    public static final int VK_F6 = 117;

    /** The virtual key VK_F7. */
    public static final int VK_F7 = 118;

    /** The virtual key VK_F8. */
    public static final int VK_F8 = 119;

    /** The virtual key VK_F9. */
    public static final int VK_F9 = 120;

    /** The virtual key VK_F10. */
    public static final int VK_F10 = 121;

    /** The virtual key VK_F11. */
    public static final int VK_F11 = 122;

    /** The virtual key VK_F12. */
    public static final int VK_F12 = 123;

    /**
     * The virtual key VK_F13.
     *
     * @since 1.2
     */
    public static final int VK_F13 = 61440;

    /**
     * The virtual key VK_F14.
     *
     * @since 1.2
     */
    public static final int VK_F14 = 61441;

    /**
     * The virtual key VK_F15.
     *
     * @since 1.2
     */
    public static final int VK_F15 = 61442;

    /**
     * The virtual key VK_F16.
     *
     * @since 1.2
     */
    public static final int VK_F16 = 61443;

    /**
     * The virtual key VK_F17.
     *
     * @since 1.2
     */
    public static final int VK_F17 = 61444;

    /**
     * The virtual key VK_F18.
     *
     * @since 1.2
     */
    public static final int VK_F18 = 61445;

    /**
     * The virtual key VK_F19.
     *
     * @since 1.2
     */
    public static final int VK_F19 = 61446;

    /**
     * The virtual key VK_F20.
     *
     * @since 1.2
     */
    public static final int VK_F20 = 61447;

    /**
     * The virtual key VK_F21.
     *
     * @since 1.2
     */
    public static final int VK_F21 = 61448;

    /**
     * The virtual key VK_F22.
     *
     * @since 1.2
     */
    public static final int VK_F22 = 61449;

    /**
     * The virtual key VK_F23.
     *
     * @since 1.2
     */
    public static final int VK_F23 = 61450;

    /**
     * The virtual key VK_F24.
     *
     * @since 1.2
     */
    public static final int VK_F24 = 61451;

    /** The virtual key VK_PRINTSCREEN. */
    public static final int VK_PRINTSCREEN = 154;

    /** The virtual key VK_INSERT. */
    public static final int VK_INSERT = 155;

    /** The virtual key VK_HELP. */
    public static final int VK_HELP = 156;

    /** The virtual key VK_META. */
    public static final int VK_META = 157;

    /** The virtual key VK_BACK_QUOTE. */
    public static final int VK_BACK_QUOTE = 192;

    /** The virtual key VK_QUOTE. */
    public static final int VK_QUOTE = 222;

    /**
     * The virtual key for the numpad VK_KP_UP.
     *
     * @see #VK_UP
     * @since 1.2
     */
    public static final int VK_KP_UP = 224;

    /**
     * The virtual key for the numpad VK_KP_DOWN.
     *
     * @see #VK_DOWN
     * @since 1.2
     */
    public static final int VK_KP_DOWN = 225;

    /**
     * The virtual key for the numpad VK_KP_LEFT.
     *
     * @see #VK_LEFT
     * @since 1.2
     */
    public static final int VK_KP_LEFT = 226;

    /**
     * The virtual key for the numpad VK_KP_RIGHT.
     *
     * @see #VK_RIGHT
     * @since 1.2
     */
    public static final int VK_KP_RIGHT = 227;

    /**
     * The virtual key VK_DEAD_GRAVE.
     *
     * @since 1.2
     */
    public static final int VK_DEAD_GRAVE = 128;

    /**
     * The virtual key VK_DEAD_ACUTE.
     *
     * @since 1.2
     */
    public static final int VK_DEAD_ACUTE = 129;

    /**
     * The virtual key VK_DEAD_CIRCUMFLEX.
     *
     * @since 1.2
     */
    public static final int VK_DEAD_CIRCUMFLEX = 130;

    /**
     * The virtual key VK_DEAD_TILDE.
     *
     * @since 1.2
     */
    public static final int VK_DEAD_TILDE = 131;

    /**
     * The virtual key VK_DEAD_MACRON.
     *
     * @since 1.2
     */
    public static final int VK_DEAD_MACRON = 132;

    /**
     * The virtual key VK_DEAD_BREVE.
     *
     * @since 1.2
     */
    public static final int VK_DEAD_BREVE = 133;

    /**
     * The virtual key VK_DEAD_ABOVEDOT.
     *
     * @since 1.2
     */
    public static final int VK_DEAD_ABOVEDOT = 134;

    /**
     * The virtual key VK_DEAD_DIAERESIS.
     *
     * @since 1.2
     */
    public static final int VK_DEAD_DIAERESIS = 135;

    /**
     * The virtual key VK_DEAD_ABOVERING.
     *
     * @since 1.2
     */
    public static final int VK_DEAD_ABOVERING = 136;

    /**
     * The virtual key VK_DEAD_DOUBLEACUTE.
     *
     * @since 1.2
     */
    public static final int VK_DEAD_DOUBLEACUTE = 137;

    /**
     * The virtual key VK_DEAD_CARON.
     *
     * @since 1.2
     */
    public static final int VK_DEAD_CARON = 138;

    /**
     * The virtual key VK_DEAD_CEDILLA.
     *
     * @since 1.2
     */
    public static final int VK_DEAD_CEDILLA = 139;

    /**
     * The virtual key VK_DEAD_OGONEK.
     *
     * @since 1.2
     */
    public static final int VK_DEAD_OGONEK = 140;

    /**
     * The virtual key VK_DEAD_IOTA.
     *
     * @since 1.2
     */
    public static final int VK_DEAD_IOTA = 141;

    /**
     * The virtual key VK_DEAD_VOICED_SOUND.
     *
     * @since 1.2
     */
    public static final int VK_DEAD_VOICED_SOUND = 142;

    /**
     * The virtual key VK_DEAD_SEMIVOICED_SOUND.
     *
     * @since 1.2
     */
    public static final int VK_DEAD_SEMIVOICED_SOUND = 143;

    /**
     * The virtual key VK_AMPERSAND.
     *
     * @since 1.2
     */
    public static final int VK_AMPERSAND = 150;

    /**
     * The virtual key VK_ASTERISK.
     *
     * @since 1.2
     */
    public static final int VK_ASTERISK = 151;

    /**
     * The virtual key VK_QUOTEDBL.
     *
     * @since 1.2
     */
    public static final int VK_QUOTEDBL = 152;

    /**
     * The virtual key VK_LESS.
     *
     * @since 1.2
     */
    public static final int VK_LESS = 153;

    /**
     * The virtual key VK_GREATER.
     *
     * @since 1.2
     */
    public static final int VK_GREATER = 160;

    /**
     * The virtual key VK_BRACELEFT.
     *
     * @since 1.2
     */
    public static final int VK_BRACELEFT = 161;

    /**
     * The virtual key VK_BRACERIGHT.
     *
     * @since 1.2
     */
    public static final int VK_BRACERIGHT = 162;

    /**
     * The virtual key VK_AT.
     *
     * @since 1.2
     */
    public static final int VK_AT = 512;

    /**
     * The virtual key VK_COLON.
     *
     * @since 1.2
     */
    public static final int VK_COLON = 513;

    /**
     * The virtual key VK_CIRCUMFLEX.
     *
     * @since 1.2
     */
    public static final int VK_CIRCUMFLEX = 514;

    /**
     * The virtual key VK_DOLLAR.
     *
     * @since 1.2
     */
    public static final int VK_DOLLAR = 515;

    /**
     * The virtual key VK_EURO_SIGN.
     *
     * @since 1.2
     */
    public static final int VK_EURO_SIGN = 516;

    /**
     * The virtual key VK_EXCLAMATION_MARK.
     *
     * @since 1.2
     */
    public static final int VK_EXCLAMATION_MARK = 517;

    /**
     * The virtual key VK_INVERTED_EXCLAMATION_MARK.
     *
     * @since 1.2
     */
    public static final int VK_INVERTED_EXCLAMATION_MARK = 518;

    /**
     * The virtual key VK_LEFT_PARENTHESIS.
     *
     * @since 1.2
     */
    public static final int VK_LEFT_PARENTHESIS = 519;

    /**
     * The virtual key VK_NUMBER_SIGN.
     *
     * @since 1.2
     */
    public static final int VK_NUMBER_SIGN = 520;

    /**
     * The virtual key VK_PLUS.
     *
     * @since 1.2
     */
    public static final int VK_PLUS = 521;

    /**
     * The virtual key VK_RIGHT_PARENTHESIS.
     *
     * @since 1.2
     */
    public static final int VK_RIGHT_PARENTHESIS = 522;

    /**
     * The virtual key VK_UNDERSCORE.
     *
     * @since 1.2
     */
    public static final int VK_UNDERSCORE = 523;

    /** The virtual key VK_FINAL. */
    public static final int VK_FINAL = 24;

    /** The virtual key VK_CONVERT. */
    public static final int VK_CONVERT = 28;

    /** The virtual key VK_NONCONVERT. */
    public static final int VK_NONCONVERT = 29;

    /** The virtual key VK_ACCEPT. */
    public static final int VK_ACCEPT = 30;

    /** The virtual key VK_MODECHANGE. */
    public static final int VK_MODECHANGE = 31;

    /** The virtual key VK_KANA. */
    public static final int VK_KANA = 21;

    /** The virtual key VK_KANJI. */
    public static final int VK_KANJI = 25;

    /**
     * The virtual key VK_ALPHANUMERIC.
     *
     * @since 1.2
     */
    public static final int VK_ALPHANUMERIC = 240;

    /**
     * The virtual key VK_KATAKANA.
     *
     * @since 1.2
     */
    public static final int VK_KATAKANA = 241;

    /**
     * The virtual key VK_HIRAGANA.
     *
     * @since 1.2
     */
    public static final int VK_HIRAGANA = 242;

    /**
     * The virtual key VK_FULL_WIDTH.
     *
     * @since 1.2
     */
    public static final int VK_FULL_WIDTH = 243;

    /**
     * The virtual key VK_HALF_WIDTH.
     *
     * @since 1.2
     */
    public static final int VK_HALF_WIDTH = 244;

    /**
     * The virtual key VK_ROMAN_CHARACTERS.
     *
     * @since 1.2
     */
    public static final int VK_ROMAN_CHARACTERS = 245;

    /**
     * The virtual key VK_ALL_CANDIDATES.
     *
     * @since 1.2
     */
    public static final int VK_ALL_CANDIDATES = 256;

    /**
     * The virtual key VK_PREVIOUS_CANDIDATE.
     *
     * @since 1.2
     */
    public static final int VK_PREVIOUS_CANDIDATE = 257;

    /**
     * The virtual key VK_CODE_INPUT.
     *
     * @since 1.2
     */
    public static final int VK_CODE_INPUT = 258;

    /**
     * The virtual key VK_JAPANESE_KATAKANA.
     *
     * @since 1.2
     */
    public static final int VK_JAPANESE_KATAKANA = 259;

    /**
     * The virtual key VK_JAPANESE_HIRAGANA.
     *
     * @since 1.2
     */
    public static final int VK_JAPANESE_HIRAGANA = 260;

    /**
     * The virtual key VK_JAPANESE_ROMAN.
     *
     * @since 1.2
     */
    public static final int VK_JAPANESE_ROMAN = 261;

    /**
     * The virtual key VK_KANA_LOCK.
     *
     * @since 1.3
     */
    public static final int VK_KANA_LOCK = 262;

    /**
     * The virtual key VK_INPUT_METHOD_ON_OFF.
     *
     * @since 1.3
     */
    public static final int VK_INPUT_METHOD_ON_OFF = 263;

    /**
     * The virtual key VK_CUT.
     *
     * @since 1.2
     */
    public static final int VK_CUT = 65489;

    /**
     * The virtual key VK_COPY.
     *
     * @since 1.2
     */
    public static final int VK_COPY = 65485;

    /**
     * The virtual key VK_PASTE.
     *
     * @since 1.2
     */
    public static final int VK_PASTE = 65487;

    /**
     * The virtual key VK_UNDO.
     *
     * @since 1.2
     */
    public static final int VK_UNDO = 65483;

    /**
     * The virtual key VK_AGAIN.
     *
     * @since 1.2
     */
    public static final int VK_AGAIN = 65481;

    /**
     * The virtual key VK_FIND.
     *
     * @since 1.2
     */
    public static final int VK_FIND = 65488;

    /**
     * The virtual key VK_PROPS.
     *
     * @since 1.2
     */
    public static final int VK_PROPS = 65482;

    /**
     * The virtual key VK_STOP.
     *
     * @since 1.2
     */
    public static final int VK_STOP = 65480;

    /**
     * The virtual key VK_COMPOSE.
     *
     * @since 1.2
     */
    public static final int VK_COMPOSE = 65312;

    /**
     * The virtual key VK_ALT_GRAPH.
     *
     * @since 1.2
     */
    public static final int VK_ALT_GRAPH = 65406;

    /**
     * The 'begin' key VK_BEGIN
     *
     * @since 1.5
     */
    public static final int VK_BEGIN = 65368;

    /**
     * The context-menu key VK_CONTEXT_MENU
     *
     * @since 1.5
     */
    public static final int VK_CONTEXT_MENU = 525;

    /**
     * The 'Windows' key VK_WINDOWS
     *
     * @since 1.5
     */
    public static final int VK_WINDOWS = 524;

    /**
     * The virtual key VK_UNDEFINED. This is used for key typed events, which
     * do not have a virtual key.
     */
    public static final int VK_UNDEFINED = 0;
}