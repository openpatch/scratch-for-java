package demos.smartRocket;

import org.openpatch.scratch.*;

public class SmartRocket extends Window {
  public SmartRocket() {
    super(800, 600, "demos/smartRocket/assets");
    this.setStage(new Level());
  }

  public static void main(String[] args) {
    new SmartRocket();
  }
}
