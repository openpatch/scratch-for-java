package spike;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * What {@code org.openpatch.scratch.Launcher} will do for Studio's runs and
 * exports: keep the process's first thread for GLFW,
 * which macOS requires with {@code -XstartOnFirstThread} and the standard GLFW
 * build, and run the learner's {@code main} on a second thread.
 *
 * <pre>java -XstartOnFirstThread -cp ... spike.Launcher spike.GameLoopCheck</pre>
 */
public final class Launcher {

  private static final BlockingQueue<Runnable> firstThread = new LinkedBlockingQueue<>();
  private static volatile boolean active;

  private Launcher() {}

  /** True when the window has to run on the first thread instead of a thread of its own. */
  static boolean isActive() {
    return active;
  }

  /** The window loop; runs on the first thread and does not return until the program ends. */
  static void runWindow(Runnable windowLoop) {
    firstThread.add(windowLoop);
  }

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.err.println("usage: Launcher <main class> [args...]");
      System.exit(2);
    }
    active = true;
    Method learnerMain = Class.forName(args[0]).getMethod("main", String[].class);
    String[] learnerArgs = Arrays.copyOfRange(args, 1, args.length);
    Thread learner = new Thread(() -> {
      try {
        learnerMain.invoke(null, (Object) learnerArgs);
      } catch (ReflectiveOperationException e) {
        (e.getCause() != null ? e.getCause() : e).printStackTrace();
      }
    }, "learner-main");
    learner.start();
    System.out.println("launcher: first thread '" + Thread.currentThread().getName() + "' waits for the window");
    // a main that never opens a window ends the program when it returns
    while (true) {
      Runnable windowLoop = firstThread.poll(50, TimeUnit.MILLISECONDS);
      if (windowLoop != null) {
        windowLoop.run();
        return;
      }
      if (!learner.isAlive()) {
        return;
      }
    }
  }
}
