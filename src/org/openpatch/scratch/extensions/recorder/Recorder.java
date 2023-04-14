package org.openpatch.scratch.extensions.recorder;

import org.openpatch.scratch.internal.Applet;

public abstract class Recorder {
  protected String path;
  protected boolean recording;

  public Recorder(String path, String ext) {
    if (!path.endsWith(ext)) {
      path += ext;
    }
    this.path = path;
    Applet.getInstance().registerMethod("post", this);
  }

  public void post() {
    if (this.recording) {
      this.saveFrame();
    }
  }

  public abstract void saveFrame();

  public void start() {
    this.recording = true;
    // wait for last frame to draw before starting
  }

  public void stop() {
    this.recording = false;
  }
}
