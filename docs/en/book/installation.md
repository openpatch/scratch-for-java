---
name: Installation
index: 1
---

# Installation

## BlueJ

You can install this library via three ways in BlueJ.

First, you need to download the Scratch for Java jar for your operating system.

::download[Windows Jar]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch4j-windows-amd64.jar"}

::download[Linux Jar]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch4j-linux-amd64.jar"}

::download[MacOS Jar]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch4j-macosx-universal.jar"}

### Project-by-project

Create a directory called "+libs" inside your project. Copy the Scratch for Java jar into the "+libs" folder. Restart BlueJ - done. Scratch for Java will now be available for this project.

### User Preferences

Open the "Preferences" dialogue and select the "Libraries" tab. Then add the location where the Scratch for Java jar is located. Restart BlueJ - done. Scratch for Java will now be available in all of your projects that you open.

You need to download the Jar file for your operating system and put it in the **+libs** folder for a local installation.

### System Wide

You need to locate the "userlib" folder, found at `<bluej-dir>/lib/userlib`. Place the Scratch for Java jar into this folder. Restart BlueJ - done. Scratch for Java will now be available in alle projects for all users.

If you have installed BlueJ via the installer you probably find the "userlib" folder here:

- Windows: `C:\Program Files\BlueJ\lib\userlib`
- MacOS: `/Application/BlueJ/BlueJ.app/Resources/java/userlib`
- Linux: `/usr/share/bluej/userlib/`

## Standalone

You need to add the Jar file for your operating system to the classpath.

::download[Windows Jar]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch4j-windows-amd64.jar"}

::download[Linux Jar]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch4j-linux-amd64.jar"}

::download[MacOS Jar]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch4j-macosx-universal.jar"}
