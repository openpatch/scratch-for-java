---
type: major
---

Turn the font settings into methods too.

`Text.DEFAULT_FONT`, `DEFAULT_FONT_SIZE`, `FONT_SIZES` and `SMOOTHING` were
public fields with the same problem as the `Window` globals: assigning one after
the window had started did nothing useful, silently, because the font is loaded
during start-up.

```java
public static void main(String[] args) {
  Text.useFont("assets/Retro Gaming.ttf", 11);
  Text.useSmoothing(false);            // off for pixel fonts
  new MyStage();
}
```

`Text.useFontSizes(14, 20)` prepares several sizes in advance, for projects that
show text at more than one size. Choosing any of them after the window exists
now prints an explanation instead of doing nothing.

`Text.getDefaultFont()` and `getDefaultFontSize()` report the current setting.
They are named "default" because `Text` already has an instance `getFont()` for
the font one particular piece of text uses.
