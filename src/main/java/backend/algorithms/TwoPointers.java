package backend.algorithms;

import java.util.HashMap;

public class TwoPointers {
    public static HashMap<Integer, Integer> find_sum(int target_sum, int[] sorted_nums) {
        int left = 0;
        int right = sorted_nums.length - 1;
        while (left < right) {
            int sum = sorted_nums[left] + sorted_nums[right];
            if (sum < target_sum) {
                left += 1;
            } else if (sum > target_sum) {
                right -= 1;
            } else {
                HashMap<Integer, Integer> result = new HashMap<>();
                result.put(left, right);
                return result;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        int[] example_arr = {2, 5, 6, 9, 13, 19, 24, 29, 32, 34, 37, 41};
        int target = 32;
        HashMap<Integer, Integer> obtained_sum = find_sum(target, example_arr);
        if (obtained_sum != null) {
            System.out.println(obtained_sum);
        } else {
            System.out.println("obtained_sum is null");
        }

    }
}
