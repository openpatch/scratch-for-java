---
type: major
---

Remove the last set-handler, `Window.setWhenExits`.

The `setWhenX` family left `Sprite` and `Stage` earlier in v5, but `Window` kept
the same pair — an overridable `whenExits()` beside a `setWhenExits(handler)`,
plus a nested `WhenExitsHandler` interface. It is gone now, along with its
reference page. Override `whenExits()` instead:

```java
public class MyWindow extends Window {
  public void whenExits() {
    GameState.save();
  }
}
```

`build.sh` now runs `mvn prepare-package` rather than `mvn compile`, so the
reference pages are regenerated as part of a docs build. Running only `compile`
left them as they were after the last `mvn package`, which is how the page for a
deleted method survived.
