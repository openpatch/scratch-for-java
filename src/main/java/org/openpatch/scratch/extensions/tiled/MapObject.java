package org.openpatch.scratch.extensions.tiled;

import java.util.List;

import org.openpatch.scratch.extensions.shape.Ellipse;
import org.openpatch.scratch.extensions.shape.Rectangle;
import org.openpatch.scratch.extensions.shape.Shape;

/**
 * Represents a map object with various properties such as dimensions, position,
 * visibility, and
 * type. It also provides methods to retrieve property values in different
 * formats and to get a
 * Shape representation of the map object.
 */
public class MapObject {
  /** The height of the map object. */
  public double height;

  /** The unique identifier of the map object. */
  public int id;

  /** The name of the map object. */
  public String name;

  /** The rotation angle of the map object. */
  public double rotation;

  /** The visibility status of the map object. */
  public boolean visible;

  /** The width of the map object. */
  public double width;

  /** The type of the map object. */
  public String type;

  /** The x-coordinate of the map object. */
  public double x;

  /** The y-coordinate of the map object. */
  public double y;

  /** A list of properties associated with the map object. */
  public List<Property> properties;

  /** An object representing an ellipse shape. */
  public Object ellipse;

  /** An object representing a point. */
  public Object point;

  /** A Polygon object representing a polygon shape. */
  protected Polygon polygon;

  /** A Polyline object representing a polyline shape. */
  protected Polyline polyline;

  /**
   * Retrieves the value of a property with the specified name and converts it to
   * an integer.
   *
   * @param name the name of the property to retrieve
   * @return the integer value of the property
   * @throws NumberFormatException if the property value cannot be parsed as an
   *                               integer
   */
  public int getPropertyInt(String name) {
    return Integer.parseInt(getProperty(name));
  }

  /**
   * Retrieves the value of a property with the specified name from the properties
   * list.
   *
   * @param name the name of the property to retrieve
   * @return the value of the property with the specified name
   */
  public String getProperty(String name) {
    var p = properties.stream().filter(n -> name.equals(n.name)).findFirst().get();
    return p.value;
  }

  /**
   * Retrieves the value of a specified property as a float.
   *
   * @param name the name of the property to retrieve
   * @return the float value of the specified property
   * @throws NumberFormatException if the property value cannot be parsed as a
   *                               float
   */
  public float getPropertyFloat(String name) {
    return Float.parseFloat(getProperty(name));
  }

  /**
   * Retrieves the value of the specified property as a boolean.
   *
   * @param name the name of the property to retrieve
   * @return the boolean value of the property, or false if the property is not
   *         found or cannot be
   *         parsed as a boolean
   */
  public boolean getPropertyBoolean(String name) {
    return Boolean.parseBoolean(getProperty(name));
  }

  /**
   * Returns a Shape object representing the current map object. The type of Shape
   * returned depends
   * on the properties of the map object: - If the object is a point, a 1x1
   * rectangle is returned. -
   * If the object is an ellipse, an Ellipse2D object with the specified width and
   * height is
   * returned. - If the object is a polygon, a Polygon object with the specified
   * points is returned.
   * - If none of the above conditions are met, a rectangle with the specified
   * width and height is
   * returned.
   *
   * @return a Shape object representing the current map object.
   */
  public Shape getShape() {
    if (point != null) {
      return new Rectangle(0, 0, 1, 1);
    } else if (ellipse != null) {
      return new Ellipse(0, 0, width, height);
    } else if (polygon != null) {
      var points = polygon.getPoints();
      var xPoints = new double[points.length];
      var yPoints = new double[points.length];

      for (int i = 0; i < points.length; i++) {
        xPoints[i] = points[i][0];
        yPoints[i] = points[i][1];
      }

      return new org.openpatch.scratch.extensions.shape.Polygon(xPoints, yPoints);
    }
    return new Rectangle(0, 0, width, height);
  }
}
