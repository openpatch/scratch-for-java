package org.openpatch.scratch.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** A branch build carrying the last release's number still is the version 7 extension API. */
class CoreVersionTest {

  @Test
  void anOlderNumberIsReportedAsTheApiItImplements() {
    assertEquals("7.0.0", CoreVersion.effective("6.5.0"));
    assertEquals("7.0.0", CoreVersion.effective("6.5.0-SNAPSHOT"));
  }

  @Test
  void aReleasedVersionIsReportedAsItIs() {
    assertEquals("7.0.0", CoreVersion.effective("7.0.0"));
    assertEquals("7.2.1", CoreVersion.effective("7.2.1"));
    assertEquals("8.0.0", CoreVersion.effective("8.0.0"));
  }

  @Test
  void theCoreReportsSevenOrLater() {
    assertEquals(true, Integer.parseInt(Extension.coreVersion().split("[.-]")[0]) >= 7);
  }
}
