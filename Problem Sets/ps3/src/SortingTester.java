import org.w3c.dom.ls.LSOutput;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class SortingTester {
    private static final Random rnd = new Random();

    private static KeyValuePair[] getRandomArray(int size) {
        KeyValuePair[] a = new KeyValuePair[size];
        for (int i = 0; i < size; i++)
            a[i] = new KeyValuePair(rnd.nextInt(10), i);
        return a;
    }

    private static KeyValuePair[] getRandomSortedArray(int size) {
        KeyValuePair[] a = getRandomArray(size);
        Arrays.sort(a);
        return a;
    }

    private static KeyValuePair[] getRandomReversedArray(int size) {
        KeyValuePair[] a = getRandomArray(size);
        Arrays.sort(a, Collections.reverseOrder());
        return a;
    }

    public static boolean checkSort(ISort sorter, int size) {
        KeyValuePair[] a = getRandomArray(size);
        sorter.sort(a);
        for (int i = 0; i < a.length - 1; i++)
            if (a[i].getKey() > a[i + 1].getKey())
                return false;
        return true;
    }

    public static boolean isStable(ISort sorter, int size) {
        KeyValuePair[] a = getRandomArray(size);
        sorter.sort(a);
        for (int i = 0; i < a.length - 1; i++)
            if (a[i].getKey() == a[i + 1].getKey())
                if (a[i].getValue() > a[i + 1].getValue())
                    return false;

        return true;
    }

    public static void main(String[] args) {
        ISort sorter = new SorterA();

        for (int i = 0; i < 10000; i++)
            if (!isStable(sorter, 1000))
                System.out.println("Not Stable");

//        System.out.println(sorter.sort(getRandomSortedArray(100)));
//        System.out.println(sorter.sort(getRandomReversedArray(100)));
    }
}
