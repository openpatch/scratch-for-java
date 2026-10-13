---
type: patch
---

Polygon objects of a Tiled map keep their shape: `MapObject.getShape()` and
`TiledMap.spawn` used to see them as an empty rectangle, because the polygon
was never read from the map file. A wall drawn as a polygon now blocks, and a
spawned sprite without a costume no longer shows the missing-costume marker.
Polylines are read as well.
