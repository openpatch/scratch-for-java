---
name: Guess the Number
index: 5
---

# Guess the Number

The computer thinks of a number and you try to find it. This is the first
project where the program **asks you something** and does different things
depending on the answer.

![a guessing game asking for numbers and giving hints](/assets/guess-the-number.gif)

The whole thing is one class, and it needs no files at all.

## Step 1: Ask a question

`ask()` puts a box at the bottom of the stage, exactly where Scratch puts it, and
collects what you type until you press Enter. `getAnswer()` gives you back what
was typed.

:::scratchblock
ask [Guess a number between 1 and 10] and wait
:::

Start with this much:

```java
import org.openpatch.scratch.*;

public class GuessStage extends Stage {
  private int secret = Random.randomInt(1, 10);

  public GuessStage() {
    super(500, 300);
    this.addBackdrop("background");
    this.ask("Guess a number between 1 and 10");
  }
}
```

## Step 2: One important difference from Scratch

In Scratch, `ask and wait` **stops** the script until you answer. In Java it does
not: `run()` keeps being called about 60 times a second while the question sits
on screen.

So instead of waiting, you check whether a question is still open:

```java
public void run() {
  if (this.isAsking()) {
    return;      // the question is still on screen, come back next time
  }
  // there is an answer waiting
}
```

That `return` is doing the job Scratch's "wait" does for you.

## Step 3: React to the answer

`getAnswer()` always gives you **text**, even when someone types a number, so it
has to be turned into a number first. If they typed something that is not a
number at all, `Integer.parseInt` fails, and the `catch` block deals with it.

Here is the finished class:

```java
import org.openpatch.scratch.*;

public class GuessStage extends Stage {
  private int secret = Random.randomInt(1, 10);
  private int tries = 0;
  private boolean solved = false;
  private Text hint = new Text();

  public GuessStage() {
    super(500, 300);
    this.addBackdrop("background");

    this.hint.setPosition(0, 60);
    this.hint.setTextSize(20);
    this.add(this.hint);

    Sprite wizard = new Sprite();
    wizard.addCostume("alienBlue_front");
    wizard.setSize(40);
    wizard.setY(-40);
    this.add(wizard);

    this.ask("Guess a number between 1 and 10");
  }

  public void run() {
    if (this.solved || this.isAsking()) {
      return;
    }

    int guess = 0;
    try {
      guess = Integer.parseInt(this.getAnswer().trim());
    } catch (NumberFormatException e) {
      this.hint.showText("That is not a number. Try again!");
      this.ask("Guess a number between 1 and 10");
      return;
    }

    this.tries = this.tries + 1;

    if (guess < this.secret) {
      this.hint.showText(guess + " is too small.");
      this.ask("Guess again");
    } else if (guess > this.secret) {
      this.hint.showText(guess + " is too big.");
      this.ask("Guess again");
    } else {
      this.hint.showText("Yes! It was " + this.secret + ", in " + this.tries + " tries.");
      this.solved = true;
    }
  }
}
```

:::alert{warn}

The `solved` variable is easy to forget, and leaving it out gives a strange bug.
Once the number is guessed, no new question is asked, so `isAsking()` stays
false — and `run()` happily counts the same answer again on every single frame.
The number of tries races upwards. `solved` is what stops it.

:::

## Things to try

- Give the player only five tries.
- Let the wizard `say()` the hints instead of writing them at the top.
- Ask for a name first and use it in the hints.
