package org.openpatch.scratch.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AssetsFolderTest {

  @Test
  void findsAFolderOnTheClasspath() {
    // src/examples/java is a resource folder, so the demos' assets are on the classpath
    assertNotNull(Applet.findAssetsFolder("demos/smartRocket/assets"));
  }

  @Test
  void findsAFolderOnDisk(@TempDir Path dir) {
    assertEquals(dir, Applet.findAssetsFolder(dir.toString()));
  }

  @Test
  void findsNoFolderThatIsNotThere() {
    assertNull(Applet.findAssetsFolder("there/is/no/such/folder"));
  }

  @Test
  void findsNoFolderWhereThereIsAFile(@TempDir Path dir) throws Exception {
    var file = Files.createFile(dir.resolve("assets"));
    assertNull(Applet.findAssetsFolder(file.toString()));
  }

  @Test
  void listsFilesByThePathsASpriteUses(@TempDir Path dir) throws Exception {
    Files.createDirectories(dir.resolve("sprites"));
    Files.createFile(dir.resolve("sprites/cat.png"));
    Files.createFile(dir.resolve("meow.wav"));

    // however the folder was found, the files are listed under the folder as it was written
    assertEquals(
        List.of("assets/meow.wav", "assets/sprites/cat.png"),
        Applet.listAssets("assets", dir));
    assertEquals(
        List.of("assets/meow.wav", "assets/sprites/cat.png"),
        Applet.listAssets("assets/", dir));
  }
}
