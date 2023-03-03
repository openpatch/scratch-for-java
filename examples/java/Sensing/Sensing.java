import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class Sensing extends Stage {
    public static Hero h, m;

    public Sensing() {
        super(800, 800);
        Window.getInstance().setDebug(true);
        h = new Hero();
        m = new MovableHero();
        this.add(h);
        this.add(m);
    }

    public void run() {
        this.display("Move the hero with WASD and rotate him with R");
    }

    public static void main(String[] args) {
        new Sensing();
    }

}

class Hero extends Sprite {
    public Hero() {
        super("hero", "sprites/hero.png");
        this.addCostume("hero2", "sprites/hero2.png");
        this.setSize(50);
        this.setDirection(45);
        this.move(280);

        int[] xHitbox = { 0, 300, 300, 0, 150 };
        int[] yHitbox = { 0, 0, 570, 570, 275 };
        this.setHitbox(xHitbox, yHitbox);
    }

    public void run() {
        if (this.isTouchingMousePointer()) {
            this.switchCostume("hero2");
        } else {
            this.switchCostume("hero");
        }
    }

}

class MovableHero extends Hero {
    public MovableHero() {
        super();
        this.setPosition(332, 578);
        this.setDirection(270);
    }

    public void run() {
        if (isKeyPressed(65)) {
            this.changeX(-1);
            ;
        }
        if (isKeyPressed(68)) {
            this.changeX(1);
        }
        if (isKeyPressed(87)) {
            this.changeY(-1);
        }
        if (isKeyPressed(83)) {
            this.changeY(1);
        }
        if (isKeyPressed(82)) {
            this.turnRight(1);
        }
        if (isTouchingSprite(Sensing.h)) {
            this.say("Hit");
        } else {
            this.say(null);
        }
    }
}
