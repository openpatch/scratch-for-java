package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * What can be asked of a Text without a window behind it.
 *
 * <p>
 * Which is not much - almost everything about a Text only happens when it is
 * drawn - but the style is worth holding onto, because draw() switches on it and
 * a null there is not a wrong picture but no picture at all.
 */
class TextTest {

  @Test
  @DisplayName("a text built with words has a style, so that it can be drawn")
  void textBuiltWithWordsHasAStyle() {
    // This constructor left the style null. draw() switches on it, so every
    // frame threw a NullPointerException from inside the loading screen, and a
    // program that used it never got past the loading screen at all: a white
    // stage, no error anyone would see, nothing drawn ever.
    var text = new Text("Hello", 0, 0, 200);
    assertNotNull(text.getStyle(), "a new text must have a style to be drawn with");
    assertEquals(TextStyle.PLAIN, text.getStyle());
  }

  @Test
  @DisplayName("every way of building a text leaves it with a style")
  void everyConstructorLeavesAStyle() {
    assertNotNull(new Text().getStyle());
    assertNotNull(new Text("Hello", 0, 0, 200).getStyle());
    assertNotNull(new Text("Hello", 0, 0, 200, TextStyle.BOX).getStyle());
    assertNotNull(new Text(new Text("Hello", 0, 0, 200)).getStyle());
  }

  @Test
  @DisplayName("the style asked for is the style kept")
  void theStyleAskedForIsKept() {
    assertEquals(TextStyle.BOX, new Text("Hello", 0, 0, 200, TextStyle.BOX).getStyle());

    var text = new Text("Hello", 0, 0, 200);
    text.setStyle(TextStyle.THINK);
    assertEquals(TextStyle.THINK, text.getStyle());

    // and a copy keeps it
    assertEquals(TextStyle.THINK, new Text(text).getStyle());
  }
}
