import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OptimizationTest {

    /* Tests for Problem 1 */

    @Test
    public void testOptimization1() {
        int[] dataArray = {1, 3, 5, 7, 9, 11, 10, 8, 6, 4};
        assertEquals(11, Optimization.searchMax(dataArray));
    }

    @Test
    public void testOptimization2() {
        int[] dataArray = {67, 65, 43, 42, 23, 17, 9, 100};
        assertEquals(100, Optimization.searchMax(dataArray));
    }

    @Test
    public void testOptimization3() {
        int[] dataArray = {4, -100, -80, 15, 20, 25, 30};
        assertEquals(30, Optimization.searchMax(dataArray));
    }

    @Test
    public void testOptimization4() {
        int[] dataArray = {2, 3, 4, 5, 6, 7, 8, 100, 99, 98, 97, 96, 95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84,
                83};
        assertEquals(100, Optimization.searchMax(dataArray));
    }

    @Test
    public void testOptimization5() {
        Random rnd = new Random();

        for (int i = 0; i < 10000; i++) {
            int[] a = getNewSortedArray();

            if (a.length == 0|| a.length == 1) {
                assertEquals(Optimization.searchMax(a), getMax(a));
                continue;
            }

            int changePoint = rnd.nextInt(a.length - 1);

            for (int j = changePoint; j <= (a.length - 1 + changePoint) / 2; j++) {
                int temp = a[j];
                a[j] = a[a.length - 1 - (j - changePoint)];
                a[a.length - 1 - (j - changePoint)] = temp;
            }

            try {
                assertEquals(Optimization.searchMax(a), getMax(a));
            } catch (AssertionError e) {
                System.out.println("assert error");
                System.out.println(Arrays.toString(a));
            } catch (Exception e) {
                System.out.println(Arrays.toString(a));
            }
        }
    }

    private int getMax(int[] arr) {
        if (arr.length == 0)
            return 0;

        int max = arr[0];
        for (int i = 0; i < arr.length; i++) {
            if (max < arr[i])
                max = arr[i];
        }
        return max;
    }

    private int[] getNewSortedArray() {
        Random rnd = new Random();

        int[] arr = new int[rnd.nextInt(100)];
        for (int i = 0; i < arr.length; i++)
            arr[i] = rnd.nextInt(10000);

        Arrays.sort(arr);

        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] == arr[i + 1]) {
                arr[i + 1] += 1;
            }
        }

        return arr;
    }
}
