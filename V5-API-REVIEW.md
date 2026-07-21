# v5 public API review — `Sprite` and `Stage`

Every public method of `Sprite` and `Stage`, judged against Scratch's own block palette.
Verdicts live in the generator, keyed by class **and** name, because 61 names exist on
both classes and a few mean different things on each.

- **KEEP** — has a Scratch block equivalent, or is unavoidable in Java
- **MOVE** — a real capability, but not a Scratch concept → `extensions`
- **DROP** — internal plumbing, a duplicate mechanism, or a redundant variant

## Status

**Step 1 (DROP) is applied.** `Sprite` + `Stage` went from **293 public methods to 252**,
plus **15 public nested `When*Handler` interfaces** removed. 151 tests pass; the cat,
pipes, stressTest, timedDot, shader and tiled demos were updated and re-verified.

Four verdicts in the tables below were **corrected while applying them** — each is marked
inline:

| Member | Was | Now | Why |
|---|---|---|---|
| `Stage.display` | DROP | **KEEP** | shows text on the stage, the Stage analogue of `say`; 46 uses in the reference examples |
| `Stage.draw` / `pre` / `keyEvent` / `mouseEvent` | DROP | **cannot drop** | called by `internal.Applet` across packages, so Java forces them public |
| `Sprite.setWidth/setHeight/changeWidth/changeHeight/getWidth/getHeight` | DROP | **MOVE** | this is the sizing API for nine-slice UI sprites; belongs with nine-slice |
| `Stage.goToFrontLayer` etc., `removeAllSprites` etc. | DROP | **made non-public** | they are the implementations behind the `Sprite` methods and `removeAll()` |

**Step 2 (MOVE) is in progress.** 36 of 80 methods moved so far; the API is at **218**.

| Group | Methods | Became |
|---|---:|---|
| Clock | 18 | `Clock` in the core package, 9 static methods (they are real Scratch blocks, so not an extension) |
| Shaders | 18 | `extensions/shader/Shaders`, reached through one `getShaders()` accessor on each class |

A refinement found while applying: several MOVE entries are *accessors*
(`getPen`, `getText`, `getCamera`, `getHitbox`, `getTimer`), and that
accessor-to-an-extension-object shape is exactly the pattern to move things
*into*, not away. Those accessors stay as the door; the operations behind them
move. `getShaders()` above is the model.

Third batch done, another 10 off the core; the API is at **208**.

| Group | Methods | Became |
|---|---:|---|
| Sorting | 4 | `extensions/sorting/Sorting` behind `getSorting()` |
| Pixels | 3 | `extensions/pixels/Pixels` behind `getPixels()` |
| `setTextureSampling` | 1 | **dropped, not moved** — duplicates the existing `Window.TEXTURE_SAMPLING_MODE` |
| Timers | 4 | **dropped by a design fix, not moved** — `getTimer(name)` now creates the timer on first use, so `addTimer`/`removeTimer` have nothing left to do |

Two more verdicts corrected while applying:

- **Hitbox — MOVE → KEEP.** `setHitbox` has seven real uses across the sensing,
  smartRocket, donutIO and tiled demos; it is the escape hatch for precise
  collision shapes. Its problem is four overloads, which is step 3, not a move.
- **`setCursor` — MOVE → KEEP.** It is per-stage state, and a menu stage wanting
  a different cursor from a game stage is reasonable. Two methods, left alone.

Still to move (~26 methods): pen/stamp 13, ui + nine-slice sizing 10,
spritesheet 1, input 2.

Then: step 3 (overloads), step 4 (packages), step 5 (docs).

## Where it lands

| | Methods | Class+name pairs |
|---|---:|---:|
| KEEP | 160 | 128 |
| MOVE | 80 | 71 |
| DROP | 53 | 52 |
| **Total** | **293** | **251** |

`Sprite` and `Stage` go from **293 public methods to 160**.
For scale, Scratch 3.0 ships roughly 100 blocks in total.

The 160 survivors carry **32 overloads** beyond one per name — a second pass worth making,
separately from this one.

## Keep — 160 methods

Grouped by the Scratch category each belongs to.

### Control

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `clone` | Sprite | 1 | create clone of / delete this clone |
| `remove` | Sprite | 1 | delete this clone |

### Events

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `broadcast` | Sprite | 2 | already has `@scratchblock` |
| `broadcast` | Stage | 2 |  |
| `run` | Sprite | 1 |  |
| `run` | Stage | 1 |  |
| `whenAddedToStage` | Sprite | 2 |  |
| `whenBackdropSwitches` | Sprite | 1 |  |
| `whenBackdropSwitches` | Stage | 1 |  |
| `whenClicked` | Sprite | 1 |  |
| `whenIReceive` | Sprite | 2 |  |
| `whenIReceive` | Stage | 2 |  |
| `whenKeyPressed` | Sprite | 1 |  |
| `whenKeyPressed` | Stage | 1 |  |
| `whenKeyReleased` | Sprite | 1 |  |
| `whenKeyReleased` | Stage | 1 |  |
| `whenMouseClicked` | Sprite | 1 |  |
| `whenMouseClicked` | Stage | 1 |  |
| `whenRemovedFromStage` | Sprite | 2 |  |

### Looks

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `addCostume` | Sprite | 3 |  |
| `changeSize` | Sprite | 1 |  |
| `changeTint` | Sprite | 1 |  |
| `changeTint` | Stage | 1 |  |
| `changeTransparency` | Sprite | 1 |  |
| `changeTransparency` | Stage | 1 |  |
| `getCurrentCostumeIndex` | Sprite | 1 |  |
| `getCurrentCostumeName` | Sprite | 1 |  |
| `getSize` | Sprite | 1 |  |
| `getTint` | Sprite | 1 |  |
| `getTransparency` | Sprite | 1 |  |
| `goLayersBackwards` | Sprite | 1 |  |
| `goLayersForwards` | Sprite | 1 |  |
| `goToBackLayer` | Sprite | 1 |  |
| `goToFrontLayer` | Sprite | 1 |  |
| `hide` | Sprite | 1 | already has `@scratchblock` |
| `isVisible` | Sprite | 1 |  |
| `nextCostume` | Sprite | 1 | already has `@scratchblock` |
| `previousCostume` | Sprite | 1 |  |
| `say` | Sprite | 2 | already has `@scratchblock` |
| `setSize` | Sprite | 1 | already has `@scratchblock` |
| `setTint` | Sprite | 3 |  |
| `setTint` | Stage | 2 |  |
| `setTransparency` | Sprite | 1 |  |
| `setTransparency` | Stage | 1 |  |
| `show` | Sprite | 1 | already has `@scratchblock` |
| `switchCostume` | Sprite | 2 | already has `@scratchblock` |
| `think` | Sprite | 2 | already has `@scratchblock` |

### Looks (Stage)

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `addBackdrop` | Stage | 3 |  |
| `changeColor` | Stage | 1 | collapse to one form |
| `getColor` | Stage | 1 | collapse to one form |
| `getCurrentBackdropIndex` | Stage | 1 |  |
| `getCurrentBackdropName` | Stage | 1 |  |
| `nextBackdrop` | Stage | 1 | already has `@scratchblock` |
| `previousBackdrop` | Stage | 1 |  |
| `randomBackdrop` | Stage | 1 |  |
| `setColor` | Stage | 3 | collapse to one form |
| `switchBackdrop` | Stage | 1 | already has `@scratchblock` |

### Motion

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `changePosition` | Sprite | 1 |  |
| `changeX` | Sprite | 1 | already has `@scratchblock` |
| `changeY` | Sprite | 1 | already has `@scratchblock` |
| `getDirection` | Sprite | 1 |  |
| `getPosition` | Sprite | 1 |  |
| `getX` | Sprite | 1 |  |
| `getY` | Sprite | 1 |  |
| `goToMousePointer` | Sprite | 1 |  |
| `goToRandomPosition` | Sprite | 1 |  |
| `goToSprite` | Sprite | 1 |  |
| `ifOnEdgeBounce` | Sprite | 1 |  |
| `move` | Sprite | 2 | already has `@scratchblock` |
| `pointInDirection` | Sprite | 2 |  |
| `pointTowardsMousePointer` | Sprite | 1 |  |
| `pointTowardsSprite` | Sprite | 1 |  |
| `setDirection` | Sprite | 2 | already has `@scratchblock` |
| `setPosition` | Sprite | 2 | already has `@scratchblock` |
| `setRotationStyle` | Sprite | 1 |  |
| `setX` | Sprite | 1 | already has `@scratchblock` |
| `setY` | Sprite | 1 | already has `@scratchblock` |
| `turnLeft` | Sprite | 1 | already has `@scratchblock` |
| `turnRight` | Sprite | 1 | already has `@scratchblock` |

### Operators

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `pickRandom` | Sprite | 1 |  |
| `pickRandom` | Stage | 1 |  |

### Sensing

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `debug` | Sprite | 1 |  |
| `debug` | Stage | 1 |  |
| `distanceToMousePointer` | Sprite | 1 |  |
| `distanceToSprite` | Sprite | 1 |  |
| `getDeltaTime` | Sprite | 1 |  |
| `getDeltaTime` | Stage | 1 |  |
| `getMouse` | Sprite | 1 |  |
| `getMouse` | Stage | 1 |  |
| `getMouseX` | Sprite | 1 |  |
| `getMouseX` | Stage | 1 |  |
| `getMouseY` | Sprite | 1 |  |
| `getMouseY` | Stage | 1 |  |
| `getStage` | Sprite | 1 |  |
| `getTimer` | Sprite | 2 |  |
| `getTimer` | Stage | 2 |  |
| `getTouchingSprite` | Sprite | 1 |  |
| `getTouchingSprites` | Sprite | 1 |  |
| `isKeyPressed` | Sprite | 1 |  |
| `isKeyPressed` | Stage | 1 |  |
| `isMouseDown` | Sprite | 1 |  |
| `isMouseDown` | Stage | 1 |  |
| `isTouchingEdge` | Sprite | 1 |  |
| `isTouchingMousePointer` | Sprite | 1 | already has `@scratchblock` |
| `isTouchingSprite` | Sprite | 2 | already has `@scratchblock` |

### Sound

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `addSound` | Sprite | 2 |  |
| `addSound` | Stage | 2 |  |
| `isSoundPlaying` | Sprite | 1 |  |
| `isSoundPlaying` | Stage | 1 |  |
| `playSound` | Sprite | 1 | already has `@scratchblock` |
| `playSound` | Stage | 1 |  |
| `stopAllSounds` | Sprite | 1 | already has `@scratchblock` |
| `stopAllSounds` | Stage | 1 |  |
| `stopSound` | Sprite | 1 |  |
| `stopSound` | Stage | 1 |  |

### Stage core

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `add` | Stage | 3 |  |
| `count` | Stage | 1 | generic find(Class)/count(Class) |
| `exit` | Stage | 1 |  |
| `find` | Stage | 1 | generic find(Class)/count(Class) |
| `getAll` | Stage | 1 |  |
| `getFrameRate` | Stage | 1 |  |
| `getHeight` | Stage | 1 | stage dimensions (javadoc wrongly says "of the sprite") |
| `getWidth` | Stage | 1 | stage dimensions (javadoc wrongly says "of the sprite") |
| `isDebug` | Stage | 1 |  |
| `remove` | Stage | 4 | remove a sprite / text / pen from the stage |
| `removeAll` | Stage | 1 |  |
| `setDebug` | Stage | 1 |  |
| `wait` | Stage | 1 |  |

## Move to an extension — 80 methods

Real capability, wrong altitude for a first lesson.

### Events -> extensions/input

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `whenMouseMoved` | Sprite | 1 | no Scratch equivalent; niche |
| `whenMouseWheelMoved` | Stage | 1 | no Scratch equivalent; niche |

### Looks -> extensions/pen

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `getPen` | Sprite | 1 | stamp is a Scratch *pen extension* block, not core |
| `stamp` | Sprite | 1 | stamp is a Scratch *pen extension* block, not core |
| `stampToBackground` | Sprite | 1 | stamp is a Scratch *pen extension* block, not core |
| `stampToForeground` | Sprite | 1 | stamp is a Scratch *pen extension* block, not core |
| `stampToUI` | Sprite | 1 | stamp is a Scratch *pen extension* block, not core |

### Looks -> extensions/shader

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `addShader` | Sprite | 1 |  |
| `addShader` | Stage | 1 |  |
| `getCurrentShader` | Sprite | 1 |  |
| `getCurrentShader` | Stage | 1 |  |
| `getCurrentShaderIndex` | Sprite | 1 |  |
| `getCurrentShaderIndex` | Stage | 1 |  |
| `getCurrentShaderName` | Sprite | 1 |  |
| `getCurrentShaderName` | Stage | 1 |  |
| `getShader` | Sprite | 1 |  |
| `getShader` | Stage | 1 |  |
| `nextShader` | Sprite | 1 |  |
| `nextShader` | Stage | 1 |  |
| `resetShader` | Sprite | 1 |  |
| `resetShader` | Stage | 1 |  |
| `switchShader` | Sprite | 2 |  |
| `switchShader` | Stage | 2 |  |

### Looks -> extensions/spritesheet

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `addCostumes` | Sprite | 1 | bulk sheet slicing |

### Looks -> extensions/text

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `getText` | Sprite | 1 |  |

### Looks -> extensions/ui

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `disableNineSlice` | Sprite | 1 | UI scaling / UI layer |
| `goToUILayer` | Stage | 1 | UI layer, same as isUI |
| `isUI` | Sprite | 2 | UI scaling / UI layer |
| `setNineSlice` | Sprite | 1 | UI scaling / UI layer |

### Sensing -> Clock helper

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `getCurrentDay` | Sprite | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |
| `getCurrentDay` | Stage | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |
| `getCurrentDayOfWeek` | Sprite | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |
| `getCurrentDayOfWeek` | Stage | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |
| `getCurrentHour` | Sprite | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |
| `getCurrentHour` | Stage | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |
| `getCurrentMillisecond` | Sprite | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |
| `getCurrentMillisecond` | Stage | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |
| `getCurrentMinute` | Sprite | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |
| `getCurrentMinute` | Stage | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |
| `getCurrentMonth` | Sprite | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |
| `getCurrentMonth` | Stage | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |
| `getCurrentSecond` | Sprite | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |
| `getCurrentSecond` | Stage | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |
| `getCurrentYear` | Sprite | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |
| `getCurrentYear` | Stage | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |
| `getDaysSince2000` | Sprite | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |
| `getDaysSince2000` | Stage | 1 | real Scratch blocks, but 9 methods duplicated on Sprite AND Stage = 18 |

### Sensing -> extensions/hitbox

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `disableHitbox` | Sprite | 1 | default hitbox stays automatic; only custom shapes move |
| `enableHitbox` | Sprite | 1 | default hitbox stays automatic; only custom shapes move |
| `getHitbox` | Sprite | 1 | default hitbox stays automatic; only custom shapes move |
| `setHitbox` | Sprite | 4 | default hitbox stays automatic; only custom shapes move |

### Sensing -> extensions/timer

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `addTimer` | Sprite | 1 | named timers; getTimer() stays |
| `addTimer` | Stage | 1 | named timers; getTimer() stays |
| `removeTimer` | Sprite | 1 | named timers; getTimer() stays |
| `removeTimer` | Stage | 1 | named timers; getTimer() stays |

### Stage -> extensions/camera

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `getCamera` | Stage | 1 |  |

### Stage -> extensions/pen

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `addStampsToBackground` | Stage | 2 |  |
| `addStampsToForeground` | Stage | 2 |  |
| `eraseAll` | Stage | 1 |  |
| `eraseBackground` | Stage | 1 |  |
| `eraseForeground` | Stage | 1 |  |
| `eraseUI` | Stage | 1 |  |

### Stage -> extensions/pixels

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `getBackgroundPixels` | Stage | 1 |  |
| `getForegroundPixels` | Stage | 1 |  |
| `getPixels` | Stage | 1 |  |

### Stage -> extensions/sorting

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `disableSort` | Stage | 1 |  |
| `enableYSort` | Stage | 1 |  |
| `isSortEnabled` | Stage | 1 |  |
| `setSorter` | Stage | 1 |  |

### Stage -> extensions/window

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `setCursor` | Stage | 2 |  |
| `setTextureSampling` | Stage | 1 |  |

## Drop — 53 methods

No Scratch equivalent and no demonstrated need, or a second way to do something that already has a first way.

### Events

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `keyEvent` | Sprite | 1 | → package-private; only `Stage` calls it |
| `keyEvent` | Stage | 1 | **cannot drop** — called by `internal.Applet` across packages, so Java forces it public; already `@ignore-in-docs` |
| `mouseEvent` | Sprite | 1 | → package-private; only `Stage` calls it |
| `mouseEvent` | Stage | 1 | **cannot drop** — called by `internal.Applet`; already `@ignore-in-docs` |
| `setRun` | Sprite | 1 | duplicate mechanism: every event already has an overridable whenX() |
| `setRun` | Stage | 1 | duplicate mechanism: every event already has an overridable whenX() |
| `setWhenAddedToStageHandler` | Sprite | 1 | duplicate mechanism: every event already has an overridable whenX() |
| `setWhenBackdropSwitches` | Sprite | 1 | duplicate mechanism: every event already has an overridable whenX() |
| `setWhenBackdropSwitches` | Stage | 1 | duplicate mechanism: every event already has an overridable whenX() |
| `setWhenClicked` | Sprite | 1 | duplicate mechanism: every event already has an overridable whenX() |
| `setWhenIReceive` | Sprite | 1 | duplicate mechanism: every event already has an overridable whenX() |
| `setWhenIReceive` | Stage | 1 | duplicate mechanism: every event already has an overridable whenX() |
| `setWhenKeyPressed` | Sprite | 1 | duplicate mechanism: every event already has an overridable whenX() |
| `setWhenKeyPressed` | Stage | 1 | duplicate mechanism: every event already has an overridable whenX() |
| `setWhenKeyReleased` | Sprite | 1 | duplicate mechanism: every event already has an overridable whenX() |
| `setWhenKeyReleased` | Stage | 1 | duplicate mechanism: every event already has an overridable whenX() |
| `setWhenMouseClicked` | Sprite | 1 | duplicate mechanism: every event already has an overridable whenX() |
| `setWhenMouseClicked` | Stage | 1 | duplicate mechanism: every event already has an overridable whenX() |
| `setWhenMouseMoved` | Sprite | 1 | duplicate mechanism: every event already has an overridable whenX() |
| `setWhenMouseWheelMoved` | Stage | 1 | duplicate mechanism: every event already has an overridable whenX() |
| `setWhenRemovedFromStageHandler` | Sprite | 1 | duplicate mechanism: every event already has an overridable whenX() |

### Looks

| Method | Class | Overloads | Note |
|---|---|---:|---|
| ~~`changeHeight`~~ | Sprite | 1 | **MOVE after review** — this is the sizing API for nine-slice UI sprites (`Button`/`Bar` in the ui demo); belongs with nine-slice in `extensions/ui`, not dropped |
| ~~`changeWidth`~~ | Sprite | 1 | **MOVE after review** — this is the sizing API for nine-slice UI sprites (`Button`/`Bar` in the ui demo); belongs with nine-slice in `extensions/ui`, not dropped |
| ~~`getHeight`~~ | Sprite | 1 | **MOVE after review** — this is the sizing API for nine-slice UI sprites (`Button`/`Bar` in the ui demo); belongs with nine-slice in `extensions/ui`, not dropped |
| ~~`getWidth`~~ | Sprite | 1 | **MOVE after review** — this is the sizing API for nine-slice UI sprites (`Button`/`Bar` in the ui demo); belongs with nine-slice in `extensions/ui`, not dropped |
| ~~`setHeight`~~ | Sprite | 1 | **MOVE after review** — this is the sizing API for nine-slice UI sprites (`Button`/`Bar` in the ui demo); belongs with nine-slice in `extensions/ui`, not dropped |
| ~~`setWidth`~~ | Sprite | 1 | **MOVE after review** — this is the sizing API for nine-slice UI sprites (`Button`/`Bar` in the ui demo); belongs with nine-slice in `extensions/ui`, not dropped |

### Looks (Stage)

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `removeBackdrop` | Stage | 1 |  |

### Motion

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `setOnEdgeBounce` | Sprite | 1 | a mode Scratch lacks; ifOnEdgeBounce() in run() is the Scratch way |

### Sensing

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `getWindow` | Sprite | 1 | plumbing; getStage() is enough |
| `getWindow` | Stage | 1 | plumbing; getStage() is enough |

### Sound

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `removeSound` | Sprite | 1 | no Scratch equivalent, no observed need |
| `removeSound` | Stage | 1 | no Scratch equivalent, no observed need |

### Stage core

| Method | Class | Overloads | Note |
|---|---|---:|---|
| `countPens` | Stage | 1 | twelve typed variants of the two generic methods above |
| `countPensOf` | Stage | 1 | twelve typed variants of the two generic methods above |
| `countSprites` | Stage | 1 | twelve typed variants of the two generic methods above |
| `countSpritesOf` | Stage | 1 | twelve typed variants of the two generic methods above |
| `countTexts` | Stage | 1 | twelve typed variants of the two generic methods above |
| `countTextsOf` | Stage | 1 | twelve typed variants of the two generic methods above |
| ~~`display`~~ | Stage | 2 | **KEEP after review** — shows text on the stage, the Stage analogue of `say`; 46 uses in the reference examples. Misclassified. |
| `draw` | Stage | 1 | **cannot drop** — called by `internal.Applet`; already `@ignore-in-docs` |
| `findPensOf` | Stage | 1 | twelve typed variants of the two generic methods above |
| `findSpritesOf` | Stage | 1 | twelve typed variants of the two generic methods above |
| `findTextsOf` | Stage | 1 | twelve typed variants of the two generic methods above |
| `goLayersBackwards` | Stage | 1 | stage-side duplicate of the Sprite method |
| `goLayersForwards` | Stage | 1 | stage-side duplicate of the Sprite method |
| `goToBackLayer` | Stage | 1 | stage-side duplicate of Sprite.goToBackLayer() |
| `goToFrontLayer` | Stage | 1 | stage-side duplicate of Sprite.goToFrontLayer() |
| `pre` | Stage | 1 | **cannot drop** — called by `internal.Applet`; already `@ignore-in-docs` |
| `removeAllPens` | Stage | 1 | twelve typed variants of the two generic methods above |
| `removeAllSprites` | Stage | 1 | twelve typed variants of the two generic methods above |
| `removeAllTexts` | Stage | 1 | twelve typed variants of the two generic methods above |

## Scratch blocks with no equivalent

Walking the palette the other way. These are gaps, not cuts:

| Scratch block | Note |
|---|---|
| glide ( ) secs to x: y: | the one Motion block with no counterpart |
| ask ( ) and wait / answer | no text input at all; rules out a whole class of first projects |
| set/change volume, volume | `Sound` handles volume internally, nothing is exposed on Sprite/Stage |
| touching color ( ) ? | colour sensing absent (`getPixels` is not this) |
| wait until < > | `Stage.wait` exists, the conditional form does not |
| set/change [pitch/pan] effect | sound effects absent entirely |

## Suggested order

1. **DROP** — 53 methods, no replacement needed, nothing downstream to update.
2. **MOVE** — 80 methods; this is where the `extensions` split earns its keep.
3. Collapse the 32 surplus overloads on the survivors.
4. Flatten packages, once the class list is final.
5. Fill the gaps above, then one `@scratchblock` + example folder per survivor.

Steps 1 and 2 together remove **133 of 293 methods (45%)** without touching behaviour
a beginner would ever reach for.

