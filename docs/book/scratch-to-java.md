---
name: Scratch → Java cheat sheet
index: 3
---

# Scratch → Java cheat sheet

Which Java line replaces which block. Inside a sprite or stage class, every
method is called on `this`: `move (10) steps` becomes `this.move(10);`. Each line
ends with a semicolon. Look a method up in the [documentation](/reference) to see
the block, an explanation and an example you can run.

:::alert{info}
Times are in **milliseconds**: `1000` is one second. Only `glide` counts in
seconds, like Scratch.
:::

## Events

| Scratch | Java |
| --- | --- |
| when green flag clicked (setting up) | the constructor: `public Cat() { ... }` |
| when green flag clicked + forever | `public void run() { ... }` |
| when [space] key pressed | `public void whenKeyPressed(KeyCode key) { if (key == KeyCode.SPACE) { ... } }` |
| when this sprite clicked | `public void whenClicked() { ... }` |
| when stage clicked | in the stage: `public void whenMouseClicked(MouseCode button) { ... }` |
| when backdrop switches to [night] | `public void whenBackdropSwitches(String name) { if (name.equals("night")) { ... } }` |
| broadcast [start] | `this.broadcast("start");` |
| when I receive [start] | `public void whenIReceive(String message) { if (message.equals("start")) { ... } }` |

## Motion

| Scratch | Java |
| --- | --- |
| move (10) steps | `this.move(10);` |
| turn ↻ (15) degrees | `this.turnRight(15);` |
| turn ↺ (15) degrees | `this.turnLeft(15);` |
| go to x: (0) y: (0) | `this.setPosition(0, 0);` |
| go to [random position] | `this.goToRandomPosition();` |
| go to [mouse-pointer] | `this.goToMousePointer();` |
| glide (1) secs to x: (100) y: (0) | `this.glide(1, 100, 0);` |
| point in direction (90) | `this.setDirection(90);` |
| point towards [mouse-pointer] | `this.pointTowardsMousePointer();` |
| change x by (10) | `this.changeX(10);` |
| set x to (0) | `this.setX(0);` |
| change y by (10) | `this.changeY(10);` |
| set y to (0) | `this.setY(0);` |
| if on edge, bounce | `this.ifOnEdgeBounce();` |
| set rotation style [left-right] | `this.setRotationStyle(RotationStyle.LEFT_RIGHT);` |
| (x position), (y position), (direction) | `this.getX()`, `this.getY()`, `this.getDirection()` |

## Looks

| Scratch | Java |
| --- | --- |
| say [Hello!] for (2) seconds | `this.say("Hello!", 2000);` |
| say [Hello!] | `this.say("Hello!");` |
| think [Hmm...] | `this.think("Hmm...");` |
| *(adding a costume)* | in the constructor: `this.addCostume("bunny1_stand");` |
| switch costume to [bunny1_jump] | `this.switchCostume("bunny1_jump");` |
| next costume | `this.nextCostume();` |
| *(adding a backdrop)* | in the stage's constructor: `this.addBackdrop("background");` |
| switch backdrop to [night] | in the stage: `this.switchBackdrop("night");` |
| next backdrop | in the stage: `this.nextBackdrop();` |
| change size by (10) | `this.changeSize(10);` |
| set size to (50) % | `this.setSize(50);` |
| change [color] effect by (25) | `this.changeTint(25);` |
| set [ghost] effect to (50) | `this.setTransparency(50);` |
| show / hide | `this.show();` / `this.hide();` |
| go to [front] layer | `this.goToFrontLayer();` |
| (costume [name]), (size) | `this.getCurrentCostumeName()`, `this.getSize()` |

## Sound

| Scratch | Java |
| --- | --- |
| *(adding a sound)* | in the constructor: `this.addSound("handleCoins");` |
| start sound [handleCoins] | `this.playSound("handleCoins");` |
| stop all sounds | `this.stopAllSounds();` |
| set volume to (50) % | `this.setVolume(50);` |
| change volume by (-10) | `this.changeVolume(-10);` |

## Control

| Scratch | Java |
| --- | --- |
| forever | `run()` is already repeated, see [Differences to Scratch](/differences-scratch) |
| wait (1) seconds | `if (this.getTimer().everyMillis(1000)) { ... }` inside `run()` |
| repeat (10) | `for (int i = 0; i < 10; i++) { ... }` |
| if <> then | `if (...) { ... }` |
| if <> then … else | `if (...) { ... } else { ... }` |
| wait until <> | `if (...) { ... }` inside `run()`: it is checked again every frame |
| repeat until <> | `while (!...) { ... }`, but never inside `run()`. Usually an `if` in `run()` is what you want. |
| stop [all] | `Window.getInstance().exit();` |
| create clone of [myself] | `this.clone();` |
| when I start as a clone | `public void whenStartsAsClone() { ... }` |
| delete this clone | `this.deleteThisClone();` |

## Sensing

| Scratch | Java |
| --- | --- |
| <touching [Coin]?> | `this.isTouchingSprite(Coin.class)` |
| <touching [mouse-pointer]?> | `this.isTouchingMousePointer()` |
| <touching [edge]?> | `this.isTouchingEdge()` |
| (distance to [mouse-pointer]) | `this.distanceToMousePointer()` |
| ask [What's your name?] and wait | `this.ask("What's your name?");` and wait for `this.isAsking()` to become false |
| (answer) | `this.getAnswer()` |
| <key [space] pressed?> | `this.isKeyPressed(KeyCode.SPACE)` |
| <mouse down?> | `this.isMouseDown()` |
| (mouse x), (mouse y) | `this.getMouseX()`, `this.getMouseY()` |
| (timer) | `Timer.millis()` |
| reset timer | `this.getTimer().reset();`, then `this.getTimer().afterMillis(30000)` is true after 30 seconds |

## Operators

| Scratch | Java |
| --- | --- |
| (a + b), (a - b), (a * b), (a / b) | `a + b`, `a - b`, `a * b`, `a / b` (careful: `7 / 2` is `3`, but `7.0 / 2` is `3.5`) |
| (pick random (1) to (10)) | `this.pickRandom(1, 10)` |
| <a > b>, <a < b> | `a > b`, `a < b` |
| <a = b> for numbers | `a == b` |
| <a = b> for text | `a.equals(b)` |
| <<> and <>>, <<> or <>>, <not <>> | `... && ...`, `... \|\| ...`, `!...` |
| (join [Hello ] [world]) | `"Hello " + "world"` |
| (letter (1) of [apple]) | `"apple".charAt(0)`: Java counts from 0 |
| (length of [apple]) | `"apple".length()` |
| <[apple] contains [a]?> | `"apple".contains("a")` |
| (a mod b) | `a % b` |
| (round (2.6)) | `Operators.round(2.6)` |
| ([sqrt] of (9)) | `Operators.sqrtOf(9)` |

## Variables

A variable for one sprite goes at the top of its class. A variable for all
sprites goes into the stage. [Differences to Scratch](/differences-scratch#variables-belong-to-an-object)
shows both.

| Scratch | Java |
| --- | --- |
| make a variable [score] | `private int score = 0;` at the top of the class (`int` for whole numbers, `double` for decimals, `String` for text, `boolean` for true/false) |
| set [score] to (0) | `this.score = 0;` |
| change [score] by (1) | `this.score = this.score + 1;` |
| show variable [score] | `this.showVariable("score", () -> this.score);` |
| make a list [names] | `private ArrayList<String> names = new ArrayList<>();` with `import java.util.ArrayList;` |
| add [Ada] to [names] | `this.names.add("Ada");` |
| (item (1) of [names]) | `this.names.get(0)`: counting starts at 0 |
| (length of [names]) | `this.names.size()` |

## Pen

Every sprite has a pen of its own, which draws wherever the sprite goes.

| Scratch | Java |
| --- | --- |
| pen down / pen up | `this.getPen().down();` / `this.getPen().up();` |
| set pen size to (5) | `this.getPen().setSize(5);` |
| erase all | in the stage: `this.eraseAll();` |
| stamp | `this.stamp();` |
