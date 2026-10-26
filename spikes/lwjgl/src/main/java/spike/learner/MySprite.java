package spike.learner;

import spike.GameLoopCheck.Canvas;

/**
 * Stands in for learner code: moves in a circle and draws with its pen. With
 * {@code -Dspike.hangAt=N} its {@code run()} never returns from tick N on, so
 * the watchdog has something to find.
 */
public class MySprite {

  public double x, y, direction;
  private final long hangAt = Long.getLong("spike.hangAt", -1);
  private volatile long spins;

  public void run(long tick, Canvas pen) {
    double lastX = this.x, lastY = this.y;
    this.direction += 3;
    this.x = Math.cos(Math.toRadians(this.direction)) * 120;
    this.y = Math.sin(Math.toRadians(this.direction)) * 80;
    if (tick > 0) {
      pen.line(lastX, lastY, this.x, this.y, 4, 0xff3355dd);
    }
    if (tick % 240 == 0) {
      pen.eraseAll();
    }
    if (tick == this.hangAt) {
      while (true) {
        this.spins++; // a loop that never ends
      }
    }
  }

  public void whenExits() {
    System.out.println("learner: whenExits ran on " + Thread.currentThread().getName());
  }
}
