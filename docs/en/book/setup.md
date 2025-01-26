---
name: Setup
index: 2
---

# Setup

## VS Code (Recommended)

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
