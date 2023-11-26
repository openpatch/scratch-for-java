package DonutIO;

public class FollowDonut extends Donut {

  private PlayerDonut player;

  public FollowDonut(PlayerDonut player) {
    this.player = player;
  }

  public void run() {
    var v = player.getPosition().sub(this.getPosition()).unitVector();
    this.setPosition(this.getPosition().add(v).multiply(this.speed));
    super.run();
  }
}
