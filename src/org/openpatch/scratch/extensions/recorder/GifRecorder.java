package org.openpatch.scratch.extensions.recorder;

import org.openpatch.scratch.internal.AnimatedGifEncoder;
import org.openpatch.scratch.internal.Applet;
import org.openpatch.scratch.internal.Color;

public class GifRecorder extends Recorder {

  private AnimatedGifEncoder gifEncoder;
  public static Color transparent = new Color(254, 1, 154);

  public GifRecorder(String filePath) {
    super(filePath, ".gif");
    this.gifEncoder = new AnimatedGifEncoder();
  }

  @Override
  public void saveFrame() {
    try {
      Applet applet = Applet.getInstance();
      applet.loadPixels();
      int[] pixels = applet.pixels;
      if (pixels != null) {
        this.gifEncoder.setFrameRate(applet.frameRate);
        this.gifEncoder.setRepeat(0);
        this.gifEncoder.addFrame(pixels, applet.pixelWidth, applet.pixelHeight);
      }
    } catch (NullPointerException e) {

    }
  }

  public void snapshot() {
    this.gifEncoder.start(this.path);
    this.saveFrame();
    this.gifEncoder.finish();
  }

  @Override
  public void start() {
    super.start();
    this.gifEncoder.start(this.path);
  }

  @Override
  public void stop() {
    this.gifEncoder.finish();
    super.stop();
  }
}
