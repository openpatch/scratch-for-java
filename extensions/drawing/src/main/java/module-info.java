/** Drawing by hand (Drawing, DrawingSprite) and bitmap texts. */
module org.openpatch.scratch.drawing {
  requires transitive org.openpatch.scratch;

  exports org.openpatch.scratch.drawing;

  provides org.openpatch.scratch.extension.Extension with org.openpatch.scratch.drawing.DrawingExtension;
}
