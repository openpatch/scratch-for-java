---
type: minor
---

Add the `scratch-gamepad` extension: controller support for up to four gamepads. `Gamepad` asks
anywhere for buttons, sticks (y up, as on the stage, with a dead zone), triggers and connections;
stages and sprites implementing `GamepadListener` are told about button presses, releases and
controllers connecting, at the same point of the tick as key events. Buttons are named in Xbox
positions (A, B, X, Y); controllers without a known layout are skipped and there is no rumble.
