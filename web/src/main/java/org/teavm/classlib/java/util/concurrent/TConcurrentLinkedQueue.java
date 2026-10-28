package org.teavm.classlib.java.util.concurrent;

import java.util.concurrent.ArrayBlockingQueue;

/** TeaVM's class library has no ConcurrentLinkedQueue: a roomy ArrayBlockingQueue, never blocked on. */
public class TConcurrentLinkedQueue<E> extends ArrayBlockingQueue<E> {
  public TConcurrentLinkedQueue() {
    super(1 << 16);
  }
}
