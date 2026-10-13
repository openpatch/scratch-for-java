import org.openpatch.scratch.*;

public class GuessStage extends Stage {

  // begin stage designer
  private Sprite wizard;
  // end stage designer

  private int secret = Random.randomInt(1, 10);
  private int tries = 0;
  private boolean solved = false;
  private Text hint = new Text();

  public GuessStage() {

    this.hint.setPosition(0, 60);
    this.hint.setTextSize(20);
    this.add(this.hint);

    // begin stage designer
    this.addBackdrop("background");
    wizard = new Sprite();
    wizard.addCostume("alienBlue_front");
    wizard.setPosition(0, -40);
    wizard.setSize(40);
    this.add(wizard);
    // end stage designer

    this.ask("Guess a number between 1 and 10");
  }

  public void run() {
    if (this.solved || this.isAsking()) {
      return;
    }

    int guess = 0;
    try {
      guess = Integer.parseInt(this.getAnswer().trim());
    } catch (Exception e) {
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

  public static void main(String[] args) {
    Window myWindow = new Window(500, 300);
    myWindow.setStage(new GuessStage());
  }
}
