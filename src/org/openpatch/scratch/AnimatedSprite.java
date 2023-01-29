package org.openpatch.scratch;

import java.util.concurrent.ConcurrentHashMap;

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

    public void addAnimation(String name, String path, int frames, int width, int height, int row) {
        String[] animation = new String[frames];
        for (int i = 0; i < animation.length; i++) {
            String costumeName = "_animation_" + name + "_" + i + "_" + row;
            this.addCostume(costumeName, path, i * width, row * height, width, height);
            animation[i] = costumeName;
        }
        animations.put(name, animation);
    }

    public void playAnimation(String name) {
        this.playAnimation(name, false);
    }

    public void playAnimation(String name, boolean once) {
        if (this.getTimer("animation") == null) {
            this.addTimer("animation");
        }
        if (this.getTimer("animation").everyMillis(animationInterval)) {
            String[] animation = animations.get(name);
            if (!animationPlayed && animationFrame != animation.length - 1 || !once) {
                animationFrame = (animationFrame + 1) % animation.length;
                this.switchCostume(animation[animationFrame]);
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
