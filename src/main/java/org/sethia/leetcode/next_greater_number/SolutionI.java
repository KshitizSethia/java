package org.sethia.leetcode.next_greater_number;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolutionI {

  public int[] nextGreaterElement(int[] nums1, int[] nums2) {
    if (nums1 == null || nums1.length == 0) {
      return nums1;
    }

    return nextGreaterElement_caching(nums1, nums2);
  }

  private int[] nextGreaterElement_caching(int[] nums1, int[] nums2) {
    Map<Integer, Integer> locations = new HashMap<>();
    int[] nextHigherIndex = new int[nums2.length];

    for (int index : getIndices(nums2.length)) {
      final int num = nums2[index];
      locations.put(num, index);

      int lookForwardIndex = index + 1;
      int higherIndex = -1;

      while (lookForwardIndex != -1
          && lookForwardIndex < nums2.length) {
        if (num < nums2[lookForwardIndex]) {
          higherIndex = lookForwardIndex;
          break;
        } else {
          lookForwardIndex = nextHigherIndex[lookForwardIndex];
        }
      }
      nextHigherIndex[index] = higherIndex;
    }

    int[] results = new int[nums1.length];
    for (int index = 0; index < nums1.length; index++) {
      int higherIndex = nextHigherIndex[locations.get(nums1[index])];
      results[index] = higherIndex == -1 ? higherIndex : nums2[higherIndex];
    }

    return results;
  }

  protected List<Integer> getIndices(final int nums2Length) {
    List<Integer> indices = new ArrayList<>();
    for (int index = nums2Length - 1; index >= 0; index--) {
      indices.add(index);
    }
    return indices;
  }

  int[] nextGreaterElement_bruteForce(int[] nums1, int[] nums2) {
    Map<Integer, Integer> locations = new HashMap<>();
    for (int index = 0; index < nums2.length; index++) {
      locations.put(nums2[index], index);
    }

    int[] results = new int[nums1.length];

    for (int index = 0; index < nums1.length; index++) {
      int result = -1;
      for (int index2 = locations.get(nums1[index]);
          index2 < nums2.length;
          index2++) {
        if (nums1[index] < nums2[index2]) {
          result = nums2[index2];
          break;
        }
      }
      results[index] = result;
    }

    return results;
  }
}
