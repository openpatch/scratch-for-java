package org.openpatch.scratch.tiled;


/** A group layer: tile layers, object layers and further groups. */
class LayerGroup {
  public String name;
  public boolean visible = true;

  public MapLayer[] layers;

  public ObjectGroup[] objectGroups;

  public LayerGroup[] groups;
}
