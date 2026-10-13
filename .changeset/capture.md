---
type: minor
---

Editors can capture the stages of a running program: with
`-Dscratch4j.capture=<folder>` each stage is saved as
`<folder>/<Stage>/frame.png` and `stage.json` the first time it has run on
screen, and the program goes on as usual. `-Dscratch4j.capture.omit=Stage:a,b`
leaves the sprites of those fields out of the picture, not out of the window.
