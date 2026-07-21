package org.openpatch.scratch.extensions.tiled;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openpatch.scratch.Bounds;

class MapObjectTest {

  private static final double DELTA = 1e-9;

  @Test
  void getShapeReturnsOnePixelRectangleForPointObjects() {
    MapObject object = new MapObject();
    object.point = new Object();

    Bounds bounds = object.getShape().getBounds();

    assertEquals(new Bounds(0, 0, 1, 1), bounds);
  }

  @Test
  void getShapeReturnsEllipseForEllipseObjects() {
    MapObject object = new MapObject();
    object.ellipse = new Object();
    object.width = 10;
    object.height = 20;

    Bounds bounds = object.getShape().getBounds();

    assertEquals(new Bounds(0, 0, 10, 20), bounds);
  }

  @Test
  void getShapeReturnsPolygonForPolygonObjects() {
    MapObject object = new MapObject();
    Polygon polygon = new Polygon();
    polygon.points = "0,0 10,0 10,10 0,10";
    object.polygon = polygon;

    var shape = object.getShape();

    assertTrue(shape.contains(5, 5));
    assertFalse(shape.contains(15, 15));
  }

  @Test
  void getShapeDefaultsToRectangleWhenNoSpecialShapeIsSet() {
    MapObject object = new MapObject();
    object.width = 10;
    object.height = 20;

    Bounds bounds = object.getShape().getBounds();

    assertEquals(new Bounds(0, 0, 10, 20), bounds);
  }

  private static MapObject withProperty(String name, String value) {
    MapObject object = new MapObject();
    Property property = new Property();
    property.name = name;
    property.value = value;
    List<Property> properties = new ArrayList<>();
    properties.add(property);
    object.properties = properties;
    return object;
  }

  @Test
  void getPropertyReturnsTheRawStringValue() {
    MapObject object = withProperty("label", "hello");

    assertEquals("hello", object.getProperty("label"));
  }

  @Test
  void getPropertyIntParsesTheValueAsAnInteger() {
    MapObject object = withProperty("health", "42");

    assertEquals(42, object.getPropertyInt("health"));
  }

  @Test
  void getPropertyFloatParsesTheValueAsAFloat() {
    MapObject object = withProperty("speed", "1.5");

    assertEquals(1.5f, object.getPropertyFloat("speed"), DELTA);
  }

  @Test
  void getPropertyBooleanParsesTheValueAsABoolean() {
    MapObject object = withProperty("visible", "true");

    assertTrue(object.getPropertyBoolean("visible"));
  }

  @Test
  void getPropertyThrowsAHelpfulMessageNamingTheMissingAndAvailableProperties() {
    MapObject object = withProperty("label", "hello");

    var thrown = assertThrows(
        java.util.NoSuchElementException.class, () -> object.getProperty("missing"));

    assertTrue(thrown.getMessage().contains("missing"));
    assertTrue(thrown.getMessage().contains("label"));
  }

  @Test
  void getPropertyThrowsAHelpfulMessageWhenThereAreNoPropertiesAtAll() {
    MapObject object = new MapObject();
    object.name = "spawn";

    var thrown = assertThrows(
        java.util.NoSuchElementException.class, () -> object.getProperty("health"));

    assertTrue(thrown.getMessage().contains("health"));
    assertTrue(thrown.getMessage().contains("spawn"));
  }
}
