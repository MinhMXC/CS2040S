import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class InversionCounterTest {

    @Test
    public void countSwapsTest1() {
        int[] arr = {3, 1, 2};
        assertEquals(2L, InversionCounter.countSwaps(arr));
    }

    @Test
    public void countSwapsTest2() {
        int[] arr = {2, 3, 4, 1};
        assertEquals(3L, InversionCounter.countSwaps(arr));
    }

    @Test
    public void mergeAndCountTest1() {
        int[] arr = {3, 1, 2};
        assertEquals(2L, InversionCounter.mergeAndCount(arr, 0, 0, 1, 2));
    }

    @Test
    public void mergeAndCountTest2() {
        int[] arr = {2, 3, 4, 1};
        assertEquals(3L, InversionCounter.mergeAndCount(arr, 0, 2, 3, 3));
    }

    @Test
    public void test() {
        Random rnd = new Random();
        int[] a = new int[100];
        for (int i = 0; i < 100000; i++) {
            for (int j = 0; j < a.length; j++)
                a[j] = rnd.nextInt(10000);
            assertEquals(countInversions(a), InversionCounter.countSwaps(a));
        }
    }

    private long countInversions(int[] a) {
        long count = 0;
        for (int i = 0; i < a.length - 1; i++)
            for (int j = i + 1; j < a.length; j++)
                if (a[i] > a[j])
                    count++;

        return count;
    }
}