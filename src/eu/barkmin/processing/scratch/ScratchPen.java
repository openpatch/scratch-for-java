package eu.barkmin.processing.scratch;

import processing.core.PGraphics;
import java.util.ArrayList;

public class ScratchPen {
  
  private float r = 255;
  private float g = 0;
  private float b = 0;
  private float opacity = 255;
  private float size = 0; 
  private ArrayList<Point> points = new ArrayList<>();
  private boolean down = false;
  
  public ScratchPen() {
  }
  
  public ScratchPen(ScratchPen p) {
    this.r = p.r;
    this.g = p.g;
    this.b = p.b;
    this.size = p.size;
    this.opacity = p.opacity;
    this.points = new ArrayList<>();
    this.down = p.down;
  }
  
  public void setColor(float h) {
    float[] rgb = ScratchColor.HSBtoRGB(h);
    this.r = rgb[0];
    this.g = rgb[1];
    this.b = rgb[2];
  }
  
  public void setColor(double h) {
    this.setColor((float) h);
  }
  
  public void setColor(float r, float g, float b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }
  
  public void changeColor(double c) {
    this.changeColor((float) c);
  }
  
  public void changeColor(float c) {
    this.setColor(ScratchColor.RGBtoHSB(this.r, this.g, this.b) + c);
  }
  
  public void setSize(float size) {
    this.size = size;
  }
  
  public float getSize() {
    return this.size;
  }
  
  public void setOpacity(float opacity) {
    this.opacity = opacity;
  }
  
  public void setPosition(float x, float y) {
    if (this.down) {
      this.points.add(new Point(x, y, this.r, this.g, this.b, this.opacity, this.size));
    }
  }
  
  public void down() {
    this.down = true;
  }
  
  public void up() {
    this.down = false;
  }
  
  public void draw() {
    if (this.points.size() > 1) {
      PGraphics buffer = ScratchStage.getInstance(null).getPenBuffer();
      Point point = this.points.get(this.points.size() - 1);
      Point previousPoint = this.points.get(this.points.size() - 2);
      buffer.beginDraw();
      buffer.stroke(point.r, point.g, point.b, point.opacity);
      buffer.strokeWeight(point.size);
      buffer.line(previousPoint.x, previousPoint.y, point.x, point.y);
      buffer.endDraw();
    }
  }
}

class Point {
  float x = 0;
  float y = 0;
  float r = 0;
  float g = 0;
  float b = 0;
  float opacity = 0;
  float size = 0;
  
  public Point(float x, float y, float r, float g, float b, float opacity, float size) {
    this.x = x;
    this.y = y;
    this.r = r;
    this.g = g;
    this.b = b;
    this.opacity = opacity;
    this.size = size;
  }
}
