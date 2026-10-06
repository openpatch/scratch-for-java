package demos.smartRocket;

import org.openpatch.scratch.*;

public class SmartRocket extends Window {
  public SmartRocket() {
    super(800, 600, "demos/smartRocket/assets");

    // scratch4j:begin window (managed by the project settings)
    this.setStage(new Level());
    // scratch4j:end window
  }

  public static void main(String[] args) {
    // scratch4j:begin options (managed by the project settings)
    // scratch4j:end options
    new SmartRocket();
  }
}
