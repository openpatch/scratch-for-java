package org.openpatch.scratch.extensions.shader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Which shaders are drawn with. The programs themselves need a window, so the
 * shaders here have none; only the choosing is checked.
 */
class ShadersTest {

  private static Shaders three() {
    Shaders shaders = new Shaders("stage");
    shaders.add(new Shader("tint", null));
    shaders.add(new Shader("waves", null));
    shaders.add(new Shader("blur", null));
    return shaders;
  }

  private static List<String> active(Shaders shaders) {
    return shaders.getActive().stream().map(Shader::getName).toList();
  }

  @Test
  void theFirstShaderAddedIsActiveAtOnce() {
    Shaders shaders = three();
    assertEquals(List.of("tint"), active(shaders));
    assertEquals("tint", shaders.getCurrentName());
    assertEquals(0, shaders.getCurrentIndex());
  }

  @Test
  void afterResetANewFirstShaderWaits() {
    Shaders shaders = new Shaders("sprite");
    shaders.reset();
    shaders.add(new Shader("tint", null));
    assertNull(shaders.getCurrent());
    assertEquals(-1, shaders.getCurrentIndex());
  }

  @Test
  void switchToLeavesExactlyOneActive() {
    Shaders shaders = three();
    shaders.activate("blur");
    shaders.switchTo("waves");
    assertEquals(List.of("waves"), active(shaders));
    shaders.switchTo(5);
    assertEquals(List.of("blur"), active(shaders), "positions wrap around");
    shaders.switchTo(-1);
    assertEquals(List.of("blur"), active(shaders), "so do negative ones");
  }

  @Test
  void nextAndResetWorkAsBefore() {
    Shaders shaders = three();
    shaders.next();
    assertEquals("waves", shaders.getCurrentName());
    shaders.next();
    shaders.next();
    assertEquals("tint", shaders.getCurrentName(), "after the last comes the first");
    shaders.reset();
    assertTrue(shaders.getActive().isEmpty());
    shaders.next();
    assertEquals("tint", shaders.getCurrentName(), "after reset, next starts at the first");
  }

  @Test
  void activatedShadersTakeTurnsInTheOrderTheyWereAdded() {
    Shaders shaders = three();
    shaders.activate("blur");
    shaders.activate("waves");
    assertEquals(List.of("tint", "waves", "blur"), active(shaders));
    assertTrue(shaders.isActive("waves"));
    assertEquals("tint", shaders.getCurrentName(), "the current one is the first active");
  }

  @Test
  void deactivateStopsOnlyThatShader() {
    Shaders shaders = three();
    shaders.activate("waves");
    shaders.deactivate("tint");
    assertEquals(List.of("waves"), active(shaders));
    assertFalse(shaders.isActive("tint"));
    assertEquals("waves", shaders.getCurrentName());
    assertEquals(1, shaders.getCurrentIndex());
  }

  @Test
  void unknownNamesChangeNothing() {
    Shaders shaders = three();
    shaders.activate("glow");
    shaders.deactivate("glow");
    shaders.switchTo("glow");
    assertEquals(List.of("tint"), active(shaders));
    assertFalse(shaders.isActive("glow"));
  }

  @Test
  void aCopyKeepsWhichShadersAreActive() {
    Shaders shaders = three();
    shaders.activate("blur");
    Shaders copy = new Shaders(shaders);
    assertEquals(List.of("tint", "blur"), active(copy));
    copy.deactivate("blur");
    assertTrue(shaders.isActive("blur"), "the original is not changed");
  }
}
