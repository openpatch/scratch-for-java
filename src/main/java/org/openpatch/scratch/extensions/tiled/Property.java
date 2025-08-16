package org.openpatch.scratch.extensions.tiled;

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
}
