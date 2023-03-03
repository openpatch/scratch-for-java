---
name: GIF
---

# GIF Recorder

With the GIF recorder you can either record an animation or just a single snapshot.

## Animation

```java
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;
import org.openpatch.scratch.Window;

public class Animation {
    public Animation() {
        Stage myStage = new Stage(254, 100);

        GifRecorder recorder = new GifRecorder("output.gif");
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

## Snapshot

```java
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;
import org.openpatch.scratch.Window;

public class Snapshot {
    public Snapshot() {
        Stage myStage = new Stage(254, 100);
        GifRecorder recorder = new GifRecorder("snapshot.gif");
        // do stuff
        recorder.snapshot();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new Snapshot();
    }
}
```