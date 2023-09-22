package org.openpatch.scratch.extensions.tiled;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class MapObject {
  public double height;
  public int id;
  public String name;
  public double rotation;
  public boolean visible;
  public double width;
  public String type;
  public double x;
  public double y;
  public List<Property> properties;
  public Object ellipse;
  public Object point;
  public Polygon polygon;
  public Polyline polyline;

  public int getPropertyInt(String name) {
    return Integer.parseInt(getProperty(name));
  }

  public String getProperty(String name) {
    var p = properties.stream().filter(n -> name.equals(n.name)).findFirst().get();
    return p.value;
  }

  public float getPropertyFloat(String name) {
    return Float.parseFloat(getProperty(name));
  }

  public boolean getPropertyBoolean(String name) {
    return Boolean.parseBoolean(getProperty(name));
  }

  public Shape getShape() {
    if (point != null) {
      return new Rectangle2D.Double(0, 0, 1, 1);
    } else if (ellipse != null) {
      return new Ellipse2D.Double(0, 0, width, height);
    } else if (polygon != null) {
      var p = new java.awt.Polygon();
      p.addPoint(0, 0);
      for (var point : polygon.getPoints()) {
        p.addPoint((int) Math.round(point[0]), (int) Math.round(point[1]));
      }
      return p;
    }
    return new Rectangle2D.Double(0, 0, width, height);
  }
}
