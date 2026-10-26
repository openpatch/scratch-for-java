package org.openpatch.scratch;

import java.util.Random;

/**
 * Provides predefined HTML colors as Color objects.
 *
 * @see <a href="https://www.w3schools.com/colors/colors_names.asp">HTML Color Names</a>
 * 
 * <p>The constants never change: colours are immutable.
 *
 * <pre>{@code
 * Color myColor = HtmlColor.CORNFLOWER_BLUE;
 * }</pre>
 *
 * @example.files HtmlColorAll.java
 */
public final class HtmlColor {

  private HtmlColor() {}

  /** The color Alice Blue. */
  public static final Color ALICE_BLUE = new Color("#F0F8FF");

  /** The color Antique White. */
  public static final Color ANTIQUE_WHITE = new Color("#FAEBD7");

  /** The color Aqua. */
  public static final Color AQUA = new Color("#00FFFF");

  /** The color Aquamarine. */
  public static final Color AQUAMARINE = new Color("#7FFFD4");

  /** The color Azure. */
  public static final Color AZURE = new Color("#F0FFFF");

  /** The color Beige. */
  public static final Color BEIGE = new Color("#F5F5DC");

  /** The color Bisque. */
  public static final Color BISQUE = new Color("#FFE4C4");

  /** The color Black. */
  public static final Color BLACK = new Color("#000000");

  /** The color Blanched Almond. */
  public static final Color BLANCHED_ALMOND = new Color("#FFEBCD");

  /** The color Blue. */
  public static final Color BLUE = new Color("#0000FF");

  /** The color Blue Violet. */
  public static final Color BLUE_VIOLET = new Color("#8A2BE2");

  /** The color Brown. */
  public static final Color BROWN = new Color("#A52A2A");

  /** The color Burlywood. */
  public static final Color BURLYWOOD = new Color("#DEB887");

  /** The color Cadet Blue. */
  public static final Color CADET_BLUE = new Color("#5F9EA0");

  /** The color Chartreuse. */
  public static final Color CHARTREUSE = new Color("#7FFF00");

  /** The color Chocolate. */
  public static final Color CHOCOLATE = new Color("#D2691E");

  /** The color Coral. */
  public static final Color CORAL = new Color("#FF7F50");

  /** The color Cornflower Blue. */
  public static final Color CORNFLOWER_BLUE = new Color("#6495ED");

  /** The color Cornsilk. */
  public static final Color CORNSILK = new Color("#FFF8DC");

  /** The color Crimson. */
  public static final Color CRIMSON = new Color("#DC143C");

  /** The color Cyan. */
  public static final Color CYAN = new Color("#00FFFF");

  /** The color Dark Blue. */
  public static final Color DARK_BLUE = new Color("#00008B");

  /** The color Dark Cyan. */
  public static final Color DARK_CYAN = new Color("#008B8B");

  /** The color Dark Goldenrod. */
  public static final Color DARK_GOLDENROD = new Color("#B8860B");

  /** The color Dark Gray. */
  public static final Color DARK_GRAY = new Color("#A9A9A9");

  /** The color Dark Green. */
  public static final Color DARK_GREEN = new Color("#006400");

  /** The color Dark Khaki. */
  public static final Color DARK_KHAKI = new Color("#BDB76B");

  /** The color Dark Magenta. */
  public static final Color DARK_MAGENTA = new Color("#8B008B");

  /** The color Dark Olive Green. */
  public static final Color DARK_OLIVE_GREEN = new Color("#556B2F");

  /** The color Dark Orange. */
  public static final Color DARK_ORANGE = new Color("#FF8C00");

  /** The color Dark Orchid. */
  public static final Color DARK_ORCHID = new Color("#9932CC");

  /** The color Dark Red. */
  public static final Color DARK_RED = new Color("#8B0000");

  /** The color Dark Salmon. */
  public static final Color DARK_SALMON = new Color("#E9967A");

  /** The color Dark Sea Green. */
  public static final Color DARK_SEA_GREEN = new Color("#8FBC8F");

  /** The color Dark Slate Blue. */
  public static final Color DARK_SLATE_BLUE = new Color("#483D8B");

  /** The color Dark Slate Gray. */
  public static final Color DARK_SLATE_GRAY = new Color("#2F4F4F");

  /** The color Dark Turquoise. */
  public static final Color DARK_TURQUOISE = new Color("#00CED1");

  /** The color Dark Violet. */
  public static final Color DARK_VIOLET = new Color("#9400D3");

  /** The color Deep Pink. */
  public static final Color DEEP_PINK = new Color("#FF1493");

  /** The color Deep Sky Blue. */
  public static final Color DEEP_SKY_BLUE = new Color("#00BFFF");

  /** The color Dim Gray. */
  public static final Color DIM_GRAY = new Color("#696969");

  /** The color Dodger Blue. */
  public static final Color DODGER_BLUE = new Color("#1E90FF");

  /** The color Firebrick. */
  public static final Color FIREBRICK = new Color("#B22222");

  /** The color Floral White. */
  public static final Color FLORAL_WHITE = new Color("#FFFAF0");

  /** The color Forest Green. */
  public static final Color FOREST_GREEN = new Color("#228B22");

  /** The color Fuchsia. */
  public static final Color FUCHSIA = new Color("#FF00FF");

  /** The color Gainsboro. */
  public static final Color GAINSBORO = new Color("#DCDCDC");

  /** The color Ghost White. */
  public static final Color GHOST_WHITE = new Color("#F8F8FF");

  /** The color Gold. */
  public static final Color GOLD = new Color("#FFD700");

  /** The color Goldenrod. */
  public static final Color GOLDENROD = new Color("#DAA520");

  /** The color Gray. */
  public static final Color GRAY = new Color("#808080");

  /** The color Green. */
  public static final Color GREEN = new Color("#008000");

  /** The color Green Yellow. */
  public static final Color GREEN_YELLOW = new Color("#ADFF2F");

  /** The color Honeydew. */
  public static final Color HONEYDEW = new Color("#F0FFF0");

  /** The color Hot Pink. */
  public static final Color HOT_PINK = new Color("#FF69B4");

  /** The color Indian Red. */
  public static final Color INDIAN_RED = new Color("#CD5C5C");

  /** The color Indigo. */
  public static final Color INDIGO = new Color("#4B0082");

  /** The color Ivory. */
  public static final Color IVORY = new Color("#FFFFF0");

  /** The color Khaki. */
  public static final Color KHAKI = new Color("#F0E68C");

  /** The color Lavender. */
  public static final Color LAVENDER = new Color("#E6E6FA");

  /** The color Lavender Blush. */
  public static final Color LAVENDER_BLUSH = new Color("#FFF0F5");

  /** The color Lawn Green. */
  public static final Color LAWN_GREEN = new Color("#7CFC00");

  /** The color Lemon Chiffon. */
  public static final Color LEMON_CHIFFON = new Color("#FFFACD");

  /** The color Light Blue. */
  public static final Color LIGHT_BLUE = new Color("#ADD8E6");

  /** The color Light Coral. */
  public static final Color LIGHT_CORAL = new Color("#F08080");

  /** The color Light Cyan. */
  public static final Color LIGHT_CYAN = new Color("#E0FFFF");

  /** The color Light Goldenrod Yellow. */
  public static final Color LIGHT_GOLDENROD_YELLOW = new Color("#FAFAD2");

  /** The color Light Gray. */
  public static final Color LIGHT_GRAY = new Color("#D3D3D3");

  /** The color Light Green. */
  public static final Color LIGHT_GREEN = new Color("#90EE90");

  /** The color Light Pink. */
  public static final Color LIGHT_PINK = new Color("#FFB6C1");

  /** The color Light Salmon. */
  public static final Color LIGHT_SALMON = new Color("#FFA07A");

  /** The color Light Sea Green. */
  public static final Color LIGHT_SEA_GREEN = new Color("#20B2AA");

  /** The color Light Sky Blue. */
  public static final Color LIGHT_SKY_BLUE = new Color("#87CEFA");

  /** The color Light Slate Gray. */
  public static final Color LIGHT_SLATE_GRAY = new Color("#778899");

  /** The color Light Steel Blue. */
  public static final Color LIGHT_STEEL_BLUE = new Color("#B0C4DE");

  /** The color Light Yellow. */
  public static final Color LIGHT_YELLOW = new Color("#FFFFE0");

  /** The color Lime. */
  public static final Color LIME = new Color("#00FF00");

  /** The color Lime Green. */
  public static final Color LIME_GREEN = new Color("#32CD32");

  /** The color Linen. */
  public static final Color LINEN = new Color("#FAF0E6");

  /** The color Magenta. */
  public static final Color MAGENTA = new Color("#FF00FF");

  /** The color Maroon. */
  public static final Color MAROON = new Color("#800000");

  /** The color Medium Aquamarine. */
  public static final Color MEDIUM_AQUAMARINE = new Color("#66CDAA");

  /** The color Medium Blue. */
  public static final Color MEDIUM_BLUE = new Color("#0000CD");

  /** The color Medium Orchid. */
  public static final Color MEDIUM_ORCHID = new Color("#BA55D3");

  /** The color Medium Purple. */
  public static final Color MEDIUM_PURPLE = new Color("#9370DB");

  /** The color Medium Sea Green. */
  public static final Color MEDIUM_SEA_GREEN = new Color("#3CB371");

  /** The color Medium Slate Blue. */
  public static final Color MEDIUM_SLATE_BLUE = new Color("#7B68EE");

  /** The color Medium Spring Green. */
  public static final Color MEDIUM_SPRING_GREEN = new Color("#00FA9A");

  /** The color Medium Turquoise. */
  public static final Color MEDIUM_TURQUOISE = new Color("#48D1CC");

  /** The color Medium Violet Red. */
  public static final Color MEDIUM_VIOLET_RED = new Color("#C71585");

  /** The color Midnight Blue. */
  public static final Color MIDNIGHT_BLUE = new Color("#191970");

  /** The color Mint Cream. */
  public static final Color MINT_CREAM = new Color("#F5FFFA");

  /** The color Misty Rose. */
  public static final Color MISTY_ROSE = new Color("#FFE4E1");

  /** The color Moccasin. */
  public static final Color MOCCASIN = new Color("#FFE4B5");

  /** The color Navajo White. */
  public static final Color NAVAJO_WHITE = new Color("#FFDEAD");

  /** The color Navy. */
  public static final Color NAVY = new Color("#000080");

  /** The color Old Lace. */
  public static final Color OLD_LACE = new Color("#FDF5E6");

  /** The color Olive. */
  public static final Color OLIVE = new Color("#808000");

  /** The color Olive Drab. */
  public static final Color OLIVE_DRAB = new Color("#6B8E23");

  /** The color Orange. */
  public static final Color ORANGE = new Color("#FFA500");

  /** The color Orange Red. */
  public static final Color ORANGE_RED = new Color("#FF4500");

  /** The color Orchid. */
  public static final Color ORCHID = new Color("#DA70D6");

  /** The color Pale Goldenrod. */
  public static final Color PALE_GOLDENROD = new Color("#EEE8AA");

  /** The color Pale Green. */
  public static final Color PALE_GREEN = new Color("#98FB98");

  /** The color Pale Turquoise. */
  public static final Color PALE_TURQUOISE = new Color("#AFEEEE");

  /** The color Pale Violet Red. */
  public static final Color PALE_VIOLET_RED = new Color("#DB7093");

  /** The color Papaya Whip. */
  public static final Color PAPAYA_WHIP = new Color("#FFEFD5");

  /** The color Peach Puff. */
  public static final Color PEACH_PUFF = new Color("#FFDAB9");

  /** The color Peru. */
  public static final Color PERU = new Color("#CD853F");

  /** The color Pink. */
  public static final Color PINK = new Color("#FFC0CB");

  /** The color Plum. */
  public static final Color PLUM = new Color("#DDA0DD");

  /** The color Powder Blue. */
  public static final Color POWDER_BLUE = new Color("#B0E0E6");

  /** The color Purple. */
  public static final Color PURPLE = new Color("#800080");

  /** The color Red. */
  public static final Color RED = new Color("#FF0000");

  /** The color Rosy Brown. */
  public static final Color ROSY_BROWN = new Color("#BC8F8F");

  /** The color Royal Blue. */
  public static final Color ROYAL_BLUE = new Color("#4169E1");

  /** The color Saddle Brown. */
  public static final Color SADDLE_BROWN = new Color("#8B4513");

  /** The color Salmon. */
  public static final Color SALMON = new Color("#FA8072");

  /** The color Sandy Brown. */
  public static final Color SANDY_BROWN = new Color("#F4A460");

  /** The color Sea Green. */
  public static final Color SEA_GREEN = new Color("#2E8B57");

  /** The color Seashell. */
  public static final Color SEASHELL = new Color("#FFF5EE");

  /** The color Sienna. */
  public static final Color SIENNA = new Color("#A0522D");

  /** The color Silver. */
  public static final Color SILVER = new Color("#C0C0C0");

  /** The color Sky Blue. */
  public static final Color SKY_BLUE = new Color("#87CEEB");

  /** The color Slate Blue. */
  public static final Color SLATE_BLUE = new Color("#6A5ACD");

  /** The color Slate Gray. */
  public static final Color SLATE_GRAY = new Color("#708090");

  /** The color Snow. */
  public static final Color SNOW = new Color("#FFFAFA");

  /** The color Spring Green. */
  public static final Color SPRING_GREEN = new Color("#00FF7F");

  /** The color Steel Blue. */
  public static final Color STEEL_BLUE = new Color("#4682B4");

  /** The color Tan. */
  public static final Color TAN = new Color("#D2B48C");

  /** The color Teal. */
  public static final Color TEAL = new Color("#008080");

  /** The color Thistle. */
  public static final Color THISTLE = new Color("#D8BFD8");

  /** The color Tomato. */
  public static final Color TOMATO = new Color("#FF6347");

  /** The color Turquoise. */
  public static final Color TURQUOISE = new Color("#40E0D0");

  /** The color Violet. */
  public static final Color VIOLET = new Color("#EE82EE");

  /** The color Wheat. */
  public static final Color WHEAT = new Color("#F5DEB3");

  /** The color White. */
  public static final Color WHITE = new Color("#FFFFFF");

  /** The color White Smoke. */
  public static final Color WHITE_SMOKE = new Color("#F5F5F5");

  /** The color Yellow. */
  public static final Color YELLOW = new Color("#FFFF00");

  /** The color Yellow Green. */
  public static final Color YELLOW_GREEN = new Color("#9ACD32");

  private static final Random RANDOM = new Random();

  /** The predefined HTML colors. */
  private static final Color[] COLORS =
      new Color[] {
        ALICE_BLUE,
        ANTIQUE_WHITE,
        AQUA,
        AQUAMARINE,
        AZURE,
        BEIGE,
        BISQUE,
        BLACK,
        BLANCHED_ALMOND,
        BLUE,
        BLUE_VIOLET,
        BROWN,
        BURLYWOOD,
        CADET_BLUE,
        CHARTREUSE,
        CHOCOLATE,
        CORAL,
        CORNFLOWER_BLUE,
        CORNSILK,
        CRIMSON,
        CYAN,
        DARK_BLUE,
        DARK_CYAN,
        DARK_GOLDENROD,
        DARK_GRAY,
        DARK_GREEN,
        DARK_KHAKI,
        DARK_MAGENTA,
        DARK_OLIVE_GREEN,
        DARK_ORANGE,
        DARK_ORCHID,
        DARK_RED,
        DARK_SALMON,
        DARK_SEA_GREEN,
        DARK_SLATE_BLUE,
        DARK_SLATE_GRAY,
        DARK_TURQUOISE,
        DARK_VIOLET,
        DEEP_PINK,
        DEEP_SKY_BLUE,
        DIM_GRAY,
        DODGER_BLUE,
        FIREBRICK,
        FLORAL_WHITE,
        FOREST_GREEN,
        FUCHSIA,
        GAINSBORO,
        GHOST_WHITE,
        GOLD,
        GOLDENROD,
        GRAY,
        GREEN,
        GREEN_YELLOW,
        HONEYDEW,
        HOT_PINK,
        INDIAN_RED,
        INDIGO,
        IVORY,
        KHAKI,
        LAVENDER,
        LAVENDER_BLUSH,
        LAWN_GREEN,
        LEMON_CHIFFON,
        LIGHT_BLUE,
        LIGHT_CORAL,
        LIGHT_CYAN,
        LIGHT_GOLDENROD_YELLOW,
        LIGHT_GRAY,
        LIGHT_GREEN,
        LIGHT_PINK,
        LIGHT_SALMON,
        LIGHT_SEA_GREEN,
        LIGHT_SKY_BLUE,
        LIGHT_SLATE_GRAY,
        LIGHT_STEEL_BLUE,
        LIGHT_YELLOW,
        LIME,
        LIME_GREEN,
        LINEN,
        MAGENTA,
        MAROON,
        MEDIUM_AQUAMARINE,
        MEDIUM_BLUE,
        MEDIUM_ORCHID,
        MEDIUM_PURPLE,
        MEDIUM_SEA_GREEN,
        MEDIUM_SLATE_BLUE,
        MEDIUM_SPRING_GREEN,
        MEDIUM_TURQUOISE,
        MEDIUM_VIOLET_RED,
        MIDNIGHT_BLUE,
        MINT_CREAM,
        MISTY_ROSE,
        MOCCASIN,
        NAVAJO_WHITE,
        NAVY,
        OLD_LACE,
        OLIVE,
        OLIVE_DRAB,
        ORANGE,
        ORANGE_RED,
        ORCHID,
        PALE_GOLDENROD,
        PALE_GREEN,
        PALE_TURQUOISE,
        PALE_VIOLET_RED,
        PAPAYA_WHIP,
        PEACH_PUFF,
        PERU,
        PINK,
        PLUM,
        POWDER_BLUE,
        PURPLE,
        RED,
        ROSY_BROWN,
        ROYAL_BLUE,
        SADDLE_BROWN,
        SALMON,
        SANDY_BROWN,
        SEA_GREEN,
        SEASHELL,
        SIENNA,
        SILVER,
        SKY_BLUE,
        SLATE_BLUE,
        SLATE_GRAY,
        SNOW,
        SPRING_GREEN,
        STEEL_BLUE,
        TAN,
        TEAL,
        THISTLE,
        TOMATO,
        TURQUOISE,
        VIOLET,
        WHEAT,
        WHITE,
        WHITE_SMOKE,
        YELLOW,
        YELLOW_GREEN
      };

  /**
   * Returns a random HTML color.
   *
   * @return a random Color from the predefined HTML colors
   *
   * @example.files HtmlColorGetRandom.java
   */
  public static final Color getRandom() {
    return COLORS[RANDOM.nextInt(COLORS.length)];
  }
}
