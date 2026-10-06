package org.openpatch.scratch.extensions.tiled;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Maps as Tiled 1.9 to 1.11 writes them load (parsing only, no window). */
class TiledFormatsTest {

  @TempDir
  Path tmp;

  private Map load(String xml) throws Exception {
    Path file = tmp.resolve("map" + System.nanoTime() + ".tmx");
    Files.writeString(file, xml);
    return TiledMap.read(file.toString(), Map.class);
  }

  private static String map(String body) {
    return """
        <?xml version="1.0" encoding="UTF-8"?>
        <map version="1.10" tiledversion="1.11.0" orientation="orthogonal" renderorder="right-down"
             width="2" height="2" tilewidth="8" tileheight="8" infinite="0">
          <tileset firstgid="1" name="t" tilewidth="8" tileheight="8" tilecount="4" columns="2">
            <image source="tiles.png" width="16" height="16"/>
          </tileset>
        %s
        </map>
        """.formatted(body);
  }

  private static byte[] gids(long... values) {
    ByteBuffer buffer = ByteBuffer.allocate(values.length * 4).order(ByteOrder.LITTLE_ENDIAN);
    for (long v : values) {
      buffer.putInt((int) v);
    }
    return buffer.array();
  }

  @Test
  void csvWithFlipBitsLoadsAndKeepsTheFlips() throws Exception {
    long flipped = 0x80000000L | 3; // horizontal flip of tile 3
    Map m = load(map("""
        <layer id="1" name="ground" width="2" height="2">
          <data encoding="csv">
        1,2,
        %d,0
        </data>
        </layer>
        """.formatted(flipped)));
    assertArrayEquals(new int[] {1, 2, 3, 0}, m.layers[0].data);
    assertEquals(4, m.layers[0].flips[2]);
  }

  @Test
  void base64ZlibAndGzipLoad() throws Exception {
    for (String compression : new String[] {"zlib", "gzip", ""}) {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      var stream = compression.equals("zlib") ? new DeflaterOutputStream(out)
          : compression.equals("gzip") ? new GZIPOutputStream(out) : out;
      stream.write(gids(1, 0x40000000L | 2, 3, 4));
      stream.close();
      String attribute = compression.isEmpty() ? "" : " compression=\"" + compression + "\"";
      Map m = load(map("""
          <layer id="1" name="ground" width="2" height="2">
            <data encoding="base64"%s>%s</data>
          </layer>
          """.formatted(attribute, Base64.getEncoder().encodeToString(out.toByteArray()))));
      assertArrayEquals(new int[] {1, 2, 3, 4}, m.layers[0].data, compression);
      assertEquals(2, m.layers[0].flips[1], compression);
    }
  }

  @Test
  void tiled19ClassIsTheObjectsType() throws Exception {
    Map m = load(map("""
        <objectgroup id="2" name="Objects" class="spawns">
          <object id="1" name="start" class="spawn-point" x="10" y="20"/>
          <object id="2" name="old" type="warp" x="0" y="0"/>
          <object id="3" name="both" type="explicit" class="ignored" x="0" y="0"/>
        </objectgroup>
        """));
    var objects = m.objectGroups[0].objects;
    assertEquals("spawn-point", objects[0].type);
    assertEquals("warp", objects[1].type);
    assertEquals("explicit", objects[2].type);
    assertEquals("spawns", m.objectGroups[0].type);
  }

  @Test
  void groupLayersMultilineAndClassProperties() throws Exception {
    Map m = load(map("""
        <group id="5" name="world">
          <layer id="1" name="inner" width="2" height="2">
            <data encoding="csv">1,1,1,1</data>
          </layer>
          <objectgroup id="2" name="things">
            <object id="1" name="sign" x="0" y="0">
              <properties>
                <property name="text">Hello
        World</property>
                <property name="door" type="class" propertytype="Door">
                  <properties>
                    <property name="locked" type="bool" value="true"/>
                  </properties>
                </property>
              </properties>
            </object>
          </objectgroup>
        </group>
        """));
    assertNull(m.layers);
    assertEquals("inner", m.groups[0].layers[0].name);
    var sign = m.groups[0].objectGroups[0].objects[0];
    assertEquals("Hello\nWorld", sign.getProperty("text").strip());
    var door = sign.properties.stream().filter(p -> p.name.equals("door")).findFirst()
        .orElseThrow();
    assertEquals("locked", door.properties.get(0).name);
    assertEquals("true", door.properties.get(0).value);
  }

  @Test
  void externalTilesetsKeepTheirSource() throws Exception {
    Map m = load("""
        <?xml version="1.0" encoding="UTF-8"?>
        <map orientation="orthogonal" width="1" height="1" tilewidth="8" tileheight="8">
          <tileset firstgid="1" source="tiles/terrain.tsx"/>
          <layer id="1" name="a" width="1" height="1"><data encoding="csv">1</data></layer>
        </map>
        """);
    assertEquals("tiles/terrain.tsx", m.tilesets[0].source);
    assertEquals(1, m.tilesets[0].firstgid);
  }

  @Test
  void infiniteMapsAndZstdExplainThemselves() {
    var infinite = assertThrows(RuntimeException.class, () -> load(map("""
        <layer id="1" name="a" width="2" height="2">
          <data encoding="csv"><chunk x="0" y="0" width="16" height="16">1</chunk></data>
        </layer>
        """)));
    assertTrue(message(infinite).contains("infinite"), message(infinite));
    var zstd = assertThrows(RuntimeException.class, () -> load(map("""
        <layer id="1" name="a" width="2" height="2">
          <data encoding="base64" compression="zstd">AAAA</data>
        </layer>
        """)));
    assertTrue(message(zstd).contains("zstd"), message(zstd));
  }

  private static String message(Throwable t) {
    StringBuilder sb = new StringBuilder();
    for (Throwable c = t; c != null; c = c.getCause()) {
      sb.append(c.getMessage()).append(" | ");
    }
    return sb.toString();
  }

  @Test
  void objectsKeepTheirStageYWhenAskedTwiceAndUnknownLayersAreExplained() throws Exception {
    Map m = load(map("""
        <layer id="1" name="ground" width="2" height="2">
          <data encoding="csv">1,2,3,4</data>
        </layer>
        <objectgroup id="2" name="Objects">
          <object id="1" name="start" x="4" y="12"><point/></object>
        </objectgroup>
        """));
    assertEquals(-12, TiledMap.objectsFromLayer(m, "Objects")[0].y);
    assertEquals(-12, TiledMap.objectsFromLayer(m, "Objects")[0].y);
    assertNull(TiledMap.objectsFromLayer(m, "Nope"));
    assertTrue(TiledMap.unknownLayer(m, "Objects").contains("object layer"));
    assertTrue(TiledMap.unknownLayer(m, "Grund").contains("Tile layers: ground"));
  }
}
