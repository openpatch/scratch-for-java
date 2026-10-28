package org.openpatch.scratch.web.export;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.tools.ToolProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.web.WebPlatform;

class WebExportTest {

  private static final String GAME = """
      import org.openpatch.scratch.*;

      public class Game extends Stage {
        public Game() {
          this.addBackdrop("bg", "assets/bg.png");
          Sprite cat = new Sprite();
          cat.addCostume("alienGreen_stand");
          this.add(cat);
          this.add(new Text("Hello", 0, 100, 200));
        }

        public void run() {
          if (this.isKeyPressed(KeyCode.SPACE)) {
            this.playSound("handleCoins");
          }
        }

        public static void main(String[] args) {
          new Window(480, 360).setStage(new Game());
        }
      }
      """;

  @Test
  void writesAPageWithTheGameTheLibraryFilesAndTheGamesOwnFiles(@TempDir Path dir) throws IOException {
    Path project = dir.resolve("project");
    Path classes = compile(project, "Game", GAME);
    Files.createDirectories(project.resolve("assets"));
    Files.write(project.resolve("assets/bg.png"), new byte[] {1, 2, 3});
    Files.createDirectories(project.resolve(".scratch4j"));
    Files.writeString(project.resolve(".scratch4j/project.json"), "{}");

    Path index = WebExport.export(new WebExport.Request(classPath(classes), "Game", project, "My <Game>",
        dir.resolve("web")));

    Path web = index.getParent();
    assertTrue(Files.size(web.resolve("game.wasm")) > 100_000, "game.wasm");
    assertTrue(Files.readString(index).contains("<title>My &lt;Game&gt;</title>"));
    for (String file : List.of("s4j.js", "wasm-gc-runtime.min.js", "UbuntuMono-Regular.ttf", "loading.png",
        "images/platformer.png", "sounds/rpg/handleCoins.ogg", "assets/bg.png")) {
      assertTrue(Files.exists(web.resolve(file)), file);
    }
    assertTrue(Files.notExists(web.resolve("Game.java")) && Files.notExists(web.resolve(".scratch4j")));
    List<String> manifest = Files.readAllLines(web.resolve("files.txt"));
    assertTrue(manifest.contains("assets/bg.png\t3"), "files.txt lists the game's files with their sizes");
    assertTrue(manifest.stream().noneMatch(line -> line.startsWith("game.wasm")));
  }

  @Test
  void namesWhatABrowserCannotRun(@TempDir Path dir) throws IOException {
    Path classes = compile(dir.resolve("project"), "Shell", """
        public class Shell {
          public static void main(String[] args) throws Exception {
            new ProcessBuilder("ls").start();
          }
        }
        """);
    WebExport.CompileException e = assertThrows(WebExport.CompileException.class,
        () -> WebExport.export(new WebExport.Request(classPath(classes), "Shell", null, "Shell", dir.resolve("web"))));
    assertTrue(e.getMessage().contains("ProcessBuilder"), e.getMessage());
  }

  @Test
  void refusesAFolderThatIsNotEmpty(@TempDir Path dir) throws IOException {
    Files.writeString(dir.resolve("keep.txt"), "mine");
    IOException e = assertThrows(IOException.class,
        () -> WebExport.export(new WebExport.Request(List.of(), "Game", null, "Game", dir)));
    assertTrue(e.getMessage().contains("not empty"));
    assertEquals("mine", Files.readString(dir.resolve("keep.txt")));
  }

  private static Path compile(Path project, String name, String source) throws IOException {
    Files.createDirectories(project);
    Path file = project.resolve(name + ".java");
    Files.writeString(file, source);
    Path classes = project.resolve("target/classes");
    Files.createDirectories(classes);
    int status = ToolProvider.getSystemJavaCompiler().run(null, null, null, "-d", classes.toString(), "-cp",
        location(Stage.class).toString(), file.toString());
    assertEquals(0, status, "the test game compiles");
    return classes;
  }

  private static List<Path> classPath(Path classes) {
    return List.of(classes, location(Stage.class), location(WebPlatform.class));
  }

  private static Path location(Class<?> type) {
    try {
      return Path.of(type.getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch (java.net.URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }
}
