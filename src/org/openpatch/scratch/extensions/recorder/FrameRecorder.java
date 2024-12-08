package org.openpatch.scratch.extensions.recorder;

import java.nio.file.Paths;
import org.openpatch.scratch.internal.Applet;

/**
 * The FrameRecorder class extends the Recorder class to provide functionality
 * for saving frames of an applet as PNG files. It allows saving the current frame
 * with a generated filename based on the frame number or taking a snapshot with
 * a specified filename.
 *
 * <p>Usage example:
 * <pre>
 * FrameRecorder recorder = new FrameRecorder("/path/to/output/folder");
 * recorder.saveFrame(); // Saves the current frame with a generated filename
 * recorder.snapshot("snapshot.png"); // Saves the current frame as "snapshot.png"
 * </pre>
 */
public class FrameRecorder extends Recorder {

  private String outputFolder;

  /**
   * Constructs a new FrameRecorder with the specified output folder.
   *
   * @param outputFolder the folder where the recorded frames will be saved
   */
  public FrameRecorder(String outputFolder) {
    super(outputFolder, ".png");
    this.outputFolder = outputFolder;
  }

  /**
   * Saves the current frame of the applet to the specified output folder.
   * The frame is saved as a PNG file with a filename pattern "#######.png".
   * The exact filename will be generated based on the current frame number.
   */
  @Override
  public void saveFrame() {
    Applet.getInstance().saveFrame(Paths.get(this.outputFolder, "#######.png").toString());
  }

  /**
   * Takes a snapshot of the current frame and saves it to the specified filename.
   *
   * @param filename the name of the file to save the snapshot to
   */
  public void snapshot(String filename) {
    Applet.getInstance().save(Paths.get(this.outputFolder, filename).toString());
  }
}
