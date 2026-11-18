package backend.algorithms;

public class SlidingWindow {
    public static int findPeak(int windowSize, int[] nums){


        if (nums == null || windowSize <= 0 || windowSize > nums.length) {
            throw new IllegalArgumentException("Некорректный размер окна или массив");
        }

        int windowSum = 0;
        int maxSum = windowSum;
        for(int i = 0; i<windowSize; i++){
            windowSum += nums[i];
        }
        for(int i = 0; i < nums.length - windowSize; i++){
            windowSum = windowSum - nums[i] + nums[i+windowSize];
            maxSum = Math.max(maxSum, windowSum);
        }
        return maxSum;
    }
    public static void main(String[] args){
        int windowSize = 3;
        int[] exampleArr = {
                42, 7, 91, 15, 63, 28, 84, 3, 55, 19,
                76, 11, 98, 34, 67, 22, 50, 89, 5, 71,
                14, 81, 39, 60, 2, 95, 48, 25, 73, 17
        };
        int peakNum = findPeak(windowSize, exampleArr);
        System.out.println("peak nums is " + peakNum);


    }
}
