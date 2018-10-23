package eu.barkmin.processing.scratch;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import java.util.ArrayList;

public class ScratchStage {
  
  private ArrayList<ScratchImage> backdrops = new ArrayList<>();
  private int currentBackdrop = 0;
  private ArrayList<ScratchSound> sounds = new ArrayList<>();
  public static PApplet parent;
  private PGraphics penBuffer;
  private static ScratchStage instance;
  
  private ScratchStage(PApplet parent) {
    parent.imageMode(PConstants.CENTER);
    ScratchStage.parent = parent;
    this.penBuffer = parent.createGraphics(parent.width, parent.height);
  }
  
  public static ScratchStage getInstance(PApplet parent) {
    if (ScratchStage.instance == null) {
      ScratchStage.instance = new ScratchStage(parent);
    }
    return ScratchStage.instance;
  }
  
  public void addBackdrop(String name, String imagePath) {
    this.backdrops.add(new ScratchImage(name, imagePath));
  }
  
  public void switchBackdrop(String name) {
    for (int i = 0; i < backdrops.size(); i++) {
      ScratchImage backdrop = backdrops.get(i);
      if (backdrop.getName().equals(name)) {
        this.currentBackdrop = i;
        return;
      }
    }
  }
  
  public void nextBackdrop() {
    this.currentBackdrop = (this.currentBackdrop + 1) % backdrops.size();
  }
  
  public String getCurrentBackdropName() {
    return this.backdrops.get(this.currentBackdrop).getName();
  }
  
  public int getCurrentBackdropIndex() {
    return this.currentBackdrop;
  }
  
  public void earseAll() {
    this.penBuffer = parent.createGraphics(parent.width, parent.height);
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
  
  public PGraphics getPenBuffer() {
    return this.penBuffer;
  }
  
  public void stopAllSounds() {
    for(ScratchSound sound : sounds) {
      sound.stop();
    }
  }
  
  
  public void setTint(int r, int g, int b) {
    this.backdrops.get(this.currentBackdrop).setTint(r, g, b);
  }
  
  public void setOpacity(float opacity) {
    this.backdrops.get(this.currentBackdrop).setOpacity(opacity);
  }
  
  public void draw() {
    // redraw background to clear screen
    ScratchStage.parent.background(255);
    
    // draw current backdrop
    if(this.backdrops.size() > 0) {
      this.backdrops.get(this.currentBackdrop).draw();
    }
    ScratchStage.parent.image(penBuffer, ScratchStage.parent.width / 2, ScratchStage.parent.height / 2);
  }
}
