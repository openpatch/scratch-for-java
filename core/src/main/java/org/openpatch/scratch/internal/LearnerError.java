package org.openpatch.scratch.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An exception from learner code, with the learner's frame found: the first stack frame outside
 * the library, the JDK and LWJGL. That is the line a learner has to look at, so it is named
 * first.
 *
 * @param error what was thrown
 * @param learnerFrame the learner's frame, or null if the trace has none (an error inside the
 *     library)
 */
public record LearnerError(Throwable error, StackTraceElement learnerFrame) {

  private static final String[] PLATFORM = {"java.", "javax.", "jdk.", "sun.", "com.sun.", "org.lwjgl."};

  /** Finds the learner's frame of {@code error}. */
  public static LearnerError of(Throwable error) {
    return new LearnerError(error, learnerFrame(error.getStackTrace()));
  }

  /** The first frame of a learner class, or null. */
  public static StackTraceElement learnerFrame(StackTraceElement[] trace) {
    for (StackTraceElement e : trace) {
      if (isLearnerFrame(e)) {
        return e;
      }
    }
    return null;
  }

  /**
   * Whether a frame belongs to learner code. A class counts as the library's when it was loaded
   * from the library's own jar or folder, so the library's tests and examples count as learner
   * code although they share its package names.
   */
  public static boolean isLearnerFrame(StackTraceElement e) {
    String name = e.getClassName();
    for (String prefix : PLATFORM) {
      if (name.startsWith(prefix)) {
        return false;
      }
    }
    if (!name.startsWith("org.openpatch.scratch.")) {
      return true;
    }
    try {
      Class<?> type = Class.forName(name, false, LearnerError.class.getClassLoader());
      return !Objects.equals(location(type), location(LearnerError.class));
    } catch (ClassNotFoundException | LinkageError | SecurityException ex) {
      return false;
    }
  }

  private static Object location(Class<?> type) {
    var domain = type.getProtectionDomain();
    return domain == null || domain.getCodeSource() == null ? null : domain.getCodeSource().getLocation();
  }

  /** {@code MySprite.run (MySprite.java:12)}, or the exception's own first frame. */
  public static String describe(StackTraceElement e) {
    String simple = e.getClassName().substring(e.getClassName().lastIndexOf('.') + 1);
    return simple + "." + e.getMethodName() + " (" + e.getFileName() + ":" + e.getLineNumber() + ")";
  }

  /**
   * What is printed for a learner: the place in the learner's code, then what happened, then the
   * learner's frames, then the full trace for the curious.
   */
  public String message() {
    StringBuilder out = new StringBuilder();
    String what = this.error.getClass().getSimpleName()
        + (this.error.getMessage() == null ? "" : ": " + this.error.getMessage());
    if (this.learnerFrame != null) {
      out.append("Error in ").append(describe(this.learnerFrame)).append(": ").append(what).append('\n');
    } else {
      out.append("Error: ").append(what).append('\n');
    }
    List<StackTraceElement> learner = new ArrayList<>();
    for (StackTraceElement e : this.error.getStackTrace()) {
      if (isLearnerFrame(e)) {
        learner.add(e);
      }
    }
    for (StackTraceElement e : learner) {
      out.append("    at ").append(e).append('\n');
    }
    // a released game ends with an error box instead (Release)
    out.append(Release.isOn() ? "The game has stopped.\n" : "The game has stopped. The window stays open until you close it.\n");
    return out.toString();
  }
}
