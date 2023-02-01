import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class RainbowVine extends Stage {
    public RainbowVine() {
        super(800, 600);
        this.setColor(0,0,0);
        this.add(new VineSprite());
    }

    public static void main(String[] args) {
        new RainbowVine();
    }

}

class VineSprite extends Sprite {
    public VineSprite() {
        this.addCostume("vine", "sprites/vine.png");
        this.getPen().down();
        this.getPen().setSize(3);
        this.getPen().setColor(120);
        this.hide();
    }

    public void run() {
        this.setPosition(Stage.parent.mouseX, Stage.parent.mouseY);
        this.turnRight(5);

        if(this.getTimer().everyMillis(60)) {
            Stage.getInstance().add(new LeafSprite(this));
        }
    }

}

class LeafSprite extends Sprite {
    VineSprite vine;

    public LeafSprite(VineSprite vine) {
        super("leaf", "sprites/vine.png");
        this.vine = vine;
        this.getPen().down();
        this.getPen().setSize(2);
        this.setDirection(vine.getDirection());
        this.getPen().setColor(vine.getPen().getColor());
        vine.getPen().changeColor(2);
        this.setPosition(vine.getX(), vine.getY());
        this.hide();
    }

    public void run() {
        this.turnRight(5);
        this.move(5);
        this.getPen().changeSize(1);
        if (this.getTimer().afterMillis(200)) {
            Stage.getInstance().remove(this);
        }
    }
}
