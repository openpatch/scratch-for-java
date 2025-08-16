package demos.donutIO;

public class PlayerDonut extends Donut {

  public PlayerDonut() {
    super(0, 0, 10);
  }

  public void run() {
    var v = this.getMouse();
    this.setPosition(
        v.sub(this.getPosition()).unitVector().multiply(this.speed).add(this.getPosition()));

    super.run();
  }
}
