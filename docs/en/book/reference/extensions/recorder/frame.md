---
name: Frame
---

# Frame Recorder

The frame recorder saves each frame in an output folder.

```java
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.recorder.FrameRecorder;

public class Animation {
    public Animation() {
        Stage myStage = new Stage(600, 240);

        FrameRecorder recorder = new FrameRecorder("output");
        recorder.start();

        // do stuff

        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new Animation();
    }
}
```
