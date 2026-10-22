package org.openpatch.scratch.save;

import org.openpatch.scratch.extension.Extension;

/** Registers the save extension; found through ServiceLoader, learners never see it. */
public final class SaveExtension implements Extension {

  @Override
  public String id() {
    return "save";
  }

  @Override
  public String name() {
    return "Save data and files";
  }

  @Override
  public String version() {
    return Extension.coreVersion();
  }

  /** First-party extensions are released together with the core. */
  @Override
  public String requires() {
    return Extension.coreVersion();
  }
}
