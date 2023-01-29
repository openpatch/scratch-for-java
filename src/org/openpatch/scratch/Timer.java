package org.openpatch.scratch;

public class Timer {
    private int startFrameEvery;
    private int startFrameFor;
    private int startFrameAfter;
    private int startFrameInterval;
    private int currentInterval;

    public Timer() {
        this.startFrameEvery = -1;
        this.startFrameFor = -1;
        this.startFrameAfter = -1;
        this.startFrameInterval = -1;
        this.currentInterval = 0;
    }

    public int getMillis() {
        return Math.round(Stage.parent.frameCount / Stage.parent.frameRate * 1000);
    }

    public boolean everyMillis(int millis) {
        int frameSkip = this.getFrameFromMillis(millis);
        if (startFrameEvery < 0) {
            startFrameEvery = Stage.parent.frameCount;
        }
        if (Stage.parent.frameCount >= startFrameEvery + frameSkip) {
            startFrameEvery = Stage.parent.frameCount;
            return true;
        }
        return false;
    }

    public boolean forMillis(int millis) {
        int frameDuration = this.getFrameFromMillis(millis);
        if (startFrameFor < 0) {
            startFrameFor = Stage.parent.frameCount;
        }
        return Stage.parent.frameCount < startFrameFor + frameDuration;
    }

    public boolean afterMillis(int millis) {
        int frameDuration = this.getFrameFromMillis(millis);
        if (startFrameAfter < 0) {
            startFrameAfter = Stage.parent.frameCount;
        }
        return Stage.parent.frameCount >= startFrameAfter + frameDuration;
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

        if (startFrameInterval < 0) {
            startFrameInterval = Stage.parent.frameCount;
            if (skipFirst) {
                startFrameInterval -= frameDuration1;
            }
        }

        if (currentInterval == 0 && Stage.parent.frameCount < startFrameInterval + frameDuration1) {
            return true;
        } else if (currentInterval == 0) {
            currentInterval = 1;
            startFrameInterval = Stage.parent.frameCount;
            return false;
        } else if (currentInterval == 1 && Stage.parent.frameCount < startFrameInterval + frameDuration2) {
            return false;
        } else {
            currentInterval = 0;
            startFrameInterval = Stage.parent.frameCount;
            return true;
        }
    }

    private int getFrameFromMillis(int millis) {
        return (int) Math.round(millis * Stage.parent.frameRate / 1000.0);
    }
}
