---
type: minor
---

Run the documentation's examples in the browser instead of watching them.

The reference pages now embed the example itself in an editor that compiles and
runs it, in place of the GIF that used to stand there. A new doclet step rewrites
each example under `src/examples/java/reference` into the shape the Online IDE
wants - no package, no imports, the constructor body as the program - so there is
still one source per example, still compiled by the build and still runnable on
the desktop. Every example uses built-in costumes and sounds, which resolve the
same way on both sides; only the Tiled map example still needs a file next to
it, because a map is not something the library bundles.

Nearly every documented method and constructor has one now, not only the hundred
that had a recording: `Sprite`, `Stage`, `Window`, `Text`, `UISprite`, `Pen`,
`Color`, `Vector2`, `Operators`, `Random`, `Clock`, `Timer`, `HtmlColor`,
`Shape` and its four kinds, `Hitbox`, `AnimatedSprite` and the `Camera`
extension. The ones that are only about a number - a vector's length, a random
seed - print it into the output panel next to the stage, where it can be read;
the ones that are about something on screen show it.

The tutorials gained the same thing at the point where each one first has a
finished program, so a reader can play the game before building it, and change a
number and see what happens without installing anything.

`mvn test` compiles both kinds of interactive example, so one that stops
compiling fails the build rather than the reader's first click.

Also fixed in the documentation itself:

- The reference documented private members. `useStandardDocletOptions` is off,
  so the `<show>public</show>` in the pom never reached javadoc and every member
  arrived at the doclet - which is how `Color.HSBtoRGB()`, `Color.RGBtoHSB()`
  and `Random.getRandom()` came to have reference pages of their own. Forty
  pages for things nobody can call are gone.
- `Random.noise(double)` was written with `/*` rather than `/**`, so its
  description never reached the page.
- `Sprite.previousCostume()` said "Switch to the next costume".
- `Stage.add(Sprite)` and `Window.getWidth()` had no documentation at all.
- The `View on GitHub` link under an example that is a single file pointed at
  the directory the examples share rather than at the file.
- `build.sh` could build the documentation with no reference section at all. The
  reference pages are deleted before being written again, but the javadoc plugin
  decides whether it has anything to do by looking at its own state file in
  `target`, which the deletion left behind - so every build that did not start
  from a clean `target` skipped the run that was supposed to write the pages
  back.
