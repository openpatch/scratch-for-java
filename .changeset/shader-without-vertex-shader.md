---
type: patch
---

`getShaders().add(name, fragmentShaderPath, null)` works. Leaving out the
vertex shader is what the documentation shows, but it stopped the program with
a `NullPointerException`; Processing's default vertex shader is now used.
