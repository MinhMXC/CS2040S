/**
 * interface FindKeys
 *
 * @author benleong
 * Description: Interface for problems
 */
public interface IFindKeys {

    /**
     * Finds the correct keys amongst the all the keys.
     *
     * @param N              Number of total keys, numbered from 0 to N-1
     * @param k              Number of correct keys (ones that open a lock)
     * @param treasureExtractor Interface that tries to unlock the treasure chest given a set of keys
     * @return Key bitmap with all correct keys marked with `1` and all other
     * keys marked with `0`
     */
    int[] findKeys(int N, int k, ITreasureExtractor treasureExtractor);
}
