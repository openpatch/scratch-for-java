package org.openpatch.scratch.extensions.recorder;

import org.openpatch.scratch.Color;
import org.openpatch.scratch.internal.AnimatedGifEncoder;
import org.openpatch.scratch.internal.Applet;
import processing.core.PImage;

/**
 * The GifRecorder class is responsible for recording and saving frames as GIF images. It extends
 * the Recorder class and utilizes the AnimatedGifEncoder to create GIF files.
 *
 * <p>The class provides methods to start and stop the recording process, save individual frames,
 * and capture snapshots as GIF images.
 *
 * <p>The GifRecorder class handles potential NullPointerExceptions that may occur during the frame
 * saving process.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * GifRecorder recorder = new GifRecorder("output.gif");
 * recorder.start();
 * // Capture frames
 * recorder.stop();
 * }</pre>
 *
 * @see Recorder
 * @see AnimatedGifEncoder
 */
public class GifRecorder extends Recorder {

  private AnimatedGifEncoder gifEncoder;

  /** The transparent color used in the GIF image. */
  public static Color transparent = new Color(254, 1, 154);

  /**
   * Constructs a new GifRecorder object.
   *
   * @param filePath the path where the GIF file will be saved
   */
  public GifRecorder(String filePath) {
    super(filePath, ".gif");
    this.gifEncoder = new AnimatedGifEncoder();
  }

  /**
   * Saves the current frame of the stage as a GIF image.
   *
   * <p>This method captures the stage at its own size, sets the frame rate, and adds the frame to
   * the GIF encoder. A window that is bigger than the stage (maximized, say) records the stage
   * without the bars around it, and the frames keep their size when the window changes during the
   * recording.
   *
   * <p>Note: This method catches and ignores any NullPointerException that may occur.
   */
  @Override
  public void saveFrame() {
    try {
      Applet applet = Applet.getInstance();
      PImage stage = applet.getStageImage();
      stage.loadPixels();
      if (stage.pixels != null) {
        this.gifEncoder.setFrameRate(applet.frameRate);
        this.gifEncoder.setRepeat(0);
        this.gifEncoder.addFrame(stage.pixels, stage.pixelWidth, stage.pixelHeight);
      }
    } catch (NullPointerException e) {

    }
  }

  /**
   * Captures a snapshot and saves it as a GIF. This method starts the GIF encoder, saves the
   * current frame, and then finishes the encoding process.
   */
  public void snapshot() {
    this.gifEncoder.start(this.path);
    this.saveFrame();
    this.gifEncoder.finish();
  }

  /** Starts the GIF recording process. */
  @Override
  public void start() {
    super.start();
    this.gifEncoder.start(this.path);
  }

  /** Stops the GIF recording process and finalizes the GIF file. */
  @Override
  public void stop() {
    // Frames are added from the render thread, so recording has to be switched
    // off before the trailer is written. The other way round, a frame arriving
    // between finish() and the flag being cleared is appended after the end of
    // the file, and the last frame of the GIF is truncated.
    super.stop();
    this.gifEncoder.finish();
  }
}
