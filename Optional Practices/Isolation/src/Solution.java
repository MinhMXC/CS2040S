import java.util.HashMap;
import java.util.Hashtable;

public class Solution {
    // TODO: Implement your solution here
    public static int solve(int[] arr) {
        HashMap<Integer, Integer> map = new HashMap<>();
        int maxLength = 0;
        int start = 0;

        for (int i = 0; i < arr.length; i++) {
            if (map.containsKey(arr[i])) {
                 int pos = map.get(arr[i]);
                 if (maxLength < pos - start + 1)
                     maxLength = pos - start + 1;
                 if (maxLength < i - start)
                     maxLength = i - start;
                 if (start < pos + 1)
                     start = pos + 1;
            }
            map.put(arr[i], i);
        }

        if (maxLength < arr.length - start)
            maxLength = arr.length - start;

        return maxLength;
    }

    public static void main(String[] args) {
        int[] arr = new int[]{  };
        System.out.println(solve(arr));
    }
}