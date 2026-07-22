package org.openpatch.scratch.internal;

import javax.sound.sampled.*;

import org.openpatch.scratch.ScratchException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Sound class represents a sound object in Scratch using Java's internal
 * audio classes.
 *
 * <p>
 * The decoded audio of a file is loaded once and shared, but every sound object
 * plays on a line of its own. Ten sprites that all added the same sound
 * therefore sound ten times at once, while a single sprite still only plays its
 * own sound once at a time.
 */
public class Sound {

  /** Decoded audio of one file, shared by every sound object using that file. */
  record Samples(AudioFormat format, byte[] data) {
  }

  private static final Map<String, Samples> soundCache = new ConcurrentHashMap<>();

  /**
   * Lines that are not playing right now, kept per sound file so the next play
   * can reuse them. Opening a line takes tens of milliseconds, long enough to be
   * seen as a dropped frame if it happened while the sound is being started.
   */
  private static final Map<String, BlockingQueue<Clip>> idleClips = new ConcurrentHashMap<>();

  /** How many lines are open per sound file, playing or waiting for reuse. */
  private static final Map<String, AtomicInteger> openClips = new ConcurrentHashMap<>();

  /**
   * How many lines one sound file may hold, and with that how many copies of it
   * can be heard at the same time. Playing a sound more often than that at the
   * same instant is not something anyone can hear apart.
   */
  private static final int MAX_CLIPS_PER_SOUND = 16;

  /**
   * How much memory the lines of one sound file may take together. Every line
   * keeps a copy of the samples, so without this a long piece of music shared by
   * many sprites could take hundreds of megabytes.
   */
  private static final long MAX_CLIP_MEMORY_PER_SOUND = 32L * 1024 * 1024;

  /**
   * Opens lines ahead of time, off whichever thread is drawing the stage. One
   * thread is enough: the work only has to be done before the sound is played
   * for the first time.
   */
  private static final ExecutorService clipOpener = Executors.newSingleThreadExecutor(task -> {
    Thread thread = new Thread(task, "scratch-sound-preload");
    thread.setDaemon(true);
    return thread;
  });

  private static boolean audioWarningPrinted = false;

  private final String name;
  private final String path;
  private final Samples samples;
  private double volume = 1.0;

  /** The line this sound is playing on, or null while it is silent. */
  private volatile Playback playing;

  /**
   * One run of a sound on a line of its own.
   *
   * <p>
   * The line goes back into the pool as soon as the run is over, no matter
   * whether it reached the end of the sound or was stopped.
   */
  private final class Playback implements LineListener {
    /** Null until a line is there, which may take a moment on the first play. */
    private Clip clip;

    /** Puts this run on a line, waiting for one to be opened if need be. */
    private void start() {
      Clip pooled = pollIdleClip();
      if (pooled != null) {
        begin(pooled);
        return;
      }
      clipOpener.execute(() -> {
        // A line prepared in the meantime is cheaper than opening another one.
        Clip opened = pollIdleClip();
        if (opened == null) {
          opened = openClip();
        }
        if (opened == null) {
          finish();
        } else if (!begin(opened)) {
          releaseClip(opened);
        }
      });
    }

    /**
     * Starts this run on a line, unless it was stopped in the meantime.
     *
     * @param onLine the line to play on
     * @return whether the line was taken over by this run
     */
    private boolean begin(Clip onLine) {
      synchronized (Sound.this) {
        if (playing != this) {
          return false;
        }
        this.clip = onLine;
        onLine.addLineListener(this);
        onLine.setFramePosition(0);
        applyVolume(onLine);
        onLine.start();
        return true;
      }
    }

    private void stop() {
      Clip onLine;
      synchronized (Sound.this) {
        onLine = this.clip;
      }
      if (onLine != null) {
        onLine.stop();
      }
      // A line that is not going does not report a stop, and a run still waiting
      // for one has nothing to report at all, so both end here instead of in the
      // listener.
      if (onLine == null || !onLine.isRunning()) {
        finish();
      }
    }

    @Override
    public void update(LineEvent event) {
      if (event.getType() == LineEvent.Type.STOP) {
        finish();
      }
    }

    /** Ends this run, at most once, and frees its line for the next one. */
    private void finish() {
      Clip onLine;
      synchronized (Sound.this) {
        if (playing != this) {
          return;
        }
        playing = null;
        onLine = this.clip;
        this.clip = null;
      }
      if (onLine != null) {
        onLine.removeLineListener(this);
        releaseClip(onLine);
      }
    }
  }

  public Sound(String name, String soundPath) {
    this.name = name;
    this.path = soundPath;
    this.samples = loadSound(soundPath);
    this.prepareLine();
  }

  public Sound(Sound s) {
    this.name = s.name;
    this.path = s.path;
    this.samples = s.samples;
    this.volume = s.volume;
    this.prepareLine();
  }

  /**
   * Gets a line ready in the background, so that this sound object can start
   * playing without a pause. Every sound object asks for one line, which is what
   * makes ten sprites with the same sound end up with ten lines.
   */
  private void prepareLine() {
    if (samples == null || samples.data().length == 0) {
      return;
    }
    clipOpener.execute(() -> {
      Clip clip = openClip();
      if (clip != null) {
        releaseClip(clip);
      }
    });
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
      AssetErrorReporter.reportUnknownBuiltinAndFail(pathOrBuiltin, "sound");
    }
    return new Sound(name, pathOrBuiltin);
  }

  /**
   * Decodes a sound file into memory, or returns it if that already happened.
   *
   * <p>
   * Only the samples are cached, not a playable line, so the same file can be
   * played by any number of objects at the same time.
   *
   * @param path the path to the sound file
   * @return the decoded audio
   */
  public static Samples loadSound(String path) {
    Samples cached = soundCache.get(path);
    if (cached != null) {
      return cached;
    }
    try {
      AudioInputStream audioInput = openStream(Applet.getPath(path));
      audioInput = toPlayableStream(audioInput);
      Samples samples = new Samples(audioInput.getFormat(), readSamples(audioInput));
      audioInput.close();
      if (samples.data().length == 0) {
        System.err.println("\n==============================================");
        System.err.println("WARNING: Sound contains no audio!");
        System.err.println("==============================================");
        System.err.println("Path: " + path);
        System.err.println("\nThe file was read but decoded to zero samples,");
        System.err.println("so playing it will not be audible.");
        System.err.println("\nTip: Re-export the file, or convert it to WAV.");
        System.err.println("==============================================\n");
      }
      soundCache.put(path, samples);
      return samples;
    } catch (IOException e) {
      AssetErrorReporter.reportAndFail(
          "sound", path, "WAV, AIFF, AU, OGG",
          new String[] { ".wav", ".aiff", ".au", ".ogg" });
      return null;
    } catch (UnsupportedAudioFileException e) {
      System.err.println("\n==============================================");
      System.err.println("ERROR: Unsupported audio file format!");
      System.err.println("==============================================");
      System.err.println("Path: " + path);
      System.err.println("\nSupported formats: WAV, AIFF, AU, OGG");
      System.err.println("\nTip: Convert your audio file to WAV or OGG");
      System.err.println("     format using an audio conversion tool.");
      System.err.println("==============================================\n");
      throw new ScratchException("Unsupported audio format: " + path);
    }
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

  /** How many empty reads in a row are taken as the end of a stream. */
  private static final int MAX_EMPTY_READS = 20;

  /**
   * Reads a whole sound stream into memory.
   *
   * <p>
   * The Ogg decoder returns 0 bytes now and then while it refills its buffer, so
   * an empty read is not the end of the stream. It is only taken as one after
   * {@link #MAX_EMPTY_READS} in a row, which keeps a decoder that gets stuck
   * from hanging the program while it loads. The result is truncated to whole
   * frames, because that is what
   * {@link Clip#open(AudioFormat, byte[], int, int)} requires.
   *
   * @param in the stream to read
   * @return the raw samples
   * @throws IOException if the stream cannot be read
   */
  static byte[] readSamples(AudioInputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buffer = new byte[8192];
    int emptyReads = 0;
    while (true) {
      int read = in.read(buffer);
      if (read == -1) {
        break;
      }
      if (read == 0) {
        if (++emptyReads > MAX_EMPTY_READS) {
          break;
        }
        continue;
      }
      emptyReads = 0;
      out.write(buffer, 0, read);
    }

    byte[] data = out.toByteArray();
    int frameSize = in.getFormat().getFrameSize();
    if (frameSize > 0 && data.length % frameSize != 0) {
      byte[] whole = new byte[data.length - data.length % frameSize];
      System.arraycopy(data, 0, whole, 0, whole.length);
      return whole;
    }
    return data;
  }

  public String getName() {
    return name;
  }

  /**
   * Plays the sound. A single sound object plays one sound at a time, but other
   * objects holding the same sound are unaffected and can play it alongside.
   */
  public void play() {
    if (samples == null || samples.data().length == 0) {
      return;
    }

    Playback playback;
    synchronized (this) {
      if (playing != null) {
        return;
      }
      playback = new Playback();
      playing = playback;
    }
    playback.start();
  }

  /** Takes a line that is waiting for reuse, or null if there is none. */
  private Clip pollIdleClip() {
    BlockingQueue<Clip> idle = idleClips.get(path);
    return idle == null ? null : idle.poll();
  }

  /**
   * How many lines this sound may hold at once. A short sound effect gets the
   * full {@link #MAX_CLIPS_PER_SOUND}, a long one fewer, so that a piece of
   * music does not fill memory with copies of itself.
   */
  private int maxClips() {
    long affordable = MAX_CLIP_MEMORY_PER_SOUND / Math.max(1, samples.data().length);
    return (int) Math.max(1, Math.min(MAX_CLIPS_PER_SOUND, affordable));
  }

  /**
   * Opens another line for this sound, unless the sound already holds as many as
   * it may. An extra play beyond that is simply dropped.
   *
   * @return the line, or null if none could be opened
   */
  private Clip openClip() {
    AtomicInteger open = openClips.computeIfAbsent(path, key -> new AtomicInteger());
    if (open.incrementAndGet() > maxClips()) {
      open.decrementAndGet();
      return null;
    }

    try {
      Clip clip = AudioSystem.getClip();
      clip.open(samples.format(), samples.data(), 0, samples.data().length);
      return clip;
    } catch (LineUnavailableException | IllegalArgumentException e) {
      open.decrementAndGet();
      printAudioUnavailableWarning();
      return null;
    }
  }

  /** Hands a line back for reuse, or closes it if enough are already kept. */
  private void releaseClip(Clip clip) {
    clip.setFramePosition(0);
    BlockingQueue<Clip> idle = idleClips.computeIfAbsent(
        path, key -> new ArrayBlockingQueue<>(MAX_CLIPS_PER_SOUND));
    if (!idle.offer(clip)) {
      clip.close();
      openClips.get(path).decrementAndGet();
    }
  }

  private static synchronized void printAudioUnavailableWarning() {
    if (audioWarningPrinted) {
      return;
    }
    audioWarningPrinted = true;
    System.err.println("\n==============================================");
    System.err.println("WARNING: Audio system unavailable!");
    System.err.println("==============================================");
    System.err.println("A sound could not be played.");
    System.err.println("\nPossible reasons:");
    System.err.println("  1. No audio output device is available");
    System.err.println("  2. Audio device is being used by another program");
    System.err.println("\nTip: Check your system's audio settings.");
    System.err.println("==============================================\n");
  }

  public void pause() {
    this.stop();
  }

  public void stop() {
    Playback current = playing;
    if (current != null) {
      current.stop();
    }
  }

  public boolean isPlaying() {
    return playing != null;
  }

  public void setVolume(double amp) {
    volume = Math.max(0, Math.min(1, amp));
    Playback current = playing;
    Clip onLine = current == null ? null : current.clip;
    if (onLine != null) {
      applyVolume(onLine);
    }
  }

  private void applyVolume(Clip clip) {
    if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
      return;
    }
    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
    float dB = (float) (20.0 * Math.log10(volume <= 0 ? 0.0001 : volume));
    gainControl.setValue(Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB)));
  }

  public void changeVolume(double step) {
    setVolume(volume + step);
  }

  public double getVolume() {
    return volume;
  }
}
