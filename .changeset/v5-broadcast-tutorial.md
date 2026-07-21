---
type: patch
---

Add the "Red Light, Green Light" tutorial.

Messages were the last whole Scratch category with no tutorial coverage. A bee,
a ladybug and a snail race while a referee broadcasts `"go"` and `"stop"`:

```java
this.broadcast("go");
```

```java
public void whenIReceive(String message) {
  if (message.equals("go")) {
    this.running = true;
  }
}
```

The point the chapter makes is that the referee never mentions a racer and no
racer mentions the referee — add a fourth racer and it starts listening on its
own, with no change to the referee.

It also introduces constructor parameters: one `Racer` class produces all three
creatures, where Scratch would have needed three sprites each carrying its own
copy of the same scripts.
