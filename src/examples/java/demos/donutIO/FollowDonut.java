package demos.donutIO;

public class FollowDonut extends Donut {

  private PlayerDonut player;

  public FollowDonut(PlayerDonut player) {
    this.player = player;

    // scratch4j:begin setup (managed by the stage designer)
    // scratch4j:end setup
  }

  public void run() {
    var v = player.getPosition().sub(this.getPosition()).unitVector();
    this.setPosition(this.getPosition().add(v).multiply(this.speed));
    super.run();
  }
}
