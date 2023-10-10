---
name: FFmpeg
---

# FFmpeg Recorder

:::alert{warn}
This recorder does only work if the FFmpeg executable is available in your path and is only supported on Linux and Mac.
:::

The FFmpeg recorder saves a video.

```java
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.recorder.FFmpegRecorder;

public class Animation {
    public Animation() {
        Stage myStage = new Stage(600, 240);

        FFmpegRecorder recorder = new FFmpegRecorder("output.mp4");
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
