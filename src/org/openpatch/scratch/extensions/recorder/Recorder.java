package org.openpatch.scratch.extensions.recorder;

import org.openpatch.scratch.internal.Applet;

/**
 * The Recorder class provides a framework for recording frames to a file.
 * It includes methods to start and stop the recording process, and an abstract
 * method saveFrame() that must be implemented by subclasses to define the
 * specific behavior for saving a frame.
 *
 * <p>This class is intended to be extended by other classes that provide
 * concrete implementations for saving frames in different formats.
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * public class MyRecorder extends Recorder {
 *     public MyRecorder(String path, String ext) {
 *         super(path, ext);
 *     }
 *
 *     @Override
 *     public void saveFrame() {
 *         // Implementation for saving a frame
 *     }
 * }
 * }
 * </pre>
 *
 * <p>Note: The Recorder class uses the Applet class to register a method
 * that is called after each frame is drawn. This ensures that frames are
 * saved at the appropriate time during the recording process.
 *
 * @see Applet
 */
public abstract class Recorder {
  protected String path;
  protected boolean recording;

  /**
   * Constructs a new Recorder object with the specified file path and extension.
   * If the provided path does not end with the specified extension, the extension
   * is appended to the path.
   *
   * @param path the file path where the recording will be saved
   * @param ext the file extension to be used for the recording
   */
  public Recorder(String path, String ext) {
    if (!path.endsWith(ext)) {
      path += ext;
    }
    this.path = path;
    Applet.getInstance().registerMethod("post", this);
  }

  /**
   * Called after each frame is drawn. This method is registered with the Applet
   * class to ensure that frames are saved at the appropriate time during the
   * recording process.
   */
  public void post() {
    if (this.recording) {
      this.saveFrame();
    }
  }

  /**
   * Saves the current frame. This method should be implemented by subclasses
   * to define the specific behavior for saving a frame.
   */
  public abstract void saveFrame();

  /**
   * Starts the recording process.
   */
  public void start() {
    this.recording = true;
    // wait for last frame to draw before starting
  }

  /**
   * Stops the recording process.
   */
  public void stop() {
    this.recording = false;
  }
}
