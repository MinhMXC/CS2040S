import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

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

    private static char translateIndex(int index) {
        if (0 <= index && index <= 9)
            return (char) (index + 48);
        else if (10 <= index && index <= 35)
            return (char) (index + 55);
        else
            return (char) (index + 61);
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

    //Should be O(l), l = total length of all string that matches the prefix
    void prefixSearch(String s, ArrayList<String> results, int limit) {
        // Setting up for Recursive Calls
        // limitArray[0] = no. of words found; limitArray[1] == limit
        int[] limitArray = new int[2];
        limitArray[1] = limit;
        ArrayList<StringBuilder> ans = new ArrayList<>();

        prefixSearchRecursive(s, ans, root, limitArray);

        for (StringBuilder sb : ans)
            results.add(sb.reverse().toString());
    }

    // Matching prefix first before finding all strings
    void prefixSearchRecursive(String s, ArrayList<StringBuilder> results, TrieNode node, int[] limitArray) {
        if (node == null)
            return;

        if (s.isEmpty()) {
            findAllStringFromNode(results, node, limitArray);
        } else if (s.charAt(0) == '.') {
            for (int i = 0; i < 62; i++) {
                ArrayList<StringBuilder> ans = new ArrayList<>();
                prefixSearchRecursive(s.substring(1), ans, node.presentChars[i], limitArray);
                for (StringBuilder sb : ans)
                    sb.append(translateIndex(i));
                results.addAll(ans);
            }
        } else {
            prefixSearchRecursive(s.substring(1), results, node.presentChars[translateChar(s.charAt(0))], limitArray);
            for (StringBuilder sb: results)
                sb.append(s.charAt(0));
        }
    }

    // To be used after all letters in the pattern is matched
    private static void findAllStringFromNode(ArrayList<StringBuilder> results, TrieNode node, int[] limitArray) {
        if (limitArray[0] >= limitArray[1])
            return;

        if (node.end) {
            results.add(new StringBuilder());
            limitArray[0]++;
        }

        for (int i = 0; i < 62; i++)
            findAllStringRecursive(translateIndex(i), results, node.presentChars[i], limitArray);
    }

    private static void findAllStringRecursive(char c, ArrayList<StringBuilder> results, TrieNode node, int[] limitArray) {
        if (node == null || limitArray[0] >= limitArray[1])
            return;

        ArrayList<StringBuilder> ans = new ArrayList<>();

        if (node.end)
            limitArray[0]++;

        for (int i = 0; i < 62; i++)
            findAllStringRecursive(translateIndex(i), ans, node.presentChars[i], limitArray);

        for (StringBuilder s : ans)
            s.append(c);

        // Add the current node character first before adding the rest to maintain sorting order
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
        try {
            Scanner s = new Scanner(new File("words.txt"));
            while (s.hasNext())
                t.insert(s.nextLine());
        } catch (FileNotFoundException e) {
            System.out.println("bruh");
        }


        for (String s : t.prefixSearch(".", 50))
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
