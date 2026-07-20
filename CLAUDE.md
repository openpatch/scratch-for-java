# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

Scratch for Java (`org.openpatch.scratch`) is a Java library that ports the Scratch (scratch.mit.edu)
block-based programming model to Java, so learners can move from blocks to text-based code while
keeping the same mental model (Window / Stage / Sprite, costumes, backdrops, broadcasts, etc.).

## Build & release commands

- `mvn compile` — compile the library (this is the command referenced in the README for day-to-day dev)
- `mvn clean package` — build the JAR
- `mvn clean package -Pall` — build a standalone JAR with all dependencies shaded in (maven-shade-plugin)
- `mvn deploy -Pcentral` — release to Maven Central (GPG signing + central-publishing-maven-plugin); not something to run casually
- `./build.sh` — full doc site build: copies CHANGELOG into the docs book, regenerates reference GIFs from `src/examples/java/reference`, substitutes the version into docs, then runs `npx hyperbook build` inside `docs/`
- `cd docs && npx hyperbook dev` — run the documentation site locally

There is no test suite (no JUnit dependency, no `*Test.java` files under `src/main`). Verifying a
change means making sure it compiles (`mvn compile`) and, where relevant, that the affected
reference/demo example under `src/examples/java` still behaves correctly.

Java 17 is required (`maven.compiler.release` in `pom.xml`).

## Source layout

```
src/main/java/org/openpatch/scratch/
  Window.java        - singleton application window (one per process)
  Stage.java          - the active "scene": holds sprites, backdrops, input/event callbacks
  Sprite.java         - the core interactive object (costumes, movement, collision, sound, ...)
  Operators.java      - Scratch "Operators" block equivalents (math/string helpers)
  KeyCode.java / MouseCode.java / RotationStyle.java
  extensions/         - optional add-on capabilities, each its own sub-package:
    animation/  camera/  color/  fs/  hitbox/  math/  pen/  recorder/  shader/  shape/  text/  tiled/  timer/
  internal/           - implementation details not part of the public API (Processing/JOGL glue,
                        image/font/sound loading, GIF encoding, simplex noise, etc.)

src/examples/java/
  demos/              - complete runnable example projects (one folder per demo)
  reference/          - one folder per documented API method/feature, used to generate docs

src/tools/java/doclets/
  Scratch4JDoclet.java - custom Javadoc doclet that turns annotated Javadoc into the reference docs

docs/                 - Hyperbook-based documentation site (English + German), built via ./build.sh
```

`src/examples/java` and `src/tools/java` are wired into the main build via
`build-helper-maven-plugin` (`add-examples-source`), so they compile as part of the project even
though they live outside `src/main`. They are excluded from the packaged JAR/sources JAR
(see the `maven-jar-plugin` / `maven-source-plugin` excludes in `pom.xml`).

## Architecture

Three concepts mirror Scratch directly and almost every feature hangs off one of them:

- **Window** — the top-level, singleton application window (constructed with size/title/asset
  path). Owns the render loop and delegates to the current `Stage`.
- **Stage** — one screen/scene of the project. Holds the collection of `Sprite`s, backdrops, and
  the `when*` event handlers (key press, mouse click, custom broadcasts, etc.).
- **Sprite** — an interactive object with costumes, position/rotation, movement, collision/sensing,
  sound, and a `run()` method invoked every frame (analogous to a Scratch script attached to a sprite).

Extensions in `extensions/` layer additional behavior onto this model rather than modifying the
core classes — e.g. `AnimatedSprite` (animation) extends `Sprite`, `Recorder`/`GifRecorder`
(recorder) capture stage output, `pen`/`shape`/`text` add drawing primitives, `tiled` adds
Tiled-map support, `hitbox` adds precise collision shapes. When adding a new capability, prefer a
new extension package over growing `Sprite.java`/`Stage.java` further (they are already ~2,000+
lines each).

The library is built on **Processing** (`org.processing:core`) for graphics/windowing and JOGL for
OpenGL (via the `central` profile's native dependencies). Jackson (`tools.jackson.*`) is used for
JSON/XML (de)serialization, e.g. in `extensions/fs` and `extensions/tiled`.

## Javadoc → documentation pipeline

The public API's Javadoc is the source of truth for the reference documentation on
https://scratch4j.openpatch.org. The custom doclet (`Scratch4JDoclet`) reads special tags on public
methods and emits per-method reference pages:

- `@example.folder <Name>` — matches a directory under `src/examples/java/reference/<Name>/`
  containing a self-contained `MySprite.java` / `MyStage.java` / `MyWindow.java` example (package
  `reference.<Name>`). The `MyWindow` typically wraps the example in a `GifRecorder` to produce the
  preview GIF.
- `@example.preview <file>.gif` — the GIF (usually generated by the example itself) shown alongside
  the docs.
- `@example.files a.java;b.java;...` — which files from that folder to display as source.
- `@index-in-docs`, `@name-in-docs`, `@ignore-in-docs`, `@scratchblock` — control ordering, display
  name, exclusion, and the Scratch-block visual shown for a member.

When adding or changing a public method that should be documented, add/update the matching folder
under `src/examples/java/reference/` and the corresponding tags on the Javadoc comment — the doclet
will not generate a page without both.

## Versioning & release flow

Releases are changeset-driven (see `.changeset/README.md`):

1. Add a Markdown file to `.changeset/` with frontmatter `type: patch|minor|major` and a description
   of the change, as part of the PR.
2. Merging to `main` triggers `.github/workflows/version.yml`, which bumps `pom.xml`'s version and
   opens a version-bump PR (updates `CHANGELOG.md` too).
3. Merging that PR triggers `.github/workflows/release.yml`, which publishes to Maven Central and
   creates a GitHub release with the shaded ("all") JAR and Javadocs.

Don't hand-edit the version in `pom.xml` — it's managed by that workflow.
