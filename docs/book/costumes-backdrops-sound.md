---
name: Costumes, Backdrops and Sounds
index: 4
---

# Costumes, Backdrops and Sounds

In Scratch you pick costumes, backdrops and sounds from built-in libraries, and
you can upload your own as well.

Scratch for Java has a built-in library too. **838 pictures and 266 sounds ship
inside the library**, so you can start a project without downloading anything:

```java
this.addCostume("bunny1_stand");
this.addBackdrop("background");
this.addSound("handleCoins");
```

Browse them here, both pages have a search box:

- **[Sprites](/sprites)** — click a picture to copy the line that uses it
- **[Sounds](/sounds)** — press play to listen before you choose

The artwork and sounds are by [Kenney](https://kenney.nl) and are released under
CC0, which means you may use them for anything, including things you sell,
without asking or crediting anyone.

## Using your own files

Once you want something that is not in the library, give a path instead of a
name:

```java
this.addCostume("hero", "assets/hero.png");
```

Anything with a file ending is treated as a path, anything without one is looked
up in the built-in library.

## Which way a costume faces

A sprite with direction 90 faces right, just like in Scratch. That is why every
built-in sprite with a front - a ship, a fish, a laser - is drawn facing right:
`move` takes it where it is looking, and `pointTowardsMousePointer` turns its
nose to the mouse.

Draw your own costumes the same way, facing right. A picture drawn facing up
would otherwise fly sideways. If you found one that faces another way, turn or
mirror it in an image editor, such as one of those listed below, before you use
it.

What Scratch for Java does *not* have is Scratch's paint and sound editors, so
for making or changing files you need separate tools. The rest of this page is a
list of places to find and edit them.

## Where to find more graphics

You can search the following sources for free graphics, which you can use for costumes or backdrop.

- [clker.com](https://clker.com)
- [openclipart.org](https://openclipart.org/)
- [unsplash.com](https://unsplash.com/)
- [OpenSprites](https://opensprites.org/)
- [Kenney's Game Assets](https://kenney.nl/assets)
- [Open Game Art](http://opengameart.org/)

If you want to modify a graphic file or want to create your own, you can use the following tools:

- [GIMP](https://www.gimp.org/)
- [Inkscape](https://inkscape.org)
- [Aseprite](https://www.aseprite.org/)

## Sounds

For sounds, you can search the following sources:

- [freesound.org](https://www.freesound.org/)
- [Wikipedia Sounds](https://en.wikipedia.org/wiki/Wikipedia:Free_sound_resources)
- [Free Music Archive](https://freemusicarchive.org/genre/Metal/)
- [8-Bit Music by EricSkiff.com](http://ericskiff.com/music/)

For modifying or creating your own sounds you can use:

- [Audacity](https://www.audacityteam.org/)
- [Ardour](https://ardour.org/)
