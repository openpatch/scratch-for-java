---
type: major
---

Add `UISprite` and move interface-only methods onto it.

Fourth pass of the v5 API slimming, 9 methods.

Buttons, bars and other interface parts want two things an ordinary sprite does
not: to be drawn on top and stay put when the camera moves, and to be sized in
pixels rather than in percent, so that nine-slice scaling can stretch one
costume to any size. Those now live on `UISprite`:

```java
public class Button extends UISprite {
  public Button() {
    this.addCostume("button", "ui/button.png");
    this.setNineSlice(12, 24, 12, 24);
    this.setWidth(600);
  }
}
```

- `setWidth`, `setHeight`, `setNineSlice`, `disableNineSlice` are `protected` on
  `Sprite` and public on `UISprite`.
- `changeWidth` and `changeHeight` are on `UISprite` only.
- `isUI(boolean)` and `isUI()` became `protected setUI(boolean)` and `isUI()`.
  A sprite class that is sometimes an interface element can still opt in from
  its own constructor.
- `Stage.goToUILayer` is gone.
- `getWidth` and `getHeight` stay on `Sprite`; they are useful generally.

`Sprite` and `Stage` are down to 199 public methods, from 293 before v5.
