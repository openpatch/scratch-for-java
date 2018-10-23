package eu.barkmin.processing.scratch;

import processing.core.PImage;
import processing.core.PApplet;

public class ScratchImage {
  private String name;
  private PImage image;
  private PImage originalImage;
  private int[] tint = {255, 255, 255};
  private float opacity = 255;
  
  public ScratchImage(String name, String imagePath) {
    this.name = name;
    this.originalImage = ScratchStage.parent.loadImage(imagePath);
    this.image = this.originalImage.copy();
  }
  
  public ScratchImage(ScratchImage i) {
    this.name = i.name;
    this.image = i.image.copy();
    this.originalImage = i.originalImage.copy();
    this.tint = new int[3];
    this.tint[0] = i.tint[0];
    this.tint[1] = i.tint[1];
    this.tint[2] = i.tint[2];
    this.opacity = i.opacity;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setTint(int r, int g, int b) {
    int[] tint = new int[3];
    tint[0] = r;
    tint[1] = g;
    tint[2] = b;
    this.tint = tint;
  }
  
  public int[] getTint() {
    return this.tint;
  }
  
  public void setOpacity(float opacity) {
    this.opacity = opacity;
  }
  
  public float getOpacity() {
    return this.opacity;
  }
  
  public PImage getImage() {
    return this.image;
  }
  
  public void setImage(String imagePath) {
    this.image = ScratchStage.parent.loadImage(imagePath);
  }
  
  public void draw(float size, float x, float y) {
    PApplet parent = ScratchStage.parent;
    parent.tint(this.tint[0], this.tint[1], this.tint[2], this.opacity);
    int newWidth = Math.round(this.originalImage.width * size / 100);
    int newHeight = Math.round(this.originalImage.height * size / 100);
    
    if (newWidth != this.image.width || newHeight != this.image.height) {
      this.image = this.originalImage.copy();
      this.image.resize(newWidth, newHeight);
    }
    
    parent.image(this.image, x, y);
    parent.noTint();
    
  }
  
  public void draw() {
    PApplet parent = ScratchStage.parent;
    parent.tint(this.tint[0], this.tint[1], this.tint[2], this.opacity);
    parent.image(this.image, 0, 0);
    parent.noTint();
  }
}
