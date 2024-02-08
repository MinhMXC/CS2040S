class InversionCounter {

    public static long countSwaps(int[] arr) {
        return helper(arr, 0, arr.length - 1);
    }

    public static long helper(int[] arr, int start, int end) {
        if (start >= end)
            return 0;
        int mid = start + (end - start) / 2;
        long a = helper(arr, start, mid);
        long b = helper(arr, mid + 1, end);
        return a + b + mergeAndCount(arr, start, mid, mid + 1, end);
    }

    /**
     * Given an input array so that arr[left1] to arr[right1] is sorted and arr[left2] to arr[right2] is sorted
     * (also left2 = right1 + 1), merges the two so that arr[left1] to arr[right2] is sorted, and returns the
     * minimum amount of adjacent swaps needed to do so.
     */
    public static long mergeAndCount(int[] arr, int left1, int right1, int left2, int right2) {
        int[] temp = new int[right2 - left1 + 1];
        int cur_left = left1;
        int cur_right = left2;
        long count = 0;

        for (int i = 0; i < temp.length; i++) {
            if (cur_left > right1) {
                temp[i] = arr[cur_right];
                cur_right++;
            } else if (cur_right > right2) {
                temp[i] = arr[cur_left];
                cur_left++;
            } else if (arr[cur_left] <= arr[cur_right]) {
                temp[i] = arr[cur_left];
                cur_left++;
            } else {
                temp[i] = arr[cur_right];
                count += right1 - cur_left + 1;
                cur_right++;
            }
        }

        //Copy temp to main arr
        for (int i = 0; i < temp.length; i++)
            arr[left1 + i] = temp[i];

        return count;
    }
}
