---
name: GIF
---

# GIF Recorder

With the GIF recorder you can either record an animation or just a single snapshot.

## Animation

```java
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class Animation {
    public Animation() {
        Stage myStage = new Stage(254, 100);

        GifRecorder recorder = new GifRecorder("output.gif");
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

## Snapshot

```java
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class Snapshot {
    public Snapshot() {
        Stage myStage = new Stage(254, 100);
        GifRecorder recorder = new GifRecorder("snapshot.gif");
        // do stuff
        recorder.snapshot();
        System.exit(0);
    }

    public static void main(String[] args) {
        new Snapshot();
    }
}
```