---
name: Continue a Scratch project
hide: true
---

# Continue a Scratch project in Java

Studio imports `.sb3` costumes, sounds, sprites and supported scripts. Open the
**Scratch migration** tab to review timing, concurrency and unsupported blocks.
Each task opens the generated Java and shows the original block ID and data.
The project keeps the complete archive at `.scratch4j/original.sb3`. Open that
archive in Scratch to inspect the original script and surrounding blocks.

Task comments mark the generated source. Studio relocates them when the project
is reopened after edits. Keep the comments until the corresponding behavior is
implemented and tested. The archive, tasks and checks survive project ZIP transfer.
Compare the imported game with the original.

## Timing

Scratch can wait inside a script while other scripts continue. Java event methods
return before the next frame is drawn. A timed `say`, glide or sound starts its
effect; a following Java statement can run immediately. Import tasks identify
those sequences.

Represent a sequence with a field recording its phase, then advance it in `run()`
when a timer finishes:

```java
import org.openpatch.scratch.*;

class Greeting extends Sprite {
    int phase = 0;
    public void run() {
        if (phase == 0) {
            this.say("Hello!", 2000);
            this.getTimer("greeting").reset();
            phase = 1;
        } else if (phase == 1 && this.getTimer("greeting").afterMillis(2000)) {
            this.changeX(20);
            phase = 2;
        }
    }
}
```

The library calls `run()` about 60 times per second. Keep each call short. A
finite loop for an array is useful; an endless loop or sleeping in `run()` holds
up the next frame. Pause/speed/step control game time in the supported revision.

## Shared state

Scratch scripts can overlap. Imported Java helper methods run in sequence.
Stage variables become shared stage fields; sprite fields belong to that sprite.
Clones copy the student's fields, including references to shared objects, while
runtime helpers such as timers are independent. Decide which game state is
shared and which belongs to each clone.

`broadcast()` invokes receivers synchronously. A receiver beginning a timed
effect does not make the caller wait for that effect to finish. Represent
completion as state or a later message. Test a sequence with two sprites first.

## Unsupported blocks

A TODO or placeholder expression needs a Java implementation. Open the task's
original block and the preserved Scratch archive. Identify its input, output and
observable effect; replace the placeholder with a method or state transition.
For an unsupported reporter, the generated `0` is only a placeholder.

Check movement after a fixed number of frames, collisions with actual costumes,
score changes and clone state. Seed randomness to reproduce failures. See
[Differences to Scratch](/differences-scratch) and the [classroom course](/classroom).
