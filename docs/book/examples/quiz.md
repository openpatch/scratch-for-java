---
name: Quiz
---

# Quiz

Asks three questions and reacts to the answers, using `ask()` and `getAnswer()`.
The question box appears at the bottom of the stage, the same place Scratch puts
it. Type an answer and press Enter.

![a quiz asking questions at the bottom of the stage](/assets/quiz.gif)

There are no image or sound files in this project. The alien and the backdrop
both come from the [built-in sprites](/sprites).

Unlike Scratch, `ask()` does not pause anything, so the stage checks whether a
question is still waiting before looking at the answer:

```java
public void run() {
  if (this.isAsking()) {
    return;
  }
  String answer = this.getAnswer();
  ...
}
```

## Source Code:

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/quiz
