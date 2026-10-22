---
name: Setup
index: 1
---

# Setup

There are three places to write Scratch for Java. The code is the same in all of
them, so you can start in one and move on to another later.

| | What you need | Best for |
| --- | --- | --- |
| **Browser** | Nothing. Open [Your first program](/tutorials/getting-started) and press ▶. | Trying it out, the first lessons, homework |
| **Studio** | [Download Studio](/download) and install it. Java and the library are included. | Working on your own computer or school computers, also offline |
| **VS Code or another Java editor** | Java 17 or newer and the library, see below. | Courses that already use one of these |

:::alert{info}
Does your course use the classes of the NRW Zentralabitur (`List`, `Queue`,
`Graph`, …)? Then read [Abiturklassen NRW](/abitur-nrw) first. There is a version
of Scratch for Java made for them.
:::

## Saving your work

- **In the browser** your code is kept **only in this browser on this computer**.
  Download your project before you switch computers or clear the browser, and
  load it again on the other side. Studio can open these downloads too.
- **In Studio** press Ctrl+S (Cmd+S on a Mac). Your project is an ordinary folder
  of Java files and pictures. Keep that folder together when you copy it.
- **In VS Code or another editor** your project is a folder as well. Copy the
  whole folder, including the `+libs` folder with the library inside.

## From the browser to your own computer

The examples in the browser start with a `void main()` above the classes:

```java
void main() {
  Window myWindow = new Window(480, 360);
  myWindow.setStage(new MyStage());
}
```

That is a complete Java program in Java 25 and newer, and Studio runs it as it
is. In other editors and with older Java, put the same lines into a `main`
method of your stage and run `MyStage`:

```java
import org.openpatch.scratch.*;

public class MyStage extends Stage {
  public MyStage() {
    Sprite bunny = new Sprite();
    bunny.addCostume("bunny1_stand");
    this.add(bunny);
  }

  public static void main(String[] args) {
    Window myWindow = new Window(480, 360);
    myWindow.setStage(new MyStage());
  }
}
```

Your own Java files also need the `import org.openpatch.scratch.*;` at the top.
The browser adds it for you.

## Studio

Create a project from the start screen and run the stage with ▶. Studio has a
stage designer, costume and sound tools, a browser for the built-in pictures and
sounds, and a debugger. You can also import the finished project of each
tutorial through the **Project** menu.

## VS Code

First make sure you have the Java Extension Pack installed. You can find it in the Extensions view by searching for `redhat.java`.

Then [download](/download) the Scratch for Java JAR, or start from the VS Code
starter project there, which is set up already.

Create a folder called `+libs` inside your project and put the jar into it. Studio
keeps the library in the same place, so a project opens in both. Then add the
following to the `.vscode/settings.json` of your project:

```json
"java.project.referencedLibraries": [
    "+libs/*.jar"
]
```

## Standalone

Add the [JAR](/download) to the classpath.


## Maven

You can also use Scratch for Java via Maven. Just add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.openpatch</groupId>
    <artifactId>scratch</artifactId>
    <version>{{VERSION}}</version>
</dependency>
```

The extensions are artifacts of their own. Add the ones you use, with the same
group and version (the `-all` jar and Studio contain all of them):

| Artifact | Package | For |
| --- | --- | --- |
| `scratch-tiled` | `org.openpatch.scratch.tiled` | maps from the Tiled map editor |
| `scratch-save` | `org.openpatch.scratch.save` | saving progress, JSON and XML files |
| `scratch-recorder` | `org.openpatch.scratch.recorder` | recording the stage as a GIF |
| `scratch-drawing` | `org.openpatch.scratch.drawing` | sprites that draw themselves, bitmap fonts |

We have also prepared a starter for you: [Maven Starter](https://github.com/openpatch/scratch-for-java-maven-starter/tree/main)
