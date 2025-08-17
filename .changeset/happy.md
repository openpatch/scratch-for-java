---
type: minor
---

Move towards supporting Java modules. Therefore we added the shape extension
and remove public facing java.awt classes. This is a breaking change, since you
can not use the java.awt classes anymore. Instead, you can use the new shape
extension, which provides the same functionality.
