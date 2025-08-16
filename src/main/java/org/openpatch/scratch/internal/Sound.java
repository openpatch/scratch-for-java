package org.openpatch.scratch.internal;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;

/**
 * The Sound class represents a sound object in Scratch using Java's internal
 * audio classes.
 * Sounds play on a separate thread but only one instance of the sound plays at
 * a time.
 */
public class Sound {
  private String name;
  private Clip clip;
  private double volume = 1.0;
  private static final AbstractMap<String, Clip> soundCache = new HashMap<>();

  public Sound(String name, String soundPath) {
    this.name = name;
    this.clip = loadSound(soundPath);
  }

  public Sound(Sound s) {
    this.name = s.name;
    this.clip = s.clip;
    this.volume = s.volume;
  }

  public static Clip loadSound(String path) {
    Clip clip = soundCache.get(path);
    if (clip == null) {
      try {
        AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File(Applet.getPath(path)));
        clip = AudioSystem.getClip();
        clip.open(audioInput);
        soundCache.put(path, clip);
      } catch (IOException e) {
        System.err.println("Error loading sound file: " + path);
        System.exit(1);
      } catch (UnsupportedAudioFileException e) {
        System.err.println("Unsupported audio file: " + path);
        System.exit(1);
      } catch (LineUnavailableException e) {
        System.err.println("Line unavailable for sound: " + path);
        System.exit(1);
      }
    }
    return clip;
  }

  public String getName() {
    return name;
  }

  /** Play the sound. Only one instance will play at a time. */
  public void play() {
    if (clip == null)
      return;

    // Only start if not already playing
    if (!clip.isRunning()) {
      new Thread(() -> {
        clip.setFramePosition(0);
        setVolume(volume);
        clip.start();
      }).start();
    }
  }

  public void pause() {
    if (clip != null && clip.isRunning()) {
      clip.stop();
    }
  }

  public void stop() {
    if (clip != null) {
      clip.stop();
      clip.setFramePosition(0);
    }
  }

  public boolean isPlaying() {
    return clip != null && clip.isRunning();
  }

  public void setVolume(double amp) {
    volume = Math.max(0, Math.min(1, amp));
    if (clip != null) {
      FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
      float dB = (float) (20.0 * Math.log10(volume <= 0 ? 0.0001 : volume));
      gainControl.setValue(dB);
    }
  }

  public void changeVolume(double step) {
    setVolume(volume + step);
  }

  public double getVolume() {
    return volume;
  }
}
