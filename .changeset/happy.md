---
type: minor
---

Move towards supporting Java modules. Therefore we added the shape extension
and remove public facing java.awt classes. This is a breaking change, since you
can not use the java.awt classes anymore. Instead, you can use the new shape
extension, which provides the same functionality.

We also have a module-info.java ready to go, but processing core is not a
module at the moment.
