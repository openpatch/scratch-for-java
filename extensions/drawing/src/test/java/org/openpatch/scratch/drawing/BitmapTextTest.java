package org.openpatch.scratch.drawing;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BitmapTextTest {
  @TempDir Path directory;

  private BitmapText text() throws Exception {
    ImageIO.write(new BufferedImage(4, 7, BufferedImage.TYPE_INT_ARGB), "png",
        directory.resolve("font065.png").toFile());
    ImageIO.write(new BufferedImage(6, 9, BufferedImage.TYPE_INT_ARGB), "png",
        directory.resolve("font066.png").toFile());
    return new BitmapText(directory.resolve("font%03d.png").toString());
  }

  @Test
  void measuresVariableGlyphsSpacesLinesAndSpriteScale() throws Exception {
    BitmapText text = text();
    assertEquals(0, text.getWidth());
    assertEquals(0, text.getHeight());
    text.setSpaceWidth(3);
    text.setLetterSpacing(1);
    text.setLineSpacing(2);
    text.showText("A B\nBA");
    assertEquals(15, text.getWidth());
    assertEquals(20, text.getHeight());
    text.setSize(150);
    assertEquals(23, text.getWidth());
    assertEquals(30, text.getHeight());
    assertEquals("A B\nBA", text.getContent());
  }

  @Test
  void overridesCanUseBuiltinsAndSurviveFontChanges() throws Exception {
    BitmapText text = text();
    text.addGlyph('!', directory.resolve("font066.png").toString());
    text.setFont(directory.resolve("unavailable%03d.png").toString());
    text.showText("!!");
    text.setLetterSpacing(-2);
    assertEquals(10, text.getWidth());
    assertEquals(9, text.getHeight());
    text.showText(" ");
    text.setSpaceWidth(5);
    assertEquals(5, text.getWidth());
  }

  @Test
  void rejectsBadPatternsAndSpacingBeforeRendering() throws Exception {
    BitmapText text = text();
    for (String pattern : new String[] {"font.png", "font%%d.png", "%d-%d.png", "%d-%s.png"}) {
      assertThrows(IllegalArgumentException.class, () -> text.setFont(pattern), pattern);
    }
    assertThrows(IllegalArgumentException.class, () -> text.setSpaceWidth(-1));
    assertThrows(IllegalArgumentException.class, () -> text.setLetterSpacing(Double.NaN));
    assertThrows(IllegalArgumentException.class, () -> text.setLineSpacing(-1));
    assertThrows(NullPointerException.class, () -> text.showText(null));
  }
}
