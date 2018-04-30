package org.sethia.leetcode.next_greater_number;

public class SolutionII {

  public int[] nextGreaterElements(int[] nums) {
    int[] results = new int[nums.length];

    for (int findNextGreaterForThisIndex = nums.length - 1; findNextGreaterForThisIndex >= 0;
        findNextGreaterForThisIndex--) {
      int lookAheadIndex = incrementAndWrap(nums.length, findNextGreaterForThisIndex);
      int indexForNextGreater = -1;
      while (indexForNextGreater == -1
          && lookAheadIndex < findNextGreaterForThisIndex) {

        if (nums[findNextGreaterForThisIndex] < nums[lookAheadIndex]) {
          indexForNextGreater = lookAheadIndex;
        } else if (results[lookAheadIndex] == -1) {
          break;
        } else if (results[lookAheadIndex] == 0) {
          lookAheadIndex = incrementAndWrap(nums.length, lookAheadIndex);
        } else {
          lookAheadIndex = results[lookAheadIndex];
        }
      }
      results[findNextGreaterForThisIndex] = indexForNextGreater;
    }

    return results;
  }

  private int incrementAndWrap(int numsLength, int findNextGreaterForThisIndex) {
    return (findNextGreaterForThisIndex + 1) % numsLength;
  }

}
