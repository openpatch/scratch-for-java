package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Origin enum.
 */
class OriginTest {

  @Test
  void testOriginEnumValues() {
    // Verify all expected origin values exist
    Origin[] origins = Origin.values();
    assertEquals(10, origins.length, "Should have 10 origin values");
    
    // Verify specific values
    assertNotNull(Origin.TOP_LEFT);
    assertNotNull(Origin.TOP_CENTER);
    assertNotNull(Origin.TOP_RIGHT);
    assertNotNull(Origin.CENTER_LEFT);
    assertNotNull(Origin.CENTER);
    assertNotNull(Origin.CENTER_RIGHT);
    assertNotNull(Origin.BOTTOM_LEFT);
    assertNotNull(Origin.BOTTOM_CENTER);
    assertNotNull(Origin.BOTTOM_RIGHT);
    assertNotNull(Origin.CUSTOM);
  }

  @Test
  void testOriginValueOf() {
    // Test that valueOf works correctly
    assertEquals(Origin.CENTER, Origin.valueOf("CENTER"));
    assertEquals(Origin.TOP_LEFT, Origin.valueOf("TOP_LEFT"));
    assertEquals(Origin.CUSTOM, Origin.valueOf("CUSTOM"));
  }

  @Test
  void testOriginToString() {
    // Test that toString returns the correct name
    assertEquals("CENTER", Origin.CENTER.toString());
    assertEquals("TOP_LEFT", Origin.TOP_LEFT.toString());
    assertEquals("CUSTOM", Origin.CUSTOM.toString());
  }
}
