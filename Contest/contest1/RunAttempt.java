import java.util.Random;

/**
 * class RunAttempt
 *
 * @author benleong
 * Description: Code that runs the 2 contests
 */
public class RunAttempt implements ITreasureExtractor {

    public static final int MAX_COUNT = 9999;

    int[] bitmap; // Bitmap used to keep track of the keys. 0 means a wrong key, 1 means a correct key
    int k = 0; // Number of correct keys

    int totalAttempts = 0;
    int totalCost = 0;

    /**
     * Attempts to unlock the chest with a given set of keys and returns true if the chest
     * was unlocked, false otherwise.
     *
     * @param keys    Key bitmap of size N, where N is the number
     *                of keys. A '1' at index i indicates that key i is to be
     *                used to unlock the chest.
     * @return true if the treasure chest was unlocked
     */
    @Override
    public boolean tryUnlockChest(int[] keys) {
        if (keys.length != bitmap.length) {
            throw new RuntimeException("Invalid key bitmap.");
        }
        if (totalAttempts > MAX_COUNT) {
            throw new RuntimeException("Count exceeded!");
        }

        int correctKeys = 0;
        int totalKeys = 0;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] < 0 || keys[i] > 1) {
                throw new RuntimeException("Invalid key bitmap.");
            }
            if (keys[i] == 1) {
                totalKeys += 1;
            }
            correctKeys += keys[i] * bitmap[i];
        }
        if (totalKeys < k) {
            throw new RuntimeException("Invalid attempt - not enough keys!");
        }

        totalAttempts++;
        totalCost += totalKeys;
        return correctKeys == k;
    }

    /**
     * Tests if the given bitmap is the correct configuration (i.e. identifies
     * all the correct keys.
     */
    private boolean testCorrect(int[] testBitmap) {
        if (testBitmap.length != bitmap.length) {
            throw new RuntimeException("Invalid test bitmap.");
        }
        for (int i = 0; i < bitmap.length; i++) {
            if (bitmap[i] != testBitmap[i]) {
                return false;
            }
        }
        return true;
    }


    /**
     * Runs the test and return the number of attempts taken to find all the correct keys.
     *
     * @param f Algorithm to be tested
     * @return Number of attempts required to find the correct keys
     */
    private int findAttempts(IFindKeys f) {
        resetStatistics();
        int[] ans = f.findKeys(bitmap.length, k, this);
        if (!testCorrect(ans)) {
            throw new RuntimeException("Wrong answer!");
        }
        return totalAttempts;
    }

    /**
     * Runs the test and return the cost to find all the correct keys.
     *
     * @param f Algorithm to be tested
     * @return Cost incurred to find the correct keys
     */
    private int findCost(IFindKeys f) {
        resetStatistics();
        int[] ans = f.findKeys(bitmap.length, k, this);
        if (!testCorrect(ans)) {
            throw new RuntimeException("Wrong answer!");
        }
        return totalCost;
    }

    private void resetStatistics() {
        totalAttempts = 0;
        totalCost = 0;
    }

    private void setTestCase(int[] bitmap) {
        int count = 0;
        for (int i = 0; i < bitmap.length; i++) {
            if (bitmap[i] < 0 || bitmap[i] > 1) {
                throw new RuntimeException("Invalid key bitmap.");
            }
            count += bitmap[i];
        }
        this.bitmap = bitmap;
        this.k = count;
    }

    /**
     * main procedure - for testing
     *
     * @param args
     */
    public static void main(String[] args) {

        // TODO: Feel free to change the test case here.
        //  bitmap is an array of n keys where 1 means that the key is correct (corresponds to one of the locks)
        Random rnd = new Random();
        int length = 1024;
        int[] bitmap = new int [1024];
        for (int i = 0; i < length; i++)
            if (rnd.nextInt(1024) < 17)
                bitmap[i] = 1;

        RunAttempt attemptRunner = new RunAttempt();
        attemptRunner.setTestCase(bitmap);
        System.out.printf("Testing on %d total keys with %d keys correct.\n", attemptRunner.bitmap.length, attemptRunner.k);
        IFindKeys minimum = new FindKeysMinimumAttempts();
        System.out.println("Total attempts tried: " + attemptRunner.findAttempts(minimum));

        IFindKeys lowest = new FindKeysLowestCost();
        System.out.println("Total cost: $" + attemptRunner.findCost(lowest));

    }

}
