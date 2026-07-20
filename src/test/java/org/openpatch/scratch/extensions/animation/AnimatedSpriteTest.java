package org.openpatch.scratch.extensions.animation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class AnimatedSpriteTest {

  @Test
  void constructingAnAnimatedSpriteDoesNotRequireALiveApplet() {
    // AnimatedSprite() just delegates to Sprite(), which used to crash the
    // JVM without a running Window/Applet (see FontTest, SpriteTest).
    AnimatedSprite sprite = new AnimatedSprite();

    assertEquals(0, sprite.getAnimationFrame());
    assertFalse(sprite.isAnimationPlayed());
  }

  @Test
  void animationIntervalDefaultsAndCanBeChanged() {
    AnimatedSprite sprite = new AnimatedSprite();

    assertEquals(120, sprite.getAnimationInterval());

    sprite.setAnimationInterval(200);
    assertEquals(200, sprite.getAnimationInterval());
  }

  @Test
  void setAnimationFrameUpdatesTheCurrentFrame() {
    AnimatedSprite sprite = new AnimatedSprite();
    sprite.setAnimationFrame(3);

    assertEquals(3, sprite.getAnimationFrame());
  }

  @Test
  void resetAnimationRestartsFromTheFirstFrame() {
    AnimatedSprite sprite = new AnimatedSprite();
    sprite.setAnimationFrame(5);

    sprite.resetAnimation();

    assertEquals(0, sprite.getAnimationFrame());
    assertFalse(sprite.isAnimationPlayed());
  }
}
