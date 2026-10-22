package org.openpatch.scratch.recorder;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.openpatch.scratch.extension.ExtensionContext;

/**
 * Records the stage as an animated GIF: every second frame of game time (30 a second), at stage
 * size, texts and the UI included.
 *
 * <pre>{@code
 * GifRecorder recorder = new GifRecorder("recording.gif");
 * recorder.start();
 * // … later
 * recorder.stop();
 * }</pre>
 */
public class GifRecorder {

  /** Recorders that are recording; the extension hands them every frame. */
  static final List<GifRecorder> RECORDING = new CopyOnWriteArrayList<>();

  private final String path;
  private GifEncoder encoder;
  private long lastTick = Long.MIN_VALUE;
  private volatile boolean snapshot;

  /**
   * @param path the GIF file to write; {@code .gif} is added when missing
   */
  public GifRecorder(String path) {
    this.path = path.toLowerCase(java.util.Locale.ROOT).endsWith(".gif") ? path : path + ".gif";
  }

  /** Starts recording from the next frame on. */
  public synchronized void start() {
    if (this.encoder != null) {
      return;
    }
    this.encoder = new GifEncoder();
    this.encoder.setRepeat(0);
    this.encoder.setDelay(1000 / 30);
    this.encoder.start(this.path);
    this.lastTick = Long.MIN_VALUE;
    RECORDING.add(this);
  }

  /** Stops recording and finishes the file. */
  public synchronized void stop() {
    // off the list first: a frame arriving after the trailer would break the file
    RECORDING.remove(this);
    if (this.encoder != null) {
      this.encoder.finish();
      this.encoder = null;
    }
  }

  /** Saves the next frame as a GIF of one frame. */
  public synchronized void snapshot() {
    this.snapshot = true;
    this.start();
  }

  /** Window thread: one drawn frame. */
  synchronized void frame(ExtensionContext.Frame frame, long tick) {
    if (this.encoder == null || tick < this.lastTick + 2) {
      return;
    }
    this.lastTick = tick;
    this.encoder.addFrame(frame.getArgb(), frame.getWidth(), frame.getHeight());
    if (this.snapshot) {
      this.snapshot = false;
      this.stop();
    }
  }
}
