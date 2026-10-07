---
name: Setup
index: 2
---

# Setup

:::alert{info}
Does your course use the Abiturklassen of the NRW Zentralabitur (`List`, `Queue`,
`Graph`, …)? Then read [Abiturklassen NRW](/abitur-nrw) first. There is a version
of Scratch for Java made for them.
:::

## Choose where to start

| Start | What to do | Saving and continuing |
| --- | --- | --- |
| **Try in the browser** | Open [Your first program](/tutorials/getting-started) and press ▶. The Scratch library is already selected. | Download workspace JSON or, in an IDE revision with ZIP support, a project ZIP. Browser storage belongs to this browser on this computer. |
| **Create with Studio** | [Download Studio](/download), create a project, and add the rabbit from the same first tutorial. Studio includes Java and the library. | Save ordinary Java and asset files. Open browser workspace JSON or a project ZIP through the Project menu. Export a Browser project ZIP to return to the browser. |
| **Use your Java IDE** | Add the standard or NRW library to BlueJ, VS Code, Maven, or your existing Java project. | Keep Java and assets together; optional Studio metadata is not needed to run the project. |

All three use **Stage**, **Sprite**, **costume**, and the same rabbit costume,
`bunny1_stand`. Continue with the [seven introductory tutorials](/tutorials).
German material is available at [Erste Schritte](/erste-schritte) and the
[Spielwerkstatt in Hyperbook Informatik](https://informatik.openpatch.org/projekte/spielwerkstatt).

## Java and library flavor

The desktop **library runs on Java 17 or newer**. Ordinary named classes with
`public static void main(String[] args)` work with Java 17. Compact source files
with `void main()` require **Java 25**; Studio uses Java 25 and includes its
runtime. Its installer needs no separate JDK. The browser runs its own Java
compiler and interpreter.

Use **standard** unless your course uses the NRW Abiturklassen. Select **NRW**
consistently in Studio and the browser. The browser provides NRW classes; desktop
projects also need the course's `List.java` and other Abiturklassen used by the
project. Checkpoints include those course files. Changing flavor can change the
return type of `find`, `getAll`, and `getTouchingSprites`.

## Studio

Create a project from the start screen. Run the stage with ▶, save changes with
Ctrl/Cmd+S, and keep a project ZIP before moving computers. Studio has a stage
designer, costume and sound tools, an asset browser, debugger, and Java editor.
For the first tutorial, create `MyStage` and use its code unchanged. You can also
import the tutorial's finished checkpoint through the project menu.

For offline lessons, prepare the [course pack](/classroom) and both library
flavors before class. Keep a backup of students' Java folders or exported ZIPs.
Studio's local history and project snapshots can restore saved work; unsaved
changes and cleared browser data cannot be recovered from another computer.

## VS Code

First make sure you have the Java Extension Pack installed. You can find it in the Extensions view by searching for `redhat.java`.

Then you need to download the Scratch for Java jar for your [operating system](/download).

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

First, you need to download the Scratch for Java jar for your [operating system](/download).

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

You need to add the Jar file for your [operating system](/download) to the classpath.


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
