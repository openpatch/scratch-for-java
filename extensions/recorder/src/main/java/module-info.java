/** Records the stage as an animated GIF. */
module org.openpatch.scratch.recorder {
  requires transitive org.openpatch.scratch;

  exports org.openpatch.scratch.recorder;

  provides org.openpatch.scratch.extension.Extension with org.openpatch.scratch.recorder.RecorderExtension;
}
