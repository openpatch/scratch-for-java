package org.openpatch.scratch.internal.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openpatch.scratch.internal.BuiltinAssets;
import org.openpatch.scratch.internal.audio.SoundData;

/** Built-in sprites and sounds by name, without Jackson. */
class BuiltinAssetsTest {

  @Test
  void theAtlasesAreRead() {
    assertTrue(BuiltinAssets.getNames().size() > 300, "names: " + BuiltinAssets.getNames().size());
    BuiltinAssets.Entry slime = BuiltinAssets.get("slimeGreen");
    assertNotNull(slime);
    assertEquals(slime, BuiltinAssets.get(slime.sheet() + "/slimeGreen"));
  }

  @Test
  void aBuiltinNameLoadsItsRegionOfTheSheet() {
    BuiltinAssets.Entry e = BuiltinAssets.get("slimeGreen");
    Image image = Images.load("slimeGreen");
    boolean turned = ((e.direction() % 180) + 180) % 180 == 0;
    assertEquals(turned ? e.height() : e.width(), image.getWidth());
  }

  @Test
  void aMistypedNameGetsSuggestions() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> Images.load("slimeGren"));
    assertTrue(ex.getMessage().contains("Did you mean"), ex.getMessage());
  }

  @Test
  void turningToFaceRight() {
    Image up = new Image(1, 2, new int[] {1, 2}); // drawn facing up: top pixel 1
    Image right = Images.turnToFaceRight(up, 0);
    assertEquals(2, right.getWidth());
    assertEquals(1, right.get(1, 0), "the top edge becomes the right edge");
  }

  @Test
  void builtinSoundsLoadByName() {
    String name = org.openpatch.scratch.internal.BuiltinSounds.getNames().get(0);
    SoundData data = SoundData.load(name);
    assertTrue(data.seconds() > 0, name);
  }
}
