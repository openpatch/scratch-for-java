/** Maps made with Tiled, drawn into the stage's layers. */
module org.openpatch.scratch.tiled {
  requires transitive org.openpatch.scratch;

  exports org.openpatch.scratch.tiled;

  provides org.openpatch.scratch.extension.Extension with org.openpatch.scratch.tiled.TiledExtension;
}
