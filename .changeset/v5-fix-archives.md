---
type: patch
---

Fix the downloadable starter projects.

Three of the six project archives no longer compiled: `cat` used
`setOnEdgeBounce`, `bouncy-hedgehog-100` and `Halloween` still imported the
pre-flattening packages and used `KeyCode.VK_*` names that stopped existing when
`KeyCode` became an enum.

`bouncy-hedgehog-100` is the finished version of the Bouncing Hedgehog tutorial,
so anyone working through it and downloading the solution to compare got a
project that would not build.

All six now compile, and the cat project was run to confirm it still bounces off
the edges after moving from `setOnEdgeBounce(true)` to `ifOnEdgeBounce()`.
