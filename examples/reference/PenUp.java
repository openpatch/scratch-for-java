import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.pen.*;
import org.openpatch.scratch.extensions.recorder.*;

public class PenUp extends Window {

  private Recorder recorder;

  public PenUp() {
    super(600, 240);
    this.setStage(new MyStage());
    recorder = new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
  }

  public void whenExits() {
    recorder.stop();
  }

  class MyStage extends Stage {
    private Pen myPen;

    public MyStage() {
      super(600, 240);
      myPen = new Pen();
      this.add(myPen);
      myPen.down();
    }

    public void run() {
      myPen.setPosition(this.getMouseX(), this.getMouseY());
      if (this.getTimer().afterMillis(3000)) {
        this.exit();
      }
    }
  }

  public static void main(String[] args) {
    new PenUp();
  }
}
