---
type: major
---

One import for everything a course needs.

Nine extension packages moved into `org.openpatch.scratch`: `pen`, `text`,
`timer`, `animation`, `ui`, `math`, `shape`, `color` and `hitbox`. So `Pen`,
`Text`, `Timer`, `AnimatedSprite`, `UISprite`, `Vector2`, `Random`, `Color`,
`Hitbox` and the shapes are all reached by:

```java
import org.openpatch.scratch.*;
```

Seven packages stay opt-in, for things a first course does not meet: `camera`,
`fs`, `pixels`, `recorder`, `shader`, `sorting` and `tiled`.

Because `Pen` now sits beside `Sprite` and `Stage`, the six `stampTo*` and
`erase*` methods that were public only so `Pen` could reach them are
package-private. `Sprite.stamp(Layer)` replaces `stampToUI()` and its siblings,
matching `Stage.stamp(stamps, Layer)`:

```java
this.stamp(Layer.UI);
```

`Sprite` and `Stage` are down to 179 public methods, from 293 before v5, and
none of them are hidden from the documentation any more.
