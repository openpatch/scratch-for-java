package demos.smartRocket;

import org.openpatch.scratch.*;

public class SmartRocket extends Window {
  public SmartRocket() {
    super(800, 600, "demos/smartRocket/assets");

    // begin project settings
    this.setStage(new Level());
    // end project settings
  }

  public static void main(String[] args) {
    // begin project settings
    // end project settings
    new SmartRocket();
  }
}
