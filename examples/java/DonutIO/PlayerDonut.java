public class PlayerDonut extends Donut {

  public PlayerDonut() {
    super(0, 0, 10);
  }

  public void run() {
    var v = this.getMouse().unitVector();

    this.setMapPosition(this.mapPosition.add(v).multiply(this.speed));

    super.run();

  }
}
