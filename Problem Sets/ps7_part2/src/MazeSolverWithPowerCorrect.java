import java.util.Arrays;
import java.util.LinkedList;

public class MazeSolverWithPowerCorrect implements IMazeSolverWithPower {
    public class TreeNode{
        int[] coords;
        int superpowers;
        int distance;
        TreeNode next;
        TreeNode childN;
        TreeNode childS;
        TreeNode childW;
        TreeNode childE;
        TreeNode parent;
        public TreeNode(int[] coords, int superpowers, int distance) {
            this.coords = coords;
            this.superpowers = superpowers;
            this.distance = distance;
        }
        public void setChildren(TreeNode[] children) {
            if (children[0] != null) {
                this.childN = children[0];
                children[0].parent = this;
            }
            if (children[1] != null) {
                this.childS = children[1];
                children[1].parent = this;
            }
            if (children[2] != null) {
                this.childW = children[2];
                children[2].parent = this;
            }
            if (children[3] != null) {
                this.childE = children[3];
                children[3].parent = this;
            }
        }

        public void setNext(TreeNode node) {
            this.next = node;
        }

        @Override
        public String toString() {
            if (this.next == null) return String.valueOf(distance);
            return String.valueOf(distance) + "." + this.next.toString();
        }
    }
    private static final int NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3;
    private static int[][] DELTAS = new int[][] {
            { -1, 0 }, // North
            { 1, 0 }, // South
            { 0, 1 }, // East
            { 0, -1 } // West
    };

    private Maze maze;
    private int startRow, startCol = -1;
    private int endRow, endCol = -1;
    private int superpowers = -1;
    TreeNode[][] been;
    private LinkedList<TreeNode> queue = new LinkedList<>();

    public MazeSolverWithPowerCorrect() {}

    @Override
    public void initialize(Maze maze) {
        this.maze = maze;
    }

    @Override
    public Integer pathSearch(int startRow, int startCol, int endRow, int endCol) throws Exception {
        if (maze == null) {
            throw new Exception("Oh no! You cannot call me without initializing the maze!");
        }

        if (startRow < 0 || startCol < 0 || startRow >= maze.getRows() || startCol >= maze.getColumns() ||
                endRow < 0 || endCol < 0 || endRow >= maze.getRows() || endCol >= maze.getColumns()) {
            throw new IllegalArgumentException("Invalid start/end coordinate");
        }

        // set all visited flag to false
        // before we begin our search
        for (int i = 0; i < maze.getRows(); ++i) {
            for (int j = 0; j < maze.getColumns(); ++j) {
                maze.getRoom(i, j).onPath = false;
            }
        }

        if (this.startRow == startRow && this.startCol == startCol && this.superpowers == 0) {
            trace(shortestDistance(this.been[endRow][endCol]));
            return this.been[endRow][endCol] != null
                    ? shortestDistance(this.been[endRow][endCol]).distance
                    : null;
        }

        this.been = new TreeNode[this.maze.getRows()][this.maze.getColumns()];
        this.been[startRow][startCol] = new TreeNode(new int[] {startRow, startCol}, 0, 0);

        this.queue.clear();
        this.queue.add(this.been[startRow][startCol]);

        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.superpowers = 0;

        solve();
        trace(shortestDistance(this.been[endRow][endCol]));
        return this.been[endRow][endCol] != null
                ? shortestDistance(this.been[endRow][endCol]).distance
                : null;
    }
    private TreeNode shortestDistance(TreeNode node) {
        if (node == null) return null;
        TreeNode curr = node;
        TreeNode result = node;
        while(curr != null) {
            if (curr.distance < result.distance) result = curr;
            curr = curr.next;
        }
        return result;
    }
    private TreeNode mostPower(TreeNode node) {
        if (node == null) return null;
        TreeNode curr = node;
        TreeNode result = node;
        while(curr != null) {
            if (curr.superpowers > result.superpowers) result = curr;
            curr = curr.next;
        }
        return result;
    }
    private boolean canGo(TreeNode node, int dir) {
        int row = node.coords[0];
        int col = node.coords[1];
        // not needed since our maze has a surrounding block of wall
        // but Joe the Average Coder is a defensive coder!
        if (row + DELTAS[dir][0] < 0 || row + DELTAS[dir][0] >= maze.getRows()) return false;
        if (col + DELTAS[dir][1] < 0 || col + DELTAS[dir][1] >= maze.getColumns()) return false;

        switch (dir) {
            case NORTH:
                return !maze.getRoom(row, col).hasNorthWall() || node.superpowers > 0;
            case SOUTH:
                return !maze.getRoom(row, col).hasSouthWall() || node.superpowers > 0;
            case EAST:
                return !maze.getRoom(row, col).hasEastWall() || node.superpowers > 0;
            case WEST:
                return !maze.getRoom(row, col).hasWestWall() || node.superpowers > 0;
        }

        return false;
    }
    private boolean requirePower(int row, int col, int dir) {
        switch (dir) {
            case NORTH:
                return maze.getRoom(row, col).hasNorthWall();
            case SOUTH:
                return maze.getRoom(row, col).hasSouthWall();
            case EAST:
                return maze.getRoom(row, col).hasEastWall();
            case WEST:
                return maze.getRoom(row, col).hasWestWall();
        }

        return false;
    }
    private int[] directionCoord(int row, int col, int direction) {
        return new int[] {row + DELTAS[direction][0], col + DELTAS[direction][1]};
    }
    private TreeNode neighbour(TreeNode node, int direction) {
        return this.been[node.coords[0] + DELTAS[direction][0]][node.coords[1] + DELTAS[direction][1]];
    }

    private void solve() {
        while (!this.queue.isEmpty()) {
            TreeNode curr = this.queue.poll();
            int row = curr.coords[0];
            int col = curr.coords[1];
            TreeNode[] children = new TreeNode[4];
            for (int direction = 0; direction < 4; ++direction) {
                if (!canGo(curr, direction)) continue;

                if (neighbour(curr, direction) == null) {
                    if (requirePower(row, col, direction)) {
                        children[direction] = new TreeNode(directionCoord(row, col, direction),
                                curr.superpowers - 1, curr.distance + 1);
                    } else {
                        children[direction] = new TreeNode(directionCoord(row, col, direction),
                                curr.superpowers, curr.distance + 1);
                    }
                    this.been[curr.coords[0] + DELTAS[direction][0]][curr.coords[1] + DELTAS[direction][1]] = children[direction];
                } else if (requirePower(row, col, direction) &&
                        curr.superpowers > 1 + mostPower(this.been[curr.coords[0] + DELTAS[direction][0]][curr.coords[1] + DELTAS[direction][1]]).superpowers) {
                    children[direction] = new TreeNode(directionCoord(row, col, direction),
                            curr.superpowers - 1, curr.distance + 1);
                    children[direction].setNext(neighbour(curr, direction));
                    this.been[curr.coords[0] + DELTAS[direction][0]][curr.coords[1] + DELTAS[direction][1]] = children[direction];
                } else if (!requirePower(row, col, direction) &&
                        curr.superpowers > mostPower(this.been[curr.coords[0] + DELTAS[direction][0]][curr.coords[1] + DELTAS[direction][1]]).superpowers) {
                    children[direction] = new TreeNode(directionCoord(row, col, direction),
                            curr.superpowers, curr.distance + 1);
                    children[direction].setNext(neighbour(curr, direction));
                    this.been[curr.coords[0] + DELTAS[direction][0]][curr.coords[1] + DELTAS[direction][1]] = children[direction];
                }
            }
            curr.setChildren(children);
            for (int direction = 0; direction < 4; direction++) {
                if (children[direction] != null) {
                    this.queue.add(children[direction]);
                }
            }
        }
    }

    private void trace(TreeNode node) {
        if (node == null) return;
        this.maze.getRoom(node.coords[0], node.coords[1]).onPath = true;
        if (node.parent == null) return;
        trace(node.parent);
    }

    @Override
    public Integer numReachable(int k) throws Exception {
        int result = 0;
        for (int i = 0; i < maze.getRows(); ++i) {
            for (int j = 0; j < maze.getColumns(); ++j) {
                if (this.been[i][j] != null && shortestDistance(this.been[i][j]).distance == k) result++;
            }
        }
        return result;
    }

    @Override
    public Integer pathSearch(int startRow, int startCol, int endRow,
                              int endCol, int superpowers) throws Exception {
        if (maze == null) {
            throw new Exception("Oh no! You cannot call me without initializing the maze!");
        }

        if (startRow < 0 || startCol < 0 || startRow >= maze.getRows() || startCol >= maze.getColumns() ||
                endRow < 0 || endCol < 0 || endRow >= maze.getRows() || endCol >= maze.getColumns()) {
            throw new IllegalArgumentException("Invalid start/end coordinate");
        }

        // set all visited flag to false
        // before we begin our search
        for (int i = 0; i < maze.getRows(); ++i) {
            for (int j = 0; j < maze.getColumns(); ++j) {
                maze.getRoom(i, j).onPath = false;
            }
        }

        if (this.startRow == startRow && this.startCol == startCol && this.superpowers == superpowers) {
            trace(shortestDistance(this.been[endRow][endCol]));
            return this.been[endRow][endCol] != null
                    ? shortestDistance(this.been[endRow][endCol]).distance
                    : null;
        }

        this.been = new TreeNode[this.maze.getRows()][this.maze.getColumns()];
        this.been[startRow][startCol] = new TreeNode(new int[] {startRow, startCol}, superpowers, 0);

        this.queue.clear();
        this.queue.add(this.been[startRow][startCol]);

        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.superpowers = superpowers;

        solve();
        trace(shortestDistance(this.been[endRow][endCol]));
        return this.been[endRow][endCol] != null
                ? shortestDistance(this.been[endRow][endCol]).distance
                : null;
    }

    public static void main(String[] args) {
        try {
            Maze maze = Maze.readMaze("maze-test.txt");
            MazeSolverWithPowerCorrect solver = new MazeSolverWithPowerCorrect();
            solver.initialize(maze);

            System.out.println(solver.pathSearch(0, 0, 3, 5, 2));
            MazePrinter.printMaze(maze);

            for (int i = 0; i <= 12; ++i) {
                System.out.println("Steps " + i + " Rooms: " + solver.numReachable(i));
            }
            for (int i = 0; i < 9; i++) {
                // System.out.println(Arrays.toString(solver.been[i]));
            }

//			System.out.println(solver.pathSearch(1, 1, 0, 0, 2));
//			MazePrinter.printMaze(maze);
//
//			for (int i = 0; i <= 9; ++i) {
//				System.out.println("Steps " + i + " Rooms: " + solver.numReachable(i));
//			}
//			for (int i = 0; i < 4; i++) {
//				System.out.println(Arrays.toString(solver.been[i]));
//			}
//
//			System.out.println(solver.pathSearch(2, 0, 1, 0));
//			MazePrinter.printMaze(maze);
//
//			for (int i = 0; i <= 9; ++i) {
//				System.out.println("Steps " + i + " Rooms: " + solver.numReachable(i));
//			}
//			for (int i = 0; i < 4; i++) {
//				System.out.println(Arrays.toString(solver.been[i]));
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}