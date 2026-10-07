---
name: For teachers
index: 10
---

# For teachers

Scratch for Java is made for the moment a class has outgrown Scratch but is not
ready to start Java from an empty file. Stage, sprite, costume, `move`, `say`,
`when key pressed`: the names stay the same, so your students can bring what they
already know with them and spend their attention on what is actually new, which is
classes, objects, methods and variables.

This page helps you plan a unit with it.

## Where your students work

| | Good for | What you prepare |
| --- | --- | --- |
| **Browser** | The first lessons, homework, computers you cannot install anything on | Nothing. The editor is built into every tutorial. |
| **Studio** | A whole unit on school computers, working offline | Install [Studio](/download). Java and the library are included. |
| **BlueJ / VS Code** | Courses that already use one of them | Add the library as described in [Setup](/setup). |

Starting in the browser and moving to Studio or BlueJ later works well. The
code stays the same. Only the `void main()` at the top becomes a class of its own.

:::alert{warn}
Work done in the browser is saved **only in that browser on that computer**. Make
saving a fixed part of the end of every lesson: download the project, or work in
Studio from the start.
:::

## The seven tutorials as a unit

Each tutorial adds one idea to the previous ones and ends with the whole project
running in the page. Plan roughly one lesson each. The hedgehog game and the
last chapter take longer.

| | Tutorial | Builds on (Scratch) | New in Java | Typical stumbling blocks |
| --- | --- | --- | --- | --- |
| 1 | [Your first program](/tutorials/getting-started) | Sprites, costumes, `forever` | A class for the stage, `new`, `run()` | Forgetting `this.add(...)`; typos in costume names |
| 2 | [Make it Walk](/tutorials/make-it-walk) | `next costume`, `wait` | Timers instead of `wait`, `AnimatedSprite`, a `boolean` | Wanting a `wait` inside `run()` |
| 3 | [Catch the Coins](/tutorials/catch-the-coins) | Variables, `pick random`, `touching?` | Fields, a `for` loop, one class for many coins | Where the score lives, and how a coin reaches it |
| 4 | [Red Light, Green Light](/tutorials/red-light-green-light) | `broadcast`, `when I receive` | Constructor parameters, `String.equals` | Comparing strings with `==` |
| 5 | [Guess the Number](/tutorials/guess-the-number) | `ask and wait`, `answer` | `if`/`else if`, turning text into a number | `ask` does not wait: the answer arrives in a later frame |
| 6 | [Bouncing Hedgehog](/tutorials/bouncy-hedgehog) | A whole small game | Combining everything, using your own pictures | Image files in the wrong folder |
| 7 | [Dodge the Rocks](/tutorials/dodge-the-rocks) | Several backdrops as screens | Several stages and a `Window` | Trying to reset a game by hand |

Every tutorial page names the Scratch block it replaces, so your students can keep
the vocabulary they already have. The
[Scratch → Java cheat sheet](/scratch-to-java) puts all of them on one page,
and it is worth handing out. [Differences to Scratch](/differences-scratch)
explains the few places where Java needs a different way of thinking. The most
important one is that there is no `forever` and no `wait` inside a sprite.

The tutorials end with **Things to try**: small extensions with a hint and a
solution to unfold. They work well for students who finish early.

## Choosing an approach

You can introduce objects from the first lesson, or start with one class and
bring in classes later. Both work with this library.
[Multiple Approach Design](/multiple-approach-design) shows the same project in
each style, so you can choose the one that fits your course.

If your course works with the classes of the NRW Zentralabitur (`List`, `Queue`,
`Graph`, …), use the NRW version of the library: [Abiturklassen NRW](/abitur-nrw).

## Material in German

The German material lives in
[Hyperbook Informatik](https://informatik.openpatch.org):

- [Grundlagen der Programmierung mit Java](https://informatik.openpatch.org/oberstufe/oop/01-grundlagen),
  a learning path from the first program to classes and objects.
- [Spielwerkstatt](https://informatik.openpatch.org/projekte/spielwerkstatt), in
  which each student builds a 2D adventure game over the Einführungsphase and on
  into the Qualifikationsphase, with a developer diary for reflection.

The Spielwerkstatt is a natural next step after the seven tutorials. Each chapter
of the learning path ends with game mechanics that apply what was just learned,
and students choose one for their own game.

## Assessing the work

A finished tutorial shows that a student followed the steps. It does not show
that they understood them. Assess the student's **own change** and their
**explanation** of it instead:

| | Developing | Secure | Extending |
| --- | --- | --- | --- |
| **Java model** | Can point at the stage and a sprite | Explains fields, methods and what one call of `run()` does | Explains why two objects of the same class behave independently |
| **Sequencing** | Uses a working example | Uses timers and variables instead of waiting | Explains in which order events happen |
| **Testing** | Runs it once and watches | Tests edges, collisions and scoring on purpose | Writes down what was tested and what happened |
| **Reflection** | Describes the change | Gives reasons for it | Compares two ways of doing it and justifies the choice |

## Working offline: the course pack

[Download the course pack](/scratch-to-java-course.zip). It holds every tutorial
as an ordinary Java project, once as a **starter** (the previous lesson's result)
and once as a **checkpoint** (the finished state), with the lesson text, these
teacher notes and the assessment grid.

In Studio, **Project → Import course pack** copies the library into every project,
so nothing needs to be downloaded in class. Give each student their own copy of
the folder. Checkpoints are there to get someone unstuck, not to be handed in.

Before the first lesson, try it once on a school computer without internet:
import the pack, run the first project, save it, close Studio and open it again.

## When something goes wrong

[It doesn't work!](/troubleshooting) collects the mistakes beginners make most
often, and what the error messages mean. It is written for students, so you can
send them there first.
