---
type: patch
---

Add two more beginner tutorials.

The tutorial path went straight from "your first program" to a finished game.
Two steps now sit in between, and neither needs a single downloaded file:

- **Catch the Coins** — falling coins, a score, and the first project that has to
  remember something. Introduces a variable that belongs to the stage, a `for`
  loop that makes four sprites instead of copying one four times, a method of
  your own in place of a custom block, and `Random.randomInt`.
- **Guess the Number** — the first project that asks the player something.
  Introduces `ask`/`getAnswer`, turning text into a number, and `if`/`else if`.

Guess the Number also teaches the one place `ask` differs from Scratch: it does
not pause, so the program has to check `isAsking()` itself. The tutorial shows
the bug you get when you forget — the same answer counted again on every frame —
because it is easier to recognise than to describe.
