package org.openpatch.scratch.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class WebFilesTest {

  @Test
  void resolvesTheSegmentsOfAPathAsAFileSystemDoes() {
    // a tileset's image path can reach out of the map's folder
    assertEquals("assets/bg.png", WebFiles.key("assets/maps/../../assets/bg.png"));
    assertEquals("assets/bg.png", WebFiles.key("./assets/./bg.png"));
    assertEquals("assets/bg.png", WebFiles.key("/assets//bg.png"));
    assertEquals("bg.png", WebFiles.key("assets/../bg.png"));
    // there is nothing above the page's root folder
    assertEquals("assets/bg.png", WebFiles.key("../../assets/bg.png"));
    assertEquals("", WebFiles.key("/"));
  }
}
