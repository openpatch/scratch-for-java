---
type: minor
---

💥 BREAKING CHANGE: transparency and `mod` now answer the way the Scratch block does.

Both claimed a Scratch block in their documentation and then did something else.

**`setTransparency` is the ghost effect.** It ran from 0 to 255, where 0 was
invisible and 255 the solid sprite you start with — upside down and on the wrong
scale next to `set [ghost v] effect to`, which the javadoc has always shown
beside it. It now runs from 0 to 100, 0 solid and 100 invisible, and a value
outside that is pinned rather than wrapped. `changeTransparency` used
`(current + step) % 255`, so stepping past the end came back round or went
negative; it now stops at the ends.

```java
mySprite.setTransparency(50);    // was nearly invisible, now half see-through
mySprite.setTransparency(100);   // gone
```

**`Operators.mod` is the Scratch modulo.** It was Java's `%`, whose answer takes
the sign of the first input, so `mod(-7, 3)` was -1 where the block says 2. The
answer now takes the sign of the second input, which is what makes it useful for
wrapping a value into a range:

```java
Operators.mod(-1, 10);   // was -1, now 9
```

**The stage's effects belong to the stage.** `setTransparency`, `setTint` and
`changeTint` on a `Stage` reached only the backdrop that was showing, so
switching backdrop quietly undid them. They now apply to all of them, as
`Sprite` has always done with its costumes and as Scratch does.

Two smaller ones that go with it: `Stage.count` returns an `int` rather than the
`long` a `Stream` happened to hand back, so `int coins = myStage.count(Coin.class)`
compiles; and `Color` has `toString`, `equals` and `hashCode`, so printing one
says `Color[r=255.0, g=128.0, b=0.0]` instead of `org.openpatch.scratch.Color@35432107`.
