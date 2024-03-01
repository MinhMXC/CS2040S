import java.util.ArrayList;
import java.util.Scanner;

class Quest implements Comparable<Quest> {
    public long energy;
    public long gold;

    public Quest(long energy, long gold) {
        this.energy = energy;
        this.gold = gold;
    }

    @Override
    public int compareTo(Quest other) {
        if (this.energy != other.energy)
            return (int) (this.energy - other.energy);
        return (int) (this.gold - other.gold);
    }
}

class Node {
    public Quest val;
    public int weight;
    public int height;
    public Node left;
    public Node right;

    public Node(Quest val) {
        this.val = val;
        this.weight = 1;
    }

    public void updateWeight() {
        weight = getWeight(left) + getWeight(right) + 1;
    }

    public void updateHeight() {
        if (left == null && right == null)
            height = 0;
        else
            height = Math.max(getHeight(left), getHeight(right)) + 1;
    }

    @SuppressWarnings("rawtypes")
    static int getWeight(Node node) {
        return node == null ? 0 : node.weight;
    }

    @SuppressWarnings("rawtypes")
    static int getHeight(Node node) {
        return node == null ? 0 : node.height;
    }
}

// Self-balancing binary tree implementation
// This is apparently too slow
// Tested runtime in SolutionTest.java and average runtime was only 2 times more than the average runtime of IdealSolution
// 3.5-4 times more slow for smaller number of teams
class SBBTree {
    public Node root;

    // Insert if not already existed
    public void insert(Quest val) {
        if (root == null) {
            root = new Node(val);
            return;
        }

        Node cur = root;

        ArrayList<Node> visited = new ArrayList<>();

        // Traversing and inserting
        while (true) {
            visited.add(cur);
            if (cur.val.compareTo(val) == 0) {
                // If already existed return
                return;
            } else if (cur.val.compareTo(val) > 0) {
                if (cur.left == null) {
                    cur.left = new Node(val);
                    break;
                }
                cur = cur.left;
            } else {
                if (cur.right == null) {
                    cur.right = new Node(val);
                    break;
                }
                cur = cur.right;
            }
        }

        for (int i = visited.size() - 1; i >= 0; i--) {
            Node current = visited.get(i);
            current.updateWeight();
            current.updateHeight();
        }

        for (int i = 0; i < visited.size(); i++) {
            Node current = visited.get(i);
            if (!isBalanced(current)) {
                if (i == 0) {
                    root = rebalance(root);
                } else {
                    Node prev = visited.get(i - 1);
                    if (prev.left == current)
                        prev.left = rebalance(current);
                    else
                        prev.right = rebalance(current);
                }

                break;
            }
        }
//        fixTreeHeightWeight(visited);
    }

    // If found, delete
    public void delete(Quest val) {
        if (root == null)
            return;

        Node cur = root;
        ArrayList<Node> visited = new ArrayList<>();

        // Finding node to be deleted
        while (true) {
            // Return if val cannot be found
            if (cur == null)
                return;
            visited.add(cur);
            if (cur.val.compareTo(val) == 0)
                break;
            else if (cur.val.compareTo(val) > 0)
                cur = cur.left;
            else
                cur = cur.right;
        }

        // Find successor if both left and right are present
        if (cur.left != null && cur.right != null) {
            Node og = cur;

            cur = cur.right;
            while (cur.left != null) {
                visited.add(cur);
                cur = cur.left;
            }

            // To align with previous loop code where the to be deleted element is also added
            visited.add(cur);

            // swap original node to delete and its successor
            Quest temp = og.val;
            og.val = cur.val;
            cur.val = temp;
        }

        Node found = visited.get(visited.size() - 1);
        visited.remove(visited.size() - 1);

        // Only happen when the root to be deleted is the root node
        // and contains only 1 child
        if (found.val.compareTo(root.val) == 0) {
            if (found.left == null)
                root = root.right;
            else
                root = root.left;
            return;
        }

        cur = visited.get(visited.size() - 1);
        cur.weight--;
        visited.remove(visited.size() - 1);

        // Deleting the node
        if (found.left == null && found.right == null) {
            if (cur.left == found)
                cur.left = null;
            else
                cur.right = null;
        } else if (found.left == null) {
            if (cur.left == found)
                cur.left = found.right;
            else
                cur.right = found.right;
        } else if (found.right == null) {
            if (cur.left == found)
                cur.left = found.left;
            else
                cur.right = found.left;
        }

        cur.updateHeight();

        fixTreeHeightWeight(visited);
    }

    public boolean search(Node node, Quest val) {
        if (node == null)
            return false;

        if (node.val.compareTo(val) == 0)
            return true;
        else if (node.val.compareTo(val) > 0)
            return search(node.left, val);
        else
            return search(node.right, val);
    }

    public int query(long energy) {
        int maxGold = 0;

        while (true) {
            Quest res = searchBestQuest(root, energy);
            if (res == null)
                break;
            delete(res);
            energy -= res.energy;
            maxGold += (int) res.gold;
        }

        return maxGold;
    }

    public Quest searchBestQuest(Node node, long energy) {
        if (node == null)
            return null;

        if (node.val.energy < energy) {
            Quest res = searchBestQuest(node.right, energy);
            if (res == null)
                return node.val;
            return res;
        } else if (node.val.energy > energy) {
            return searchBestQuest(node.left, energy);
        } else {
            while (node.right != null && node.right.val.energy == energy) {
                node = node.right;
            }
            return node.val;
        }
    }

    // Quick inorder traversal
    public void enumerate(Node node, ArrayList<Quest> a) {
        if (node == null)
            return;
        enumerate(node.left, a);
        a.add(node.val);
        enumerate(node.right, a);
    }

    // Routine to go up the tree and fix Height and Weight of tree
    private void fixTreeHeightWeight(ArrayList<Node> visited) {
        while (!visited.isEmpty()) {
            Node pos = visited.get(visited.size() - 1);

            pos.updateWeight();
            pos.updateHeight();

            if (!isBalanced(pos)) {
                if (pos.val.compareTo(root.val) == 0) {
                    root = rebalance(root);
                    visited.remove(visited.size() - 1);
                    return;
                }

                Node prev = visited.get(visited.size() - 2);
                if (prev.left == pos)
                    prev.left = rebalance(pos);
                else
                    prev.right = rebalance(pos);
            }
            visited.remove(visited.size() - 1);
        }
    }

    private Node rebalance(Node node) {
        if (Node.getHeight(node.left) > Node.getHeight(node.right)) {
            Node left = node.left;
            if (Node.getHeight(left.right) > Node.getHeight(left.left))
                node.left = rotateLeft(left);
            return rotateRight(node);
        } else {
            Node right = node.right;
            if (Node.getHeight(right.left) > Node.getHeight(right.right))
                node.right = rotateRight(right);
            return rotateLeft(node);
        }
    }

    private boolean isBalanced(Node node) {
        if (node == null)
            return true;

        int leftHeight = node.left == null ? 0 : node.left.height;
        int rightHeight = node.right == null ? 0 : node.right.height;
        return Math.abs(leftHeight - rightHeight) <= 1;
    }

    // Return new root
    private Node rotateLeft(Node node) {
        Node right = node.right;
        node.right = right.left;
        right.left = node;

        // Order of operation is important here
        node.updateWeight();
        right.updateWeight();

        node.updateHeight();
        right.updateHeight();

        return right;
    }

    private Node rotateRight(Node node) {
        Node left = node.left;
        node.left = left.right;
        left.right = node;

        // Order of operation is important here
        node.updateWeight();
        left.updateWeight();

        node.updateHeight();
        left.updateHeight();

        return left;
    }

    public boolean checkWeight(Node node) {
        if (node.left == null && node.right == null)
            return node.weight == 1;
        else if (node.left == null)
            return checkWeight(node.right) && node.weight == node.right.weight + 1;
        else if (node.right == null)
            return checkWeight(node.left) && node.weight == node.left.weight + 1;
        else
            return checkWeight(node.left) && checkWeight(node.right) && node.weight == node.left.weight + node.right.weight + 1;
    }

    public boolean checkHeight(Node node) {
        if (node.left == null && node.right == null)
            return node.height == 0;
        else if (node.left == null)
            return checkHeight(node.right) && node.height == node.right.height + 1;
        else if (node.right == null)
            return checkHeight(node.left) && node.height == node.left.height + 1;
        else
            return checkHeight(node.left) && checkHeight(node.right) && node.height == Math.max(node.left.height, node.right.height) + 1;
    }
}

public class Solution {
    private SBBTree t;

    public Solution() {
        t = new SBBTree();
    }

    void add(long energy, long value) {
        t.insert(new Quest(energy, value));
    }

    long query(long remainingEnergy) {
        return t.query(remainingEnergy);
    }

    public static void main(String[] args) {
        Solution s = new Solution();
        s.add(5, 10);
        s.add(3, 10);
        s.add(4, 20);
        System.out.println(s.query(4));
//        Scanner scanner = new Scanner(System.in);
//        int testCase = scanner.nextInt();
//        for (int i = 0; i < testCase; i++) {
//            String command = scanner.next();
//            if (command.compareTo("add") == 0) {
//                s.add(scanner.nextInt(), scanner.nextInt());
//            } else {
//                System.out.println(s.query(scanner.nextInt()));
//            }
//        }
    }
}
