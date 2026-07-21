---
type: minor
---

Add `ask` and `answer`, so a program can take typed input.

Scratch's `ask ( ) and wait` and `(answer)` had no equivalent, which ruled out a
whole category of first projects: quizzes, guessing games, anything that greets
you by name. A box now appears at the bottom of the stage, the same place
Scratch puts it, and collects what is typed until Enter:

```java
public void run() {
  if (!this.isAsking() && this.getAnswer().isEmpty()) {
    this.ask("What is your name?");
  }
  if (!this.getAnswer().isEmpty()) {
    this.display("Hello " + this.getAnswer() + "!");
  }
}
```

Available on both `Sprite` and `Stage`, as in Scratch.

Unlike Scratch it does not pause the script — this library has no per-sprite
wait — so `run()` keeps being called while the question is on screen. Check
`isAsking()`, or wait for `getAnswer()` to change. While a question is waiting,
key presses go to the answer rather than to sprites, so a sprite does not run
around while someone types their name.
