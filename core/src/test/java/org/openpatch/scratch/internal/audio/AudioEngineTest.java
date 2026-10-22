package org.openpatch.scratch.internal.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Plays for real where there is a device (a developer's machine) and checks the silent mode where
 * there is none (CI); never fails for lack of a device.
 */
class AudioEngineTest {

  @Test
  void playsOverlappingCopiesOrStaysSilent() throws Exception {
    AudioEngine engine = AudioEngine.get();
    SoundData data = SoundData.load("sounds/impact/impactMining_002.ogg");
    int buffer = engine.buffer(data);
    if (engine.isSilent()) {
      assertEquals(0, buffer);
      assertEquals(0, engine.play(buffer, 1, false));
      assertFalse(engine.isPlaying(0));
      return;
    }
    assertEquals(buffer, engine.buffer(data), "uploaded once");
    engine.setMasterVolume(0.05); // quiet: this runs on developers' machines
    int a = engine.play(buffer, 1, false), b = engine.play(buffer, 1, false);
    assertTrue(engine.isPlaying(a) && engine.isPlaying(b), "two copies at once");
    engine.stop(a);
    assertFalse(engine.isPlaying(a));
    assertTrue(engine.isPlaying(b));
    engine.stopAll();
    assertFalse(engine.isPlaying(b));
    engine.setMasterVolume(1);
  }
}
