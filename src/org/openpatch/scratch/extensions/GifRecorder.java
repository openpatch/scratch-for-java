package org.openpatch.scratch.extensions;

import org.openpatch.scratch.Color;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.internal.AnimatedGifEncoder;

public class GifRecorder extends Recorder {

    private AnimatedGifEncoder gifEncoder;
    public static Color transparent = new Color(254, 1, 154);

    public GifRecorder(String filePath) {
        super(filePath, ".gif");
        this.gifEncoder = new AnimatedGifEncoder();
    }

    @Override
    public void saveFrame() {
        Stage.parent.loadPixels();
        int[] pixels = Stage.parent.pixels;
        if (pixels != null) {
            this.gifEncoder.setFrameRate(Stage.parent.frameRate);
            this.gifEncoder.setRepeat(0);
            this.gifEncoder.addFrame(pixels, Stage.parent.pixelWidth, Stage.parent.pixelHeight);
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
