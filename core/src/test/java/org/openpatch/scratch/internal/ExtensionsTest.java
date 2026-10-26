package org.openpatch.scratch.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.openpatch.scratch.extension.Extension;

class ExtensionsTest {

  record Fake(String id, String requires) implements Extension {
    public String name() {
      return "Fake " + this.id;
    }

    public String version() {
      return "1.0";
    }
  }

  @Test
  void majorOnlyAcceptsEveryMinorOfThatMajor() {
    assertTrue(Extensions.satisfies("7", "7.0.0"));
    assertTrue(Extensions.satisfies("7", "7.9.3"));
    assertFalse(Extensions.satisfies("7", "8.0.0"));
    assertFalse(Extensions.satisfies("7", "6.5.0"));
  }

  @Test
  void minorAndPatchAreLowerBounds() {
    assertTrue(Extensions.satisfies("7.1", "7.1.0"));
    assertTrue(Extensions.satisfies("7.1", "7.2.0"));
    assertFalse(Extensions.satisfies("7.1", "7.0.9"));
    assertTrue(Extensions.satisfies("7.1.2", "7.1.2"));
    assertFalse(Extensions.satisfies("7.1.2", "7.1.1"));
    assertTrue(Extensions.satisfies("7.1.2", "7.2.0"));
  }

  @Test
  void suffixesAreIgnored() {
    assertTrue(Extensions.satisfies("7.0.0", "7.0.0-SNAPSHOT"));
  }

  @Test
  void garbageIsReported() {
    assertThrows(IllegalArgumentException.class, () -> Extensions.satisfies("seven", "7.0.0"));
  }

  @Test
  void firstOfTwoWithTheSameIdWinsAndUnmetRequirementsAreLeftOut() {
    Fake a = new Fake("a", "7"), b = new Fake("b", "7.2"), a2 = new Fake("a", "7");
    List<Extension> chosen = Extensions.select(List.of(a, b, a2), "7.1.0");
    assertEquals(List.of(a), chosen);
  }

  @Test
  void theCoreKnowsItsVersion() {
    assertFalse(Extension.coreVersion().isBlank());
    assertFalse(Extension.coreVersion().contains("${"), "the version file was not filtered");
  }
}
