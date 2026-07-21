package org.openpatch.scratch.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class BuiltinAssetsTest {

  @Test
  void readsEverySubTextureFromEveryBundledAtlas() {
    // The four Kenney atlases hold 845 sub textures, but four of them are listed
    // twice within platformer.xml and three names exist on two sheets, leaving
    // 838 distinct bare names.
    assertEquals(838, BuiltinAssets.getNames().size());
  }

  @Test
  void listsEveryDistinctSprite() {
    // 845 sub textures minus the four listed twice inside platformer.xml.
    assertEquals(841, BuiltinAssets.getEntries().size());
  }

  @Test
  void aQualifiedAndABareNameResolveToTheSameSprite() {
    // Both lookups must hand back one and the same entry, otherwise a sprite
    // listed twice in an atlas would show up twice in the documentation.
    assertSame(BuiltinAssets.get("grass"), BuiltinAssets.get("platformer/grass"));
    assertSame(BuiltinAssets.get("bunny1_jump"), BuiltinAssets.get("jumper/bunny1_jump"));
  }

  @Test
  void everyEntryHasAReferenceNameThatResolvesBackToIt() {
    for (BuiltinAssets.Entry entry : BuiltinAssets.getEntries()) {
      String reference = BuiltinAssets.getReferenceName(entry);
      assertSame(entry, BuiltinAssets.get(reference), "broken reference name " + reference);
    }
  }

  @Test
  void findsASpriteByItsBareName() {
    BuiltinAssets.Entry entry = BuiltinAssets.get("bunny1_jump");

    assertNotNull(entry);
    assertEquals("jumper", entry.sheet);
    assertEquals("images/jumper.png", entry.sheetPath);
    assertEquals(150, entry.width);
    assertEquals(181, entry.height);
  }

  @Test
  void lookupIgnoresCase() {
    assertNotNull(BuiltinAssets.get("BUNNY1_JUMP"));
    assertNotNull(BuiltinAssets.get("Bunny1_Jump"));
  }

  @Test
  void bareNameOnSeveralSheetsResolvesToTheFirstSheet() {
    // "cactus" exists on both platformer and jumper; platformer is listed first.
    assertEquals("platformer", BuiltinAssets.get("cactus").sheet);
    assertEquals("jumper", BuiltinAssets.get("jumper/cactus").sheet);
    assertEquals("platformer", BuiltinAssets.get("platformer/cactus").sheet);
  }

  @Test
  void qualifyingANameWithTheWrongSheetDoesNotResolve() {
    assertTrue(BuiltinAssets.contains("bunny1_jump"));
    assertFalse(BuiltinAssets.contains("platformer/bunny1_jump"));
  }

  @Test
  void unknownNamesAreNotFound() {
    assertFalse(BuiltinAssets.contains("definitely_not_a_sprite"));
    assertFalse(BuiltinAssets.contains("jumper/grassy"));
  }

  @Test
  void namesWithoutAnExtensionAreTreatedAsBuiltins() {
    assertTrue(BuiltinAssets.isBuiltinName("bunny1_jump"));
    assertTrue(BuiltinAssets.isBuiltinName("platformer/grass"));
  }

  @Test
  void pathsAreNotTreatedAsBuiltins() {
    assertFalse(BuiltinAssets.isBuiltinName("sprites/cat.png"));
    assertFalse(BuiltinAssets.isBuiltinName("cat.png"));
    assertFalse(BuiltinAssets.isBuiltinName("~/pictures/cat.png"));
    assertFalse(BuiltinAssets.isBuiltinName("/tmp/cat.png"));
    assertFalse(BuiltinAssets.isBuiltinName(""));
    assertFalse(BuiltinAssets.isBuiltinName(null));
  }

  @Test
  void everySpriteLiesInsideItsSheet() throws Exception {
    java.util.Map<String, int[]> sheetSizes = new java.util.HashMap<>();

    for (String name : BuiltinAssets.getNames()) {
      BuiltinAssets.Entry entry = BuiltinAssets.get(name);
      int[] size = sheetSizes.computeIfAbsent(entry.sheetPath, BuiltinAssetsTest::readImageSize);

      assertTrue(entry.width > 0 && entry.height > 0, name + " has an empty rectangle");
      assertTrue(
          entry.x >= 0 && entry.y >= 0
              && entry.x + entry.width <= size[0]
              && entry.y + entry.height <= size[1],
          name + " lies outside " + entry.sheetPath);
    }
  }

  private static int[] readImageSize(String resourcePath) {
    try (java.io.InputStream in = BuiltinAssets.class.getClassLoader().getResourceAsStream(resourcePath)) {
      assertNotNull(in, "missing bundled sheet " + resourcePath);
      java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(in);
      return new int[] { image.getWidth(), image.getHeight() };
    } catch (java.io.IOException e) {
      throw new IllegalStateException("could not read " + resourcePath, e);
    }
  }

  @Test
  void suggestsCloseNamesForATypo() {
    List<String> suggestions = BuiltinAssets.suggest("bunny1_jmp", 6);

    assertTrue(suggestions.contains("bunny1_jump"), "expected a suggestion, got " + suggestions);
  }

  @Test
  void suggestsNamesSharingAPrefix() {
    List<String> suggestions = BuiltinAssets.suggest("bunny1", 6);

    assertFalse(suggestions.isEmpty());
    assertTrue(suggestions.stream().allMatch(s -> s.toLowerCase().contains("bunny1")));
  }
}
