package org.openpatch.scratch.tiled;

import java.util.List;

/**
 * The Property class represents a property of a Tiled object. It includes fields for the property
 * name, type, and value.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * Property p = new Property();
 * p.name = "myProperty";
 * p.type = "number";
 * p.value = "42";
 * }</pre>
 */
public class Property {
  /** The name of the property. */
  public String name;

  /** The type of the property. */
  public String type = "string";

  /** The value of the property. */
  public String value;

  /** A class-typed property (Tiled 1.8+) holds its members as nested properties. */
  public List<Property> properties;

  /**
   * Multi-line strings are written as the element's text instead of a value
   * attribute.
   *
   * @param text the element text
   */
  public void setText(String text) {
    if (this.value == null && text != null && !text.isBlank()) {
      this.value = text;
    }
  }
}
