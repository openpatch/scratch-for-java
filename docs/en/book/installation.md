---
name: Installation
index: 1
---

# Installation

## BlueJ

You can install this library via three ways in BlueJ.

First, you need to download the Scratch for Java jar for your operating system.

::download[Windows Jar]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch-standalone-windows-amd64.jar"}

::download[Linux Jar]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch-standalone-linux-amd64.jar"}

::download[MacOS Jar]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch-standalone-macosx-universal.jar"}

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

## Processing

### Install with the Contribution Manager

Add contributed Libraries by selecting the menu item _Sketch_ → _Import Library..._ → _Add Library..._ This will open the Contribution Manager, where you can browse for Scratch, or any other Library you want to install.

Not all available Libraries have been converted to show up in this menu. If a Library isn't there, it will need to be installed manually by following the instructions below.

### Manual Install

Contributed Libraries may be downloaded separately and manually placed within the `libraries` folder of your Processing sketchbook. To find (and change) the Processing sketchbook location on your computer, open the Preferences window from the Processing application (PDE) and look for the "Sketchbook location" item at the top.

By default, the following locations are used for your sketchbook folder:

- For Mac users, the sketchbook folder is located inside `~/Documents/Processing`
- For Windows users, the sketchbook folder is located inside `My Documents/Processing`

Download Scratch from https://github.com/openpatch/scratch-for-java/releases/latest

Unzip and copy the contributed Library's folder into the `libraries` folder in the Processing sketchbook. You will need to create this `libraries` folder if it does not exist.

The folder structure for Library Scratch should be as follows:

```
Processing
  libraries
    Scratch
      examples
      library
        Scratch.jar
      reference
      src
```

Some folders like `examples` or `src` might be missing. After Library Scratch has been successfully installed, restart the Processing application.

## Standalone

You need to add the Jar file for your operating system to the classpath.

::download[Windows Jar]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch-standalone-windows-amd64.jar"}

::download[Linux Jar]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch-standalone-linux-amd64.jar"}

::download[MacOS Jar]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch-standalone-macosx-universal.jar"}
