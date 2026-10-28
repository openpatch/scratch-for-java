package org.openpatch.scratch.web;

import org.teavm.classlib.ResourceSupplier;
import org.teavm.classlib.ResourceSupplierContext;

/**
 * Compile time only: the class-path resources TeaVM puts into the game. The built-in sprite
 * sheets' atlases are read with getResourceAsStream; images, sounds and fonts come from the
 * page's folder, where the export puts them.
 */
public final class ResourceList implements ResourceSupplier {
  @Override
  public String[] supplyResources(ResourceSupplierContext context) {
    return new String[] {"images/platformer.xml", "images/jumper.xml", "images/space_shooter.xml",
        "images/tappy_plane.xml", "images/cat.xml", "org/openpatch/scratch/internal/version.properties"};
  }
}
