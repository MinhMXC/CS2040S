import java.util.HashMap;

public class Solution {
    public static int solve(int[] arr, int target) {
        HashMap<Integer, Integer> map = new HashMap<>();
        int count = 0;

        for (int i = 0; i < arr.length; i++) {
            int a = map.getOrDefault(arr[i], 0);
            int b = map.getOrDefault(target - arr[i], 0);

            if (b <= 0) {
                map.put(arr[i], a + 1);
            } else {
                count++;
                map.put(target - arr[i], b - 1);
            }
        }

        return count;
    }

    public static void main(String[] args) {
        int[] arr = new int[]{ 1, 1, 3, 3, 3, 10 };
        System.out.println(solve(arr, 4));
    }
}
