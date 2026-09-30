---
name: Abiturklassen NRW
index: 5
---

# Abiturklassen NRW

In North Rhine-Westphalia the Zentralabitur in computer science comes with its
own classes: `List`, `Queue`, `Stack`, `BinaryTree`, `BinarySearchTree`,
`ComparableContent`, `Graph`, `Vertex`, `Edge`, `DatabaseConnector` and
`QueryResult`. Students work with them throughout the Oberstufe, and they are the
classes the Abitur tasks are written against.

You can use them together with Scratch for Java. The Abiturklassen then hold the
sprites of your game, for example all the enemies on the stage in a `List`.

## Two versions of Scratch for Java

Scratch for Java comes in two versions. They differ in three methods, which hand
you a list of sprites:

| JAR                             | `find`, `getAll` and `getTouchingSprites` return |
| ------------------------------- | ------------------------------------------------ |
| `scratch-<version>-all.jar`     | Java's own `java.util.List`                      |
| `scratch-<version>-nrw-all.jar` | the `List` of the Abiturklassen                  |

Everything else is the same in both. If your course uses the Abiturklassen, take
the NRW version from the [Download](/download) page.

## Setting up a project

1. Download `scratch-<version>-nrw-all.jar` from the [Download](/download) page
   and add it to your project, the same way as described in [Setup](/setup). In
   BlueJ, for example, put it into the `+libs` folder of your project.
2. Get the Abiturklassen. QUA-LiS NRW publishes them on the Lehrplannavigator as
   [Implementationen von Klassen für das Zentralabitur ab 2018](https://lehrplannavigator.nrw.de/system/files/media/document/file/2020-03-11_implementationen_von_klassen_fuer_das_zentralabitur_ab_2018.zip)
   (ZIP). Your teacher may also hand them out.
3. Copy the `.java` files you need into your project, next to your own classes.
   `List.java` is the one Scratch for Java needs. Take the others as your course
   uses them. `Graph.java` needs `List.java`, and `DatabaseConnector.java` needs
   `Queue.java` and `QueryResult.java`.

A BlueJ project then looks like this:

```text
MeinSpiel/
  +libs/
    scratch-<version>-nrw-all.jar
  List.java           <- from the Abiturklassen
  Queue.java          <- from the Abiturklassen, if you use it
  MyStage.java        <- your classes
  Gegner.java
  Muenze.java
```

Do not put the Abiturklassen into a package, and do not put them into `+libs`.
They have to sit next to your own classes. Do not change the files either. Use
them exactly as they come.

## Using the List

With the NRW version, write the Abitur's `List` on the left, and the sprites come
back in it.

In a stage, `find` gives you all sprites of one class:

```java
List<Gegner> gegner = find(Gegner.class);
gegner.toFirst();
while (gegner.hasAccess()) {
  gegner.getContent().changeY(-5);
  gegner.next();
}
```

`getAll` gives you every sprite on the stage:

```java
List<Sprite> alle = getAll();
alle.toFirst();
while (alle.hasAccess()) {
  alle.getContent().hide();
  alle.next();
}
```

In a sprite, `getTouchingSprites` gives you the sprites it is touching right now:

```java
List<Muenze> muenzen = getTouchingSprites(Muenze.class);
muenzen.toFirst();
while (muenzen.hasAccess()) {
  getStage().remove(muenzen.getContent());
  muenzen.next();
}
```

Each call returns a new list. Adding to it or removing from it does not add
sprites to the stage or remove them. Use `add` and `remove` on the stage for that,
as in the last example.

## Using the other Abiturklassen

The other Abiturklassen need nothing special. Create them yourself and put sprites
into them like any other object. A `Queue` lets enemies take turns, for example:

```java
Queue<Gegner> warteschlange = new Queue<>();

List<Gegner> gegner = find(Gegner.class);
gegner.toFirst();
while (gegner.hasAccess()) {
  warteschlange.enqueue(gegner.getContent());
  gegner.next();
}

// later, whenever it is the next enemy's turn
Gegner amZug = warteschlange.front();
warteschlange.dequeue();
amZug.move(20);
warteschlange.enqueue(amZug);
```

## How it works

The Abiturklassen are **not** part of Scratch for Java. The NRW version only
expects a class called `List` in your project. Whenever `find`, `getAll` or
`getTouchingSprites` is called, it looks that class up, creates an empty list
with `new List()`, adds the sprites with `append`, and hands the list to you.

That is why `List.java` has to be in your project, and why it works with the files
exactly as QUA-LiS publishes them. It is always your copy of the Abiturklassen
that is used, never a different one.

## Troubleshooting

**`incompatible types: java.util.List<…> cannot be converted to List<…>`**

You are using the normal JAR. Replace it with `scratch-<version>-nrw-all.jar`.

**`There is no class List in your project.`**

`List.java` is missing, is in a package, or is in `+libs`. Copy it next to your
own classes.

**`var` does not work**

`var gegner = find(Gegner.class);` does not tell Java which list you want. Write
the type out: `List<Gegner> gegner = find(Gegner.class);`.

**The examples in the reference do not compile**

The examples in the reference are written for the normal version, for example
`find(Coin.class).size()`. With the NRW version, walk through the list with
`toFirst`, `hasAccess` and `next` instead.

## Using the Abiturklassen with the normal version

You can also keep the normal JAR and still use the Abiturklassen for everything
else. Then `find`, `getAll` and `getTouchingSprites` return Java's list. The
name `List` means the Abitur's list, so write Java's list with its full name:

```java
java.util.List<Gegner> gegner = find(Gegner.class);
for (Gegner g : gegner) {
  g.hide();
}
```
