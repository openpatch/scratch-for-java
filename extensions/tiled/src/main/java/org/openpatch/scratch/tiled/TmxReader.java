package org.openpatch.scratch.tiled;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Reads Tiled's .tmx and .tsx files into the map classes: a small XML parser and the binding
 * Tiled's format needs, no library. Works wherever the core does, in a web page too (no
 * reflection, no javax.xml). Attributes a file leaves out keep the fields' defaults; unknown
 * elements and attributes are skipped, so maps from newer Tiled versions still load.
 */
final class TmxReader {

  private TmxReader() {}

  /** An XML element: its attributes in order, its child elements, its text. */
  static final class Element {
    final String name;
    final java.util.Map<String, String> attributes = new LinkedHashMap<>();
    final List<Element> children = new ArrayList<>();
    final StringBuilder text = new StringBuilder();

    Element(String name) {
      this.name = name;
    }

    String attribute(String key) {
      return this.attributes.get(key);
    }

    Element child(String childName) {
      for (Element child : this.children) {
        if (child.name.equals(childName)) {
          return child;
        }
      }
      return null;
    }

    List<Element> children(String childName) {
      List<Element> out = new ArrayList<>();
      for (Element child : this.children) {
        if (child.name.equals(childName)) {
          out.add(child);
        }
      }
      return out;
    }
  }

  // ---- binding ------------------------------------------------------------------------------

  static Map map(String xml) {
    Element e = parse(xml);
    expect(e, "map");
    Map map = new Map();
    map.properties = properties(e);
    map.compressionlevel = integer(e, "compressionlevel", map.compressionlevel);
    map.height = integer(e, "height", map.height);
    map.infinite = integer(e, "infinite", map.infinite);
    map.orientation = text(e, "orientation", map.orientation);
    map.renderorder = text(e, "renderorder", map.renderorder);
    map.tiledversion = text(e, "tiledversion", map.tiledversion);
    map.tileheight = integer(e, "tileheight", map.tileheight);
    map.tilewidth = integer(e, "tilewidth", map.tilewidth);
    map.type = text(e, "type", map.type);
    map.width = integer(e, "width", map.width);
    map.layers = layers(e);
    map.objectGroups = objectGroups(e);
    map.groups = groups(e);
    List<Element> tilesets = e.children("tileset");
    map.tilesets = tilesets.isEmpty() ? null : tilesets.stream().map(TmxReader::tileset).toArray(Tileset[]::new);
    return map;
  }

  static Tileset tileset(String xml) {
    Element e = parse(xml);
    expect(e, "tileset");
    return tileset(e);
  }

  private static Tileset tileset(Element e) {
    Tileset t = new Tileset();
    t.columns = integer(e, "columns", t.columns);
    t.firstgid = integer(e, "firstgid", t.firstgid);
    t.margin = integer(e, "margin", t.margin);
    t.name = text(e, "name", t.name);
    t.spacing = integer(e, "spacing", t.spacing);
    t.tilecount = integer(e, "tilecount", t.tilecount);
    t.tileheight = integer(e, "tileheight", t.tileheight);
    t.tilewidth = integer(e, "tilewidth", t.tilewidth);
    t.source = text(e, "source", t.source);
    Element image = e.child("image");
    if (image != null) {
      t.image = new TilesetImage();
      t.image.source = text(image, "source", t.image.source);
      t.image.width = integer(image, "width", t.image.width);
      t.image.height = integer(image, "height", t.image.height);
    }
    List<Element> tiles = e.children("tile");
    if (!tiles.isEmpty()) {
      t.tile = tiles.stream().map(TmxReader::tile).toArray(Tile[]::new);
    }
    return t;
  }

  private static Tile tile(Element e) {
    Tile tile = new Tile();
    tile.id = integer(e, "id", tile.id);
    Element animation = e.child("animation");
    if (animation != null) {
      tile.animation = animation.children.stream().map(frame -> {
        AnimationFrame f = new AnimationFrame();
        f.duration = integer(frame, "duration", f.duration);
        f.tileid = integer(frame, "tileid", f.tileid);
        return f;
      }).toArray(AnimationFrame[]::new);
    }
    Element group = e.child("objectgroup");
    if (group != null) {
      tile.objectgroup = objectGroup(group);
    }
    return tile;
  }

  private static MapLayer[] layers(Element parent) {
    List<Element> layers = parent.children("layer");
    return layers.isEmpty() ? null : layers.stream().map(TmxReader::layer).toArray(MapLayer[]::new);
  }

  private static ObjectGroup[] objectGroups(Element parent) {
    List<Element> groups = parent.children("objectgroup");
    return groups.isEmpty() ? null : groups.stream().map(TmxReader::objectGroup).toArray(ObjectGroup[]::new);
  }

  private static LayerGroup[] groups(Element parent) {
    List<Element> groups = parent.children("group");
    return groups.isEmpty() ? null : groups.stream().map(TmxReader::group).toArray(LayerGroup[]::new);
  }

  private static MapLayer layer(Element e) {
    MapLayer layer = new MapLayer();
    layer.height = integer(e, "height", layer.height);
    layer.id = integer(e, "id", layer.id);
    layer.name = text(e, "name", layer.name);
    layer.opacity = decimal(e, "opacity", layer.opacity);
    layer.type = text(e, "type", layer.type);
    layer.visible = bool(e, "visible", layer.visible);
    layer.width = integer(e, "width", layer.width);
    layer.x = integer(e, "x", layer.x);
    layer.y = integer(e, "y", layer.y);
    if (e.attribute("class") != null) {
      layer.setClassName(e.attribute("class"));
    }
    Element data = e.child("data");
    if (data != null) {
      LayerData d = new LayerData();
      d.encoding = text(data, "encoding", d.encoding);
      d.compression = text(data, "compression", d.compression);
      d.text = data.text.toString();
      if (data.child("chunk") != null) {
        d.chunk = "chunk";
      }
      if (d.encoding == null && data.child("tile") != null) {
        d.text = "<tile>"; // XML tiles: LayerData names the setting to change
      }
      layer.setData(d);
    }
    return layer;
  }

  private static ObjectGroup objectGroup(Element e) {
    ObjectGroup group = new ObjectGroup();
    group.height = decimal(e, "height", group.height);
    group.id = integer(e, "id", group.id);
    group.name = text(e, "name", group.name);
    group.rotation = decimal(e, "rotation", group.rotation);
    group.type = text(e, "type", group.type);
    group.visible = bool(e, "visible", group.visible);
    group.width = decimal(e, "width", group.width);
    group.x = decimal(e, "x", group.x);
    group.y = decimal(e, "y", group.y);
    if (e.attribute("class") != null) {
      group.setClassName(e.attribute("class"));
    }
    List<Element> objects = e.children("object");
    if (!objects.isEmpty()) {
      group.objects = objects.stream().map(TmxReader::object).toArray(MapObject[]::new);
    }
    return group;
  }

  private static LayerGroup group(Element e) {
    LayerGroup group = new LayerGroup();
    group.name = text(e, "name", group.name);
    group.visible = bool(e, "visible", group.visible);
    group.layers = layers(e);
    group.objectGroups = objectGroups(e);
    group.groups = groups(e);
    return group;
  }

  private static MapObject object(Element e) {
    MapObject o = new MapObject();
    o.height = decimal(e, "height", o.height);
    o.id = integer(e, "id", o.id);
    o.name = text(e, "name", o.name);
    o.rotation = decimal(e, "rotation", o.rotation);
    o.visible = bool(e, "visible", o.visible);
    o.width = decimal(e, "width", o.width);
    o.type = text(e, "type", o.type);
    if (e.attribute("class") != null) {
      o.setClassName(e.attribute("class"));
    }
    o.x = decimal(e, "x", o.x);
    o.y = decimal(e, "y", o.y);
    o.properties = properties(e);
    if (e.child("ellipse") != null) {
      o.ellipse = "";
    }
    if (e.child("point") != null) {
      o.point = "";
    }
    Element polygon = e.child("polygon");
    if (polygon != null) {
      o.polygon = new Polygon();
      o.polygon.points = text(polygon, "points", o.polygon.points);
    }
    Element polyline = e.child("polyline");
    if (polyline != null) {
      o.polyline = new Polyline();
      o.polyline.points = text(polyline, "points", o.polyline.points);
    }
    return o;
  }

  /** The {@code <properties>} of an element, or null without one. */
  private static List<Property> properties(Element e) {
    Element properties = e.child("properties");
    if (properties == null) {
      return null;
    }
    List<Property> out = new ArrayList<>();
    for (Element p : properties.children("property")) {
      Property property = new Property();
      property.name = text(p, "name", property.name);
      property.type = text(p, "type", property.type);
      property.value = text(p, "value", property.value);
      property.properties = properties(p);
      property.setText(p.text.toString());
      out.add(property);
    }
    return out;
  }

  private static void expect(Element e, String name) {
    if (!e.name.equals(name)) {
      throw new IllegalArgumentException("Not a Tiled " + (name.equals("map") ? "map (.tmx)" : "tileset (.tsx)")
          + ": the file starts with <" + e.name + ">.");
    }
  }

  private static String text(Element e, String key, String fallback) {
    String value = e.attribute(key);
    return value != null ? value : fallback;
  }

  private static int integer(Element e, String key, int fallback) {
    String value = e.attribute(key);
    if (value == null || value.isBlank()) {
      return fallback;
    }
    try {
      return Integer.parseInt(value.trim());
    } catch (NumberFormatException ex) {
      return (int) Double.parseDouble(value.trim()); // Tiled writes some sizes with a fraction
    }
  }

  private static double decimal(Element e, String key, double fallback) {
    String value = e.attribute(key);
    return value == null || value.isBlank() ? fallback : Double.parseDouble(value.trim());
  }

  private static boolean bool(Element e, String key, boolean fallback) {
    String value = e.attribute(key);
    if (value == null || value.isBlank()) {
      return fallback;
    }
    value = value.trim();
    return value.equals("1") || value.equalsIgnoreCase("true");
  }

  // ---- XML ----------------------------------------------------------------------------------

  /** The document's root element. Comments, processing instructions and the DOCTYPE are skipped. */
  static Element parse(String xml) {
    Element root = null;
    List<Element> open = new ArrayList<>();
    int i = 0, n = xml.length();
    if (n > 0 && xml.charAt(0) == '﻿') {
      i = 1; // byte order mark
    }
    while (i < n) {
      char c = xml.charAt(i);
      if (c != '<') {
        int end = xml.indexOf('<', i);
        if (end < 0) {
          end = n;
        }
        if (!open.isEmpty()) {
          open.get(open.size() - 1).text.append(unescape(xml.substring(i, end)));
        }
        i = end;
      } else if (xml.startsWith("<!--", i)) {
        i = skipPast(xml, i, "-->");
      } else if (xml.startsWith("<![CDATA[", i)) {
        int end = xml.indexOf("]]>", i);
        if (end < 0) {
          throw error("unfinished CDATA section");
        }
        if (!open.isEmpty()) {
          open.get(open.size() - 1).text.append(xml, i + 9, end);
        }
        i = end + 3;
      } else if (xml.startsWith("<?", i)) {
        i = skipPast(xml, i, "?>");
      } else if (xml.startsWith("<!", i)) {
        i = skipDoctype(xml, i);
      } else if (xml.startsWith("</", i)) {
        int end = xml.indexOf('>', i);
        if (end < 0) {
          throw error("unfinished closing tag");
        }
        String name = xml.substring(i + 2, end).trim();
        if (open.isEmpty() || !open.get(open.size() - 1).name.equals(name)) {
          throw error("</" + name + "> does not close the open element");
        }
        open.remove(open.size() - 1);
        i = end + 1;
      } else {
        int[] position = {i + 1};
        Element element = new Element(name(xml, position));
        boolean selfClosing = false;
        while (true) {
          skipSpace(xml, position);
          if (position[0] >= n) {
            throw error("unfinished <" + element.name + ">");
          }
          char next = xml.charAt(position[0]);
          if (next == '>') {
            position[0]++;
            break;
          }
          if (next == '/' && position[0] + 1 < n && xml.charAt(position[0] + 1) == '>') {
            position[0] += 2;
            selfClosing = true;
            break;
          }
          String key = name(xml, position);
          skipSpace(xml, position);
          if (position[0] >= n || xml.charAt(position[0]) != '=') {
            throw error("attribute " + key + " of <" + element.name + "> has no value");
          }
          position[0]++;
          skipSpace(xml, position);
          char quote = position[0] < n ? xml.charAt(position[0]) : 0;
          if (quote != '"' && quote != '\'') {
            throw error("attribute " + key + " of <" + element.name + "> is not quoted");
          }
          int end = xml.indexOf(quote, position[0] + 1);
          if (end < 0) {
            throw error("unfinished attribute " + key);
          }
          element.attributes.put(key, unescape(xml.substring(position[0] + 1, end)));
          position[0] = end + 1;
        }
        if (open.isEmpty()) {
          if (root != null) {
            throw error("more than one root element");
          }
          root = element;
        } else {
          open.get(open.size() - 1).children.add(element);
        }
        if (!selfClosing) {
          open.add(element);
        }
        i = position[0];
      }
    }
    if (root == null) {
      throw error("no element");
    }
    if (!open.isEmpty()) {
      throw error("<" + open.get(open.size() - 1).name + "> is not closed");
    }
    return root;
  }

  private static IllegalArgumentException error(String what) {
    return new IllegalArgumentException("The file is not well-formed XML: " + what + ".");
  }

  private static int skipPast(String xml, int from, String end) {
    int at = xml.indexOf(end, from);
    if (at < 0) {
      throw error("missing " + end);
    }
    return at + end.length();
  }

  private static int skipDoctype(String xml, int from) {
    int depth = 0;
    for (int i = from; i < xml.length(); i++) {
      char c = xml.charAt(i);
      if (c == '[') {
        depth++;
      } else if (c == ']') {
        depth--;
      } else if (c == '>' && depth <= 0) {
        return i + 1;
      }
    }
    throw error("unfinished <!DOCTYPE");
  }

  private static void skipSpace(String xml, int[] position) {
    while (position[0] < xml.length() && Character.isWhitespace(xml.charAt(position[0]))) {
      position[0]++;
    }
  }

  private static String name(String xml, int[] position) {
    int start = position[0];
    while (position[0] < xml.length()) {
      char c = xml.charAt(position[0]);
      if (Character.isWhitespace(c) || c == '=' || c == '>' || c == '/') {
        break;
      }
      position[0]++;
    }
    if (position[0] == start) {
      throw error("a name is missing at character " + start);
    }
    return xml.substring(start, position[0]);
  }

  /** The five predefined entities and character references. */
  static String unescape(String text) {
    if (text.indexOf('&') < 0) {
      return text;
    }
    StringBuilder out = new StringBuilder(text.length());
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      int end = c == '&' ? text.indexOf(';', i) : -1;
      if (end < 0) {
        out.append(c);
        continue;
      }
      String entity = text.substring(i + 1, end);
      switch (entity) {
        case "amp" -> out.append('&');
        case "lt" -> out.append('<');
        case "gt" -> out.append('>');
        case "quot" -> out.append('"');
        case "apos" -> out.append('\'');
        default -> {
          if (entity.startsWith("#x") || entity.startsWith("#X")) {
            out.appendCodePoint(Integer.parseInt(entity.substring(2), 16));
          } else if (entity.startsWith("#")) {
            out.appendCodePoint(Integer.parseInt(entity.substring(1)));
          } else {
            out.append('&').append(entity).append(';');
          }
        }
      }
      i = end;
    }
    return out.toString();
  }
}
