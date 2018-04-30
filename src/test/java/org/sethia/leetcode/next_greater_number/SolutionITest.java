package org.sethia.leetcode.next_greater_number;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class SolutionITest {

  SolutionI solutionI = new SolutionI();

  private int[] a(int... elements) {
    return elements;
  }

  @Test
  public void test_cornerCases() {
    assertArrayEquals(a(), solutionI.nextGreaterElement(a(), a(1)));
    assertArrayEquals(a(), solutionI.nextGreaterElement(a(), a()));
    assertArrayEquals(null, solutionI.nextGreaterElement(null, a(1)));
  }

  @Test
  public void test_nextGreaterElement_t1() {
    assertArrayEquals(a(-1, 3, -1), solutionI.nextGreaterElement(a(4, 1, 2), a(1, 3, 4, 2)));
  }

  @Test
  public void test_nextGreaterElement_t2() {
    assertArrayEquals(a(3, -1), solutionI.nextGreaterElement(a(2, 4), a(1, 2, 3, 4)));
  }

  @Test
  public void test_nextGreaterElement_againstRandoms() {
    Random r = new Random();
    int failCounter=0;
    for (int testNum = 0; testNum < 100; testNum++) {

      List<Integer> list = new ArrayList<>();
      Set<Integer> uniques = new HashSet<>();
      for (int i = 0; i < 10; i++) {
        final int num = r.nextInt(100);
        if(uniques.contains(num)){
          i--;
          continue;
        }
        uniques.add(num);
        list.add(num);
      }

      int[] input = list.stream().mapToInt(i -> i).toArray();

      try {
        Assert.assertArrayEquals(solutionI.nextGreaterElement_bruteForce(input, input),
            solutionI.nextGreaterElement(input, input));
      } catch (AssertionError e) {
        System.out.println("failed for " + list);
        failCounter++;
      }
    }
    assertEquals(0, failCounter);
  }

  @Test
  public void test_nextGreaterElement_randomSeq1() {
    int[] a = a(14, 97, 24, 1, 27, 13, 38, 46, 96, 87);
    assertArrayEquals(a(97, -1, 27, 27, 38, 38, 46, 96, -1, -1), solutionI.nextGreaterElement(a, a));
  }
}