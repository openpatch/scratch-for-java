# Coin Quest: three save slots

Run `SaveSlotGame.main`. The game uses only built-in costumes, a backdrop and a
sound, so there are no external assets to copy. Collect coins with the arrow keys.

- **1, 2, 3:** select a slot; this does not load or overwrite it.
- **S:** save your coin count, player position and the next coin's position.
- **L:** load the selected slot. An empty slot starts a fresh round in memory.
- **R:** start a fresh round without changing any saved slot.

Try saving different scores to all three slots, close the game or reload the
browser page, and load each slot again. Saves use the stable game ID
`org.openpatch.coinquest`. Desktop saves live in your user-data folder; browser
saves live in IndexedDB for the site's origin and browser profile. They are
independent of project files and are not synchronized between computers.

The game freezes movement while saving or loading. Failed loads retain the
current round; errors are shown on screen and explained in the console.

`SaveSlotProgress` contains only public data fields. Its `version` field lets
the game reject unsupported formats before applying them. For a future format,
add an explicit migration rather than replacing an unreadable save with defaults.
