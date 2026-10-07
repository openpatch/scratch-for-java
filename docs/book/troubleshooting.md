---
name: It doesn't work!
index: 5
---

# It doesn't work!

Everybody's programs go wrong, all the time. That is normal. Here are the mistakes
that happen most often, and how to fix them. Find the one that sounds like your
problem.

## My sprite does not appear

**You forgot `this.add(...)`.** Creating a sprite with `new` is not enough. It
also has to be put on the stage:

```java
Bunny bunny = new Bunny();
this.add(bunny);   // without this line, the bunny exists but is not on the stage
```

**The stage is never created.** Something has to run `new MyStage()`. In the
browser and in Studio that is the `void main()` at the top. In BlueJ, right-click
`MyStage` and choose `new MyStage()`.

**It is off the stage or very small.** The middle of the stage is x: 0, y: 0. A
stage is usually 480 wide and 360 high, so x goes from -240 to 240 and y from -180
to 180. Check `setPosition` and `setSize`.

## "Costume not found" or a picture is missing

Look at the output below the editor, or the console in BlueJ. Scratch for Java
tells you which name it could not find and suggests the closest names it knows.

- Names are case-sensitive: `bunny1_stand` works, `Bunny1_Stand` does not.
- Look the name up on the [Sprites](/sprites) page. Click a picture there to copy
  the line that uses it.
- Your own picture must be in the project folder, and the path must match exactly,
  including `.png` or `.jpg`.

## Pressing keys does nothing

**Click on the stage first.** The stage only gets the keys when it has the focus,
just like a text field needs a click before you can type into it.

**The method is never called.** `whenKeyPressed` must be written exactly like this,
or Java treats it as a new method of yours that nobody calls:

```java
public void whenKeyPressed(KeyCode key) {
  if (key == KeyCode.SPACE) {
    this.say("Jump!");
  }
}
```

Put `@Override` on the line above. Then Java reports an error if the name or the
parameter is not quite right. The same goes for `run`, `whenClicked` and
`whenIReceive`.

## The program freezes

There is a loop in `run()` that never ends, or one that waits:

```java
public void run() {
  while (true) {        // never finishes, so the next frame is never drawn
    this.move(1);
  }
}
```

`run()` is already called again and again, about 60 times a second. Take the loop
out and let `run()` do the repeating. To wait, use a timer:
[Differences to Scratch](/differences-scratch) shows how.

## Something happens far too often

Everything in `run()` happens **in every frame**. A coin that touches the basket
for half a second counts 30 times:

```java
public void run() {
  if (this.isTouchingSprite(Basket.class)) {
    this.score = this.score + 1;     // happens in every frame while touching
  }
}
```

Make sure the reason goes away: move the coin back to the top, hide it, or
remember in a `boolean` that it has already been counted.

## A text comparison is never true

Texts are compared with `equals`, not with `==`:

```java
if (message.equals("go")) {   // right
}
if (message == "go") {        // compiles, but is usually false
}
```

## A division gives the wrong number

`7 / 2` is `3` in Java, because two whole numbers give a whole number. Write
`7.0 / 2` or use `double` variables if you want `3.5`.

## What the error messages mean

Java checks your program before it runs. The error message names the line, and
the mistake is usually there or on the line just before it.

| Message | What it usually means |
| --- | --- |
| `cannot find symbol` | A name is misspelled, or has the wrong capital letters (`setx` instead of `setX`). Or the `import` line at the top is missing. |
| `';' expected` | A semicolon is missing at the end of the line before. |
| `reached end of file while parsing` | A `}` is missing. Every `{` needs one. |
| `class, interface, enum, or record expected` | There is one `}` too many, or code outside a class. |
| `incompatible types: String cannot be converted to int` | You used text where a number is needed. `Integer.parseInt(text)` turns text into a number. |
| `missing return statement` | A method that promises a value (`int`, `boolean`, …) has a way to end without `return`. |
| `method does not override or implement a method from a supertype` | Your `@Override` caught a typo in the method's name or parameter, see [keys](#pressing-keys-does-nothing). |
| `class Coin is public, should be declared in a file named Coin.java` | The file name must match the class name exactly. |
| `non-static method … cannot be referenced from a static context` | You called a method on the class instead of on an object: `Coin.move(5)` instead of `coin.move(5)`. |

## Still stuck?

- Compare with the finished project at the end of the tutorial. The download link
  is there.
- Read your code out loud, line by line, and say what each line does.
- When you ask someone for help, show them three things: your code, the exact
  error message, and what you expected to happen.
