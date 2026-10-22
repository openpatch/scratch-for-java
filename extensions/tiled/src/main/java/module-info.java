/** Maps made with Tiled, drawn into the stage's layers. */
module org.openpatch.scratch.tiled {
  requires transitive org.openpatch.scratch;
  requires tools.jackson.core;
  requires tools.jackson.databind;
  requires tools.jackson.dataformat.xml;
  requires com.fasterxml.jackson.annotation;

  exports org.openpatch.scratch.tiled;
  // the map files are read into these classes by reflection
  opens org.openpatch.scratch.tiled to tools.jackson.databind;

  provides org.openpatch.scratch.extension.Extension with org.openpatch.scratch.tiled.TiledExtension;
}
