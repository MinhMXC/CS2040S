class Sorter {

    public static boolean isGreaterThan(String str1, String str2) {
        int res1 = Character.compare(str1.charAt(0), str2.charAt(0));
        int res2 = Character.compare(str1.charAt(1), str2.charAt(1));
        int ans =  res1 != 0 ? res1 : res2;
        return ans > 0;
    }

    public static void sortStrings(String[] arr) {
        for (int i = 1; i < arr.length; i++)
            for (int j = i - 1; j >= 0; j--) {
                if (isGreaterThan(arr[j], arr[j + 1])) {
                    String temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                } else {
                    break;
                }
            }
    }
}
