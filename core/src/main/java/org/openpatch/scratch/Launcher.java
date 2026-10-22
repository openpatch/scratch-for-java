package org.openpatch.scratch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Starts a program so that its window runs on the process's first thread, which macOS requires
 * (with {@code -XstartOnFirstThread}); the program's {@code main} runs on a second thread. Studio
 * starts every run and every exported game this way; a plain {@code java}
 * run does not need it.
 *
 * <pre>java -XstartOnFirstThread -cp game.jar org.openpatch.scratch.Launcher MyGame</pre>
 *
 * @ignore-in-docs
 */
public final class Launcher {

  private static final BlockingQueue<Runnable> firstThread = new LinkedBlockingQueue<>();
  private static volatile boolean active;

  private Launcher() {}

  /** Whether the window has to run on the first thread. */
  public static boolean isActive() {
    return active;
  }

  /** Hands the window loop to the first thread; it does not return before the program ends. */
  public static void runOnFirstThread(Runnable windowLoop) {
    firstThread.add(windowLoop);
  }

  /**
   * @param args the main class, then its arguments
   * @throws Exception when the main class cannot be found
   */
  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.err.println("usage: java [-XstartOnFirstThread] org.openpatch.scratch.Launcher <main class> [args…]");
      System.exit(2);
    }
    active = true;
    Method learnerMain = Class.forName(args[0], false, Launcher.class.getClassLoader()).getMethod("main",
        String[].class);
    String[] learnerArgs = Arrays.copyOfRange(args, 1, args.length);
    Thread learner = new Thread(() -> {
      try {
        learnerMain.invoke(null, (Object) learnerArgs);
      } catch (InvocationTargetException e) {
        e.getCause().printStackTrace();
      } catch (ReflectiveOperationException e) {
        e.printStackTrace();
      }
    }, "main");
    learner.start();
    // a main that never opens a window ends the program when it returns
    while (true) {
      Runnable windowLoop = firstThread.poll(20, TimeUnit.MILLISECONDS);
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
