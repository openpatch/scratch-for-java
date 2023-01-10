package org.openpatch.scratch.extensions;

import org.openpatch.scratch.Stage;

import java.nio.file.Paths;

public class FrameRecorder extends Recorder {

    private String outputFolder;

    public FrameRecorder(String outputFolder) {
        super(outputFolder, ".png");
        this.outputFolder = outputFolder;
    }

    @Override
    public void saveFrame() {
        Stage.parent.saveFrame(Paths.get(this.outputFolder, "#######.png").toString());
    }

    public void snapshot(String filename) {
        Stage.parent.save(Paths.get(this.outputFolder, filename).toString());
    }
}
