package org.openpatch.scratch.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

import org.openpatch.scratch.ScratchException;

/**
 * Builds a {@code List} of the kind the NRW Zentralabitur hands out.
 *
 * <p>
 * Those classes live in the default package of the student's project, where a
 * library cannot name them. So the class is looked up by name when it is
 * needed, through the class loader of the student's own classes, and filled
 * through its {@code append} method. The caller hands the result back with a
 * type the compiler infers from the assignment, which is how
 * {@code List<Gegner> gegner = find(Gegner.class)} compiles in the NRW build.
 */
public final class NrwList {

  private static final String CLASS_NAME = "List";

  private NrwList() {}

  /**
   * Returns a new NRW {@code List} holding the given items, in order.
   *
   * @param items the items to append
   * @param hints classes of the student's project, whose class loader also
   *              loaded their {@code List}; the context class loader is tried
   *              after them
   * @param <L>   the student's {@code List} type, inferred at the call site
   * @return the filled list
   */
  public static <L> L of(Iterable<?> items, Class<?>... hints) {
    return of(items, CLASS_NAME, hints);
  }

  @SuppressWarnings("unchecked")
  static <L> L of(Iterable<?> items, String className, Class<?>... hints) {
    Class<?> listClass = find(className, hints);
    try {
      Constructor<?> constructor = listClass.getDeclaredConstructor();
      Method append = listClass.getMethod("append", Object.class);
      constructor.setAccessible(true);
      append.setAccessible(true);
      Object list = constructor.newInstance();
      for (Object item : items) {
        append.invoke(list, item);
      }
      return (L) list;
    } catch (NoSuchMethodException e) {
      return fail(
          "Your class " + className + " is not the List from the NRW Abitur.",
          "It needs a constructor without parameters and a method append(ContentType).");
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }
      throw new ScratchException("Could not fill the List: " + cause);
    } catch (ReflectiveOperationException e) {
      throw new ScratchException("Could not create the List: " + e);
    }
  }

  private static Class<?> find(String className, Class<?>... hints) {
    Set<ClassLoader> loaders = new LinkedHashSet<>();
    for (Class<?> hint : hints) {
      if (hint != null && hint.getClassLoader() != null) {
        loaders.add(hint.getClassLoader());
      }
    }
    ClassLoader context = Thread.currentThread().getContextClassLoader();
    if (context != null) {
      loaders.add(context);
    }
    loaders.add(NrwList.class.getClassLoader());

    for (ClassLoader loader : loaders) {
      try {
        return Class.forName(className, false, loader);
      } catch (ClassNotFoundException ignored) {
        // try the next loader
      }
    }
    return fail(
        "There is no class " + className + " in your project.",
        "Copy List.java from the NRW Abitur classes into your project, next to your"
            + " own classes (not into a package). If you do not use the Abitur"
            + " classes, use the normal Scratch for Java JAR instead of the NRW one.");
  }

  private static <T> T fail(String problem, String tip) {
    System.err.println("\n==============================================");
    System.err.println("ERROR: Could not create an NRW List!");
    System.err.println("==============================================");
    System.err.println(problem);
    System.err.println("\nTip: " + tip);
    System.err.println("==============================================\n");
    throw new ScratchException(problem);
  }
}
