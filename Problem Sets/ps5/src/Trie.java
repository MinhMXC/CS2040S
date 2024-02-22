import java.util.ArrayList;
import java.util.Arrays;

public class Trie {

    // Wildcards
    final char WILDCARD = '.';

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

    private static int translateChar(char c) {
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
            int val = translateChar(s.charAt(i));

            if (cur.presentChars[val] == null)
                return false;

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
        TrieNode cur = this.root;
        for (int i = 0; i < s.length(); i++) {
            int val = translateChar(s.charAt(i));

            if (cur.presentChars[val] == null)
                return;

            cur = cur.presentChars[val];
        }

        ArrayList<StringBuilder> ans = new ArrayList<>();
        int[] limitA = new int[2];
        limitA[1] = limit;

        findAllStringFromNode(s.charAt(s.length() - 1), ans, cur, limitA);

        for (StringBuilder sb : ans) {
            sb.append(s);
            sb.reverse();
            results.add(sb.toString());
        }
    }

    private static char translateIndex(int index) {
        if (0 <= index && index <= 9)
            return (char) (index + 48);
        else if (10 <= index && index <= 35)
            return (char) (index + 55);
        else
            return (char) (index + 61);
    }

    private static void findAllStringFromNode(char c, ArrayList<StringBuilder> results, TrieNode node, int[] limit) {
        if (node == null)
            return;
        if (limit[0] >= limit[1])
            return;

        ArrayList<StringBuilder> ans = new ArrayList<>();

        if (node.end)
            limit[0]++;

        for (int i = 0; i < 62; i++) {
            findAllStringFromNode(translateIndex(i), ans, node.presentChars[i], limit);
        }

        for (StringBuilder s : ans)
            s.append(c);

        if (node.end)
            results.add(new StringBuilder().append(c));

        results.addAll(ans);
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
        t.insert("a");
        t.insert("aa");
        t.insert("aaa");
        t.insert("aaaa");
        t.insert("minh");

        for (String s : t.prefixSearch("mi", 10))
            System.out.println(s);
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
//        String[] result1 = t.prefixSearch("pe", 10);
//        String[] result2 = t.prefixSearch("pe.", 10);
        // result1 should be:
        // ["peck", "pepi", "peppers", "pepppito", "peter"]
        // result2 should contain the same elements with result1 but may be ordered arbitrarily
    }
}
