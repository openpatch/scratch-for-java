package reference;
import org.openpatch.scratch.*;

public class TextGoLayersForwards {
  public TextGoLayersForwards() {
    Stage myStage = new Stage(600, 240);

    // The one at the back: every text added after it covers it.
    Text one = new Text("One", -30, 15, 200);
    one.setStyle(TextStyle.BOX);
    one.setBackgroundColor(250, 240, 180);
    myStage.add(one);

    Text two = new Text("Two", 0, 0, 200);
    two.setStyle(TextStyle.BOX);
    two.setBackgroundColor(180, 220, 250);
    myStage.add(two);

    Text three = new Text("Three", 30, -15, 200);
    three.setStyle(TextStyle.BOX);
    three.setBackgroundColor(200, 240, 200);
    myStage.add(three);

    while (true) {
      myStage.wait(1200);
      one.goLayersForwards(1);
      myStage.wait(1200);
      one.goLayersForwards(1);
      myStage.wait(1200);
      one.goLayersBackwards(2);
    }
  }

  public static void main(String[] args) {
    new TextGoLayersForwards();
  }
}
