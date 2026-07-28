---
type: patch
---

Speech and thought bubbles hang off the sprite's hitbox.

They were placed from the size of the costume, at nine tenths of its width and
eleven tenths of its height. A costume is usually drawn into a canvas bigger than
what is painted on it — a standing pose in a canvas tall enough to also hold a
jumping one — so the bubble floated up and to the right of the sprite it belonged
to, with its tail pointing at nothing. It now sits on the top right corner of the
hitbox, which is the sprite as it looks.

For the built-in slime, whose painted pixels fill 88 by 62 of a 128 by 128
costume, that moves the bubble 14 pixels left and 72 pixels down, onto its
shoulder.
