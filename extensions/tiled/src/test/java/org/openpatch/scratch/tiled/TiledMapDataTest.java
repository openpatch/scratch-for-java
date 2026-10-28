package org.openpatch.scratch.tiled;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openpatch.scratch.Bounds;

class TiledMapDataTest {
  @TempDir Path directory;

  private TiledMap map(String cells) throws Exception {
    ImageIO.write(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), "png",
        directory.resolve("tiles.png").toFile());
    Files.writeString(directory.resolve("tiles.tsx"), """
        <tileset name="test" tilewidth="16" tileheight="16" tilecount="1" columns="1">
          <image source="tiles.png" width="16" height="16"/>
          <tile id="0"><objectgroup>
            <object id="1" x="2" y="3" width="4" height="6"/>
            <object id="2" x="10" y="2" width="4" height="2"><ellipse/></object>
            <object id="3" x="4" y="8" rotation="90"><polygon points="0,0 3,0 0,2"/></object>
          </objectgroup></tile>
        </tileset>
        """);
    Files.writeString(directory.resolve("map.tmx"), """
        <map orientation="orthogonal" width="8" height="1" tilewidth="16" tileheight="16">
          <properties>
            <property name="biome" value="forest"/>
            <property name="lives" type="int" value="3"/>
            <property name="gravity" type="float" value="1.25"/>
            <property name="tutorial" type="bool" value="true"/>
            <property name="message">first line
        second line</property>
          </properties>
          <tileset firstgid="7" source="tiles.tsx"/>
          <group name="group"><layer name="ground" width="8" height="1">
            <data encoding="csv">%s</data>
          </layer></group>
        </map>
        """.formatted(cells));
    return new TiledMap(directory.resolve("map.tmx").toString());
  }

  @Test
  void readsMapPropertiesDimensionsAndUnflaggedTileIdsWithoutWindow() throws Exception {
    TiledMap map = map("7,0,2147483655,0,0,0,0,0");
    assertEquals(8, map.getWidth());
    assertEquals(1, map.getHeight());
    assertEquals(16, map.getTileWidth());
    assertEquals(16, map.getTileHeight());
    assertEquals("forest", map.getProperty("biome"));
    assertEquals(3, map.getPropertyInt("lives"));
    assertEquals(1.25, map.getPropertyFloat("gravity"));
    assertTrue(map.getPropertyBoolean("tutorial"));
    assertEquals("first line\nsecond line", map.getProperty("message"));
    assertThrows(NoSuchElementException.class, () -> map.getProperty("absent"));
    assertEquals("default", map.getProperty("absent", "default"));
    assertEquals(7, map.getTileId("ground", 2, 0));
    assertEquals(0, map.getTileId("ground", 1, 0));
    assertEquals(0, map.getTileId("ground", -1, 0));
    assertEquals(0, map.getTileId("ground", 8, 0));
    assertEquals(0, map.getTileId("ground", 0, 1));
    assertThrows(IllegalArgumentException.class, () -> map.getTileId("missing", 0, 0));
    assertThrows(IllegalStateException.class, () -> map.showLayer("ground", org.openpatch.scratch.Layer.BACKGROUND));
  }

  @Test
  void exposesIndependentCollisionShapesFromExternalTilesets() throws Exception {
    TiledMap map = map("7,0,0,0,0,0,0,0");
    var shapes = map.getTileCollisionShapes(7);
    assertEquals(3, shapes.length);
    bounds(shapes[0].getBounds(), 2, -9, 4, 6);
    bounds(shapes[1].getBounds(), 10, -4, 4, 2);
    bounds(shapes[2].getBounds(), 2, -11, 2, 3);
    shapes[0] = shapes[0].translate(100, 100);
    bounds(map.getTileCollisionShapes(7)[0].getBounds(), 2, -9, 4, 6);
    assertEquals(0, map.getTileCollisionShapes(0).length);
    assertEquals(0, map.getTileCollisionShapes(999).length);
    assertEquals(0, map.getTileCollisionShapes("ground", -1, 0).length);
  }

  @Test
  void clearsRetainedHexagonalFlagForEveryOrthogonalFlip() throws Exception {
    StringBuilder cells = new StringBuilder();
    for (int flags = 0; flags < 8; flags++) {
      if (flags != 0) cells.append(',');
      cells.append(7L | 0x10000000L | ((long) flags << 29));
    }
    TiledMap flagged = map(cells.toString());
    for (int flags = 0; flags < 8; flags++) {
      assertEquals(7, flagged.getTileId("ground", flags, 0));
      assertEquals(3, flagged.getTileCollisionShapes("ground", flags, 0).length);
    }
  }

  @Test
  void collisionShapesFollowAllEightTileFlipCombinations() throws Exception {
    StringBuilder cells = new StringBuilder();
    for (int flags = 0; flags < 8; flags++) {
      if (flags != 0) cells.append(',');
      cells.append(7L | ((long) flags << 29));
    }
    TiledMap map = map(cells.toString());
    for (int flags = 0; flags < 8; flags++) {
      double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
      double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
      for (double originalX : new double[] {2, 6}) for (double originalY : new double[] {3, 9}) {
        double x = originalX, y = originalY;
        if ((flags & 1) != 0) { double swap = x; x = y; y = swap; }
        double px = (flags & 4) != 0 ? 16 - x : x;
        double py = (flags & 2) != 0 ? 16 - y : y;
        minX = Math.min(minX, px); maxX = Math.max(maxX, px);
        minY = Math.min(minY, py); maxY = Math.max(maxY, py);
      }
      bounds(map.getTileCollisionShapes("ground", flags, 0)[0].getBounds(),
          flags * 16 + minX, -maxY, maxX - minX, maxY - minY);
    }
  }

  private static void bounds(Bounds actual, double x, double y, double width, double height) {
    assertEquals(x, actual.x(), 0.00001);
    assertEquals(y, actual.y(), 0.00001);
    assertEquals(width, actual.width(), 0.00001);
    assertEquals(height, actual.height(), 0.00001);
  }

  @Test
  void aShownLayerIsDrawnEveryFrameThroughTheLayerDrawerOnlyWhereTheCameraLooks() throws Exception {
    var game = org.openpatch.scratch.internal.Game.headless(480, 360);
    game.getExtensions().install(java.util.List.of(new TiledExtension()));
    var stage = new org.openpatch.scratch.Stage();
    game.setStage(stage);
    map("7,0,2147483655,0,0,0,0,0"); // writes the files
    TiledMap map = new TiledMap(directory.resolve("map.tmx").toString(), stage);
    map.showLayer("ground", org.openpatch.scratch.Layer.BACKGROUND);
    var items = game.drawList().layers().get(org.openpatch.scratch.Layer.BACKGROUND.ordinal()).items();
    assertEquals(2, items.size(), "two tiles in the row");
    var plain = (org.openpatch.scratch.internal.draw.ImageDraw) items.get(0);
    var flipped = (org.openpatch.scratch.internal.draw.ImageDraw) items.get(1);
    // the first tile's top-left corner is the world origin; the flipped one starts at its right edge
    assertEquals(0, plain.corners()[0]);
    assertEquals(0, plain.corners()[1]);
    assertEquals(48, flipped.corners()[0], "flipped horizontally: its top-left corner is drawn on the right");
    stage.getCamera().setPosition(5000, 5000);
    assertTrue(game.drawList().layers().get(org.openpatch.scratch.Layer.BACKGROUND.ordinal()).items().isEmpty(),
        "tiles the camera does not show are not drawn");
    stage.getCamera().setPosition(0, 0);
    map.hideLayer("ground");
    assertTrue(game.drawList().layers().get(org.openpatch.scratch.Layer.BACKGROUND.ordinal()).items().isEmpty());
  }

  public static class Coin extends org.openpatch.scratch.Sprite {}

  public static class Rock extends org.openpatch.scratch.Sprite {
    final double madeAtX;

    public Rock(double x, double y) {
      this.madeAtX = x;
    }
  }

  public static class Sign extends org.openpatch.scratch.Sprite {
    final String text;

    public Sign(MapObject object) {
      this.text = object.getProperty("text");
    }
  }

  @Test
  void spawnMakesTheGivenClassesWhereTheObjectsTypesNameThem() throws Exception {
    var game = org.openpatch.scratch.internal.Game.headless(480, 360);
    game.getExtensions().install(java.util.List.of(new TiledExtension()));
    var stage = new org.openpatch.scratch.Stage();
    game.setStage(stage);
    map("0,0,0,0,0,0,0,0"); // writes the tileset
    Files.writeString(directory.resolve("objects.tmx"), """
        <map orientation="orthogonal" width="8" height="1" tilewidth="16" tileheight="16">
          <tileset firstgid="1" source="tiles.tsx"/>
          <objectgroup name="Objects">
            <object id="1" type="Coin" x="10" y="20"><point/></object>
            <object id="2" type="Rock" x="30" y="40"><point/></object>
            <object id="3" type="Sign" x="50" y="60">
              <properties><property name="text" value="hello"/></properties><point/>
            </object>
            <object id="4" type="Coin" x="70" y="80" width="8" height="4"/>
            <object id="5" type="marker" x="0" y="0"><point/></object>
            <object id="6" x="0" y="0"><point/></object>
          </objectgroup>
        </map>
        """);
    TiledMap map = new TiledMap(directory.resolve("objects.tmx").toString(), stage);

    var made = map.spawn("Objects", Coin.class, Rock.class);
    assertEquals(3, made.size(), "only the given classes: Sign is a sprite class, but not one of them");
    assertInstanceOf(Coin.class, made.get(0));
    assertEquals(10, made.get(0).getX());
    assertEquals(-20, made.get(0).getY());
    assertEquals(30, ((Rock) made.get(1)).madeAtX, "(double x, double y) gets the object's position");
    assertEquals(4, made.get(2).getHitbox().getBounds().height(), 0.00001, "an area becomes the hitbox");
    assertEquals(3, stage.getAll().size());

    var signs = map.spawn("Objects", Sign.class);
    assertEquals(1, signs.size());
    assertEquals("hello", ((Sign) signs.get(0)).text);

    var thrown = assertThrows(IllegalArgumentException.class, () -> map.spawn("Objects"));
    assertTrue(thrown.getMessage().contains("map.spawn(\"Objects\", Player.class, Wall.class)"));
    assertThrows(IllegalArgumentException.class, () -> map.spawn("Missing", Coin.class));
  }
}
