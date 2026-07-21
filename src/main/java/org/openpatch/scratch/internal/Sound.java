package org.openpatch.scratch.internal;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

  /**
   * Creates a sound from either the name of a sound bundled with Scratch for
   * Java or a path to a sound file.
   *
   * <p>
   * A string without a file extension is looked up in {@link BuiltinSounds},
   * everything else is treated as a path, so existing code keeps working.
   *
   * @param name          the name the sound should have
   * @param pathOrBuiltin a bundled sound name or a path to a sound file
   * @return the sound
   */
  public static Sound ofNameOrPath(String name, String pathOrBuiltin) {
    if (BuiltinAssets.isBuiltinName(pathOrBuiltin)) {
      String resource = BuiltinSounds.get(pathOrBuiltin);
      if (resource != null) {
        return new Sound(name, resource);
      }
      AssetErrorReporter.reportUnknownBuiltinAndExit(pathOrBuiltin, "sound");
    }
    return new Sound(name, pathOrBuiltin);
  }

  public static Clip loadSound(String path) {
    Clip clip = soundCache.get(path);
    if (clip == null) {
      try {
        AudioInputStream audioInput = openStream(Applet.getPath(path));
        audioInput = toPlayableStream(audioInput);
        clip = AudioSystem.getClip();
        clip.open(audioInput);
        if (clip.getFrameLength() == 0) {
          System.err.println("\n==============================================");
          System.err.println("WARNING: Sound contains no audio!");
          System.err.println("==============================================");
          System.err.println("Path: " + path);
          System.err.println("\nThe file was read but decoded to zero samples,");
          System.err.println("so playing it will not be audible.");
          System.err.println("\nTip: Re-export the file, or convert it to WAV.");
          System.err.println("==============================================\n");
        }
        soundCache.put(path, clip);
      } catch (IOException e) {
        AssetErrorReporter.reportAndExit(
            "sound", path, "WAV, AIFF, AU, OGG",
            new String[]{".wav", ".aiff", ".au", ".ogg"});
      } catch (UnsupportedAudioFileException e) {
        System.err.println("\n==============================================");
        System.err.println("ERROR: Unsupported audio file format!");
        System.err.println("==============================================");
        System.err.println("Path: " + path);
        System.err.println("\nSupported formats: WAV, AIFF, AU, OGG");
        System.err.println("\nTip: Convert your audio file to WAV or OGG");
        System.err.println("     format using an audio conversion tool.");
        System.err.println("==============================================\n");
        System.exit(1);
      } catch (LineUnavailableException e) {
        System.err.println("\n==============================================");
        System.err.println("ERROR: Audio system unavailable!");
        System.err.println("==============================================");
        System.err.println("Path: " + path);
        System.err.println("\nPossible reasons:");
        System.err.println("  1. No audio output device is available");
        System.err.println("  2. Audio device is being used by another program");
        System.err.println("\nTip: Check your system's audio settings.");
        System.err.println("==============================================\n");
        System.exit(1);
      }
    }
    return clip;
  }

  /**
   * The Ogg decoder needs at least this many bytes available on its first read,
   * otherwise it silently decodes nothing.
   */
  private static final int MIN_OGG_STREAM_SIZE = 16384;

  /**
   * Opens a sound file as a stream {@link AudioSystem} can read.
   *
   * <p>
   * Ogg files shorter than {@link #MIN_OGG_STREAM_SIZE} are padded with trailing
   * zero bytes. The Ogg decoder reads in fixed-size blocks and gives up without
   * an error when the very first block is short, which silently loses every
   * sound effect below roughly 8 kB. The padding sits behind the last Ogg page,
   * where the container format says trailing bytes are ignored, so it changes
   * nothing about the audio itself. Other formats are passed through untouched.
   *
   * @param resolvedPath the path as resolved by {@link Applet#getPath(String)}
   * @return a stream over the file's bytes
   * @throws IOException                   if the file cannot be read
   * @throws UnsupportedAudioFileException if the format is not supported
   */
  static AudioInputStream openStream(String resolvedPath)
      throws IOException, UnsupportedAudioFileException {
    byte[] data = Files.readAllBytes(new File(resolvedPath).toPath());

    if (isOgg(data) && data.length < MIN_OGG_STREAM_SIZE) {
      byte[] padded = new byte[MIN_OGG_STREAM_SIZE];
      System.arraycopy(data, 0, padded, 0, data.length);
      data = padded;
    }

    return AudioSystem.getAudioInputStream(new BufferedInputStream(new ByteArrayInputStream(data)));
  }

  /** Checks for the "OggS" marker every Ogg file starts with. */
  static boolean isOgg(byte[] data) {
    return data.length >= 4
        && data[0] == 'O' && data[1] == 'g' && data[2] == 'g' && data[3] == 'S';
  }

  /**
   * Decodes a sound stream into raw PCM if it is not already.
   *
   * <p>
   * Compressed formats such as Ogg Vorbis are decoded by a service provider
   * that hands back a stream still in the compressed encoding. A {@link Clip}
   * can only play raw PCM, so the stream has to be converted first. WAV, AIFF
   * and AU files are usually PCM already and are passed through untouched.
   *
   * @param in the stream as handed over by {@link AudioSystem}
   * @return a stream a {@link Clip} can open
   */
  static AudioInputStream toPlayableStream(AudioInputStream in) {
    AudioFormat source = in.getFormat();
    if (AudioFormat.Encoding.PCM_SIGNED.equals(source.getEncoding())
        || AudioFormat.Encoding.PCM_UNSIGNED.equals(source.getEncoding())) {
      return in;
    }

    AudioFormat target = new AudioFormat(
        AudioFormat.Encoding.PCM_SIGNED,
        source.getSampleRate(),
        16,
        source.getChannels(),
        source.getChannels() * 2,
        source.getSampleRate(),
        false);
    return AudioSystem.getAudioInputStream(target, in);
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
