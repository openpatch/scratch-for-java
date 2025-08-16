package org.openpatch.scratch.extensions.tiled;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;

class MapLayer {
  public int[] data;
  public int height;
  public int id;
  public String name;
  public double opacity;
  public String type;
  public boolean visible;
  public int width;
  public int x;
  public int y;

  @JsonProperty("data")
  public void setData(String data) {
    var sData = data.replaceAll("\n", "").split(",");
    this.data = Arrays.stream(sData).mapToInt(Integer::parseInt).toArray();
  }
}
