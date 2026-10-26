package org.openpatch.scratch.recorder;

import org.openpatch.scratch.extension.Extension;
import org.openpatch.scratch.extension.ExtensionContext;

/** Registers the recorder extension; found through ServiceLoader, learners never see it. */
public final class RecorderExtension implements Extension {

  @Override
  public String id() {
    return "recorder";
  }

  @Override
  public String name() {
    return "GIF recorder";
  }

  @Override
  public String version() {
    return Extension.coreVersion();
  }

  /** Hands every drawn frame to the recorders that are recording; frames are read back only then. */
  @Override
  public void install(ExtensionContext ctx) {
    ctx.afterFrame((stage, frame) -> {
      for (GifRecorder recorder : GifRecorder.RECORDING) {
        recorder.frame(frame, ctx.getTickCount());
      }
    });
    ctx.onExit(() -> {
      for (GifRecorder recorder : GifRecorder.RECORDING) {
        recorder.stop(); // a program that ends while recording still leaves a complete GIF
      }
    });
  }

  /** First-party extensions are released together with the core. */
  @Override
  public String requires() {
    return Extension.coreVersion();
  }
}
