import java.util.Random;

/**
 * class NaiveKeysLowestCost
 *
 * @author benleong
 * Description: Naive algorithm that randomly picks k keys in an attempt to open the treasure chest
 */
public class NaiveKeysLowestCost implements IFindKeys {

    Random random = new Random();

    @Override
    public int[] findKeys(int N, int k, ITreasureExtractor treasureExtractor) {
        while (true) {
            int[] bitmap = new int[N];
            for (int i = 0; i < k; i++) {
                boolean found = false;
                while (!found) {
                    int rnd = random.nextInt(bitmap.length);
                    if (bitmap[rnd] == 0) {
                        bitmap[rnd] = 1;
                        found = true;
                    }
                }
            }
            if (treasureExtractor.tryUnlockChest(bitmap)) {
                return bitmap;
            }
        }
    }
}
