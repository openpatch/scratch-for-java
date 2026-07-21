# Red Light, Green Light - finished project

The completed code from the [Red Light, Green Light](https://scratch4j.openpatch.org/tutorials/red-light-green-light)
tutorial.

## Step 1: Get the library

Download `scratch-<version>-all.jar` from
https://scratch4j.openpatch.org/download and put it in the `+libs` folder next
to this file.

## Step 2: Run it

**BlueJ** — open this folder, then right-click `RaceStage` and choose `new RaceStage()`.

**VS Code** — open this folder. The `.vscode` settings already point at `+libs`,
so press the Run button above `main` in `RaceStage.java`.

**A terminal** — from inside this folder:

```
javac -cp "+libs/*" -d . *.java
java -cp "+libs/*:." RaceStage
```

On Windows the classpath separator is `;` rather than `:`:

```
java -cp "+libs/*;." RaceStage
```

## Assets

None. Every costume, backdrop and sound this project uses is built into Scratch
for Java and is referred to by name, so there are no image or sound files to
keep track of.
