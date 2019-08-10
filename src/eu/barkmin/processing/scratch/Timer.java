package eu.barkmin.processing.scratch;

public class Timer {
    private int nextFrameEvery;
    private int nextFrameFor;
    private int nextFrameAfter;
    private int nextFrameInterval;
    private int currentInterval;

    public Timer () {
        this.nextFrameEvery = -1;
        this.nextFrameFor = -1;
        this.nextFrameAfter = -1;
        this.nextFrameInterval = -1;
        this.currentInterval = 0;
    }

    public int getMillis() {
       return Math.round(ScratchStage.parent.frameCount / ScratchStage.parent.frameRate * 1000);
    }

    public boolean everyMillis(int millis) {
        int frameSkip = this.getFrameFromMillis(millis);
        if (nextFrameEvery < 0) {
            nextFrameEvery = ScratchStage.parent.frameCount + frameSkip;
        }
        if (ScratchStage.parent.frameCount >= nextFrameEvery) {
            nextFrameEvery = ScratchStage.parent.frameCount + frameSkip;
            return true;
        }
        return false;
    }

    public boolean forMillis(int millis) {
        int frameDuration = this.getFrameFromMillis(millis);
        if (nextFrameFor < 0) {
            nextFrameFor = ScratchStage.parent.frameCount + frameDuration;
        }
        return ScratchStage.parent.frameCount < nextFrameFor;
    }

    public boolean afterMillis(int millis) {
        int frameDuration = this.getFrameFromMillis(millis);
        if (nextFrameAfter < 0) {
            nextFrameAfter = ScratchStage.parent.frameCount + frameDuration;
        }
        return ScratchStage.parent.frameCount >= nextFrameAfter;
    }

    public boolean intervalMillis(int millis) {
        return this.intervalMillis(millis, millis, false);
    }

    public boolean intervalMillis(int millis, boolean skipFirst) {
        return this.intervalMillis(millis, millis, skipFirst);
    }

    public boolean intervalMillis(int millis1, int millis2) {
       return this.intervalMillis(millis1, millis2, false);
    }

    public boolean intervalMillis(int milli1, int millis2, boolean skipFirst) {
        int frameDuration1 = this.getFrameFromMillis(milli1);
        int frameDuration2 = this.getFrameFromMillis(millis2);

        if (skipFirst && nextFrameInterval < 0) {
            nextFrameInterval = ScratchStage.parent.frameCount + frameDuration1;
        } else if(!skipFirst && nextFrameInterval < 0) {
            currentInterval = 1;
            nextFrameInterval = ScratchStage.parent.frameCount + frameDuration2;
        }
        if(currentInterval == 0 && ScratchStage.parent.frameCount < nextFrameInterval) {
            return true;
        } else if (currentInterval == 0) {
            currentInterval = 1;
            nextFrameInterval = ScratchStage.parent.frameCount + frameDuration2;
            return false;
        } else if (currentInterval == 1 && ScratchStage.parent.frameCount < nextFrameInterval) {
            return false;
        } else {
            currentInterval = 0;
            nextFrameInterval = ScratchStage.parent.frameCount + frameDuration1;
            return true;
        }
    }

    private int getFrameFromMillis(int millis) {
        return (int) Math.round(millis * ScratchStage.parent.frameRate / 1000.0);
    }
}
