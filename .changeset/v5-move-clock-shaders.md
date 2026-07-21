---
type: major
---

Move the clock and shader methods off `Sprite` and `Stage`.

Second pass of the v5 API slimming, 36 methods.

- **`Clock`** replaces `getCurrentYear()`, `getCurrentMonth()` and the seven
  others, which existed on both `Sprite` and `Stage` — 18 methods for 9 values.
  They are genuine Scratch blocks, so `Clock` lives in the core package and is
  covered by `import org.openpatch.scratch.*`:

  ```java
  int second = Clock.getSecond();
  ```

- **`Shaders`** replaces the nine shader methods that also existed on both
  classes. Each class now has one `getShaders()` accessor:

  ```java
  this.getShaders().add("blur", "blur.frag", null);
  this.getShaders().switchTo("blur");
  ```

`Sprite` and `Stage` are down to 218 public methods, from 293 before v5.
