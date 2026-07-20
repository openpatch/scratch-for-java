---
type: patch
---

Fix incorrect math in `Operators` and `Color`, found while adding a unit test suite for the library's pure-logic classes (`Operators`, `extensions/math`, `extensions/shape`, `extensions/hitbox`, `extensions/color`).

- **`Operators.asinOf`, `Operators.acosOf`, `Operators.atanOf`**: these applied the degrees conversion to the input before taking the inverse trig function, and never converted the result back to degrees, so `asinOf`/`acosOf` returned `NaN` for almost any real input, and `atanOf` returned radians instead of degrees. They now correctly return the degree-valued inverse of `sinOf`/`cosOf`/`tanOf` (e.g. `asinOf(1.0) == 90.0`).
- **`Operators.max(double...)`**: seeded its accumulator with `Double.MIN_VALUE` (the smallest *positive* double) instead of `Double.NEGATIVE_INFINITY`, so it returned the wrong result whenever every input was negative. Fixed to seed with `Double.NEGATIVE_INFINITY`.
- **`Color(double r, double g, double b)`**: passed `g` twice to `setRGB`, silently dropping the blue channel and replacing it with the green value. Fixed to pass `r, g, b`. This also corrects `GifRecorder.transparent`, which is constructed with this constructor.
