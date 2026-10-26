/** Saving and loading files and save games (JSON, XML). */
module org.openpatch.scratch.save {
  requires transitive org.openpatch.scratch;
  requires tools.jackson.core;
  requires tools.jackson.databind;
  requires tools.jackson.dataformat.xml;
  requires com.fasterxml.jackson.annotation;

  exports org.openpatch.scratch.save;
  opens org.openpatch.scratch.save to tools.jackson.databind;

  provides org.openpatch.scratch.extension.Extension with org.openpatch.scratch.save.SaveExtension;
}
