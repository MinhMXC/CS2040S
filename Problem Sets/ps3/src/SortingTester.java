import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class SortingTester {
    private static final Random rnd = new Random();

    private static KeyValuePair[] getRandomArray(int size, int upperBound) {
        if (upperBound < 1)
            upperBound = 1;
        KeyValuePair[] a = new KeyValuePair[size];
        for (int i = 0; i < size; i++)
            a[i] = new KeyValuePair(rnd.nextInt(upperBound), i);
        return a;
    }

    private static KeyValuePair[] getRandomSortedArray(int size) {
        KeyValuePair[] a = getRandomArray(size, 10);
        Arrays.sort(a);
        return a;
    }

    private static KeyValuePair[] getRandomReversedArray(int size) {
        KeyValuePair[] a = getRandomArray(size, 10);
        Arrays.sort(a, Collections.reverseOrder());
        return a;
    }

    private static boolean DrEvilTest() {
        ISort sorter = new SorterB();
        for (int i = 0; i < 100000; i++)
            if (!checkSort(sorter,rnd.nextInt(100) + 1))
                return true;
        return false;
    }

    private static void examineSorter(ISort sorter, int size) {
        long costSorted = 0;
        long costReversed = 0;
        long costAverage = 0;
        int ITERATION = 10000;

        for (int i = 0; i < ITERATION; i++) {
            costSorted += sorter.sort(getRandomSortedArray(size));
            costReversed += sorter.sort(getRandomReversedArray(size));
            costAverage += sorter.sort(getRandomArray(size, 10));
        }

        System.out.printf("Cost Sorted: %d%n", costSorted / ITERATION);
        System.out.printf("Cost Reversed: %d%n", costReversed / ITERATION);
        System.out.printf("Cost Average: %d%n", costAverage / ITERATION);
        System.out.printf("Stable: %b\n", isStable(sorter, 100));
    }

    private static void estimateONotation(ISort sorter) {
        long costAverage100 = 0;
        long costAverage10000 = 0;
        int ITERATION = 5;

        for (int i = 0; i < ITERATION; i++) {
            costAverage100 += sorter.sort(getRandomArray(100, 100));
            costAverage10000 += sorter.sort(getRandomArray(10000, 10000));
        }
        costAverage100 /= ITERATION;
        costAverage10000 /= ITERATION;

        // I know this kinda doesn't account for other runtime but
        // the sorters we learn is either O(nlogn) or O(n^2) so this will suffice
        // 9000 to make to account for variations. Theoretically would be 10000.
        if (costAverage10000 / costAverage100 > 9000)
            System.out.println("Run Time: O(n^2)");
        else
            System.out.println("Run Time: O(nlogn)");
    }

    public static boolean checkSort(ISort sorter, int size) {
        KeyValuePair[] a = getRandomArray(size, 10);
        sorter.sort(a);
        for (int i = 0; i < a.length - 1; i++)
            if (a[i].getKey() > a[i + 1].getKey())
                return false;
        return true;
    }

    public static boolean isStable(ISort sorter, int size) {
        KeyValuePair[] a = getRandomArray(size, size / 2);
        sorter.sort(a);
        for (int i = 0; i < a.length - 1; i++)
            if (a[i].getKey() == a[i + 1].getKey())
                if (a[i].getValue() > a[i + 1].getValue())
                    return false;

        return true;
    }

    public static void main(String[] args) {

        ISort sorter = new SorterC();
        examineSorter(sorter, 100);
        estimateONotation(sorter);

        long cost = 0;

        for (int i = 0; i < 10000; i++) {
            KeyValuePair[] a = getRandomSortedArray(100);
            a[a.length - 1] = new KeyValuePair(-1, -1);
            cost += sorter.sort(a);
        }

        System.out.printf("\nCost Almost Sorted (last element is the smallest): %d", cost / 10000);
    }
}
