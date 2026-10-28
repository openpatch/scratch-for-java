/** Saving and loading files and save games (JSON). */
module org.openpatch.scratch.save {
  requires transitive org.openpatch.scratch;

  exports org.openpatch.scratch.save;

  provides org.openpatch.scratch.extension.Extension with org.openpatch.scratch.save.SaveExtension;
}
