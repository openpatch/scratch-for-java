package org.openpatch.scratch.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.openpatch.scratch.ScratchException;

class NrwListTest {

  /** Stands in for the Abitur's List, which lives in the default package. */
  static class AbiturList<ContentType> {
    final java.util.List<ContentType> appended = new ArrayList<>();

    public void append(ContentType pContent) {
      appended.add(pContent);
    }
  }

  static class NotAnAbiturList {}

  private static final String ABITUR_LIST = AbiturList.class.getName();

  @Test
  void appendsEveryItemInOrder() {
    AbiturList<String> list = NrwList.of(Arrays.asList("a", "b", "c"), ABITUR_LIST, NrwListTest.class);

    assertEquals(Arrays.asList("a", "b", "c"), list.appended);
  }

  @Test
  void returnsAnEmptyListForNoItems() {
    AbiturList<String> list = NrwList.of(new ArrayList<String>(), ABITUR_LIST, NrwListTest.class);

    assertEquals(0, list.appended.size());
  }

  @Test
  void findsTheClassThroughTheContextClassLoaderWithoutHints() {
    Object list = NrwList.of(Arrays.asList(1), ABITUR_LIST);

    assertInstanceOf(AbiturList.class, list);
  }

  @Test
  void failsWhenThereIsNoListInTheProject() {
    ScratchException e = assertThrows(ScratchException.class,
        () -> NrwList.of(Arrays.asList(1), "NoSuchList", NrwListTest.class));

    assertEquals("There is no class NoSuchList in your project.", e.getMessage());
  }

  @Test
  void failsWhenTheListHasNoAppend() {
    String name = NotAnAbiturList.class.getName();
    ScratchException e = assertThrows(ScratchException.class,
        () -> NrwList.of(Arrays.asList(1), name, NrwListTest.class));

    assertEquals("Your class " + name + " is not the List from the NRW Abitur.", e.getMessage());
  }
}
