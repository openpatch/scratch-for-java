---
type: patch
---

Fix native audio streams accumulating when stages or sprites repeatedly add the same sounds. Prewarm one shared clip per sound file, open additional clips only for simultaneous playback, and close surplus clips when playback ends. Keep each sprite's playback and volume independent, return cancelled playback resources, and close partially opened clips after audio-device failures.

Update the documentation's embedded Online IDE to v2.2.1-hyperbook.33, which includes the matching browser audio fix and Scratch for Java 6.2.1 compatibility metadata.
