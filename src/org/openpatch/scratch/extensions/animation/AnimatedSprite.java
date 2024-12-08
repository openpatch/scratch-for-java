package org.openpatch.scratch.extensions.animation;

import java.util.concurrent.ConcurrentHashMap;
import org.openpatch.scratch.Sprite;

/**
 * The AnimatedSprite class represents a sprite that can play animations.
 * It extends the Sprite class and provides methods to add animations, play animations,
 * set the interval between animation frames, and reset the animation.
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * AnimatedSprite sprite = new AnimatedSprite();
 * sprite.addCostume("idle", "assets/idle.png");
 * sprite.addAnimation("walk", "assets/walk_%d.png", 4);
 * sprite.playAnimation("walk");
 * }</pre>
 * 
 * @see Sprite
 */
public class AnimatedSprite extends Sprite {

  private ConcurrentHashMap<String, String[]> animations = new ConcurrentHashMap<>();
  private int animationInterval = 120;
  private int animationFrame = 0;
  private boolean animationPlayed = false;

  /**
   * Adds an animation to the sprite.
   * 
   * @param name the name of the animation
   * @param pattern the pattern (@see String#format) of the file names 
   * @param frames the number of frames in the animation
   */
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

  /**
   * Adds an animation to the sprite.
   *
   * @param name   the name of the animation
   * @param path   the path to the animation frames
   * @param frames the number of frames in the animation
   * @param width  the width of each frame
   * @param height the height of each frame
   */
  public void addAnimation(String name, String path, int frames, int width, int height) {
    this.addAnimation(name, path, frames, width, height, 0);
  }

  /**
   * Plays the animation with the specified name.
   *
   * @param name the name of the animation to play
   */
  public void playAnimation(String name) {
    this.playAnimation(name, false);
  }

  /**
   * Adds an animation to the sprite.
   * 
   * @param name the name of the animation
   * @param path the path to the animation frames
   * @param frames the number of frames in the animation
   * @param width the width of each frame
   * @param height the height of each frame
   * @param row the row of the animation frames
   */
  public void addAnimation(String name, String path, int frames, int width, int height, int row) {
    String[] animation = new String[frames];
    for (int column = 0; column < animation.length; column++) {
      String costumeName = "_animation_" + name + "_" + column + "_" + row;
      this.addCostume(costumeName, path, column * width, row * height, width, height);
      animation[column] = costumeName;
    }
    animations.put(name, animation);
  }

  /**
   * Adds an animation to the sprite.
   * @param name the name of the animation
   * @param path the path to the animation frames
   * @param frames the number of frames in the animation
   * @param width the width of each frame
   * @param height the height of each frame
   * @param column the column of the animation frames
   * @param useColumns whether to use columns or rows
   */
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

  /**
   * Plays the animation with the specified name.
   * @param name the name of the animation to play
   * @param once whether to play the animation once
   */
  public void playAnimation(String name, boolean once) {
    if (this.getTimer("animation") == null) {
      this.addTimer("animation");
    }
    String[] animation = animations.get(name);
    this.switchCostume(animation[animationFrame % animation.length]);
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

  /**
   * Resets the animation.
   * The animation will start from the first frame.
   */
  public void resetAnimation() {
    animationFrame = 0;
    animationPlayed = false;
  }

  /**
   * Sets the interval between animation frames.
   * @param interval the interval between animation frames
   */
  public void setAnimationInterval(int interval) {
    animationInterval = interval;
  }

  /**
   * Gets the interval between animation frames.
   * @return the interval between animation frames
   */
  public int getAnimationInterval() {
    return animationInterval;
  }

  /**
   * Gets the current animation frame.
   * @return the current animation frame
   */
  public int getAnimationFrame() {
    return animationFrame;
  }

  /**
   * Sets the current animation frame.
   * @param frame the current animation frame
   */
  public void setAnimationFrame(int frame) {
    animationFrame = frame;
  }

  /**
   * Checks if the animation is played.
   * @return true if the animation is played, false otherwise
   */
  public boolean isAnimationPlayed() {
    return animationPlayed;
  }
}
