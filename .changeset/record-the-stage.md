---
type: patch
---

Recordings show the stage when the window is bigger than it, for example after
maximizing it: `GifRecorder`, `FrameRecorder` and `FFmpegRecorder` save the stage
at its own size, without the bars around it. A GIF that was started before the
window was maximized no longer shows only the window's top-left corner after it,
and recording a maximized window slows the program down far less, because the
frames are encoded at the stage's size.
