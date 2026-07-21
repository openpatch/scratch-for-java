---
type: patch
---

Add browsable documentation pages for the built-in sprites and sounds.

Two pages are generated straight from `src/main/resources`, so they can never
drift from what the library actually ships:

- **Sprites** shows all 841 bundled sprites, searchable, with a click on a
  sprite copying its `addCostume(...)` line.
- **Sounds** shows all 266 bundled sounds, searchable and playable in the
  browser, with a click on a name copying its `addSound(...)` line.

The sprite previews are cut out of the four sheets with CSS rather than
exported one by one, so the pages cost four images instead of 841. Each
preview is trimmed to the painted part of its cell, otherwise sprites drawn
into a taller cell would show up small and unevenly sized.

Generation runs as part of `mvn package` and of `./build.sh`, next to the
reference pages produced by the doclet.
