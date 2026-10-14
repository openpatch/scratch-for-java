---
type: minor
---

Add `SaveData<T>` for game progress and named save slots. Desktop games store
public data fields as JSON in their user-data directory; the online IDE uses
IndexedDB through the same Java API. Missing slots use caller-supplied defaults,
while corrupt data and failed writes report errors without silently resetting
progress. Writes commit before the game continues.

The Coin Quest demo uses built-in assets and three slots, with controls to save,
load and start a fresh round. Its score and sprite positions survive a desktop
restart or browser reload.
