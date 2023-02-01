---
name: Frame
---

# Frame Recorder

The frame recorder saves each frame in a output folder.

```java
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.FrameRecorder;

public class Animation {
    public Animation() {
        Stage myStage = new Stage(254, 100);

        FrameRecorder recorder = new FrameRecorder("output");
        recorder.start();

        // do stuff

        recorder.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        new Animation();
    }
}
```