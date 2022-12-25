package org.openpatch.scratch.extensions;

import org.openpatch.scratch.Stage;

import java.nio.file.Paths;

public class Recorder {

    private String outputFolder;
    private String filePattern;
    private boolean recording;

    public Recorder(String outputFolder) {
        this.outputFolder = outputFolder;
        this.recording = false;

        Stage.parent.registerMethod("post", this);
    }

    public void post() {
        if (this.recording) {
            Stage.parent.saveFrame(Paths.get(this.outputFolder, this.filePattern).toString());
        }
    }

    public void start(String filePattern) {
        this.recording = true;
        this.filePattern = filePattern;
    }

    public void snapshot(String filename) {
        Stage.parent.save(Paths.get(this.outputFolder, filename).toString());
    }

    public void stop() {
        this.recording = false;
    }
}
