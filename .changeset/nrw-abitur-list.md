---
type: minor
---

Add an NRW version of the library, `scratch-<version>-nrw-all.jar`, for courses that use the classes of the NRW Zentralabitur. In it, `Stage.find`, `Stage.getAll` and `Sprite.getTouchingSprites` return the Abitur's `List` instead of `java.util.List`, so students can write `List<Gegner> gegner = find(Gegner.class);`. `List.java` from the Abitur classes has to be in the project's default package. The normal JAR is unchanged.
