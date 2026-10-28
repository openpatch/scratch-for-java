package org.openpatch.scratch.web;

import org.teavm.extension.introspect.IntrospectClass;
import org.teavm.extension.spi.reflection.SimpleReflectionPolicy;

/**
 * Compile time only: lets the game's own classes be read by reflection in the page, as saving
 * (scratch-save's SaveData and File) and scratch-tiled's {@code spawn} do: their public fields,
 * public methods (bean getters and setters) and constructors. The JDK, TeaVM and the library itself
 * stay closed. Only classes the game's code names are in the page: none is found by its name alone.
 */
public final class GameReflection extends SimpleReflectionPolicy {

  private static final String[] CLOSED = {"java.", "javax.", "jdk.", "sun.", "org.teavm.", "org.openpatch.scratch.",
      "org.joda.", "com.jcraft."};

  @Override
  protected void setup() {
    this.selectClasses(GameReflection::isGameClass)
        .reflectablePublicFields()
        .reflectablePublicMethods()
        .reflectableMethods(method -> method.isConstructor());
  }

  private static boolean isGameClass(IntrospectClass<?> type) {
    if (type.isPrimitive() || type.isArray() || type.isAnnotation()) {
      return false;
    }
    String name = type.name();
    for (String prefix : CLOSED) {
      if (name.startsWith(prefix)) {
        return false;
      }
    }
    return true;
  }
}
