import benchmark.Bench;
import org.openpatch.scratch.Window;

/**
 * Starts a scene of {@code examples/src/main/java/benchmark/Bench.java} on 6.x, for
 * {@code scripts/benchmark.sh --with-6x}: compiled against a 6.x build of {@code main}.
 */
public class Main6 {
  public static void main(String[] args) {
    Bench.version = "6.x";
    Bench.scene = args[0];
    Bench.n = Integer.parseInt(args[1]);
    Bench.width = args[0].equals("stress") ? 1200 : 960;
    Bench.height = args[0].equals("stress") ? 800 : 720;
    Window window = new Window(Bench.width, Bench.height, "demos/stressTest/assets");
    window.setStage(new Bench());
  }
}
