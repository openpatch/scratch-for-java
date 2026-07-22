---
type: patch
---

Let the same sound be heard from several sprites at once.

Ten sheep that all added the sound `meh` and bleated together only made one
bleat. Every sound was cached as a single Java `Clip`, and a `Clip` is one
playback line: whichever sprite got there first held it, and the rest were
skipped until it was done.

Only the decoded samples are shared now. Each sound object gets a line of its
own, so ten sheep are ten bleats, while a single sheep still only bleats once at
a time, as before.

Lines are pooled and reused once a sound has finished, and are opened on a
background thread, so playing a sound never stalls the drawing of a frame -
opening one takes tens of milliseconds. A sound holds at most 16 lines, fewer
for a long one, which keeps a stage full of clones from filling memory with
copies of the same music.
