---
type: patch
---

`MapObject.getProperty()` now throws a helpful error message naming the missing property and, when available, the properties that do exist on that map object — instead of a bare `NoSuchElementException` with no context. It also now gives a clear message when the map object has no custom properties at all, rather than a raw `NullPointerException`. This matches the library's existing beginner-friendly error reporting for missing assets.
