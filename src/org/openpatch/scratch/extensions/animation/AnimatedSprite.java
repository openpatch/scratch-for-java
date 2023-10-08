package org.openpatch.scratch.extensions.animation;

import java.util.concurrent.ConcurrentHashMap;
import org.openpatch.scratch.Sprite;

public class AnimatedSprite extends Sprite {

  private ConcurrentHashMap<String, String[]> animations = new ConcurrentHashMap<>();
  private int animationInterval = 120;
  private int animationFrame = 0;
  private boolean animationPlayed = false;

  public void addAnimation(String name, String pattern, int frames) {
    String[] animation = new String[frames];
    for (int i = 0; i < animation.length; i++) {
      String costumeName = "_animation_" + name + "_" + i;
      String file = String.format(pattern, i + 1);
      this.addCostume(costumeName, file);
      animation[i] = costumeName;
    }
    animations.put(name, animation);
  }

  public void addAnimation(String name, String path, int frames, int width, int height) {
    this.addAnimation(name, path, frames, width, height, 0);
  }

  public void playAnimation(String name) {
    this.playAnimation(name, false);
  }

  public void addAnimation(String name, String path, int frames, int width, int height, int row) {
    String[] animation = new String[frames];
    for (int column = 0; column < animation.length; column++) {
      String costumeName = "_animation_" + name + "_" + column + "_" + row;
      this.addCostume(costumeName, path, column * width, row * height, width, height);
      animation[column] = costumeName;
    }
    animations.put(name, animation);
  }

  public void addAnimation(
      String name, String path, int frames, int width, int height, int column, boolean useColumns) {
    String[] animation = new String[frames];
    for (int row = 0; row < animation.length; row++) {
      String costumeName = "_animation_" + name + "_" + column + "_" + row;
      this.addCostume(costumeName, path, column * width, row * height, width, height);
      animation[row] = costumeName;
    }
    animations.put(name, animation);
  }

  public void playAnimation(String name, boolean once) {
    if (this.getTimer("animation") == null) {
      this.addTimer("animation");
    }
    String[] animation = animations.get(name);
    this.switchCostume(animation[animationFrame]);
    if (this.getTimer("animation").everyMillis(animationInterval)) {
      if (!animationPlayed && animationFrame != animation.length - 1 || !once) {
        if (animationFrame >= animation.length) {
          animationFrame = 0;
        }
        this.switchCostume(animation[animationFrame]);
        animationFrame = (animationFrame + 1) % animation.length;
      } else {
        animationPlayed = true;
      }
    }
  }

  public void resetAnimation() {
    animationFrame = 0;
    animationPlayed = false;
  }

  public void setAnimationInterval(int interval) {
    animationInterval = interval;
  }

  public int getAnimationInterval() {
    return animationInterval;
  }

  public int getAnimationFrame() {
    return animationFrame;
  }

  public void setAnimationFrame(int frame) {
    animationFrame = frame;
  }

  public boolean isAnimationPlayed() {
    return animationPlayed;
  }
}
