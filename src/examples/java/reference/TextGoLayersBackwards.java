package reference;
import org.openpatch.scratch.*;

public class TextGoLayersBackwards {
  public TextGoLayersBackwards() {
    Stage myStage = new Stage(600, 240);

    Text one = new Text("One", -30, 15, 200);
    one.setStyle(TextStyle.BOX);
    one.setBackgroundColor(250, 240, 180);
    myStage.add(one);

    Text two = new Text("Two", 0, 0, 200);
    two.setStyle(TextStyle.BOX);
    two.setBackgroundColor(180, 220, 250);
    myStage.add(two);

    // The one at the front: added last, so it covers the other two.
    Text three = new Text("Three", 30, -15, 200);
    three.setStyle(TextStyle.BOX);
    three.setBackgroundColor(200, 240, 200);
    myStage.add(three);

    while (true) {
      myStage.wait(1200);
      three.goLayersBackwards(1);
      myStage.wait(1200);
      three.goLayersBackwards(1);
      myStage.wait(1200);
      three.goLayersForwards(2);
    }
  }

  public static void main(String[] args) {
    new TextGoLayersBackwards();
  }
}
