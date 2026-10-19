package org.openpatch.scratch.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
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

  @Test
  void findsAFolderInsideAJar(@TempDir Path dir) throws Exception {
    // a project that was packaged and shared keeps its assets in the jar
    var jar = dir.resolve("game.jar");
    try (var out = new JarOutputStream(Files.newOutputStream(jar))) {
      out.putNextEntry(new JarEntry("jarAssets/"));
      out.closeEntry();
      out.putNextEntry(new JarEntry("jarAssets/sprites/"));
      out.closeEntry();
      out.putNextEntry(new JarEntry("jarAssets/sprites/cat.png"));
      out.closeEntry();
    }

    var thread = Thread.currentThread();
    var before = thread.getContextClassLoader();
    try (var loader = new URLClassLoader(new URL[] { jar.toUri().toURL() }, before)) {
      thread.setContextClassLoader(loader);
      var root = Applet.findAssetsFolder("jarAssets");
      assertNotNull(root);
      assertEquals(List.of("jarAssets/sprites/cat.png"), Applet.listAssets("jarAssets", root));
    } finally {
      thread.setContextClassLoader(before);
    }
  }

  @Test
  void knowsTheFilesOfTheBuiltins() {
    assertTrue(BuiltinAssets.isSheet("images/platformer.png"));
    assertFalse(BuiltinAssets.isSheet("assets/platformer.png"));
    assertTrue(BuiltinSounds.isResource(BuiltinSounds.get(BuiltinSounds.getNames().get(0))));
    assertFalse(BuiltinSounds.isResource("assets/meow.wav"));
  }

  @Test
  void findsAFolderInsideAJarWithoutEntriesForFolders(@TempDir Path dir) throws Exception {
    // not every tool writes an entry for each folder, the runnable jar of Studio does not
    var jar = dir.resolve("game.jar");
    try (var out = new JarOutputStream(Files.newOutputStream(jar))) {
      out.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
      out.write("Manifest-Version: 1.0\r\n".getBytes());
      out.closeEntry();
      out.putNextEntry(new JarEntry("flatAssets/sprites/cat.png"));
      out.closeEntry();
    }

    var thread = Thread.currentThread();
    var before = thread.getContextClassLoader();
    try (var loader = new URLClassLoader(new URL[] { jar.toUri().toURL() }, before)) {
      thread.setContextClassLoader(loader);
      var root = Applet.findAssetsFolder("flatAssets");
      assertNotNull(root);
      assertEquals(List.of("flatAssets/sprites/cat.png"), Applet.listAssets("flatAssets", root));
    } finally {
      thread.setContextClassLoader(before);
    }
  }
}
