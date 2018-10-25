package eu.barkmin.processing.scratch;

public class Timer {
    private int start;

    public Timer () {
       this.start = ScratchStage.parent.millis();
    }

    public int getMillis() {
       return ScratchStage.parent.millis() - start;
    }

    public boolean everyMillis(int millis) {
        return this.getMillis() % millis == 0;
    }

    public void reset() {
        this.start = ScratchStage.parent.millis();
    }
}
