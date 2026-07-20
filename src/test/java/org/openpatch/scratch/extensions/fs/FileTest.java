package org.openpatch.scratch.extensions.fs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileTest {

  public static class SaveData {
    public String name;
    public int score;

    public SaveData() {
    }

    public SaveData(String name, int score) {
      this.name = name;
      this.score = score;
    }
  }

  @TempDir
  Path tempDir;

  @Test
  void saveAndLoadRoundTripAsJson() {
    String path = tempDir.resolve("save.json").toString();
    File.save(path, new SaveData("Alice", 42));

    SaveData loaded = File.load(path, SaveData.class);

    assertEquals("Alice", loaded.name);
    assertEquals(42, loaded.score);
  }

  @Test
  void saveXmlAndLoadXmlRoundTrip() {
    String path = tempDir.resolve("save.xml").toString();
    File.saveXML(path, new SaveData("Bob", 7));

    SaveData loaded = File.loadXML(path, SaveData.class);

    assertEquals("Bob", loaded.name);
    assertEquals(7, loaded.score);
  }

  @Test
  void saveCreatesMissingParentDirectories() {
    String path = tempDir.resolve("nested/dir/save.json").toString();
    File.save(path, new SaveData("Carol", 1));

    assertTrue(java.nio.file.Files.exists(Path.of(path)));
  }

  @Test
  void loadReturnsNullWhenFileDoesNotExist() {
    String path = tempDir.resolve("missing.json").toString();

    SaveData loaded = File.load(path, SaveData.class);

    assertNull(loaded);
  }

  @Test
  void loadOrCreateCreatesADefaultFileWhenMissing() {
    String path = tempDir.resolve("created.json").toString();

    SaveData loaded = File.loadOrCreate(path, SaveData.class);

    assertNull(loaded.name);
    assertEquals(0, loaded.score);
    assertTrue(java.nio.file.Files.exists(Path.of(path)));
  }

  @Test
  void loadOrCreateLoadsExistingFileInsteadOfOverwritingIt() {
    String path = tempDir.resolve("existing.json").toString();
    File.save(path, new SaveData("Dana", 99));

    SaveData loaded = File.loadOrCreate(path, SaveData.class);

    assertEquals("Dana", loaded.name);
    assertEquals(99, loaded.score);
  }
}
