package eu.barkmin.processing.scratch;

import processing.core.PApplet;
import java.util.ArrayList;

public class ScratchSprite {
  private ArrayList<ScratchImage> costumes = new ArrayList<>();
  private int currentCostume = 0;
  private ArrayList<ScratchSound> sounds = new ArrayList<>();
  private boolean show = true;
  private float size = 100; 
  private boolean onEdgeBounce = false;
  private float rotation = 0;
  private float x = 0;
  private float y = 0;
  private ScratchPen pen;
  
  public ScratchSprite(String name, String imagePath) {
    ScratchImage costume = new ScratchImage(name, imagePath);
    this.costumes.add(costume);
    this.pen = new ScratchPen();
  }
  
  public ScratchSprite(ScratchSprite s) {
    this.costumes = new ArrayList<>();
    for (ScratchImage costume : s.costumes) {
      this.costumes.add(new ScratchImage(costume));
    }
    this.currentCostume = s.currentCostume;
    this.sounds = new ArrayList<>();
    for (ScratchSound sound : s.sounds) {
      this.sounds.add(new ScratchSound(sound));
    }
    this.show = s.show;
    this.size = s.size;
    this.onEdgeBounce = s.onEdgeBounce;
    this.rotation = s.rotation;
    this.x = s.x;
    this.y = s.y;
    this.pen = new ScratchPen(s.pen);
  }
  
  public void addCostume(String name, String imagePath) {
    this.costumes.add(new ScratchImage(name, imagePath));
  }
  
  public void switchCostume(String name) {
    for (int i = 0; i < costumes.size(); i++) {
      ScratchImage costume = costumes.get(i);
      if (costume.getName().equals(name)) {
        this.currentCostume = i;
        return;
      }
    }
  }
  
  public void nextCostume() {
    this.currentCostume = (this.currentCostume + 1) % costumes.size();
  }
  
  public String getCurrentCostumeName() {
    if (costumes.size() == 0) return null;
    
    return this.costumes.get(this.currentCostume).getName();
  }
  
  public int getCurrentCostumeIndex() {
    return this.currentCostume;
  }
  
  public void addSound(String name, String soundPath) {
    this.sounds.add(new ScratchSound(name, soundPath));
  }
  
  public void playSound(String name) {
    for (int i = 0; i < sounds.size(); i++) {
      ScratchSound sound = sounds.get(i);
      if (sound.getName().equals(name) && !sound.isPlaying()) {
        sound.play();
      }
    }
  }
  
  public void stopAllSounds() {
    for(ScratchSound sound : sounds) {
      sound.stop();
    }
  }
  
  public void setTint(int r, int g, int b) {
    if (costumes.size() == 0) return;
    
    this.costumes.get(this.currentCostume).setTint(r, g, b);
  }
  
  public void setOpacity(float opacity) {
    if (costumes.size() == 0) return;
    
    this.costumes.get(this.currentCostume).setOpacity(opacity);
  }
  
  public void hide() {
    this.show = false;
  }
  
  public void show() {
    this.show = true;
  }
  
  public void setSize(float percentage) {
    this.size = percentage;
  }
  
  public float getSize() {
    return this.size;
  }
  
  public void setOnEdgeBounce(boolean b) {
    this.onEdgeBounce = true;
  }
  
  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  public void turnLeft(float degrees) {
    this.rotation = (this.rotation - degrees) % 360;
  }
  
  public void turnRight(float degrees) {
    this.rotation = (this.rotation + degrees) % 360;
  }
  
  public void setRotation(float degrees) {
    this.rotation = degrees;
  }
  
  public ScratchPen getPen() {
    return this.pen;
  }
  
  public void move(float steps) {
    PApplet parent = ScratchStage.parent;
    
    // convert degrees to radians
    float newX = steps * (float) Math.cos(this.rotation * Math.PI / 180) + this.x;
    float newY = steps * (float) Math.sin(this.rotation * Math.PI / 180) + this.y;
    
    ScratchImage currentCostume = this.costumes.get(this.currentCostume);
    
    if (this.onEdgeBounce) {
      float spriteWidth = this.show ? currentCostume.getImage().width : this.pen.getSize();
      if (newX > parent.width - spriteWidth / 2 || newX <  spriteWidth / 2) {
        this.rotation = this.calculateAngleOfReflection(this.rotation, false);
      }
      
      float spriteHeight = this.show ? currentCostume.getImage().height : this.pen.getSize();
      if (newY > parent.height - spriteHeight / 2 || newY < spriteHeight / 2) {
        this.rotation = this.calculateAngleOfReflection(this.rotation, true);
      }
    }
    
    this.x = newX;
    this.y = newY;
    
    this.pen.setPosition(this.x, this.y);
  }
  
  private float calculateAngleOfReflection(float angleOfIncidence, boolean horizontalWall) {
    if(horizontalWall) {
      float angleOfReflection = 360 - angleOfIncidence;
      while(angleOfReflection < 0) angleOfReflection += 360;
      return angleOfReflection;
    } else {
      float angleOfReflection = 180 - angleOfIncidence;
      while(angleOfReflection < 0) angleOfReflection += 360;
      return angleOfReflection;
    }
    
  }
  
  public void draw() {
    this.pen.draw();
    if (costumes.size() > 0 && this.show) {   
      this.costumes.get(this.currentCostume).draw(this.size, this.x, this.y);
    }
  }
}
