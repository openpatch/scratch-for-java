package DonutIO;
public class FollowDonut extends Donut {

  private PlayerDonut player;

  public FollowDonut(PlayerDonut player) {
    this.player = player;
  }

  public void run() {
    var v = player.mapPosition.sub(this.mapPosition).unitVector();

    this.setMapPosition(this.mapPosition.add(v).multiply(this.speed));

    super.run();
  }
}
