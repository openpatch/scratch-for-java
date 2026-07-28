---
type: patch
---

`Stage.count` returns an `int`.

It returned the `long` a `Stream` happened to hand back, so
`int coins = myStage.count(Coin.class)` did not compile.
