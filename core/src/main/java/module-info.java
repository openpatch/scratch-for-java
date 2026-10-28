/**
 * Scratch for Java. Learners use {@code org.openpatch.scratch}; extensions also use
 * {@code org.openpatch.scratch.extension}. Everything under {@code org.openpatch.scratch.internal}
 * may change without notice and is not exported; its platform layer is open to the module that
 * implements it on the desktop ({@code scratch-desktop}).
 */
module org.openpatch.scratch {
  exports org.openpatch.scratch;
  exports org.openpatch.scratch.extension;
  exports org.openpatch.scratch.internal.platform to org.openpatch.scratch.desktop;

  uses org.openpatch.scratch.extension.Extension;
  uses org.openpatch.scratch.internal.platform.Platform;
}
