/**
 * The Optimization class contains a static routine to find the maximum in an array that changes direction at most once.
 */
public class Optimization {

    /**
     * A set of test cases.
     */
    static int[][] testCases = {
            {1, 3, 5, 7, 9, 11, 10, 8, 6, 4},
            {67, 65, 43, 42, 23, 17, 9, 100},
            {4, -100, -80, 15, 20, 25, 30},
            {2, 3, 4, 5, 6, 7, 8, 100, 99, 98, 97, 96, 95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84, 83}
    };

    /**
     * Returns the maximum item in the specified array of integers which changes direction at most once.
     *
     * @param dataArray an array of integers which changes direction at most once.
     * @return the maximum item in data Array
     */
    public static int searchMax(int[] dataArray) {
        if (dataArray.length == 0)
            return 0;
        else if (dataArray.length == 1)
            return dataArray[0];
        else if (dataArray[0] > dataArray[1]) //Descending
            return Math.max(dataArray[0], dataArray[dataArray.length - 1]);
        else { //Ascending
            int high = dataArray.length - 1;
            int low = 0;

            while (low < high) {
                int mid = (low + high) / 2;

                //Integers are rounded down so the case mid == dataArray.length - 1 will never happen
                if (mid != 0)
                    if (dataArray[mid - 1] < dataArray[mid] && dataArray[mid] > dataArray[mid + 1])
                        return dataArray[mid];

                if (dataArray[mid] < dataArray[mid + 1])
                    low = mid + 1;
                else
                    high = mid - 1;
            }
            return dataArray[low];
        }
    }

    /**
     * A routine to test the searchMax routine.
     */
    public static void main(String[] args) {
//        for (int[] testCase : testCases) {
//            System.out.println(searchMax(testCase));
//        }
        System.out.println(searchMax(new int[]{312, 663, 1150, 1347, 9950, 9933, 9911, 9875, 8543, 7930, 7787, 7282, 6674, 5185, 4294, 4129, 3546, 2988, 2479, 2140, 2001}));
    }
}