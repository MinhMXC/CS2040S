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
        TreeNode[] nodes = new TreeNode[countNodes(node, child)];

        // Make node refer to the correct subnode to enumerate
        node = child == Child.LEFT ? node.left : node.right;

        if (node == null)
            return new TreeNode[]{};

        TreeNode[] left = enumerateNodes(node, Child.LEFT);
        TreeNode[] right = enumerateNodes(node, Child.RIGHT);

        for (int i = 0; i < left.length; i++)
            nodes[i] = left[i];

        nodes[left.length] = node;

        for (int i = 0; i < right.length; i++)
            nodes[left.length + 1 + i] = right[i];

        return nodes;
    }

    /**
     * Builds a tree from the list of nodes
     * Returns the node that is the new root of the subtree
     *
     * @param nodeList ordered array of nodes
     * @return the new root node
     */
    public TreeNode buildTree(TreeNode[] nodeList) {
        if (nodeList.length == 0)
            return null;

        TreeNode mid = nodeList[nodeList.length / 2];
        mid.left = buildTree(Arrays.copyOfRange(nodeList, 0, nodeList.length / 2));
        mid.right = buildTree(Arrays.copyOfRange(nodeList,nodeList.length / 2 + 1, nodeList.length));
        return mid;
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
//        tree.rebuild(tree.root, Child.RIGHT);
//        TreeNode bruh = tree.buildTree(tree.enumerateNodes(tree.root, Child.RIGHT));
//        System.out.println(Arrays.toString(tree.enumerateNodes(bruh, Child.LEFT)));
//        System.out.println(Arrays.toString(tree.enumerateNodes(tree.root, Child.RIGHT)));
    }
}
