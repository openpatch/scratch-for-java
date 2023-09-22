package org.openpatch.scratch.extensions.tiled;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

class ObjectGroup {
  public double height;
  public int id;
  public String name;
  public double rotation;
  public String type;
  public boolean visible;
  public double width;
  public double x;
  public double y;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonProperty("object")
  public MapObject[] objects;
}
