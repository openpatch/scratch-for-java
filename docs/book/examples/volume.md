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

## Source Code:

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/volume
