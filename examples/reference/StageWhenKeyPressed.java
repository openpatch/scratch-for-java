import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageWhenKeyPressed {

	class CustomStage extends Stage {

		public CustomStage() {
			super(254, 100);
			GifRecorder recorder = new GifRecorder("StageWhenKeyPressed.gif");
			recorder.start();
			while (this.getTimer().forMillis(3000))
				;
			recorder.stop();
			Window.getInstance().exit();
		}

		@Override
		public void whenKeyPressed(int keyCode) {
			this.display("Key Pressed: " + keyCode);
		}
	}

	public StageWhenKeyPressed() {
		new CustomStage();
	}

	public static void main(String[] args) {
		new StageWhenKeyPressed();
	}
}