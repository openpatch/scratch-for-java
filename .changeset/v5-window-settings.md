---
type: major
---

Replace the two `Window` globals with methods, and name the sampling modes.

`Window.FULL_SCREEN` and `Window.TEXTURE_SAMPLING_MODE` were public fields you
assigned before creating a stage. Assigning one afterwards did nothing at all —
no error, no warning, just a windowed program and no explanation. They were also
invisible in BlueJ, where you explore a class by its constructors and methods,
and they needed the idea of a static field, which comes long after constructors
in a first course.

They are methods now, and they say something when used too late:

```java
public static void main(String[] args) {
  Window.useFullScreen();
  Window.useTextureSampling(TextureSampling.POINT);
  new MyStage();
}
```

`TextureSampling` is an enum — `POINT`, `LINEAR`, `BILINEAR`, `TRILINEAR` —
instead of the numbers 2 to 5, so `POINT` for pixel art reads as itself rather
than as `TEXTURE_SAMPLING_MODE = 2`. Invalid values are no longer possible,
which also retires the warning that used to check for them.

`Window.getTextureSampling()` reports the current setting.
