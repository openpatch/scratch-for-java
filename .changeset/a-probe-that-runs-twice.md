---
type: patch
---

A parity probe, so the two runtimes stop drifting apart unnoticed.

The desktop library and the browser port have disagreed several times now, and
never in a way a signature could catch: a transparency that counted the other way
round, a `getWidth()` that measured the rotated bounding box rather than the
costume, a hitbox reported in screen pixels rather than around the middle of the
stage. Every one was found by running the same program in both and reading the
two outputs side by side.

That program is now written down. `src/examples/java/parity/ParityProbe.java`
prints 77 values that have to read the same in both places;
`./scripts/parity.sh` runs it on the desktop and holds it to
`src/test/resources/parity/expected.txt`, and `--record` writes that file when a
change is meant.

`python3 scripts/parity-fixture.py` carries the stage-free half of it into the
online IDE's own test suite, where it runs on every `npm test`. Only half,
because a `Stage` needs WebGL and the browser's tests run in node — the rest is
held to the recorded values on the desktop, and to the same signatures in the
browser by its `ScratchTest.java`. Anything that can be written without a stage
should be, because that half is checked by a machine.
