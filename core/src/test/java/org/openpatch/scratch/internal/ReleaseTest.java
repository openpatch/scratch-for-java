package org.openpatch.scratch.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ReleaseTest {

  @Test
  void boxTextHasNothingTinyfdOrZenityRefuses() {
    String text = Release.boxText("Error in Outer$Inner.run: Cannot invoke \"String.length()\" because \"<local1>\" "
        + "is null; it's `x` & y");
    for (char refused : new char[] {'"', '\'', '`', '<', '>', '&', '$'}) {
      assertFalse(text.indexOf(refused) >= 0, "contains " + refused + ": " + text);
    }
  }

  @Test
  void boxTextIsWrappedAtSpacesAndSlashes() {
    assertEquals("one two\nthree", Release.wrap("one two three", 8));
    assertEquals("/home/ada/\n.local/\nlatest.log", Release.wrap("/home/ada/.local/latest.log", 11));
    assertEquals("abcdef\nghij", Release.wrap("abcdefghij", 6));
    assertEquals("a\n\nb", Release.wrap("a\n\nb", 6));
  }

  @Test
  void logsLiveWhereSavesLive() {
    Path home = Path.of("/home/ada");
    assertEquals(Path.of("/data"), Release.dataFolder("Linux", home, null, "/data"));
    assertEquals(home.resolve(".local/share"), Release.dataFolder("Linux", home, null, "relative"));
    assertEquals(home.resolve("Library/Application Support"), Release.dataFolder("Mac OS X", home, null, null));
    assertEquals(Path.of("/C/Users/ada/AppData/Local"),
        Release.dataFolder("Windows 11", home, "/C/Users/ada/AppData/Local", null));
  }

  @Test
  void aGameNameBecomesAFolderName() {
    assertEquals("My_Game", Release.safe("My Game"));
    assertEquals("__x", Release.safe("../x")); // no way out of the logs folder
    assertEquals("game", Release.safe("   "));
  }
}
