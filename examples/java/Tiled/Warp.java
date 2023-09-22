import org.openpatch.scratch.*;

public class Warp extends Sprite {
    private double mapX;
    private double mapY;
    private String toMap;
    private double toX;
    private double toY;
    private World world;

    public Warp(World world, double mapX, double mapY, String toMap, double toX, double toY) {
        this.world = world;
        this.mapX = mapX;
        this.mapY = mapY;
        this.toMap = toMap;
        this.toX = toX;
        this.toY = toY;

        this.setX(this.mapX - GameState.get().camX);
        this.setY(this.mapY - GameState.get().camY);
    }

    public void run() {

        if (this.isTouchingSprite(World.PLAYER)) {
            this.world.loadMap(toMap);
            World.PLAYER.setMapX(toX);
            World.PLAYER.setMapY(toY);
        }

        this.setX(this.mapX - GameState.get().camX);
        this.setY(this.mapY - GameState.get().camY);
    }

}
