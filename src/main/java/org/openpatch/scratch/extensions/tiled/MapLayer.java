package org.openpatch.scratch.extensions.tiled;

import com.fasterxml.jackson.annotation.JsonProperty;

class MapLayer {
  /** Tile ids without Tiled's flip bits (0 = no tile). */
  public int[] data;
  /** Tiled's flip bits per tile: 4 = horizontal, 2 = vertical, 1 = diagonal. */
  public int[] flips;
  public int height;
  public int id;
  public String name;
  public double opacity;
  public String type;
  public boolean visible;
  public int width;
  public int x;
  public int y;

  /** Tiled 1.9+ calls a layer's type its class. */
  @JsonProperty("class")
  public void setClassName(String className) {
    if (this.type == null || this.type.isEmpty()) {
      this.type = className;
    }
  }

  @JsonProperty("data")
  public void setData(LayerData layerData) {
    long[] gids = layerData.gids();
    this.data = new int[gids.length];
    this.flips = new int[gids.length];
    for (int i = 0; i < gids.length; i++) {
      this.data[i] = (int) (gids[i] & TiledMap.GID_MASK);
      this.flips[i] = (int) (gids[i] >>> 29) & 0b111;
    }
  }
}
