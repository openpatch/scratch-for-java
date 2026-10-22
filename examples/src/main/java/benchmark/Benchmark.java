package benchmark;

import org.openpatch.scratch.Window;
import org.openpatch.scratch.extension.Extension;
import org.openpatch.scratch.extension.ExtensionContext;

/** Starts a scene of {@link Bench} on 7.0: {@code Benchmark <stress|collide|color> <n>}. */
public class Benchmark {
  public static void main(String[] args) {
    Bench.version = "7.0";
    Bench.scene = args[0];
    Bench.n = Integer.parseInt(args[1]);
    Bench.width = args[0].equals("stress") ? 1200 : 960;
    Bench.height = args[0].equals("stress") ? 800 : 720;
    // counts the frames the window shows: ticks and frames are separate in 7.0
    Window.use(new Extension() {
      public String id() {
        return "benchmark-frames";
      }

      public String name() {
        return "Frame counter";
      }

      public String version() {
        return "1";
      }

      public void install(ExtensionContext ctx) {
        Bench.frames = 0;
        ctx.afterFrame((stage, frame) -> Bench.frames++);
      }
    });
    Window window = new Window(Bench.width, Bench.height, "demos/stressTest/assets");
    window.setStage(new Bench());
  }
}
