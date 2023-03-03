package org.openpatch.scratch;

public class Timer {
    private int startMillisEvery;
    private int startMillisFor;
    private int startMillisAfter;
    private int startMillisInterval;
    private int currentInterval;

    public Timer() {
        this.reset();
    }

    public void reset() {
        this.startMillisEvery = -1;
        this.startMillisFor = -1;
        this.startMillisAfter = -1;
        this.startMillisInterval = -1;
        this.currentInterval = 0;
    }

    private int millis() {
        return Applet.getInstance().millis();
    }

    public boolean everyMillis(int millis) {
        int nowMillis = millis();
        if (startMillisEvery < 0) {
            startMillisEvery = nowMillis;
        }
        if (nowMillis >= startMillisEvery + millis) {
            startMillisEvery = nowMillis;
            return true;
        }
        return false;
    }

    public boolean forMillis(int millis) {
        int nowMillis = millis();
        if (startMillisFor < 0) {
            startMillisFor = nowMillis;
        }
        return nowMillis < startMillisFor + millis;
    }

    public boolean afterMillis(int millis) {
        int nowMillis = millis();
        if (startMillisAfter < 0) {
            startMillisAfter = nowMillis; 
        }
        return nowMillis >= startMillisAfter + millis;
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

    public boolean intervalMillis(int millis1, int millis2, boolean skipFirst) {
        int nowMillis = millis();

        if (startMillisInterval < 0) {
            startMillisInterval = nowMillis;
            if (skipFirst) {
                startMillisInterval -= millis1;
            }
        }

        if (currentInterval == 0 && nowMillis < startMillisInterval + millis1) {
            return true;
        } else if (currentInterval == 0) {
            currentInterval = 1;
            startMillisInterval = nowMillis;
            return false;
        } else if (currentInterval == 1 && nowMillis < startMillisInterval + millis2) {
            return false;
        } else {
            currentInterval = 0;
            startMillisInterval = nowMillis;
            return true;
        }
    }
}
