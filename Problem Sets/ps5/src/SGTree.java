/**
 * ScapeGoat Tree class
 * <p>
 * This class contains some basic code for implementing a ScapeGoat tree. This version does not include any of the
 * functionality for choosing which node to scapegoat. It includes only code for inserting a node, and the code for
 * rebuilding a subtree.
 */

public class SGTree {

    // Designates which child in a binary tree
    enum Child {LEFT, RIGHT}

    /**
     * TreeNode class.
     * <p>
     * This class holds the data for a node in a binary tree.
     * <p>
     * Note: we have made things public here to facilitate problem set grading/testing. In general, making everything
     * public like this is a bad idea!
     */
    public static class TreeNode {
        int key;
        int weight;
        public TreeNode left = null;
        public TreeNode right = null;

        TreeNode(int k) {
            key = k;
            weight = 1;
        }
    }

    // Root of the binary tree
    public TreeNode root = null;

    /**
     * Counts the number of nodes in the specified subtree.
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
     * Builds an array of nodes in the specified subtree.
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

        enumerateNodesRecursive(node, ans, 0);

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

    /**
     * Builds a tree from the list of nodes Returns the node that is the new root of the subtree
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

        return buildTreeRecursive(nodeList, 0, nodeList.length - 1);
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

    /**
     * Determines if a node is balanced. If the node is balanced, this should return true. Otherwise, it should return
     * false. A node is unbalanced if either of its children has weight greater than 2/3 of its weight.
     *
     * @param node a node to check balance on
     * @return true if the node is balanced, false otherwise
     */
    public boolean checkBalance(TreeNode node) {
        if (node == null)
            return true;

        int weightLeft = node.left == null ? 0 : node.left.weight;
        int weightRight = node.right == null ? 0 : node.right.weight;
        double weight = node.weight * 2 / 3.0;

        return !(weightLeft > weight || weightRight > weight);
    }

    /**
     * Rebuilds the specified subtree of a node.
     *
     * @param node  the part of the subtree to rebuild
     * @param child specifies which child is the root of the subtree to rebuild
     */
    public void rebuild(TreeNode node, Child child) {
        // Error checking: cannot rebuild null tree
        if (node == null) return;
        // First, retrieve a list of all the nodes of the subtree rooted at child
        TreeNode[] nodeList = enumerateNodes(node, child);
        // Then, build a new subtree from that list
        TreeNode newChild = buildTree(nodeList);
        // Fix Weight of new child
        fixWeights(newChild);
        // Finally, replace the specified child with the new subtree
        if (child == Child.LEFT) {
            node.left = newChild;
        } else if (child == Child.RIGHT) {
            node.right = newChild;
        }
    }

    private int fixWeights(TreeNode node) {
        if (node == null)
            return 0;

        node.weight = fixWeights(node.left) + fixWeights(node.right) + 1;
        return node.weight;
    }

    /**
     * Inserts a key into the tree.
     *
     * @param key the key to insert
     */
    public void insert(int key) {
        if (root == null) {
            root = new TreeNode(key);
            return;
        }

        Child unbalancedChild = null;
        TreeNode highestUnbalance = null;
        TreeNode prevprev = null;
        TreeNode prev = null;
        TreeNode node = root;

        while (true) {
            node.weight += 1;

            // Check highest unbalanced
            if (highestUnbalance == null && prev != root && !checkBalance(prev)) {
                highestUnbalance = prevprev;
                if (prevprev.left == prev)
                    unbalancedChild = Child.LEFT;
                else
                    unbalancedChild = Child.RIGHT;
            }

            prevprev = prev;
            prev = node;

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

        rebuild(highestUnbalance, unbalancedChild);
    }

    // Simple main function for debugging purposes
    public static void main(String[] args) {
        SGTree tree = new SGTree();
        for (int i = 0; i < 100; i++) {
            tree.insert(i);
        }
    }
}
