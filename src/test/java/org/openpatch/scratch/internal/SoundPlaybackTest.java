package org.openpatch.scratch.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Proxy;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import org.junit.jupiter.api.Test;

/** Exercises the real playback/pooling code without requiring an audio device. */
class SoundPlaybackTest {
  private static final Sound.Samples AUDIO = new Sound.Samples(
      new AudioFormat(8000, 16, 1, true, false), new byte[160]);

  @Test
  void recreatingStagesAndCloningSpritesPreparesOnlyOneLinePerFile() {
    AudioDevice device = new AudioDevice();
    Sound.ClipPool pool = device.pool(AUDIO);
    for (int round = 0; round < 100; round++) {
      Sound sound = new Sound("effect", pool);
      new Sound(sound);
    }
    assertEquals(1, device.tasks.size(), "preloading is queued once per file");
    device.drain();
    assertEquals(1, device.clips.size());
    assertEquals(1, device.openLines());
  }

  @Test
  void concurrentSpritesKeepIndependentPlaybackAndVolume() {
    AudioDevice device = new AudioDevice();
    Sound original = new Sound("effect", device.pool(AUDIO));
    original.setVolume(0.3);
    Sound copy = new Sound(original);
    copy.setVolume(0.8);
    device.drain();

    original.play();
    original.play();
    copy.play();
    device.drain();
    assertEquals(2, device.openLines());
    assertEquals(1, device.clips.get(0).starts, "one instance plays once at a time");
    assertEquals(20 * Math.log10(0.3), device.clips.get(0).gain.getValue(), 0.001);
    assertEquals(20 * Math.log10(0.8), device.clips.get(1).gain.getValue(), 0.001);

    original.stop();
    assertFalse(original.isPlaying());
    assertTrue(copy.isPlaying());
    copy.setVolume(0.5);
    assertEquals(20 * Math.log10(0.5), device.clips.get(1).gain.getValue(), 0.001);
    copy.stop();
    assertEquals(1, device.openLines(), "surplus native streams are closed");
  }

  @Test
  void completedPlaybackReusesTheWarmLineAndDetachesListeners() {
    AudioDevice device = new AudioDevice();
    Sound sound = new Sound("effect", device.pool(AUDIO));
    device.drain();
    for (int round = 0; round < 100; round++) {
      sound.play();
      assertTrue(sound.isPlaying());
      device.clips.get(0).end();
      assertFalse(sound.isPlaying());
      assertTrue(device.clips.get(0).listeners.isEmpty());
    }
    assertEquals(1, device.clips.size());
  }

  @Test
  void cancelledQueuedPlaybackDoesNotOpenAnotherLine() {
    AudioDevice device = new AudioDevice();
    Sound sound = new Sound("effect", device.pool(AUDIO));
    sound.play();
    assertTrue(sound.isPlaying(), "queued playback counts as playing");
    sound.stop();
    device.drain();
    assertFalse(sound.isPlaying());
    assertEquals(1, device.clips.size());
    assertEquals(0, device.clips.get(0).starts);
    sound.play();
    assertTrue(sound.isPlaying());
    sound.stop();
  }

  @Test
  void aLineOpenedDuringCancellationIsReturnedToThePool() {
    AudioDevice device = new AudioDevice();
    Sound first = new Sound("effect", device.pool(AUDIO));
    Sound second = new Sound(first);
    device.drain();
    first.play();
    second.play();
    device.onCreate = second::stop;
    device.drain();
    assertFalse(second.isPlaying());
    assertEquals(0, device.clips.get(1).starts);
    first.stop();
    assertEquals(1, device.openLines());
    assertTrue(device.clips.stream().allMatch(clip -> clip.listeners.isEmpty()));
  }

  @Test
  void concurrencyIsCappedAndThePoolShrinksAfterTheBurst() {
    AudioDevice device = new AudioDevice();
    Sound.ClipPool pool = device.pool(AUDIO);
    List<Sound> sounds = new ArrayList<>();
    for (int i = 0; i < 17; i++) {
      sounds.add(new Sound("effect", pool));
    }
    device.drain();
    sounds.forEach(Sound::play);
    device.drain();
    assertEquals(16, device.openLines());
    assertEquals(16, sounds.stream().filter(Sound::isPlaying).count());
    sounds.forEach(Sound::stop);
    sounds.forEach(Sound::stop);
    assertEquals(1, device.openLines());
    for (int round = 0; round < 100; round++) {
      Sound sound = new Sound("effect", pool);
      sound.play();
      sound.stop();
    }
    device.drain();
    assertEquals(16, device.clips.size(), "later rounds reuse the retained line");
  }

  @Test
  void longSoundsKeepTheirMemoryBasedConcurrencyLimit() {
    AudioDevice device = new AudioDevice();
    Sound.Samples music = new Sound.Samples(AUDIO.format(), new byte[17 * 1024 * 1024]);
    Sound first = new Sound("music", device.pool(music));
    Sound second = new Sound(first);
    device.drain();
    first.play();
    second.play();
    device.drain();
    assertEquals(1, device.openLines());
    assertTrue(first.isPlaying());
    assertFalse(second.isPlaying());
    first.stop();
  }

  @Test
  void failedOpeningClosesThePartialLineAndAllowsRetry() {
    AudioDevice device = new AudioDevice();
    device.failNextOpen = true;
    Sound sound = new Sound("effect", device.pool(AUDIO));
    device.drain();
    assertTrue(device.clips.get(0).closed);
    assertEquals(0, device.openLines());
    sound.play();
    device.drain();
    assertTrue(sound.isPlaying());
    assertEquals(1, device.openLines());
    sound.stop();
  }

  @Test
  void emptyAudioDoesNotQueueOrOpenLines() {
    AudioDevice device = new AudioDevice();
    Sound sound = new Sound("empty", device.pool(new Sound.Samples(AUDIO.format(), new byte[0])));
    new Sound(sound).play();
    sound.play();
    assertFalse(sound.isPlaying());
    assertTrue(device.tasks.isEmpty());
    assertTrue(device.clips.isEmpty());
  }

  private static class AudioDevice implements Executor, Sound.ClipFactory {
    final Queue<Runnable> tasks = new ArrayDeque<>();
    final List<FakeClip> clips = new ArrayList<>();
    boolean failNextOpen;
    Runnable onCreate;

    Sound.ClipPool pool(Sound.Samples samples) {
      return new Sound.ClipPool(samples, this, this);
    }

    @Override
    public void execute(Runnable task) {
      tasks.add(task);
    }

    void drain() {
      while (!tasks.isEmpty()) {
        tasks.remove().run();
      }
    }

    long openLines() {
      return clips.stream().filter(clip -> clip.open && !clip.closed).count();
    }

    @Override
    public Clip create() {
      FakeClip clip = new FakeClip(failNextOpen);
      failNextOpen = false;
      clips.add(clip);
      if (onCreate != null) {
        Runnable callback = onCreate;
        onCreate = null;
        callback.run();
      }
      return clip.line;
    }
  }

  private static class FakeClip {
    final List<LineListener> listeners = new ArrayList<>();
    final FloatControl gain = new FloatControl(FloatControl.Type.MASTER_GAIN,
        -80, 6, 0.1f, 1, 0, "dB") {};
    final Clip line;
    boolean open;
    boolean closed;
    boolean running;
    int starts;

    FakeClip(boolean failOpen) {
      line = (Clip) Proxy.newProxyInstance(Clip.class.getClassLoader(), new Class<?>[] { Clip.class },
          (proxy, method, args) -> {
            switch (method.getName()) {
              case "open":
                if (failOpen) {
                  throw new LineUnavailableException("simulated audio device failure");
                }
                open = true;
                return null;
              case "close": closed = true; running = false; return null;
              case "start": starts++; running = true; return null;
              case "stop": end(); return null;
              case "isRunning": return running;
              case "addLineListener": listeners.add((LineListener) args[0]); return null;
              case "removeLineListener": listeners.remove(args[0]); return null;
              case "isControlSupported": return true;
              case "getControl": return gain;
              case "setFramePosition": return null;
              default: throw new AssertionError("Unexpected Clip operation: " + method.getName());
            }
          });
    }

    void end() {
      running = false;
      for (LineListener listener : List.copyOf(listeners)) {
        listener.update(new LineEvent(line, LineEvent.Type.STOP, 0));
      }
    }
  }
}
