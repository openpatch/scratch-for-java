package org.openpatch.scratch.extensions.tiled;

import java.util.List;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlText;

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
  @JacksonXmlElementWrapper(localName = "properties")
  @JacksonXmlProperty(localName = "property")
  public List<Property> properties;

  /**
   * Multi-line strings are written as the element's text instead of a value
   * attribute.
   *
   * @param text the element text
   */
  @JacksonXmlText
  public void setText(String text) {
    if (this.value == null && text != null && !text.isBlank()) {
      this.value = text;
    }
  }
}
