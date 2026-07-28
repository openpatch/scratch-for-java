---
type: patch
---

Took the GIF recording apparatus out of the reference examples.

Every example is now the example and nothing else. The `GifRecorder`, the
`exit()` that closed the recording, and the `while (stage.getTimer().forMillis(
3000))` loop that timed it are gone; a loop that was doing the work runs on as
`while (true)`, and a loop that had no body at all - it only held the desktop
program open while the GIF was taken - is gone with the rest. 28 MB of recordings
went with them, and `build.sh` no longer copies any.

The rewriter that turns an example into an Online IDE program loses the same
amount: it existed largely to undo the recording again.

Two examples were wrong because of the recording, and are right now:

- `Stage.exit()`, `Window.exit()` and `Window.whenExits()` showed a program with
  no `exit()` in it. The rewriter treated every line calling `exit()` as
  recording scaffolding and dropped it, including the one line those three pages
  are about.
- `Pen.up()` had no interactive example and its source never called `up()`. It
  draws two lines with a gap between them now.

`GifRecorder` itself is unchanged - it is still there for anyone recording their
own project.
