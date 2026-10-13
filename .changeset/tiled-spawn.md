---
type: minor
---

`TiledMap.spawn("Objects")` creates a sprite for each object of a layer whose
type (Tiled's "class") is the name of a sprite class, puts it where the object
is and adds it to the stage. Objects drawn as areas give their sprite that area
as its hitbox. The sprite class needs a constructor taking the `MapObject`, one
taking `(double x, double y)`, or one without parameters.
