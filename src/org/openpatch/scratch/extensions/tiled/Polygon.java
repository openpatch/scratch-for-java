package org.openpatch.scratch.extensions.tiled;

import java.util.List;

public class Polygon {
  public String points;

  public double[][] getPoints() {
    var ps = points.split(" ");
    var pi = new double[ps.length][2];

    for (int i = 0; i<pi.length; i++) {
      var s = ps[i].split(",");
      var x = Double.parseDouble(s[0]);
      var y = Double.parseDouble(s[1]);
      pi[i][0] = x;
      pi[i][1] = y;
    }

    return pi;
  }
}
