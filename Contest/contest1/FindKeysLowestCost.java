import java.util.Arrays;

public class FindKeysLowestCost implements IFindKeys {
    @Override
    public int[] findKeys(int N, int k, ITreasureExtractor treasureExtractor) {
        int[] keys = new int[N];
        Arrays.fill(keys, 1);

        int prevKeyPos = -1;
        for (int i = 0; i < k; i++) {
            int low = prevKeyPos + 1;
            int high = N - 1;

            while (low != high) {
                int mid = low + (high - low) / 2;
                fillArray(keys, low, mid, 0);
                fillArray(keys, mid + 1, high, 1);

                if (i + (N - 1 - mid) < k) {
                    high = mid;
                    continue;
                }

                if (treasureExtractor.tryUnlockChest(keys))
                    low = mid + 1;
                else
                    high = mid;
            }
            prevKeyPos = low;
            keys[prevKeyPos] = 1;
        }

        fillArray(keys, prevKeyPos + 1, N - 1, 0);
        return keys;
    }

    private int[] fillArray(int[] a, int low, int high, int val) {
        for (int i = low; i <= high; i++)
            a[i] = val;
        return a;
    }
}
