import java.util.ArrayList;

public class Trie {

    // Wildcards
    final char WILDCARD = '.';

    // I'll be ignoring all the OOP good practices for simplicity’s sake
    private class TrieNode {
        public boolean end;
        public TrieNode[] presentChars;

        public TrieNode() {
            end = false;
            presentChars = new TrieNode[62];
        }
    }

    TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    int translateChar(char c) {
        if (48 <= c && c <= 57)
            return c - 48;
        else if (65 <= c && c <= 90)
            return c - 55;
        else
            return c - 61;
    }

    /**
     * Inserts string s into the Trie.
     *
     * @param s string to insert into the Trie
     */
    void insert(String s) {
        TrieNode cur = this.root;
        for (int i = 0; i < s.length(); i++) {
            int val = translateChar(s.charAt(i));

            if (cur.presentChars[val] == null)
                cur.presentChars[val] = new TrieNode();

            cur = cur.presentChars[val];
        }
        cur.end = true;
    }

    /**
     * Checks whether string s exists inside the Trie or not.
     *
     * @param s string to check for
     * @return whether string s is inside the Trie
     */
    boolean contains(String s) {
        TrieNode cur = this.root;
        for (int i = 0; i < s.length(); i++) {
            if (cur == null)
                return false;

            int val = translateChar(s.charAt(i));
            cur = cur.presentChars[val];
        }
        return cur.end;
    }

    /**
     * Searches for strings with prefix matching the specified pattern sorted by lexicographical order. This inserts the
     * results into the specified ArrayList. Only returns at most the first limit results.
     *
     * @param s       pattern to match prefixes with
     * @param results array to add the results into
     * @param limit   max number of strings to add into results
     */
    void prefixSearch(String s, ArrayList<String> results, int limit) {
        // TODO
    }


    // Simplifies function call by initializing an empty array to store the results.
    // PLEASE DO NOT CHANGE the implementation for this function as it will be used
    // to run the test cases.
    String[] prefixSearch(String s, int limit) {
        ArrayList<String> results = new ArrayList<String>();
        prefixSearch(s, results, limit);
        return results.toArray(new String[0]);
    }


    public static void main(String[] args) {
        Trie t = new Trie();
//        t.insert("a");
//        t.insert("ab");
//        t.insert("abc");
//        System.out.println(t.contains(""));
//        t.insert("peter");
//        t.insert("piper");
//        t.insert("picked");
//        t.insert("a");
//        t.insert("peck");
//        t.insert("of");
//        t.insert("pickled");
//        t.insert("peppers");
//        t.insert("pepppito");
//        t.insert("pepi");
//        t.insert("pik");
//
//        t.insert("");
//        System.out.println(t.contains(""));
//
//        String[] result1 = t.prefixSearch("pe", 10);
//        String[] result2 = t.prefixSearch("pe.", 10);
        // result1 should be:
        // ["peck", "pepi", "peppers", "pepppito", "peter"]
        // result2 should contain the same elements with result1 but may be ordered arbitrarily
    }
}
