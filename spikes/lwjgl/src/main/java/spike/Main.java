package spike;

/**
 * Stands in for a student's {@code main}: it opens the window and carries on,
 * as {@code new Window(480, 360)} does with Processing today. If the window
 * blocked here, BlueJ and every {@code main} that calls {@code setStage} after
 * the window would hang.
 *
 * <p>System properties:
 * <ul>
 * <li>{@code spike.exitAfter=N} closes the window after N seconds</li>
 * <li>{@code spike.capture=file.png} saves the stage after one second</li>
 * <li>{@code spike.hidden=true} never shows the window (Studio's stage preview)</li>
 * <li>{@code spike.vsync=true} paces by vsync on top of the 60 FPS clock</li>
 * <li>{@code spike.shader=path} a fragment shader body in the 7.0 format</li>
 * </ul>
 */
public class Main {

  public static void main(String[] args) throws Exception {
    long start = System.nanoTime();
    SpikeWindow window = new SpikeWindow(480, 360);
    System.out.printf("main: window constructor returned after %d ms on thread '%s'%n",
        (System.nanoTime() - start) / 1_000_000, Thread.currentThread().getName());
    // what a student's main does next: things that need the window to exist
    window.awaitReady();
    System.out.printf("main: window ready after %d ms%n", (System.nanoTime() - start) / 1_000_000);
    System.out.println("main: returns now; the render thread keeps the program alive");
  }
}
