# Copilot Instructions for Scratch for Java

## Repository Overview

**Scratch for Java** is a Java library that replicates the functionality and concepts of Scratch, helping learners transition from block-based programming to text-based coding in Java. The library provides an approachable API inspired by Scratch blocks, making it easier for beginners to understand programming concepts while gaining experience with real Java syntax and tools.

- **Main Package**: `org.openpatch.scratch`
- **Current Version**: 4.24.1
- **Java Version**: 17
- **Build Tool**: Maven
- **Documentation Site**: https://scratch4j.openpatch.org
- **Repository Size**: ~5,500 lines of core Java code, 146 reference examples, 59 demo projects

## High-Level Architecture

The library is structured around three core concepts:
1. **Window**: The main application window (extends `Window` class)
2. **Stage**: The game/application stage where sprites and elements are displayed
3. **Sprite**: Interactive objects that can move, detect collisions, play sounds, etc.

### Key Dependencies
- **Processing 4.4.6**: Core graphics and windowing framework
- **Jackson 2.19.2**: JSON/XML processing
- **JOGL 2.5.0**: OpenGL bindings (from jogamp repository)
- **Gluegen 2.5.0**: Native library loading

## Build Instructions and Known Issues

### Prerequisites
- Java 17+ (verified with OpenJDK 17.0.16)
- Maven 3.9.11+
- Node.js + npm (for documentation)

### Maven Build Commands

**Standard Build Commands**:
```bash
mvn clean compile          # Basic compilation
mvn clean package          # Create standard JAR
mvn clean test             # Run tests (if any)
```

**Build Profiles**:
```bash
# For Maven Central release (includes native dependencies)
mvn clean compile -Pcentral

# For standalone JAR with all dependencies
mvn clean package -Pall      # Creates target/*-all.jar
```

**Note**: The library uses JOGL for OpenGL graphics rendering, which requires access to the jogamp.org repository. This dependency is now working correctly in the CI environment.

### Documentation Build

The documentation uses **Hyperbook** (Node.js-based static site generator):

```bash
cd docs
npx hyperbook dev    # Start development server
npx hyperbook build  # Build static documentation
```

Documentation includes:
- English and German versions (`docs/en/` and `docs/de/`)
- Reference examples with generated GIFs
- API documentation generated from Java source

### Documentation Coverage Validation

Use the `s4j` script to check documentation coverage:

```bash
./s4j                  # Check documentation coverage
./s4j --create         # Create missing documentation files
```

This script validates that all public classes and methods have corresponding documentation files in both English and German.

## Project Layout and Architecture

### Source Code Structure
```
src/
├── main/java/org/openpatch/scratch/
│   ├── Stage.java                    # Core stage class (1,907 lines)
│   ├── Sprite.java                   # Core sprite class (2,173 lines)
│   ├── Window.java                   # Main window class (304 lines)
│   ├── extensions/                   # Extension modules
│   │   ├── animation/                # Sprite animation support
│   │   ├── camera/                   # Camera/viewport functionality
│   │   ├── color/                    # Color manipulation
│   │   ├── fs/                       # File system operations
│   │   ├── math/                     # Mathematical utilities
│   │   ├── pen/                      # Drawing/pen functionality
│   │   ├── recorder/                 # GIF/video recording
│   │   ├── shader/                   # OpenGL shader support
│   │   ├── shape/                    # Geometric shapes
│   │   ├── text/                     # Text rendering
│   │   └── timer/                    # Timer functionality
│   └── internal/                     # Internal implementation classes
└── examples/java/
    ├── demos/                        # 59 complete demo projects
    └── reference/                    # 146 method reference examples
```

### Configuration Files
- `pom.xml` - Maven build configuration with multiple profiles
- `.vscode/settings.json` - VS Code Java project settings
- `.vscode/extensions.json` - Recommended VS Code extensions
- `docs/hyperlibrary.json` - Multi-language documentation config
- `.github/workflows/` - CI/CD pipelines

### Key Build Profiles
- **Default**: Basic compilation (currently broken due to jogamp issue)
- **central**: Maven Central publishing with native dependencies and GPG signing
- **all**: Creates standalone JAR with all dependencies using maven-shade-plugin

## Continuous Integration and Validation

### GitHub Workflows

1. **Version Bump Workflow** (`.github/workflows/version.yml`)
   - Triggers on changeset files in `.changeset/`
   - Automatically bumps version (patch/minor/major)
   - Creates release PR

2. **Release Workflow** (`.github/workflows/release.yml`)
   - Triggers when release PR is merged
   - Publishes to Maven Central with GPG signing
   - Creates GitHub release with fat JAR
   - Publishes Javadocs to GitHub Pages
   - Deletes changeset branch

3. **Javadocs Workflow** (`.github/workflows/javadocs.yml`)
   - Builds and publishes API documentation

### Validation Steps
1. **Documentation Coverage**: Run `./s4j` to ensure all public APIs are documented
2. **Example Compilation**: All reference examples should compile successfully
3. **Maven Build**: Standard build commands work with all profiles
4. **Documentation Build**: `npx hyperbook build` should complete successfully

## Templates and Starter Projects

### VS Code Starter Template (`templates/vscode-starter/`)
```java
// MyWindow.java - Main application entry point
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

### BlueJ Starter Template (`templates/bluej-starter/`)
Similar structure optimized for BlueJ IDE.

## Common Development Patterns

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

## Development Environment Setup

### VS Code Setup
The repository includes VS Code configuration with:
- Java extension pack recommended
- Hyperbook Studio for documentation editing  
- Custom Java source paths including examples
- Java formatter configuration

### Required Extensions
- `redhat.java` - Java language support
- `openpatch.hyperbook-studio` - Documentation editing

## Troubleshooting Common Issues

### Build Failures
1. **Maven cache issues**: Run `mvn clean` and retry
2. **Java version mismatch**: Ensure Java 17+ is installed and active
3. **Dependency download issues**: Check network connectivity and retry

### Documentation Issues
1. **Missing documentation warnings**: Run `./s4j --create` to generate templates
2. **Hyperbook build failures**: Ensure Node.js dependencies are installed in `docs/en/`

### Example Compilation
Examples in `src/examples/java/` should compile with the main source. They're included via the build-helper-maven-plugin.

## Working with This Repository

**ALWAYS** trust these instructions and refer to them before exploring. Focus on:

1. Understanding the core Sprite/Stage/Window architecture
2. Using the reference examples in `src/examples/java/reference/` as patterns
3. Running `./s4j` to validate documentation coverage
4. Testing documentation builds with `npx hyperbook dev` in the `docs/` directory

When making changes, ensure:
- All public APIs have corresponding documentation
- Examples compile successfully
- Changes follow the established patterns in existing code
- Documentation is updated in both English and German if adding new features