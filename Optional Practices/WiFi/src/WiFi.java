import java.util.Arrays;
class WiFi {

    /**
     * Implement your solution here
     */
    public static double computeDistance(int[] houses, int numOfAccessPoints) {
        if (houses.length == 0 || numOfAccessPoints == 0)
            return 0;

        Arrays.sort(houses);

        double PRECISION = 0.01;
        double max_distance = (houses[houses.length - 1] - houses[0]) / 2.0;
        double min_distance = 0;

        while (min_distance < max_distance) {
            if (max_distance - min_distance <= PRECISION)
                break;

            double mid = min_distance + (max_distance - min_distance) / 2;
            if (coverable(houses, numOfAccessPoints, mid)) {
                max_distance = mid;
            } else {
                min_distance = mid;
            }
        }

        return Math.round(max_distance * 10) / 10.0;
    }

    /**
     * Implement your solution here
     */
    public static boolean coverable(int[] houses, int numOfAccessPoints, double distance) {
        if (houses.length == 0)
            return true;
        if (numOfAccessPoints <= 0 || distance < 0)
            return false;

        Arrays.sort(houses);

        int count = 1;
        int currentPos = houses[0];

        for (int i = 0; i < houses.length; i++) {
            if (currentPos + distance * 2 < houses[i]) {
                currentPos = houses[i];
                count += 1;
            }
        }

        return count <= numOfAccessPoints;
    }
}
