---
type: minor
---

A window loads a folder named `assets` while its loading screen shows, without the folder being passed to its constructor: `new Window(480, 360)` is enough. An assets folder that is passed but does not exist now gets a warning instead of being skipped in silence.

The images, sounds and fonts loaded in advance are now also found again when a sprite uses them. When the assets folder was on the classpath, they were stored under a different path than the one a sprite asks for, so each was loaded a second time on first use, in the middle of the game.
