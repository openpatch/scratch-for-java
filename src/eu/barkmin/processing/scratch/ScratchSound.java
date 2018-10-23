package eu.barkmin.processing.scratch;

import processing.sound.*;

public class ScratchSound {
  private String name;
  private SoundFile sound;
  
  public ScratchSound(String name, String soundPath) {
    this.name = name;
    this.sound = new SoundFile(ScratchStage.parent, soundPath);
  }
  
  public ScratchSound(ScratchSound s) {
    this.name = s.name;
    this.sound = s.sound;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void play() {
    this.sound.play();
  }
  
  public void pause() {
    this.sound.pause();
  }
  
  public void stop() {
    this.sound.stop();
  }
  
  public boolean isPlaying() {
    return this.sound.isPlaying();
  }
  
  public void setVolume(float amp) {
    if (amp <= 1 && amp >= 0) {
      this.sound.amp(amp);
    }
  }
}
