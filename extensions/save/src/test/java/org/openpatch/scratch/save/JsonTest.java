package org.openpatch.scratch.save;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class JsonTest {

  public enum Kind { COIN, GEM }

  public static class Item {
    public String name = "";
    public Kind kind = Kind.COIN;
    public double x;
  }

  public static class Inventory {
    public List<Item> items = new ArrayList<>();
    public Map<String, Integer> counts = new LinkedHashMap<>();
    public Set<Kind> seen = Set.of();
    public int[][] grid = {{1, 2}, {3}};
    public char letter = 'ä';
    public double ratio = Double.NaN;
    public Long nothing;
    public String text = "line\n\"quoted\" \u0001 🦔";
  }

  /** Bean properties count for File, not for SaveData. */
  public static class Bean {
    private int score = 3;

    public int getScore() {
      return this.score;
    }

    public void setScore(int score) {
      this.score = score;
    }
  }

  @Test
  void everyKindOfValueSurvivesTheRoundTrip() throws Exception {
    Inventory inventory = new Inventory();
    Item gem = new Item();
    gem.name = "ruby";
    gem.kind = Kind.GEM;
    gem.x = -1.5;
    inventory.items.add(gem);
    inventory.counts.put("ruby", 2);
    inventory.seen = Set.of(Kind.GEM);

    Inventory back = Json.read(Json.write(inventory, false), Inventory.class, true, false);

    assertEquals(1, back.items.size());
    assertEquals("ruby", back.items.get(0).name);
    assertEquals(Kind.GEM, back.items.get(0).kind);
    assertEquals(-1.5, back.items.get(0).x);
    assertEquals(Map.of("ruby", 2), back.counts);
    assertEquals(Set.of(Kind.GEM), back.seen);
    assertArrayEquals(new int[] {3}, back.grid[1]);
    assertEquals('ä', back.letter);
    assertTrue(Double.isNaN(back.ratio));
    assertEquals(null, back.nothing);
    assertEquals(inventory.text, back.text);
  }

  @Test
  void strictReadingNamesWhatDoesNotFit() {
    Json.JsonException e = assertThrows(Json.JsonException.class,
        () -> Json.read("{\"items\": [{\"name\": 5}]}", Inventory.class, true, false));
    assertEquals("$.items[0].name should be a string, but is 5", e.getMessage());
    e = assertThrows(Json.JsonException.class,
        () -> Json.read("{\"items\": [{\"kind\": \"STAR\"}]}", Inventory.class, true, false));
    assertTrue(e.getMessage().startsWith("$.items[0].kind should be one of [COIN, GEM]"), e.getMessage());
  }

  @Test
  void beansAreReadAndWrittenOnlyWhenAsked() throws Exception {
    assertEquals("{}\n", Json.write(new Bean(), false));
    assertTrue(Json.write(new Bean(), true).contains("\"score\": 3"));
    assertEquals(9, Json.read("{\"score\": 9}", Bean.class, false, true).getScore());
  }
}
