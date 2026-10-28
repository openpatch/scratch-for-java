package org.teavm.classlib.java.util.concurrent;

import java.util.concurrent.TimeUnit;

/** TeaVM's class library has no CountDownLatch: one on a monitor, which TeaVM's threads have. */
public class TCountDownLatch {

  private long count;

  public TCountDownLatch(int count) {
    this.count = count;
  }

  public synchronized void countDown() {
    if (this.count > 0 && --this.count == 0) {
      this.notifyAll();
    }
  }

  public synchronized long getCount() {
    return this.count;
  }

  public synchronized void await() throws InterruptedException {
    while (this.count > 0) {
      this.wait();
    }
  }

  public synchronized boolean await(long timeout, TimeUnit unit) throws InterruptedException {
    long until = System.currentTimeMillis() + unit.toMillis(timeout);
    while (this.count > 0) {
      long left = until - System.currentTimeMillis();
      if (left <= 0) {
        return false;
      }
      this.wait(left);
    }
    return true;
  }
}
