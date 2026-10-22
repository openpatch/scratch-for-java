package org.openpatch.scratch.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import org.openpatch.scratch.extension.Extension;

/** Finds the extensions on the classpath and decides which of them may be installed. */
public final class Extensions {

  private Extensions() {}

  /**
   * The extensions found by {@link ServiceLoader} followed by {@code registered}, without
   * duplicate ids (the first wins) and without those whose {@link Extension#requires()} the
   * core does not meet. Each one left out is reported on {@code System.err} in one line.
   */
  public static List<Extension> discover(List<Extension> registered, String coreVersion) {
    List<Extension> found = new ArrayList<>();
    ServiceLoader<Extension> loader = ServiceLoader.load(Extension.class, Extensions.class.getClassLoader());
    for (var it = loader.iterator(); ; ) {
      try {
        if (!it.hasNext()) {
          break;
        }
        found.add(it.next());
      } catch (ServiceConfigurationError e) {
        System.err.println("Scratch for Java: an extension could not be loaded: " + e.getMessage());
      }
    }
    found.addAll(registered);
    return select(found, coreVersion);
  }

  static List<Extension> select(List<Extension> candidates, String coreVersion) {
    Map<String, Extension> byId = new LinkedHashMap<>();
    for (Extension e : candidates) {
      Extension first = byId.get(e.id());
      if (first != null) {
        System.err.printf("Scratch for Java: two extensions with the id \"%s\": %s is used, %s is not.%n",
            e.id(), first.getClass().getName(), e.getClass().getName());
        continue;
      }
      if (!satisfies(e.requires(), coreVersion)) {
        System.err.printf("Scratch for Java: %s %s needs Scratch for Java %s or newer below %d, "
                + "but this is %s. It is not installed.%n",
            e.name(), e.version(), e.requires(), major(e.requires()) + 1, coreVersion);
        continue;
      }
      byId.put(e.id(), e);
    }
    return List.copyOf(byId.values());
  }

  /** Whether a core of version {@code core} meets {@code requires} (see {@link Extension#requires()}). */
  static boolean satisfies(String requires, String core) {
    int[] need = parse(requires), have = parse(core);
    if (need[0] != have[0]) {
      return false;
    }
    for (int i = 1; i < 3; i++) {
      if (have[i] != need[i]) {
        return have[i] > need[i];
      }
    }
    return true;
  }

  private static int major(String version) {
    return parse(version)[0];
  }

  /** "7", "7.1" or "7.1.2", ignoring a suffix such as "-SNAPSHOT"; missing parts are 0. */
  private static int[] parse(String version) {
    int[] parts = new int[3];
    String[] split = version.split("-", 2)[0].split("\\.");
    for (int i = 0; i < Math.min(3, split.length); i++) {
      try {
        parts[i] = Integer.parseInt(split[i].trim());
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("not a version: \"" + version + "\"", e);
      }
    }
    return parts;
  }
}
