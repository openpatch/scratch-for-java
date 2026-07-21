package demos.quiz;

import org.openpatch.scratch.*;

/**
 * Asks a question and answers back, using ask() and getAnswer().
 *
 * <p>
 * The question box appears at the bottom of the stage. Type an answer and press
 * Enter. Nothing here needs an image or sound file.
 */
public class Quiz extends Stage {

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

  public static void main(String[] args) {
    new Quiz();
  }
}
