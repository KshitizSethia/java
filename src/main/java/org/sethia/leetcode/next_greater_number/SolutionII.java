package org.sethia.leetcode.next_greater_number;

public class SolutionII {

  public int[] nextGreaterElements(int[] nums) {
    if (nums == null || nums.length == 0) {
      return nums;
    }
    int[] resultIndices = new int[nums.length];
    int[] results = new int[nums.length];

    for (int findNextGreaterForThisIndex = nums.length - 1; findNextGreaterForThisIndex >= 0;
        findNextGreaterForThisIndex--) {
      int lookAheadIndex = (1 + findNextGreaterForThisIndex) % nums.length;
      int indexForNextGreater = -1;
      while (indexForNextGreater == -1) {

        if (nums[findNextGreaterForThisIndex] < nums[lookAheadIndex]) {
          indexForNextGreater = lookAheadIndex;
        } else if (resultIndices[lookAheadIndex] == -1) {
          break;
        } else {
          if (resultIndices[lookAheadIndex] == 0) {
            lookAheadIndex = (1 + lookAheadIndex) % nums.length;
          } else {
            lookAheadIndex = resultIndices[lookAheadIndex];
          }
          if (lookAheadIndex == findNextGreaterForThisIndex) {
            break;
          }
        }
      }
      resultIndices[findNextGreaterForThisIndex] = indexForNextGreater;
      results[findNextGreaterForThisIndex] =
          indexForNextGreater == -1 ? -1 : nums[indexForNextGreater];
    }

    return results;
  }
}
