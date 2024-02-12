public interface ITreasureExtractor {
    /**
     * Tries to unlock the chest with a group of keys and returns true if the treasure
     * chest was successfully unlocked (i.e. all correct keys were present), false otherwise.
     *
     * @param keys    Key bitmap of size N, where N is the number
     *                of keys. A '1' at index i indicates that key i is to be
     *                used in this attempt at opening the chest.
     * @return true if there's the treasure chest was unlocked in this attempt
     */
    boolean tryUnlockChest(int[] keys);

}
