---
type: patch
---

Make the tutorial projects runnable outside BlueJ.

The finished projects had no `main`, so they could only be started by
right-clicking a class in BlueJ. Every tutorial's stage class now has one, the
same way the Bouncing Hedgehog project always did, which makes the projects work
from VS Code's Run button and from a terminal as well.

Each project's README now spells out all three ways to start it, including the
Windows classpath separator.
