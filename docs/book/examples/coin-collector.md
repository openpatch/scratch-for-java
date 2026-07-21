---
name: Coin Collector
---

# Coin Collector

A small game built entirely from the assets that ship with Scratch for Java.
Look into the project folder: there is no images or sounds folder. Every
costume, backdrop and sound is written as a plain name.

![coin collector example](/assets/coin-collector.gif)

Walk with the left and right arrow keys, jump with space, and collect all six
coins.

## Assets by name

The alien, the coins, the grass and the sky all come from the library:

```java
this.addCostume("alienGreen_stand");
this.addAnimation("walk", "alienGreen_walk%d", 2);
this.addSound("handleCoins");
```

The backdrop works the same way:

```java
this.addBackdrop("background");
```

You can look up every available name here:

- [Built-in Sprites](/sprites)
- [Built-in Sounds](/sounds)

## Source Code:

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/coinCollector
