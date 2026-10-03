---
type: minor
---

Every built-in sprite with a front now faces right, the way a sprite with
direction 90 faces in Scratch. The ships, lasers and other parts of the space
shooter sheet were drawn pointing up (the enemies down), and the bee, fly,
fishes, frog, ladybug, mouse, slimes, snail and worms of the platformer sheet
facing left. So a slime walking right with `move` walked backwards, and a ship
told to `pointTowardsMousePointer` pointed its side at the mouse. They are now
turned when they are loaded. A program that showed a ship pointing up at
direction 90 now shows it pointing right; `setDirection(0)` points it up, and
then `move` flies it up as well.
