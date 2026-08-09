---
type: patch
---

A class that extends `Window` and has a `main` method which is not static no
longer fails with "Cannot create multiple Windows!". Since Java 21 the JVM
builds an instance of such a class before it calls `main`, so the
`new MyWindow()` inside `main` was the second window and the project stopped
before it started. That window is now reused, and a note explains how to be rid
of the extra one by making `main` static. Two windows asked for by the project
itself are still an error.
