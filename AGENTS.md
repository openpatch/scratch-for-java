# AGENTS.md

Guidance for coding agents (and people) working in this repository.

## 7.0 rewrite (branch `v7`) — read this first

7.0 replaces Processing with LWJGL and moves the extensions into jars of their own. Where they
exist, `plan.md` (specification and decision log) and `v7-execution.md` (requirements, evidence,
log) are local working notes and are not committed. 7.0 breaks 6.x on purpose wherever the design needs it
(`docs/book/migrate-to-7.md` lists every change). The 6.x sources are on `main` and in the
history; everything in them has been ported.

## What this is

Scratch for Java (`org.openpatch.scratch`) is a Java library that ports the Scratch (scratch.mit.edu)
block-based programming model to Java, so learners can move from blocks to text-based code while
keeping the same mental model (Window / Stage / Sprite, costumes, backdrops, broadcasts, etc.).

## Build & commands

- **Never `mvn install` on `v7`.** The version is still the released one (6.5.0, managed by the
  release workflow), so an install overwrites the real `org.openpatch:scratch:6.5.0` in `~/.m2`
  with 7.0 code, and every other project on the machine (Studio) silently builds against it.
  Build inside the reactor instead (`-pl <module> -am`).
- `mvn test` — all modules, no window needed. If a test fails with "Unresolved compilation
  problems", an IDE (Eclipse compiler) wrote into `target/`: run `mvn clean test`.
- `mvn verify` — additionally builds the fat jar `all/target/scratch-<version>-all.jar` and runs
  `all/src/build/BuildChecks.java` (no `java.desktop` in shipped code, extensions use no
  `internal`, no NRW-sensitive calls or GLFW callbacks in extensions, fat-jar
  manifest/natives/notices).
- `mvn package -pl all,examples -am -DskipTests` — the fat jar and the examples, quickly.
- `mvn package -Pnrw` — the NRW fat jar (`scratch-<version>-nrw-all.jar`): sources rewritten by
  `core/src/build/NrwVariant.java` from the `// nrw:` markers in Stage and Sprite, so `find`,
  `getAll` and `getTouchingSprites` return the `List` of the NRW Zentralabitur.
- `mvn deploy -Pcentral` — release to Maven Central; done by the release workflow, not by hand.
- `./scripts/run.sh` — pick a demo or reference example and run it (`--each` runs every demo,
  `-- <jvm args>` passes JVM arguments); builds in the reactor first.
- `python3 scripts/migrate-7.py --out <dir> <6.x folder>` migrates 6.x code, shaders and the
  ```java blocks of Markdown pages (run once per project; it marks what needs a person with
  `// TODO 7.0:`).
- `./build.sh` — full doc site build (reference pages, demo pages, asset pages, version
  substitution, then `npx hyperbook build` in `docs/`); `cd docs && npx hyperbook@latest dev` runs
  the site locally (always the newest Hyperbook, as in CI).

Java 17 builds the library (`maven.compiler.release`), but the tests want Java 25: the
documentation's interactive examples are compact source files (`void main()` beside the classes),
and the tests that compile them skip on anything older. CI runs 25.

## Seeing and testing without a window

- Scene logic is tested headless: `Game.headless(480, 360)`, `game.setStage(stage)`,
  `game.runTicks(n)`, `game.drawList()` (see `core/src/test/.../SceneTest.java`). Sprites,
  costumes, hitboxes, clones, pens, colour sensing (`Pixels.of(stage)`), text layout, timers and
  transitions all work there.
- To see a frame: run a program with `-Dscratch4j.preview=<dir>` (writes `frame.png` and the
  sprites in `stage.json`, then ends). `-Dscratch4j.capture=<dir>` does the same per stage while the
  program keeps running (Studio uses it for its stage designer).
- Under Xvfb in a Wayland session: `XDG_SESSION_TYPE=x11 xvfb-run -a java -Dscratch4j.platform=x11 …`
  (GLFW picks Wayland otherwise). Under Xvfb the window is exactly the render size, so letterbox
  and HiDPI bugs do not reproduce there. `-Dscratch4j.debugGl` prints window, framebuffer, monitor
  and letterbox geometry and vsync switches.
- Reference examples load their assets relative to `examples/src/main/java/reference`: run them
  from there (`run.sh` does).
- Parity: `ParityTest` and `LifecycleParityTest` (core, headless; `./scripts/parity.sh --record`),
  `./scripts/visual-parity.sh [--record]` (23 frames under Xvfb, compared exactly).
- Documentation guards in the examples module: `DocumentationSnippetsTest` compiles every
  ```java block on the hand-written pages under `docs/book` — **if you change a public API, this
  tells you which tutorial you just broke**; `DemoPagesTest` checks that `docs/book/examples/*.md`
  match the demos they are generated from (a page opts in with `<!-- demo: <folder> -->` above its
  `:::onlineide` block). **After changing a demo, run `./scripts/update-demo-pages.sh`** and commit
  the pages it touches (it also copies the demos' assets to `docs/public/examples`, ignored).
  `OnlineExampleTest` compiles the interactive examples.

## Source layout

```
pom.xml                  parent (scratch-parent); modules in lockstep, one version
core/                    artifact `scratch`
  src/main/java/org/openpatch/scratch/
    Window, Stage, Sprite, Text, Pen, Camera, Layer, Shader(s), Transition, Pixels, Timer,
    Color, Vector2, shapes, Operators, KeyCode, ...    the learner API, one flat package
    Launcher             macOS: window on the first thread (-XstartOnFirstThread)
    extension/           public extension API (Extension, ExtensionContext, Canvas)
    internal/            not exported: Game, loop/ (game thread, tick clock), draw/ (draw list),
                         render/ (OpenGL), window/ (GLFW), canvas/ + compose/ (CPU pen canvases,
                         compositor for colour sensing), text/ (stb_truetype engine), audio/
                         (OpenAL), image/ (stb_image), geom/, input/, Assets, Preview, Release
  src/build/NrwVariant.java
extensions/{tiled,save,recorder,drawing}/   `scratch-<name>`, package org.openpatch.scratch.<name>,
                         public API only, registered through ServiceLoader
all/                     fat jar + src/build/BuildChecks.java
examples/                demos/ (runnable projects), reference/ (one example per documented
                         method), parity/; src/tools/java (doclet, docs.GenerateDemoPages,
                         docs.GenerateAssetPages); built, never published
templates/extension-starter/   standalone Maven project for third-party extensions
docs/                    Hyperbook site (English)
```

## Architecture

Three concepts mirror Scratch directly and almost every feature hangs off one of them:

- **Window** — the singleton window (`new Window(480, 360)`, then `setStage(...)`). Settings
  that must be known before it opens are static and called first (`usePixelArt()`,
  `useFullScreen()`, ...).
- **Stage** — one scene: sprites, texts, pens, backdrops, the `when*` event handlers.
- **Sprite** — costumes, animations, movement, collision/sensing, sound, and a `run()` method
  called every tick (a Scratch script attached to a sprite).

Threads: a **window thread** owns GLFW and OpenGL, a **game thread** runs
every tick and all learner callbacks, the learner's `main` runs on its own thread. They work in
lockstep: a tick is exactly 1/60 s of game time; at the end of a batch of ticks the game thread
builds a **draw list**, and that is all the renderer sees — the scene never draws itself.
Everything learners can observe (collisions, colour sensing through the CPU compositor, text
layout) is computed on the CPU and is the same on every GPU and in headless tests.

Extensions build on the public extension API only (`ExtensionContext` hooks: `beforeTick`,
layer and overlay drawers on a `Canvas`, `afterFrame`, `dispatch` to listener interfaces,
attachments via `stage.extension(...)`), never on `internal`, and register themselves through
`ServiceLoader`. New capabilities go into an extension if they can be built that way and pull in
a dependency (Jackson); things that need the renderer or the game loop stay in the core.

No `java.awt`/`java.desktop` in shipped code (BuildChecks enforces it); tests and build tools may
use it. Jackson (`tools.jackson.*`) is used by tiled and save.

## Javadoc → documentation pipeline

The public API's Javadoc is the source of truth for the reference documentation on
https://scratch4j.openpatch.org. `./scripts/reference-docs.sh` runs the doclet
(`examples/src/tools/java/doclets`) over core and the extensions and writes
`docs/book/reference`:

- `@example.files a.java;b.java` — files from `examples/src/main/java/reference` shown as source
  (`@example.folder <Name>` for a folder of them).
- `@scratchblock`, `@index-in-docs`, `@name-in-docs`, `@ignore-in-docs` — the Scratch block shown,
  ordering, display name, exclusion.

When adding or changing a public method that should be documented, add/update its reference
example and the tags on the Javadoc comment. Other generators: `docs.GenerateDemoPages --write`
(example pages from the demos) and `docs.GenerateAssetPages` (built-in sprites and sounds).

## Versioning & release flow

Releases are changeset-driven (see `.changeset/README.md`):

1. Add a Markdown file to `.changeset/` with frontmatter `type: patch|minor|major` and a description
   of the change, as part of the PR. **Only when the change reaches the user**: the public API moved,
   or a bug is gone. Documentation, tests, examples, tooling and CI changes get no changeset — they
   would put a release note in front of readers about something they cannot see from the library.
2. Merging to `main` triggers `.github/workflows/version.yml`, which bumps the version of every module
   (`mvn versions:set`, all poms in lockstep) and opens a version-bump PR (updates `CHANGELOG.md` too).
3. Merging that PR triggers `.github/workflows/release.yml`, which publishes to Maven Central and
   creates a GitHub release with the fat jars (`-all`, `-nrw-all`) and Javadocs.

Don't hand-edit the version in the poms — it's managed by that workflow.
