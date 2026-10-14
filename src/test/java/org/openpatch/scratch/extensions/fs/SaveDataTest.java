package org.openpatch.scratch.extensions.fs;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SaveDataTest {
  public static class Progress {
    public int version = 1;
    public int coins = 0;
    public String name = "Hedgehog 🦔";
    public int[] collected = {1, 2};
    public Progress child;
    public transient int temporary = 42;
    private int secret = 7;
    public int getSecret() { return secret; }
  }

  @TempDir Path root;
  private String previousRoot;

  @BeforeEach void overrideRoot() {
    previousRoot = System.getProperty("scratch4j.saveDataRoot");
    System.setProperty("scratch4j.saveDataRoot", root.toString());
  }

  @AfterEach void restoreRoot() {
    if (previousRoot == null) System.clearProperty("scratch4j.saveDataRoot");
    else System.setProperty("scratch4j.saveDataRoot", previousRoot);
  }

  private SaveData<Progress> slot(String name) {
    return new SaveData<>("org.example.game", name, Progress.class);
  }

  @Test void missingSlotUsesDefaultsWithoutWriting() throws IOException {
    SaveData<Progress> save = slot("slot1");
    assertEquals(1, save.loadOrDefault(Progress::new).version);
    assertFalse(Files.exists(save.path()));
  }

  @Test void threeSlotsSurviveReopeningAndRemainIndependent() throws IOException {
    for (int index = 1; index <= 3; index++) {
      Progress progress = new Progress();
      progress.coins = index * 10;
      progress.child = new Progress();
      progress.child.coins = index;
      slot("slot" + index).save(progress);
    }
    for (int index = 1; index <= 3; index++) {
      Progress loaded = slot("slot" + index).loadOrDefault(() -> {
        throw new AssertionError("Existing saves must not invoke defaults");
      });
      assertEquals(index * 10, loaded.coins);
      assertEquals(index, loaded.child.coins);
      assertEquals("Hedgehog 🦔", loaded.name);
      assertArrayEquals(new int[] {1, 2}, loaded.collected);
    }
    SaveData<Progress> other = new SaveData<>("org.example.other", "slot1", Progress.class);
    assertEquals(0, other.loadOrDefault(Progress::new).coins);
  }

  @Test void jsonUsesOnlyPublicNonTransientFieldsAndPreservesDefaults() throws IOException {
    SaveData<Progress> save = slot("slot1");
    save.save(new Progress());
    String json = Files.readString(save.path());
    assertFalse(json.contains("temporary"));
    assertFalse(json.contains("secret"));
    Files.writeString(save.path(), "{\"coins\":7}");
    Progress loaded = save.loadOrDefault(Progress::new);
    assertEquals(7, loaded.coins);
    assertEquals(1, loaded.version);
    assertEquals("Hedgehog 🦔", loaded.name);
  }

  @Test void corruptOrIncompatibleSavesAreReportedAndPreserved() throws IOException {
    SaveData<Progress> save = slot("slot1");
    save.save(new Progress());
    for (String invalid : new String[] {"{broken", "", "null", "{} {}", "[]",
        "{\"futureField\":1}", "{\"coins\":\"five\"}", "{\"coins\":1.5}", "{\"coins\":null}"}) {
      Files.writeString(save.path(), invalid);
      AtomicInteger defaults = new AtomicInteger();
      assertThrows(IOException.class, () -> save.loadOrDefault(() -> {
        defaults.incrementAndGet();
        return new Progress();
      }), invalid);
      assertEquals(0, defaults.get());
      assertEquals(invalid, Files.readString(save.path()));
    }
  }

  @Test void replacementAndSerializationFailurePreserveCommittedData() throws IOException {
    SaveData<Progress> save = slot("slot1");
    Progress progress = new Progress();
    save.save(progress);
    progress.coins = 12;
    save.save(progress);
    assertEquals(12, save.loadOrDefault(Progress::new).coins);
    String committed = Files.readString(save.path());
    progress.child = progress;
    assertThrows(IOException.class, () -> save.save(progress));
    assertEquals(committed, Files.readString(save.path()));
  }

  @Test void failedWriteReportsErrorAndCleansTemporaryFiles() throws IOException {
    SaveData<Progress> save = slot("slot1");
    Files.createDirectories(save.path());
    assertThrows(IOException.class, () -> save.save(new Progress()));
    assertTrue(Files.isDirectory(save.path()));
    try (var files = Files.list(save.path().getParent())) {
      assertEquals(1, files.count());
    }
  }

  @Test void identifiersCannotEscapeTheGameDirectory() {
    for (String invalid : new String[] {"../game", "a/b", "a\\b", "..", "CON", "aux.json", "game."}) {
      assertThrows(IllegalArgumentException.class, () -> new SaveData<>(invalid, "slot1", Progress.class));
      assertThrows(IllegalArgumentException.class, () -> new SaveData<>("org.example.game", invalid, Progress.class));
    }
  }

  @Test void platformFoldersRespectOverridesAndFallbacks() {
    assertEquals(root, SaveData.platformDirectory("Windows 11", root, root.toString(), null));
    assertEquals(root.resolve("AppData/Local"), SaveData.platformDirectory("Windows 11", root, null, null));
    assertEquals(root.resolve("Library/Application Support"), SaveData.platformDirectory("Mac OS X", root, null, null));
    assertEquals(root.resolve("Library/Application Support"), SaveData.platformDirectory("Darwin", root, null, null));
    assertEquals(root, SaveData.platformDirectory("Linux", root, null, root.toString()));
    assertEquals(root.resolve(".local/share"), SaveData.platformDirectory("Linux", root, null, "relative"));
    assertEquals(slot("slot1").path().toString(), slot("slot1").getLocation());
  }
}
