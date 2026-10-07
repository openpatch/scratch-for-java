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
| **BlueJ, VS Code or another Java editor** | Java 17 or newer and the library, see below. | Courses that already use one of these |

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
- **In BlueJ or VS Code** your project is a folder as well. Copy the whole folder,
  including the `+libs` folder with the library inside.

## From the browser to your own computer

The examples in the browser start with a `void main()` above the classes:

```java
void main() {
  new MyStage();
}
```

That is a complete Java program in Java 25 and newer, and Studio runs it as it
is. In BlueJ you leave it out and create the stage by right-clicking `MyStage`
and choosing `new MyStage()`. Elsewhere, put the same line into a `main` method
of your stage:

```java
import org.openpatch.scratch.*;

public class MyStage extends Stage {
  public MyStage() {
    Sprite bunny = new Sprite();
    bunny.addCostume("bunny1_stand");
    this.add(bunny);
  }

  public static void main(String[] args) {
    new MyStage();
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

Then [download](/download) the Scratch for Java JAR.

Afterward, you need to add the jar to your project. You can do this by adding the following to your `settings.json`:

```json
"java.project.referencedLibraries": [
    "path/to/scratch4j.jar"
]
```

If you want to be compatible with BlueJ you should use a "+libs" folder in your project, add the jar there and then add the following to your `settings.json`:

```json
"java.project.referencedLibraries": [
    "+libs/*.jar"
]
```

## BlueJ

You can install this library via three ways in BlueJ.

First, [download](/download) the Scratch for Java JAR, or start from the BlueJ starter project there, which already has it in its `+libs` folder.

### Project-by-project

Create a directory called "+libs" inside your project. Copy the Scratch for Java jar into the "+libs" folder. Restart BlueJ - done. Scratch for Java will now be available for this project.

### User Preferences

Open the "Preferences" dialogue and select the "Libraries" tab. Then add the location where the Scratch for Java jar is located. Restart BlueJ - done. Scratch for Java will now be available in all of your projects that you open.

### System Wide

You need to locate the "userlib" folder, found at `<bluej-dir>/lib/userlib`. Place the Scratch for Java jar into this folder. Restart BlueJ - done. Scratch for Java will now be available in all projects for all users.

If you have installed BlueJ via the installer you probably find the "userlib" folder here:

- Windows: `C:\Program Files\BlueJ\lib\userlib`
- MacOS: `/Application/BlueJ/BlueJ.app/Resources/java/userlib`
- Linux: `/usr/share/bluej/userlib/`

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

We have also prepared a starter for you: [Maven Starter](https://github.com/openpatch/scratch-for-java-maven-starter/tree/main)
