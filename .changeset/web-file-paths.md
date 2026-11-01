---
type: patch
---

Fixed file paths with `..` segments in the web export. The page's virtual file
system now resolves `.` and `..` like a real file system does, so files that a
game opens through a path that reaches out of a folder — a Tiled tileset whose
image lies outside the map's folder (`maps/../../assets/…`) — are found. Before,
every such file load failed with "Image not found" on the web page only, which
switched the tiled extension's map drawing off.
