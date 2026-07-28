---
type: minor
---

A built-in sprite name now works everywhere a picture is named.

`new Sprite("player", "slimeGreen")` threw `Could not load image: slimeGreen`.
The constructor loaded its costume straight from a path while `addCostume()`
went through the built-in lookup, so the same name worked in one and not the
other - and worked in the browser, where the Online IDE has always resolved it.
The constructor now does the same lookup.

Three more places took a path where a built-in name was the natural thing to
write, and now take either:

- `Stage.setCursor(...)`, which the browser had already been resolving as a
  built-in name and nothing else
- `Window.useSplashLogo(...)`
- `Sprite.addCostumes(...)`, which cuts the named sheet into tiles. A built-in
  sprite is a region of a shared sheet rather than a file of its own, so the
  tiles are cut from inside that region instead of from the corner of the file
  it happens to share.

A string with a file extension is still treated as a path, so projects that name
their own artwork keep working.
