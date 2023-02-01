import org.openpatch.scratch.*;

public class HalloweenStage extends Stage
{    
    int numberPumpkins = 10;
    HouseSprite house;
    GhostSprite ghost;
    
    public HalloweenStage() {
        super(400, 400, Renderer.P2D);
        this.addBackdrop("bg", "sprites/background.jpg");
        this.addSound("bg", "sounds/background.wav");
        
        house = new HouseSprite();
        this.add(house);
    
        for (int i = 0; i < numberPumpkins; i++) {
            PumpkinSprite p = new PumpkinSprite();
            this.add(p);
        }
        ghost = new GhostSprite();
        this.add(ghost);
    }
    
    public void run() {
        this.playSound("bg");
    }
}
