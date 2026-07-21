---
type: patch
---

Add the "Make it Walk" tutorial, and compile the documentation on every build.

Costumes were the largest hole in the tutorials: not one of them changed a
sprite's appearance, even though Looks is a whole category in Scratch. The new
chapter builds animation twice — first by hand, two costumes swapped by a timer,
so it is clear that animation is only pictures being swapped; then with
`AnimatedSprite`, which does the swapping for you:

```java
this.addAnimation("walk", "alienGreen_walk%d", 2);
```

It sits second, right after the first program, since a character that moves its
legs is the most rewarding thing a beginner can add.

The tutorial index is no longer an empty page: it lists the five chapters in
order with what each one introduces.

`DocumentationSnippetsTest` compiles every Java example in the hand-written
documentation as part of `mvn test`. Two tutorial examples had been broken since
`KeyCode` became an enum and nothing noticed, which is exactly what this
prevents.
