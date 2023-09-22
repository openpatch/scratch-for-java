import org.openpatch.scratch.*;

public class Water extends Sprite {
    private double mapX;
    private double mapY;

    public Water(double mapX, double mapY) {
        this.mapX = mapX;
        this.mapY = mapY;
    }

    public void run() {
        this.setX(this.mapX - GameState.get().camX);
        this.setY(this.mapY - GameState.get().camY);
    }

}
