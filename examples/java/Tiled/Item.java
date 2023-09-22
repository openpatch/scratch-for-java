import org.openpatch.scratch.*;

public class Item extends Sprite {
    private double mapX;
    private double mapY;
    private String name;
    
    // Constructor for displaying the inventory
    public Item() {
        this.addCostume("scroll-water", "assets/ScrollWater.png");
        this.addCostume("scroll-fire", "assets/ScrollFire.png");
        this.hide();
    }

    // Constructor for displaying an item on the map
    public Item(String name, double mapX, double mapY) {
        this();
        
        this.show();
        this.name = name;
        this.mapX = mapX;
        this.mapY = mapY;
        
        this.run();

        this.switchCostume(name);
    }

    public void collect() {
        GameState.get().items.add(name);
        // auto-save when an item is collected
        GameState.get().save();
        this.hide();
        this.getStage().display(I18n.get("item-found", I18n.get(name)), 2000);
    
        if (name.equals("scroll-fire")) {
            this.getStage().display(I18n.get("tutorial-scroll-fire"), 2000);
        }
    }

    public String getName() {
        return name;
    }

    public void run() {
        this.setX(this.mapX - GameState.get().camX);
        this.setY(this.mapY - GameState.get().camY);
    }
}