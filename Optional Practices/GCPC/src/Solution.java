import java.util.ArrayList;

class Score implements Comparable<Score> {
    public int id;
    public int a;
    public long b;

    public Score(int i) {
        id = i + 1;
    }

    public void update(long newPenalty) {
        a++;
        b += newPenalty;
    }

    @Override
    public int compareTo(Score other) {
        if (other == this)
            return 0;

        return this.a != other.a
                ? this.a - other.a
                : (int) (other.b - this.b);
    }

    @Override
    public String toString() {
        return String.format("ID: %d, Solved: %d, Penalty: %d", id, a, b);
    }
}


class Node<T extends Comparable<T>> {
    public T val;
    public int weight;
    public int height;
    public Node<T> left;
    public Node<T> right;

    public Node(T val) {
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
class SBBTree<T extends Comparable<T>> {
    public Node<T> root;

    // Insert if not already existed
    public void insert(T val) {
        if (root == null) {
            root = new Node<>(val);
            return;
        }

        Node<T> cur = root;

        ArrayList<Node<T>> visited = new ArrayList<>();

        // Traversing and inserting
        while (true) {
            visited.add(cur);
            if (cur.val.compareTo(val) == 0) {
                // If already existed return
                return;
            } else if (cur.val.compareTo(val) > 0) {
                if (cur.left == null) {
                    cur.left = new Node<>(val);
                    break;
                }
                cur = cur.left;
            } else {
                if (cur.right == null) {
                    cur.right = new Node<>(val);
                    break;
                }
                cur = cur.right;
            }
        }

        for (int i = visited.size() - 1; i >= 0; i--) {
            Node<T> current = visited.get(i);
            current.updateWeight();
            current.updateHeight();
        }

        for (int i = 0; i < visited.size(); i++) {
            Node<T> current = visited.get(i);
            if (!isBalanced(current)) {
                if (i == 0) {
                    root = rebalance(root);
                } else {
                    Node<T> prev = visited.get(i - 1);
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
    public void delete(T val) {
        if (root == null)
            return;

        Node<T> cur = root;
        ArrayList<Node<T>> visited = new ArrayList<>();

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
            Node<T> og = cur;

            cur = cur.right;
            while (cur.left != null) {
                visited.add(cur);
                cur = cur.left;
            }

            // To align with previous loop code where the to be deleted element is also added
            visited.add(cur);

            // swap original node to delete and its successor
            T temp = og.val;
            og.val = cur.val;
            cur.val = temp;
        }

        Node<T> found = visited.get(visited.size() - 1);
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

    public boolean search(Node<T> node, T val) {
        if (node == null)
            return false;

        if (node.val.compareTo(val) == 0)
            return true;
        else if (node.val.compareTo(val) > 0)
            return search(node.left, val);
        else
            return search(node.right, val);
    }

    // Quick inorder traversal
    public void enumerate(Node<T> node, ArrayList<T> a) {
        if (node == null)
            return;
        enumerate(node.left, a);
        a.add(node.val);
        enumerate(node.right, a);
    }

    // Routine to go up the tree and fix Height and Weight of tree
    private void fixTreeHeightWeight(ArrayList<Node<T>> visited) {
        while (!visited.isEmpty()) {
            Node<T> pos = visited.get(visited.size() - 1);

            pos.updateWeight();
            pos.updateHeight();

            if (!isBalanced(pos)) {
                if (pos.val.compareTo(root.val) == 0) {
                    root = rebalance(root);
                    visited.remove(visited.size() - 1);
                    return;
                }

                Node<T> prev = visited.get(visited.size() - 2);
                if (prev.left == pos)
                    prev.left = rebalance(pos);
                else
                    prev.right = rebalance(pos);
            }
            visited.remove(visited.size() - 1);
        }
    }

    private Node<T> rebalance(Node<T> node) {
        if (Node.getHeight(node.left) > Node.getHeight(node.right)) {
            Node<T> left = node.left;
            if (Node.getHeight(left.right) > Node.getHeight(left.left))
                node.left = rotateLeft(left);
            return rotateRight(node);
        } else {
            Node<T> right = node.right;
            if (Node.getHeight(right.left) > Node.getHeight(right.right))
                node.right = rotateRight(right);
            return rotateLeft(node);
        }
    }

    private boolean isBalanced(Node<T> node) {
        if (node == null)
            return true;

        int leftHeight = node.left == null ? 0 : node.left.height;
        int rightHeight = node.right == null ? 0 : node.right.height;
        return Math.abs(leftHeight - rightHeight) <= 1;
    }

    // Return new root
    private Node<T> rotateLeft(Node<T> node) {
        Node<T> right = node.right;
        node.right = right.left;
        right.left = node;

        // Order of operation is important here
        node.updateWeight();
        right.updateWeight();

        node.updateHeight();
        right.updateHeight();

        return right;
    }

    private Node<T> rotateRight(Node<T> node) {
        Node<T> left = node.left;
        node.left = left.right;
        left.right = node;

        // Order of operation is important here
        node.updateWeight();
        left.updateWeight();

        node.updateHeight();
        left.updateHeight();

        return left;
    }

    public boolean checkWeight(Node<T> node) {
        if (node.left == null && node.right == null)
            return node.weight == 1;
        else if (node.left == null)
            return checkWeight(node.right) && node.weight == node.right.weight + 1;
        else if (node.right == null)
            return checkWeight(node.left) && node.weight == node.left.weight + 1;
        else
            return checkWeight(node.left) && checkWeight(node.right) && node.weight == node.left.weight + node.right.weight + 1;
    }

    public boolean checkHeight(Node<T> node) {
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
    private Score[] scores;
    private SBBTree<Integer> tree;

    public Solution(int numTeams) {
        scores = new Score[numTeams];
        tree = new SBBTree<>();

        for (int i = 0; i < numTeams; i++)
            scores[i] = new Score(i);
    }

    public int update(int team, long newPenalty) {
        if (team == 1) {
            scores[team - 1].update(newPenalty);

            ArrayList<Integer> a = new ArrayList<>();
            tree.enumerate(tree.root, a);
            for (Integer i : a)
                if (scores[i].compareTo(scores[0]) <= 0)
                    tree.delete(i);
        } else {
            tree.delete(team - 1);

            scores[team - 1].update(newPenalty);

            if (scores[team - 1].compareTo(scores[0]) > 0)
                tree.insert(team - 1);
        }

        return Node.getWeight(tree.root) + 1;
    }

    public static void main(String[] args) {
//        Random r = new Random();
//        Solution s1 = new Solution(10);
//        for (int i = 0; i < 10; i++) {
//            int team = r.nextInt(10) + 1;
//            int penalty = r.nextInt(10);
//            System.out.printf("%d %d%n", team, penalty);
//            s1.update(team, penalty);
//        }
//        Solution s = new Solution(4);
//        System.out.println(s.update(2, 7));
//        System.out.println(s.update(3, 10));
//        System.out.println(s.update(1, 9));
//        System.out.println(s.update(1, 3));

        int SIZE = 1000000;
        long startA = System.nanoTime();

        Solution s = new Solution(SIZE);
        for (int i = 1; i < SIZE; i++)
            s.update(i, 10);
        s.update(1, 5);
//        s.update(2, 10);
//        s.update(3, 9);
//        s.update(4, 9);

        long endA = System.nanoTime();

        IdealSolution is = new IdealSolution(SIZE);
        for (int i = 1; i < SIZE; i++)
            is.update(i, 10);
        is.update(1, 5);

        long endB = System.nanoTime();

        System.out.printf("Solution: %d; IdealSolution: %d\n", (endA - startA) / 100000, (endB - endA) / 100000);

//        SBBTree<Integer> t = new SBBTree();
//        for (int i = 0; i < 100; i++) {
//            t.insert(i);
//        }
//
//        for (int i = 0; i < 99; i++) {
//            System.out.println(t.checkHeight(t.root));
//            System.out.println(t.checkWeight(t.root));
//            t.delete(i);
//        }
    }
}

