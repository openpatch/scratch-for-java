package eu.barkmin.processing.scratch;

public class Timer {
    private int nextFrame;

    public Timer () {
        this.nextFrame = -1;
    }

    public int getMillis() {
       return Math.round(ScratchStage.parent.frameCount / ScratchStage.parent.frameRate * 1000);
    }

    public boolean everyMillis(int millis) {
        int frameSkip = this.getFrameFromMillis(millis);
        if (nextFrame < 0) {
            nextFrame = ScratchStage.parent.frameCount + frameSkip;
        }
        if (ScratchStage.parent.frameCount >= nextFrame) {
            nextFrame = ScratchStage.parent.frameCount + frameSkip;
            return true;
        }
        return false;
    }

    private int getFrameFromMillis(int millis) {
        return (int) Math.round(millis * ScratchStage.parent.frameRate / 1000.0);
    }

    public void reset() {
        this.nextFrame = -1;
    }
}
