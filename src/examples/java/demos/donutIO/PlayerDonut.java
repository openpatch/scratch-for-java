package demos.donutIO;

public class PlayerDonut extends Donut {

  public PlayerDonut() {
    super(0, 0, 10);

    // scratch4j:begin setup (managed by the stage designer)
    // scratch4j:end setup
  }

  public void run() {
    var v = this.getMouse();
    this.setPosition(
        v.sub(this.getPosition()).unitVector().multiply(this.speed).add(this.getPosition()));

    super.run();
  }
}
