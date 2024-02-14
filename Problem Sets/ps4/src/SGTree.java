import com.sun.source.tree.Tree;

import java.util.Arrays;

/**
 * ScapeGoat Tree class
 *
 * This class contains some of the basic code for implementing a ScapeGoat tree.
 * This version does not include any of the functionality for choosing which node
 * to scapegoat.  It includes only code for inserting a node, and the code for rebuilding
 * a subtree.
 */

// Simple array-based stack for iterative implementations
class Stack<T> {
    private T[] m_a;
    //Pos to insert into next
    private int m_pos;

    @SuppressWarnings("unchecked")
    public Stack(int length) {
        m_a = (T[]) new Object[length];
    }

    public T pop() {
        m_pos--;
        T temp = m_a[m_pos];
        m_a[m_pos] = null;
        return temp;
    }

    public void push(T e) {
        m_a[m_pos] = e;
        m_pos++;
    }

    public boolean isEmpty() {
        return m_a[0] == null;
    }
}

public class SGTree {

    // Designates which child in a binary tree
    enum Child {LEFT, RIGHT}

    /**
     * TreeNode class.
     *
     * This class holds the data for a node in a binary tree.
     *
     * Note: we have made things public here to facilitate problem set grading/testing.
     * In general, making everything public like this is a bad idea!
     *
     */
    public static class TreeNode {
        int key;
        public TreeNode left = null;
        public TreeNode right = null;

        TreeNode(int k) {
            key = k;
        }

        @Override
        public String toString() {
            return Integer.toString(key);
        }
    }

    // Root of the binary tree
    public TreeNode root = null;

    /**
     * Counts the number of nodes in the specified subtree
     *
     * @param node  the parent node, not to be counted
     * @param child the specified subtree
     * @return number of nodes
     */
    public int countNodes(TreeNode node, Child child) {
        return node == null
                ? 0
                : child == Child.LEFT
                ? countNodesFromNode(node.left)
                : countNodesFromNode(node.right);
    }

    // Count nodes on both left and right subtree, including the parent node
    private int countNodesFromNode(TreeNode node) {
        return node == null
                ? 0
                : countNodesFromNode(node.left) + countNodesFromNode(node.right) + 1;
    }

    /**
     * Builds an array of nodes in the specified subtree
     *
     * @param node  the parent node, not to be included in returned array
     * @param child the specified subtree
     * @return array of nodes
     */
    public TreeNode[] enumerateNodes(TreeNode node, Child child) {
        if (node == null)
            return new TreeNode[]{};

        int nodesCount = countNodes(node, child);

        // Make node refer to the correct subnode to enumerate
        node = child == Child.LEFT ? node.left : node.right;

        //Guarantee that the side looking at is not null
        if (node == null)
            return new TreeNode[]{};

        TreeNode[] ans = new TreeNode[nodesCount];

        // Stack for iterative implementation
        // Guaranteed to not overflow because max height of tree == nodeCount
        // Store every node, which right side has not been looked into
        Stack<TreeNode> stack = new Stack<>(nodesCount);

        enumerateNodesIterative(node, ans, stack);

        // Recursive call
        // enumerateNodesRecursive(node, ans, 0);

        return ans;
    }

    public int enumerateNodesRecursive(TreeNode node, TreeNode[] ans, int ansPos) {
        if (node == null)
            return ansPos;

        ansPos = enumerateNodesRecursive(node.left, ans, ansPos);

        ans[ansPos] = node;
        ansPos++;

        ansPos = enumerateNodesRecursive(node.right, ans, ansPos);
        return ansPos;
    }

    // Told you I'd find an iterative solution, albeit at 2am and now im dead
    public void enumerateNodesIterative(TreeNode node, TreeNode[] ans, Stack<TreeNode> stack) {
        int ansPos = 0;

        stack.push(node);
        while (node.left != null) {
            stack.push(node.left);
            node = node.left;
        }

        while (!stack.isEmpty()) {
            node = stack.pop();
            ans[ansPos] = node;
            ansPos++;

            // Look into right side
            node = node.right;

            if (node == null) {
                if (stack.isEmpty())
                    break;
                continue;
            }

            stack.push(node);

            while (node.left != null) {
                stack.push(node.left);
                node = node.left;
            }
        }
    }

    /**
     * Builds a tree from the list of nodes
     * Returns the node that is the new root of the subtree
     *
     * @param nodeList ordered array of nodes
     * @return the new root node
     */
    public TreeNode buildTree(TreeNode[] nodeList) {
        //Delete the previous tree structure
        for (TreeNode node : nodeList) {
            node.right = null;
            node.left = null;
        }

        return buildTreeIterative(nodeList);
        // Recursive
        // return buildTreeRecursive(nodeList, 0, nodeList.length - 1);
    }

    public TreeNode buildTreeRecursive(TreeNode[] nodeList, int low, int high) {
        if (high - low < 0)
            return null;

        int midIndex = low + (high - low) / 2;
        TreeNode mid = nodeList[midIndex];
        mid.left = buildTreeRecursive(nodeList, low, midIndex - 1);
        mid.right = buildTreeRecursive(nodeList, midIndex + 1, high);
        return mid;
    }

    // Realised that I'm just mimicking the stack frame in CPU
    public TreeNode buildTreeIterative(TreeNode[] nodeList) {
        if (nodeList.length == 1)
            return nodeList[0];
        if (nodeList.length == 0)
            return null;

        //Guaranteed to not overflow because max height = log(nodeList.length)
        Stack<Integer> lows = new Stack<>(nodeList.length);
        Stack<Integer> highs = new Stack<>(nodeList.length);
        Stack<TreeNode> nodes = new Stack<>(nodeList.length);

        int low = 0;
        int high = nodeList.length - 1;
        int i_mid = low + (high - low) / 2;

        TreeNode root = nodeList[i_mid];
        TreeNode cur = root;

        lows.push(i_mid + 1);
        highs.push(high);
        nodes.push(cur);

        high = i_mid - 1;

        // Iterate LEFT side
        while (low <= high) {
            int mid = low + (high - low) / 2;

            cur.left = nodeList[mid];
            cur = cur.left;

            lows.push(mid + 1);
            highs.push(high);
            nodes.push(cur);

            high = mid - 1;
        }

        while(!nodes.isEmpty()) {
            cur = nodes.pop();
            low = lows.pop();
            high = highs.pop();

            // Iterate RIGHT side
            if (low <= high) {
                int l_mid = low + (high - low) / 2;
                cur.right = nodeList[l_mid];
                cur = cur.right;

                lows.push(l_mid + 1);
                highs.push(high);
                nodes.push(cur);

                high = l_mid - 1;
            }

            // Iterate LEFT side
            while (low <= high) {
                int mid = low + (high - low) / 2;

                cur.left = nodeList[mid];
                cur = cur.left;

                lows.push(mid + 1);
                highs.push(high);
                nodes.push(cur);

                high = mid - 1;
            }
        }

        return root;
    }

    /**
    * Rebuilds the specified subtree of a node
    *
    * @param node the part of the subtree to rebuild
    * @param child specifies which child is the root of the subtree to rebuild
    */
    public void rebuild(TreeNode node, Child child) {
        // Error checking: cannot rebuild null tree
        if (node == null) return;
        // First, retrieve a list of all the nodes of the subtree rooted at child
        TreeNode[] nodeList = enumerateNodes(node, child);
        // Then, build a new subtree from that list
        TreeNode newChild = buildTree(nodeList);
        // Finally, replace the specified child with the new subtree
        if (child == Child.LEFT) {
            node.left = newChild;
        } else if (child == Child.RIGHT) {
            node.right = newChild;
        }
    }

    /**
    * Inserts a key into the tree
    *
    * @param key the key to insert
    */
    public void insert(int key) {
        if (root == null) {
            root = new TreeNode(key);
            return;
        }

        TreeNode node = root;

        while (true) {
            if (key <= node.key) {
                if (node.left == null) break;
                node = node.left;
            } else {
                if (node.right == null) break;
                node = node.right;
            }
        }

        if (key <= node.key) {
            node.left = new TreeNode(key);
        } else {
            node.right = new TreeNode(key);
        }
    }


    // Simple main function for debugging purposes
    public static void main(String[] args) {
        SGTree tree = new SGTree();
        for (int i = 0; i < 100; i++) {
            tree.insert(i);
        }

        TreeNode root = tree.buildTree(tree.enumerateNodes(tree.root, Child.RIGHT));
        System.out.println(Arrays.toString(tree.enumerateNodes(root, Child.RIGHT)));
    }
}