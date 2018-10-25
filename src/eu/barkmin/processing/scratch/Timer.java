package eu.barkmin.processing.scratch;

public class Timer {
    private int start;
    private int lastTime;

    public Timer () {
       this.start = ScratchStage.parent.millis();
       this.lastTime = this.start;
    }

    public int getMillis() {
       return ScratchStage.parent.millis() - start;
    }

    public boolean everyMillis(int millis) {
        if (ScratchStage.parent.millis() - lastTime > millis) {
            lastTime = ScratchStage.parent.millis();
            return true;
        }
        return false;
    }

    public void reset() {
        this.start = ScratchStage.parent.millis();
        this.lastTime = this.start;
    }
}
