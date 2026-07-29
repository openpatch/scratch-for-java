---
name: Volume
---

# Volume

Turning sounds up and down with `setVolume()` and `changeVolume()`.

Press space to play a sound, and the up and down arrow keys to change how loud
it is. The volume is a percentage, as in Scratch, and stays between 0 and 100
however far you push it.

```java
this.addSound("handleCoins");
this.setVolume(100);
...
this.changeVolume(-10);
```

The sound is one of the [built-in sounds](/sounds), so there is no file to find
first.

## Run it here

Click the stage so it takes the keyboard, then press space for the sound and
the up and down arrows to change how loud it is.

:::onlineide{height="480px" libraries="scratch"}

```java Volume.java

void main() {
  new Volume();
}

class Volume extends Stage {

  private final Text label = new Text();

  public Volume() {
    super(480, 260);
    this.addBackdrop("background");

    this.addSound("handleCoins");
    this.setVolume(100);

    this.label.setPosition(0, 40);
    this.label.setTextSize(20);
    this.add(this.label);

    Sprite speaker = new Sprite();
    speaker.addCostume("hudCoin");
    speaker.setSize(60);
    speaker.setY(-40);
    this.add(speaker);

    this.showVolume();
  }

  private void showVolume() {
    this.label.showText("Volume: " + Math.round(this.getVolume())
        + "%   (space = play, up/down = louder/quieter)");
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.SPACE) {
      this.playSound("handleCoins");
    }
    if (keyCode == KeyCode.UP) {
      this.changeVolume(10);
      this.showVolume();
    }
    if (keyCode == KeyCode.DOWN) {
      this.changeVolume(-10);
      this.showVolume();
    }
  }
}
```

:::

## Source Code:

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/volume
