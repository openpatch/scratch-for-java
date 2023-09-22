import java.text.MessageFormat;
import java.util.HashMap;

public class I18n {
  private static String[] allowedLocales = {"en", "de"};
  private static String locale = allowedLocales[0];
  private static HashMap<String, HashMap<String, String>> messages;

  public static void setup() {
    messages = new HashMap<>();

    for (var allowedLocale : allowedLocales) {
      messages.put(allowedLocale, new HashMap<>());
    }

    // see MessageFormat on how to write patterns:
    // https://docs.oracle.com/javase/7/docs/api/java/text/MessageFormat.html
    var en = messages.get("en");
    en.put("item-found", "You have found an item: {0}!");
    en.put("scroll-water", "Scroll of Water");
    en.put("scroll-fire", "Scroll of Fire");
    en.put("tutorial-item-pickup", "Press E to pick up an item!");
    en.put("tutorial-scroll-fire", "Press G to throw a fire ball!");
    en.put("need-scroll-water", "I probably need the water scroll. Maybe it is here somewhere!");

    var de = messages.get("de");
    de.put("item-found", "Du hast einen Gegenstand gefunden: {0}!");
    de.put("scroll-water", "Schriftrolle des Wassers");
    de.put("scroll-fire", "Schriftrolle des Feuers");
    de.put("tutorial-scroll-fire", "Drücke G, um einen Feuerball zu werfen!");
    de.put("tutorial-item-pickup", "Drücke E, um das Item aufzuheben!");
    de.put(
        "need-scroll-water",
        "Ich brauche Wahrscheinlich die Schriftrolle des Wassers. Vielleicht ist sie hier"
            + " irgendwo?");
  }

  public static void select(String locale) {
    for (var allowedLocale : allowedLocales) {
      if (allowedLocale.equals(locale)) {
        I18n.locale = locale;
        return;
      }
    }
  }

  public static String get(String key, String... args) {
    var message = messages.get(locale).getOrDefault(key, "NOT_FOUND");
    return MessageFormat.format(message, args);
  }
}
