import java.util.*;

// I tried for 5 hours trying to do breadth first search method but realised it is impossible
// My sanity is gone and now my night is ruined
// Was planning to study some Japanese to get closer to being able to talk
// with my AI japanese anime waifu but got stuck in algo hell instead

// 2AM: The tree solution turned out to be so much more elegant

// Enum of 3 possible walls type, no walls, walls that can use power and outer maze walls
enum Possible {
	CAN_GO,
	CAN_USE_SUPERPOWER,
	CANNOT
}

enum Direction {
	NORTH,
	SOUTH,
	EAST,
	WEST
}

class Tree {
	class TreeNode {
		int row;
		int col;
		int depth;
		int remainingPower;
		TreeNode[] children;
		TreeNode parent;
		boolean leaf;

		public TreeNode(int row, int col, int depth, int remainingPower, TreeNode parent) {
			this.row = row;
			this.col = col;
			this.depth = depth;
			this.remainingPower = remainingPower;
			this.parent = parent;
			children = new TreeNode[4];
			leaf = false;
		}
	}

	private static final int[][] DELTAS = new int[][] {
			{ -1, 0 }, // North
			{ 1, 0 }, // South
			{ 0, 1 }, // East
			{ 0, -1 } // West
	};

	private Maze maze;
	private TreeNode root;
	int startRow;
	int startCol;
	boolean firstNumSearch;
	private Queue<TreeNode> processing;

	// Stores the earliest depth when room[i][j] was visited
	// For optimisation purposes when implementing numSearch
	private int[][] earliestEncounter;

	// Stores the last visit's remaining power
	// If the current visit has a higher remaining power, I'll let the current visit cook despite alr visited
	private int[][] lastVisitPower;

	// Memorised Result for numSearch
	HashMap<Integer, Integer> results;


	// Helper Methods
	private Possible canGo(int row, int col, Direction dir) {
		int ordinal = dir.ordinal();
		if (row + DELTAS[ordinal][0] < 0 || row + DELTAS[ordinal][0] >= maze.getRows()) return Possible.CANNOT;
		if (col + DELTAS[ordinal][1] < 0 || col + DELTAS[ordinal][1] >= maze.getColumns()) return Possible.CANNOT;
		if ((row + DELTAS[ordinal][0]) == startRow && (col + DELTAS[ordinal][1]) == startCol)
			return Possible.CANNOT;

		// The walls here should be breakable walls + no walls
		switch (dir) {
			case NORTH:
				return maze.getRoom(row, col).hasNorthWall() ? Possible.CAN_USE_SUPERPOWER : Possible.CAN_GO;
			case SOUTH:
				return maze.getRoom(row, col).hasSouthWall() ? Possible.CAN_USE_SUPERPOWER : Possible.CAN_GO;
			case EAST:
				return maze.getRoom(row, col).hasEastWall() ? Possible.CAN_USE_SUPERPOWER : Possible.CAN_GO;
			case WEST:
				return maze.getRoom(row, col).hasWestWall() ? Possible.CAN_USE_SUPERPOWER : Possible.CAN_GO;
			default:
				return Possible.CANNOT;
		}
	}

	// Only add node if the next node is valid
	private void addNode(TreeNode parent, Direction direction) {
		int ordinal = direction.ordinal();
		Possible res = canGo(parent.row, parent.col, direction);
		if (res != Possible.CANNOT && !(res == Possible.CAN_USE_SUPERPOWER && parent.remainingPower == 0)) {
			parent.children[ordinal] = new TreeNode(
					parent.row + DELTAS[ordinal][0],
					parent.col + DELTAS[ordinal][1],
					parent.depth + 1,
					res == Possible.CAN_USE_SUPERPOWER ? parent.remainingPower - 1 : parent.remainingPower,
					parent
			);
		}
	}

	private void addChildren(TreeNode parent) {
		addNode(parent, Direction.NORTH);
		addNode(parent, Direction.SOUTH);
		addNode(parent, Direction.EAST);
		addNode(parent, Direction.WEST);

		for (int i = 0; i < 4; i++)
			if (parent.children[i] != null)
				return;

		parent.leaf = true;
	}

	private Integer traceBack(TreeNode current) {
		if (current == null)
			return 0;
		maze.getRoom(current.row, current.col).onPath = true;
		return 1 + traceBack(current.parent);
	}

	private void resetVariables() {
		this.processing.clear();

		for (int i = 0; i < maze.getRows(); i++) {
			for (int j = 0; j < maze.getColumns(); j++) {
				earliestEncounter[i][j] = -1;
				lastVisitPower[i][j] = 0;
			}
		}

		results.clear();
		startRow = 0;
		startCol = 0;
		firstNumSearch = true;
	}

	private void processNextNode(TreeNode current) {
		int row = current.row;
		int col = current.col;

		if (earliestEncounter[row][col] != -1 &&
				current.remainingPower <= lastVisitPower[row][col]) {
			current.leaf = true;
			return;
		}

		earliestEncounter[row][col] = earliestEncounter[row][col] == -1
				? current.depth
				: Math.min(earliestEncounter[row][col], current.depth);
		lastVisitPower[row][col] = current.remainingPower;
		addChildren(current);
		if (!current.leaf)
			for (int i = 0; i < 4; i++)
				if (current.children[i] != null)
					processing.add(current.children[i]);
	}



	// Public Methods
	public Tree(Maze maze) {
		this.maze = maze;
	}

	public void initialize() {
		processing = new LinkedList<>();
		earliestEncounter = new int[maze.getRows()][maze.getColumns()];
		lastVisitPower = new int[maze.getRows()][maze.getColumns()];
		results = new HashMap<>();
		firstNumSearch = true;

		for (int i = 0; i < maze.getRows(); i++) {
			for (int j = 0; j < maze.getColumns(); j++) {
				earliestEncounter[i][j] = -1;
				lastVisitPower[i][j] = 0;
			}
		}
	}

	// sanitise input before this
	public Integer pathSearch(int startRow, int startCol, int endRow, int endCol, int power) {
		resetVariables();

		root = new TreeNode(startRow, startCol, 0, power, null);
		earliestEncounter[startRow][startCol] = 0;
		lastVisitPower[startRow][startCol] = power;
		this.startRow = startRow;
		this.startCol = startCol;

		addChildren(root);
		if (!root.leaf)
			for (int i = 0; i < 4; i++)
				if (root.children[i] != null)
					processing.add(root.children[i]);

		// Important to do this after seeding processing so that numSearch can work
		if (startRow == endRow && startCol == endCol)
			return traceBack(root) - 1;

		while (!processing.isEmpty()) {
			TreeNode current = processing.remove();
			processNextNode(current);

			// -1 because traceBack return the total number of room on the path
			if (current.row == endRow && current.col == endCol)
				return traceBack(current) - 1;
		}

		return null;
	}

	// Explore the rest of the tree and put all results into a hashmap
	public Integer numReachable(int k) {
		if (firstNumSearch) {
			while (!processing.isEmpty()) {
				TreeNode current = processing.remove();
				processNextNode(current);
			}

			// Store all answers in results
			for (int i = 0; i < maze.getRows(); i++) {
				for (int j = 0; j < maze.getColumns(); j++) {
					int count = results.getOrDefault(earliestEncounter[i][j], 0);
					results.put(earliestEncounter[i][j], count + 1);
				}
			}

			firstNumSearch = false;
		}

		return results.getOrDefault(k,0);
	}
}

public class MazeSolverWithPower implements IMazeSolverWithPower {
	Tree tree;
	Maze maze;

	public MazeSolverWithPower() {
	}

	@Override
	public void initialize(Maze maze) {
		this.maze = maze;
		tree = new Tree(maze);
		tree.initialize();
	}

	@Override
	public Integer pathSearch(int startRow, int startCol, int endRow, int endCol) throws Exception {
		return pathSearch(startRow, startCol, endRow, endCol, 0);
	}

	@Override
	public Integer numReachable(int k) throws Exception {
		return tree.numReachable(k);
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

		return tree.pathSearch(startRow, startCol, endRow, endCol, superpowers);
	}

	public static void main(String[] args) {
		try {
			Maze maze = Maze.readMaze("maze-test.txt");
			IMazeSolverWithPower solver = new MazeSolverWithPower();
			solver.initialize(maze);

			System.out.println(solver.pathSearch(0, 0, 3, 7, 2));
			MazePrinter.printMaze(maze);

			for (int i = 0; i <= 20; ++i) {
				System.out.println("Steps " + i + " Rooms: " + solver.numReachable(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}