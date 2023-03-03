package org.openpatch.scratch.extensions;

import org.openpatch.scratch.Applet;

import java.nio.file.Paths;

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
