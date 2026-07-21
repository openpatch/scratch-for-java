---
type: patch
---

Rewrite the beginner documentation around the built-in library.

**Getting Started now contains Java.** It used to be install BlueJ, download a
zip, right-click — no code at all. It now walks through a seven-line program
that puts a rabbit on screen with nothing to download, then makes it walk with
the arrow keys, with the matching Scratch script shown beside the Java.

**Pages that had become wrong were corrected.** "Costumes, Backdrops and Sounds"
opened with *"Scratch for Java does not come with any of that"*, which stopped
being true when 838 sprites and 266 sounds moved into the jar. "Differences to
Scratch" likewise said there is no sprite or sound library.

**Code samples now compile.** Every Java block in the beginner pages was
extracted and compiled against the current API. That turned up two samples that
had been broken since `KeyCode` became an enum: the Bouncing Hedgehog tutorial
declared `whenKeyPressed(int keyCode)` and compared against `KeyCode.VK_LEFT`,
neither of which exists any more. The "Multiple Approach Design" page used
`setRun` and `setOnEdgeBounce`, both removed in v5.

**`build.sh` no longer burns the version into the sources.** It substituted
`{{VERSION}}` in place and never restored it, so the placeholders had already
been replaced by `4.28.1` and every later release would have shipped stale
download links. The substitution is now undone after the build.
