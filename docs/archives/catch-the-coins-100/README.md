# Catch the Coins - finished project

The completed code from the [Catch the Coins](https://scratch4j.openpatch.org/tutorials/catch-the-coins)
tutorial.

## Step 1: Get the library

Download `scratch-<version>-all.jar` from
https://scratch4j.openpatch.org/download and put it in the `+libs` folder next
to this file.

## Step 2: Run it

**BlueJ** — open this folder, then right-click `CatchStage` and choose `new CatchStage()`.

**VS Code** — open this folder. The `.vscode` settings already point at `+libs`,
so press the Run button above `main` in `CatchStage.java`.

**A terminal** — from inside this folder:

```
javac -cp "+libs/*" -d . *.java
java -cp "+libs/*:." CatchStage
```

On Windows the classpath separator is `;` rather than `:`:

```
java -cp "+libs/*;." CatchStage
```

## Assets

None. Every costume, backdrop and sound this project uses is built into Scratch
for Java and is referred to by name, so there are no image or sound files to
keep track of.
