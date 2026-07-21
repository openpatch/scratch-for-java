package org.openpatch.scratch.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Finds names close to what someone typed, so a wrong asset name can be
 * answered with "did you mean ...?" instead of a bare error.
 */
final class Suggestions {

  private Suggestions() {
  }

  /**
   * Ranks candidates by how close they are to the given name: names starting
   * with it first, then names containing it, then names within a small typo
   * distance.
   *
   * @param candidates all known names
   * @param name       the name that could not be found
   * @param limit      how many suggestions to return at most
   * @return the closest matching names, best first
   */
  static List<String> closestTo(List<String> candidates, String name, int limit) {
    List<String> suggestions = new ArrayList<>();
    if (name == null || name.isEmpty()) {
      return suggestions;
    }

    String needle = normalise(stripExtension(name.substring(name.lastIndexOf('/') + 1)));
    List<String> prefixed = new ArrayList<>();
    List<String> contained = new ArrayList<>();
    List<String> similar = new ArrayList<>();

    for (String candidate : candidates) {
      String lower = normalise(candidate);
      if (lower.equals(needle)) {
        continue;
      }
      if (lower.startsWith(needle)) {
        prefixed.add(candidate);
      } else if (lower.contains(needle) || needle.contains(lower)) {
        contained.add(candidate);
      } else if (distance(lower, needle) <= 2) {
        similar.add(candidate);
      }
    }

    suggestions.addAll(prefixed);
    suggestions.addAll(contained);
    suggestions.addAll(similar);
    return suggestions.size() > limit ? suggestions.subList(0, limit) : suggestions;
  }

  static String normalise(String name) {
    return name.toLowerCase(Locale.ROOT);
  }

  static String stripExtension(String name) {
    int dot = name.lastIndexOf('.');
    return dot > 0 ? name.substring(0, dot) : name;
  }

  /** Levenshtein distance, used only to catch typos in a name. */
  private static int distance(String a, String b) {
    int[] previous = new int[b.length() + 1];
    int[] current = new int[b.length() + 1];
    for (int j = 0; j <= b.length(); j++) {
      previous[j] = j;
    }
    for (int i = 1; i <= a.length(); i++) {
      current[0] = i;
      for (int j = 1; j <= b.length(); j++) {
        int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
        current[j] = Math.min(Math.min(current[j - 1] + 1, previous[j] + 1), previous[j - 1] + cost);
      }
      int[] swap = previous;
      previous = current;
      current = swap;
    }
    return previous[b.length()];
  }
}
