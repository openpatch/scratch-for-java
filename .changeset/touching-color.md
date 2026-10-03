---
type: minor
---

Sprites can sense colours, like Scratch's `touching color?` and
`color is touching color?` blocks:

```java
if (this.isTouchingColor(HtmlColor.RED)) { ... }
if (this.isTouchingColor(255, 0, 0)) { ... }
if (this.isColorTouchingColor(HtmlColor.YELLOW, HtmlColor.BLUE)) { ... }
```

A sprite touches a colour when anything it paints lies over that colour on the
stage: on the backdrop, on what the pen drew, or on another sprite. Nearly
equal colours count, with the same tolerance as Scratch, so anti-aliased edges
still match. The check only looks at the pixels the sprite covers, so it costs
the same on a 480 x 360 stage as on a 1920 x 1080 one; a 128 x 128 sprite takes
well under a millisecond.
