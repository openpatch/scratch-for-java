package docs;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Keeps the interactive examples on the pages under {@code docs/book/examples}
 * the same as the demos they are generated from.
 *
 * <p>
 * A page is regenerated only when someone runs
 * {@code ./scripts/update-demo-pages.sh}, and a changed demo gives no sign that
 * it has to be. Before the pages were generated at all, they were copies kept up
 * by hand, and by the time they were compared again eight of them no longer
 * matched their demo. This test is what notices now.
 */
class DemoPagesTest {

  @Test
  void everyDemoPageShowsItsDemo() throws IOException {
    if (!Files.isDirectory(GenerateDemoPages.PAGES) || !Files.isDirectory(GenerateDemoPages.DEMOS)) {
      return; // the docs and the examples are not part of every checkout
    }

    var stale = new ArrayList<String>();
    var checked = 0;

    for (Path page : GenerateDemoPages.pages()) {
      String text = Files.readString(page);
      if (GenerateDemoPages.demosOn(text).isEmpty()) {
        continue;
      }
      checked++;
      String generated = GenerateDemoPages.render(page, text);
      if (!generated.equals(text)) {
        stale.add(page + "\n" + firstDifference(text, generated));
      }
    }

    assertTrue(checked > 0, "no page under " + GenerateDemoPages.PAGES + " names a demo");
    assertTrue(stale.isEmpty(),
        "these pages no longer show their demo; run ./scripts/update-demo-pages.sh "
            + "and commit what it changes:\n\n" + String.join("\n\n", stale));
  }

  /** The first line where the page and its demo part, so the failure says where. */
  private static String firstDifference(String page, String demo) {
    String[] a = page.split("\n", -1);
    String[] b = demo.split("\n", -1);
    for (int i = 0; i < Math.max(a.length, b.length); i++) {
      String onPage = i < a.length ? a[i] : "(end of page)";
      String inDemo = i < b.length ? b[i] : "(end of demo)";
      if (!onPage.equals(inDemo)) {
        return "  line " + (i + 1) + "\n    page: " + onPage + "\n    demo: " + inDemo;
      }
    }
    return "";
  }
}
