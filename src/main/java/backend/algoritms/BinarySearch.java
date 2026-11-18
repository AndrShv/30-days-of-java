package backend.algoritms;


public class BinarySearch {
    public static int find_number_index(int target, int[] sorted_nums){
        int left = 0;
        int right = sorted_nums.length - 1;
        while(left<=right){
            int mid =  left + (right - left)/2;
            if (sorted_nums[mid] == target){
                return mid;
            } else if (sorted_nums[mid] < target) {
                left = mid+1;
            } else if (sorted_nums[mid] > target) {
                right = mid-1;
            }

        }

        return -1;
    }
    public static void main(String[] args){
        int[] example_arr = {2,5,6,9,13,19,24,29, 34, 37, 41};
        int found_index = find_number_index(37, example_arr);
        if (found_index != -1) {
            System.out.println("Индекс найденного числа: " + found_index);
        } else {
            System.out.println("Число не найдено в массиве.");
        }

    }
}
