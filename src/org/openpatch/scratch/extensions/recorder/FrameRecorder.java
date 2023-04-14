package org.openpatch.scratch.extensions.recorder;

import java.nio.file.Paths;
import org.openpatch.scratch.internal.Applet;

public class FrameRecorder extends Recorder {

  private String outputFolder;

  public FrameRecorder(String outputFolder) {
    super(outputFolder, ".png");
    this.outputFolder = outputFolder;
  }

  @Override
  public void saveFrame() {
    Applet.getInstance().saveFrame(Paths.get(this.outputFolder, "#######.png").toString());
  }

  public void snapshot(String filename) {
    Applet.getInstance().save(Paths.get(this.outputFolder, filename).toString());
  }
}
