package org.openpatch.scratch.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
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
import org.openpatch.scratch.internal.image.Image;
import org.openpatch.scratch.internal.image.Images;

class AssetsTest {

  @Test
  void findsAFolderOnTheClasspath() throws Exception {
    // test-classes/costumes: not a folder relative to the working directory
    Path root = Assets.findFolder("costumes");
    assertNotNull(root);
    assertTrue(Files.isDirectory(root));
  }

  @Test
  void findsAFolderOnDiskAndListsFilesByTheirSpritePaths(@TempDir Path dir) throws Exception {
    Files.createDirectories(dir.resolve("sub"));
    Files.write(dir.resolve("b.png"), new byte[0]);
    Files.write(dir.resolve("sub/a.wav"), new byte[0]);
    String written = dir.toString() + "/";
    assertEquals(dir, Assets.findFolder(dir.toString()));
    assertEquals(List.of(dir + "/b.png", dir + "/sub/a.wav"),
        Assets.list(written, Assets.findFolder(dir.toString())));
  }

  @Test
  void noFolderIsNull() {
    assertNull(Assets.findFolder("no-such-folder-anywhere"));
  }

  @Test
  void decodesIntoTheCacheThenWaitsForTheUploads() throws Exception {
    Assets assets = Assets.start("costumes");
    // decoding finishes on its own; the images then wait for the window thread
    long until = System.nanoTime() + 10_000_000_000L;
    while (assets.progress() < 0.5 && System.nanoTime() < until) {
      Thread.sleep(10);
    }
    Thread.sleep(50);
    assertFalse(assets.isDone(), "uploads outstanding");
    assertEquals(0.5, assets.progress(), 1e-9, "seven images decoded, none uploaded");
    int uploads = 0;
    for (Image image; (image = assets.nextUpload()) != null; uploads++) {
      // what a sprite's addCostume("costumes/red10.png") finds: the very same image
      if (image.getWidth() == 10 && image.getHeight() == 10) {
        assertSame(image, Images.load("costumes/red10.png"));
      }
    }
    assertEquals(7, uploads);
    assertTrue(assets.isDone());
    assertEquals(1, assets.progress(), 1e-9);
  }

  @Test
  void aMissingFolderIsDoneAtOnce() throws Exception {
    Assets assets = Assets.start("no-such-folder-anywhere");
    long until = System.nanoTime() + 5_000_000_000L;
    while (!assets.isDone() && System.nanoTime() < until) {
      Thread.sleep(5);
    }
    assertTrue(assets.isDone());
    assertEquals(1, assets.progress(), 1e-9);
  }

  @Test
  void findsNoFolderWhereThereIsAFile(@TempDir Path dir) throws Exception {
    var file = Files.createFile(dir.resolve("assets"));
    assertNull(Assets.findFolder(file.toString()));
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
      var root = Assets.findFolder("jarAssets");
      assertNotNull(root);
      assertEquals(List.of("jarAssets/sprites/cat.png"), Assets.list("jarAssets", root));
    } finally {
      thread.setContextClassLoader(before);
    }
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
      var root = Assets.findFolder("flatAssets");
      assertNotNull(root);
      assertEquals(List.of("flatAssets/sprites/cat.png"), Assets.list("flatAssets", root));
    } finally {
      thread.setContextClassLoader(before);
    }
  }

  @Test
  void knowsTheFilesOfTheBuiltins() {
    assertTrue(BuiltinAssets.getSheets().contains("platformer"));
    assertTrue(BuiltinAssets.getEntries().stream()
        .anyMatch(e -> e.sheet().equals("platformer")
            && e.sheetPath().equals("images/platformer.png")));
    assertFalse(BuiltinAssets.getEntries().stream()
        .anyMatch(e -> e.sheetPath().equals("assets/platformer.png")));
    assertTrue(BuiltinSounds.isResource(BuiltinSounds.get(BuiltinSounds.getNames().get(0))));
    assertFalse(BuiltinSounds.isResource("assets/meow.wav"));
  }
}
