---
type: major
---

Stop failing silently, and stop killing the whole development environment.

**A sprite without a costume is no longer invisible.** It used to draw nothing at
all, which looks exactly like a broken program. It now draws a marked box where
the sprite is and explains once on the console what to add:

```
WARNING: Bunny has no costume, so there is nothing to draw!

Tip: Call addCostume() in the constructor, for example
     this.addCostume("bunny1_stand");
```

**Asset failures throw `ScratchException` instead of calling `System.exit`.**
Ending the process took BlueJ's virtual machine down with it, so a mistyped
costume name closed everything the student had open. The detailed message is
printed exactly as before, then the program stops instead of the environment.

This affects images, sounds, fonts and shaders.
