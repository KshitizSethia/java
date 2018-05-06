package org.sethia.leetcode.next_greater_number;

import static org.junit.Assert.assertArrayEquals;
import static org.sethia.test.TestUtils.a;

import java.util.Arrays;
import java.util.Random;
import org.junit.Test;

public class SolutionIITest {

  private static final SolutionII sol = new SolutionII();

  @Test
  public void nextGreaterElements() {
    assertArrayEquals(a(2, -1, 2), sol.nextGreaterElements(a(1, 2, 1)));
  }

  @Test
  public void nextGreaterElements_item2() {
    assertArrayEquals(a(3, -1, 2, 3, 3), sol.nextGreaterElements(a(1, 3, 1, 2, 1)));
  }

  @Test
  public void nextGenerateElements_random1() {
    assertArrayEquals(a(8, 8, 9, -1, 6, 6, 6, 9, -1, 8)
        , sol.nextGreaterElements(a(1, 1, 8, 9, 5, 4, 4, 6, 9, 3)));
  }

  @Test
  public void nextGenerateElements_random2() {
    assertArrayEquals(a(62, 91, 91, 99, 99, -1, 62, 79, 91), sol.nextGreaterElements(a(3, 62, 2, 91, 43, 99, 44, 62, 79)));
  }

  @Test
  public void nextGreaterElements_randomGenerator() {
    Random r = new Random();

    for (int i = 0; i < 10; i++) {
      System.out.println(Arrays.toString(r.ints(0, 10).limit(10).toArray()));
    }
  }
}