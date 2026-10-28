package org.teavm.classlib.java.util.concurrent;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * TeaVM's class library has no LinkedBlockingQueue, and its LinkedBlockingDeque does not block;
 * its ArrayBlockingQueue does. TeaVM uses this class wherever the library says
 * java.util.concurrent.LinkedBlockingQueue.
 */
public class TLinkedBlockingQueue<E> extends ArrayBlockingQueue<E> {
  public TLinkedBlockingQueue() {
    super(1 << 12);
  }
}
