# Copilot Instructions for Scratch for Java

## Repository Overview

**Scratch for Java** is a Java library that replicates the functionality and concepts of Scratch, helping learners transition from block-based programming to text-based coding in Java. The library provides an approachable API inspired by Scratch blocks, making it easier for beginners to understand programming concepts while gaining experience with real Java syntax and tools.

- **Main Package**: `org.openpatch.scratch`
- **Version**: see `<version>` in `pom.xml` — managed automatically by the changeset release flow, do not hand-edit it
- **Java Version**: 17
- **Build Tool**: Maven
- **Documentation Site**: https://scratch4j.openpatch.org
- **Tests**: none — there is no JUnit dependency and no `*Test.java` files under `src/main`. Validate changes with `mvn compile` and by checking the relevant demo/reference example still behaves correctly.

## High-Level Architecture

The library is structured around three core concepts:
1. **Window**: the singleton application window (`Window` class) — owns the render loop and the current `Stage`
2. **Stage**: the active scene, holding sprites, backdrops, and `when*` event handlers (key press, mouse click, broadcasts, ...)
3. **Sprite**: interactive objects that can move, detect collisions, play sounds, etc., with a `run()` method invoked every frame

### Key Dependencies (see `pom.xml`)
- **Processing** (`org.processing:core`) — core graphics and windowing framework
- **Jackson** (`tools.jackson.*` + `com.fasterxml.jackson.core:jackson-annotations`) — JSON/XML processing, used e.g. in `extensions/fs` and `extensions/tiled`
- **word-wrap** (`com.github.davidmoten:word-wrap`) — text wrapping for `extensions/text`

## Build Instructions

### Prerequisites
- Java 17+ (CI builds with Temurin; `docs.yml` uses Java 25, `release.yml` uses Java 17 — either works locally, but match `maven.compiler.release=17` for compatibility)
- Maven
- Node.js + npm (for the documentation site only)

### Maven Build Commands

```bash
mvn compile                # basic compilation (the day-to-day command from README.md)
mvn clean package          # create the standard JAR
```

**Build Profiles**:
```bash
mvn deploy -Pcentral         # release to Maven Central: GPG signing + central-publishing-maven-plugin
                              # (the real release path — not something to run casually)
mvn clean package -Pall      # standalone JAR with all dependencies shaded in -> target/*-all.jar
```

### Documentation Build

The documentation site uses **Hyperbook** (Node.js-based static site generator) and lives entirely under `docs/` (single language, English):

```bash
cd docs
npx hyperbook dev    # start development server
npx hyperbook build  # build static documentation
```

`./build.sh` (run from the repo root) is the full pipeline CI uses: it copies `CHANGELOG.md` into `docs/book/changelog.md`, regenerates the reference GIFs from `src/examples/java/reference` into `docs/public/reference/`, substitutes the version into `docs/book/download.md` / `docs/book/index.md`, then runs `npx hyperbook build`.

## Project Layout and Architecture

### Source Code Structure
```
src/main/java/org/openpatch/scratch/
├── Window.java                       # singleton application window
├── Stage.java                        # scene: sprites, backdrops, event handlers
├── Sprite.java                       # core interactive object
├── Operators.java                    # Scratch "Operators" block equivalents
├── KeyCode.java / MouseCode.java / RotationStyle.java
├── extensions/                       # optional capabilities, one sub-package each:
│   ├── animation/                    # AnimatedSprite etc.
│   ├── camera/                       # camera/viewport
│   ├── color/                        # color manipulation
│   ├── fs/                           # file system / save-data
│   ├── hitbox/                       # precise collision shapes
│   ├── math/                         # math utilities
│   ├── pen/                          # drawing/pen
│   ├── recorder/                     # GIF recording (Recorder / GifRecorder)
│   ├── shader/                       # OpenGL shader support
│   ├── shape/                        # geometric shapes
│   ├── text/                         # text rendering
│   ├── tiled/                        # Tiled map support
│   └── timer/                        # timers
└── internal/                         # implementation details, not public API
    (Processing glue, image/font/sound loading, GIF encoding, simplex noise, ...)

src/examples/java/
├── demos/                            # complete runnable example projects
└── reference/                        # one folder per documented API method/feature

src/tools/java/doclets/
└── Scratch4JDoclet.java              # custom Javadoc doclet that generates the reference docs

docs/                                  # Hyperbook documentation site
├── book/                              # page content (tutorials, examples, generated reference)
├── archives/                          # starter project templates (vs-code-starter, bluej-starter)
│                                       # and other downloadable/archived project snapshots
├── templates/                        # Handlebars templates used by the doclet-generated pages
└── public/                            # static assets, incl. generated reference GIFs
```

Prefer adding a new `extensions/` sub-package for new capabilities over growing `Sprite.java`/`Stage.java` further (they are already ~2,000+ lines each) — e.g. `AnimatedSprite` (animation) extends `Sprite` rather than adding animation state directly to it.

`src/examples/java` and `src/tools/java` are wired into the main build via the
`build-helper-maven-plugin` (`add-examples-source` execution), so they compile as part of the
project even though they live outside `src/main`. They are excluded from the packaged JAR and
sources JAR (see the `maven-jar-plugin` / `maven-source-plugin` `<excludes>` in `pom.xml`).

### Configuration Files
- `pom.xml` - Maven build configuration with `central` and `all` profiles
- `.vscode/settings.json` - VS Code Java project settings (Hyperbook root, formatter)
- `docs/hyperbook.json` - documentation site config
- `.github/workflows/` - CI/CD pipelines (`version.yml`, `release.yml`, `docs.yml`)

## Continuous Integration

1. **Version Bump Workflow** (`.github/workflows/version.yml`) — triggers on changeset files in `.changeset/`, bumps the version (patch/minor/major), updates `CHANGELOG.md`, opens a release PR.
2. **Release Workflow** (`.github/workflows/release.yml`) — triggers when the version-bump PR is merged; deploys to Maven Central (`mvn deploy -Pcentral`, GPG-signed), builds the fat JAR (`-Pall`), and creates a GitHub release.
3. **Documentation Workflow** (`.github/workflows/docs.yml`) — on push to `main`: `mvn clean package`, `./build.sh`, copies `target/apidocs` into the built docs, deploys to GitHub Pages.

### Release Flow (changesets)
Releases are changeset-driven (`.changeset/README.md`): add a Markdown file to `.changeset/` with
frontmatter `type: patch | minor | major` and a description as part of your PR. Merging that PR
triggers the version-bump workflow above; merging *its* PR triggers the actual release.

## Javadoc → Documentation Pipeline

Public API Javadoc is the source of truth for https://scratch4j.openpatch.org. The custom doclet
(`Scratch4JDoclet`) reads special tags on public members and emits per-method reference pages:

- `@example.folder <Name>` — matches `src/examples/java/reference/<Name>/`, a self-contained example
  (package `reference.<Name>`) with `MySprite.java` / `MyStage.java` / `MyWindow.java`. `MyWindow`
  typically wraps the example in a `GifRecorder` to produce the preview GIF.
- `@example.preview <file>.gif` — the GIF shown alongside the docs.
- `@example.files a.java;b.java;...` — which files from that folder to display as source.
- `@index-in-docs`, `@name-in-docs`, `@ignore-in-docs`, `@scratchblock` — control ordering, display
  name, exclusion, and the Scratch-block visual shown for a member.

When adding or changing a documented public method, add/update the matching folder under
`src/examples/java/reference/` and the Javadoc tags together — the doclet won't generate a page
without both.

## Common Development Patterns

### Application Entry Point
```java
import org.openpatch.scratch.Window;

public class MyWindow extends Window {
  public MyWindow() {
    super(800, 600, "assets");
    this.setStage(new MyStage());
  }

  public static void main(String[] args) {
    new MyWindow();
  }
}
```

### Basic Sprite Implementation
```java
import org.openpatch.scratch.Sprite;

public class MySprite extends Sprite {
  public MySprite() {
    this.setCostume("name", "path/to/image.png");
    this.setPosition(100, 100);
  }

  public void run() {
    // Called every frame
    this.ifOnEdgeBounce();
    this.move(5);
  }
}
```

### Stage with Event Handlers
```java
import org.openpatch.scratch.Stage;

public class MyStage extends Stage {
  public MyStage() {
    this.add(new MySprite());
    this.setWhenKeyPressed((stage, keyCode) -> {
      // Handle key press events
    });
  }
}
```

## Working with This Repository

Focus on:
1. Understanding the core Sprite/Stage/Window architecture
2. Using the reference examples in `src/examples/java/reference/` and demos in `src/examples/java/demos/` as patterns
3. Keeping new capabilities in their own `extensions/` sub-package rather than expanding `Sprite.java`/`Stage.java`
4. Adding/updating the matching `src/examples/java/reference/<Name>/` folder and Javadoc tags whenever a public, documented API changes
5. Testing documentation builds with `npx hyperbook dev` in `docs/` if editing documentation content
