package eu.barkmin.processing.scratch;

public class Timer {
    private int start;
    private int counter = 1;

    public Timer () {
       this.start = ScratchStage.parent.millis();
    }

    public int getMillis() {
       return ScratchStage.parent.millis() - start;
    }

    public boolean everyMillis(int millis) {
        if (this.getMillis() >= millis * counter) {
            counter++;
            return true;
        }
        return false;
    }

    public void start() {
        this.start = ScratchStage.parent.millis();
    }

    public void reset() {
        this.start = ScratchStage.parent.millis();
    }
}
