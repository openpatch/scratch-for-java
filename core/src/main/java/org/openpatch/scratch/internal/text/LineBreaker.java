package org.openpatch.scratch.internal.text;

import java.util.ArrayList;
import java.util.List;

/**
 * Where lines break: {@code \n} always; otherwise at spaces and after
 * hyphens, greedily; a word wider than the line breaks between letters, never so that a hyphen
 * starts a line. Spaces at a break belong to neither line. Small and separate so that the online
 * IDE can port it line by line.
 */
public final class LineBreaker {

  /** The width of codepoints {@code [from, to)} as one run on a line, kerning included. */
  @FunctionalInterface
  public interface Measure {
    double width(int[] codepoints, int from, int to);
  }

  private LineBreaker() {}

  /**
   * The lines of a text as codepoint arrays.
   *
   * @param maxWidth the widest a line may be; 0, negative or infinite means no wrapping
   */
  public static List<int[]> breakLines(String text, double maxWidth, Measure measure) {
    boolean wrap = maxWidth > 0 && !Double.isInfinite(maxWidth);
    List<int[]> lines = new ArrayList<>();
    for (String paragraph : text.split("\n", -1)) {
      int[] cps = paragraph.codePoints().toArray();
      if (!wrap) {
        lines.add(cps);
        continue;
      }
      int start = 0;
      while (true) {
        int end = start;
        while (end < cps.length && measure.width(cps, start, end + 1) <= maxWidth) {
          end++;
        }
        if (end == cps.length) {
          lines.add(slice(cps, start, end));
          break;
        }
        // cps[start, end) fits, cps[end] does not: find a break at a space or after a hyphen
        int at = -1;
        for (int p = end; p > start; p--) {
          if (cps[p] == ' ' || cps[p - 1] == '-') {
            at = p;
            break;
          }
        }
        int next;
        if (at > start) {
          lines.add(slice(cps, start, trimEnd(cps, start, at)));
          next = at;
        } else {
          // a word wider than the line: between letters, at least one per line
          int cut = Math.max(end, start + 1);
          if (cut < cps.length && cps[cut] == '-' && cut - 1 > start) {
            cut--; // the hyphen goes with the letter before it, not to the start of a line
          }
          lines.add(slice(cps, start, cut));
          next = cut;
        }
        while (next < cps.length && cps[next] == ' ') {
          next++;
        }
        if (next == cps.length) {
          break;
        }
        start = next;
      }
    }
    return lines;
  }

  private static int trimEnd(int[] cps, int start, int end) {
    while (end > start && cps[end - 1] == ' ') {
      end--;
    }
    return end;
  }

  private static int[] slice(int[] cps, int from, int to) {
    int[] out = new int[to - from];
    System.arraycopy(cps, from, out, 0, out.length);
    return out;
  }
}
