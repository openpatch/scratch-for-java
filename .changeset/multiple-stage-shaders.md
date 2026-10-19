---
type: minor
---

A stage can draw with several shaders at once, like Scratch's graphic effects. `getShaders().activate(name)` adds a shader to those already active and `deactivate(name)` removes one; `isActive(name)` and `getActive()` tell which are on. Active shaders take turns in the order they were added, each working on what the one before it drew, so a stage can be tinted and wobble at the same time.

`switchTo`, `next` and `reset` work as before and leave exactly one shader active (or none), and `getCurrent()` returns the first active one. A sprite draws with the first of its active shaders. The browser version behaves the same.
