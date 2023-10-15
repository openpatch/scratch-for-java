package DonutIO;

public class PlayerDonut extends Donut {

  public PlayerDonut() {
    super(0, 0, 10);
  }

  public void run() {
    var v = this.getMouse().unitVector();
    this.setPosition(
        this.getPosition()
            .add(v)
            .multiply(this.speed));

    super.run();
  }
}
