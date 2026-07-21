package org.openpatch.scratch.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

class BuiltinSoundsTest {

  @Test
  void findsEveryBundledSound() {
    assertEquals(266, BuiltinSounds.getNames().size());
  }

  @Test
  void everyNameIsUniqueSoNoFolderIsNeeded() {
    List<String> names = BuiltinSounds.getNames();

    assertEquals(names.size(), new HashSet<>(names).size());
  }

  @Test
  void resolvesToAResourcePathThatExists() {
    String path = BuiltinSounds.get("footstep_carpet_000");

    assertNotNull(path);
    assertTrue(path.startsWith("sounds/"), "unexpected path " + path);
    assertTrue(Files.exists(Path.of(Applet.getPath(path))), "resource missing at " + path);
  }

  @Test
  void resolvesSoundsInNestedFoldersWithSpaces() {
    // The jingles live in folders such as "sounds/jingles/8-Bit jingles".
    String nested = BuiltinSounds.getNames().stream()
        .map(BuiltinSounds::get)
        .filter(p -> p.contains("jingles/"))
        .findFirst()
        .orElse(null);

    assertNotNull(nested, "expected at least one jingle");
    assertTrue(Files.exists(Path.of(Applet.getPath(nested))), "resource missing at " + nested);
  }

  @Test
  void lookupIgnoresCase() {
    assertNotNull(BuiltinSounds.get("FOOTSTEP_CARPET_000"));
  }

  @Test
  void unknownNamesAreNotFound() {
    assertNull(BuiltinSounds.get("definitely_not_a_sound"));
    assertFalse(BuiltinSounds.contains("definitely_not_a_sound"));
  }

  @Test
  void suggestsCloseNamesForATypo() {
    List<String> suggestions = BuiltinSounds.suggest("footstep_carpet_00", 6);

    assertTrue(suggestions.contains("footstep_carpet_000"), "got " + suggestions);
  }
}
