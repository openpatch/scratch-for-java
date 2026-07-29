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

## Run it here

Type an answer into the box at the bottom of the stage and press Enter.

:::onlineide{height="560px" libraries="scratch"}

```java Quiz.java

void main() {
  new Quiz();
}

class Quiz extends Stage {

  private final String[] questions = {
      "What is your name?",
      "What is 7 * 6?",
      "Which animal says meow?"
  };
  private final String[] expected = { null, "42", "cat" };

  private int current = -1;
  private int right = 0;
  private final Text score = new Text();

  public Quiz() {
    super(600, 360);
    this.addBackdrop("background");

    this.score.setPosition(0, 120);
    this.score.setTextSize(22);
    this.add(this.score);

    Sprite host = new Sprite();
    host.addCostume("alienGreen_stand");
    host.setSize(40);
    host.setY(-20);
    this.add(host);

    this.nextQuestion();
  }

  private void nextQuestion() {
    this.current += 1;
    if (this.current < this.questions.length) {
      this.ask(this.questions[this.current]);
    }
  }

  public void run() {
    // A question is on screen: nothing to do until it has been answered.
    if (this.isAsking()) {
      return;
    }
    if (this.current >= this.questions.length) {
      return;
    }

    String answer = this.getAnswer();
    String want = this.expected[this.current];

    if (want == null) {
      this.score.showText("Hello " + answer + "!");
    } else if (answer.trim().equalsIgnoreCase(want)) {
      this.right += 1;
      this.score.showText("Right! " + this.right + " correct");
    } else {
      this.score.showText("Not quite, it was " + want);
    }

    this.nextQuestion();
    if (this.current >= this.questions.length) {
      this.score.showText("Done. " + this.right + " of 2 correct.");
    }
  }
}
```

:::

## Source Code:

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/quiz
