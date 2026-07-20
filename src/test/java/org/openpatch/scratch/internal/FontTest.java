package org.openpatch.scratch.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FontTest {

  @Test
  void constructingAFontDoesNotRequireALiveApplet() {
    // Regression guard: Font used to eagerly rasterize the font file (via
    // Applet.getInstance()) in its constructor, which crashed the whole JVM
    // with System.exit() when there was no running Applet/Window - e.g. for
    // every Sprite, since Sprite's constructor builds a Text, which builds a
    // default Font. Loading is now deferred to the first getFont() call.
    Font font = new Font("default", "UbuntuMono-Regular.ttf");

    assertEquals("default", font.getName());
  }

  @Test
  void copyConstructorDoesNotRequireALiveApplet() {
    Font original = new Font("default", "UbuntuMono-Regular.ttf");

    Font copy = new Font(original);

    assertEquals("default", copy.getName());
  }
}
