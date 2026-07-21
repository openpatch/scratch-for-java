---
type: minor
---

Add Ogg Vorbis support and built-in sounds.

Sounds can now be `.ogg` files, not just WAV, AIFF and AU. 266 CC0 sound
effects from [kenney.nl](https://kenney.nl) ship inside the jar and can be used
by name, the same way as built-in costumes:

```java
this.addSound("footstep_carpet_000");
this.playSound("footstep_carpet_000");
```

- `Sprite.addSound(String)` and `Stage.addSound(String)` take a built-in name
  directly, and the existing two-argument forms accept one in place of a path.
  Every built-in sound name is unique, so no folder has to be given.
- Ogg files below roughly 8 kB used to decode to silence without any error,
  which covers most short sound effects. They are now padded before decoding,
  so they play correctly.
- A sound that decodes to zero samples reports a warning instead of failing
  silently.
- The shaded "all" jar now merges `META-INF/services` entries, so the Ogg
  decoder survives shading.
