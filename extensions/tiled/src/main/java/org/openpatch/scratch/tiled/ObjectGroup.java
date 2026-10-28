package org.openpatch.scratch.tiled;


class ObjectGroup {
  public double height;
  public int id;
  public String name;
  public double rotation;
  public String type;

  /** Whether the objects' y already points up (see TiledMap.getObjectsFromLayer). */
  boolean stageCoordinates;

  /** Tiled 1.9+ calls a layer's type its class. */
  public void setClassName(String className) {
    if (this.type == null || this.type.isEmpty()) {
      this.type = className;
    }
  }
  public boolean visible;
  public double width;
  public double x;
  public double y;

  public MapObject[] objects;
}
