import java.util.*;
enum Direction {
	NORTH,
	SOUTH,
	EAST,
	WEST
}

class Data {
	public int row;
	public int col;
	public int depth;
	public Direction direction;

	// To be used with numSearch
	public Data(int row, int col, int depth, Direction direction) {
		this.row = row;
		this.col = col;
		this.depth = depth;
		this.direction = direction;
	}
}

public class MazeSolver implements IMazeSolver {
	private static int[][] DELTAS = new int[][] {
			{ -1, 0 }, // North
			{ 1, 0 }, // South
			{ 0, 1 }, // East
			{ 0, -1 } // West
	};

	private Maze maze;
	private boolean[][] visited;
	private Direction[][] parents;
	private int startRow, startCol, endRow, endCol;
	private ArrayList<Integer> results; // To store memoized result from numSearch
	private LinkedList<Data> queue; // Queue for numSearch
	public MazeSolver() {
		maze = null;
	}

	@Override
	public void initialize(Maze maze) {
		this.maze = maze;
		visited = new boolean[maze.getRows()][maze.getColumns()];
		parents = new Direction[maze.getRows()][maze.getColumns()];
		results = new ArrayList<>();
		queue = new LinkedList<>();
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

		// set all visited flag to false and parents to null
		// before we begin our search
		for (int i = 0; i < maze.getRows(); ++i) {
			for (int j = 0; j < maze.getColumns(); ++j) {
				this.visited[i][j] = false;
				this.parents[i][j] = null;
				maze.getRoom(i, j).onPath = false;
			}
		}

		results.clear();
		queue.clear();

		this.startRow = startRow;
		this.startCol = startCol;
		this.endRow = endRow;
		this.endCol = endCol;
		return solve(startRow, startCol);
	}

	// Add to queue the possible next moves when performing BFS
	private void addToQueue(LinkedList<Data> queue, int row, int col, int depth) {
		if (canGo(row, col, Direction.NORTH) && !visited[row - 1][col]) {
			queue.addLast(new Data(row, col, depth, Direction.NORTH));
		}
		if (canGo(row, col, Direction.SOUTH) && !visited[row + 1][col]) {
			queue.addLast(new Data(row, col, depth, Direction.SOUTH));
		}
		if (canGo(row, col, Direction.EAST) && !visited[row][col + 1]) {
			queue.addLast(new Data(row, col, depth, Direction.EAST));
		}
		if (canGo(row, col, Direction.WEST) && !visited[row][col - 1]) {
			queue.addLast(new Data(row, col, depth, Direction.WEST));
		}
	}

	private boolean canGo(int row, int col, Direction dir) {
		// not needed since our maze has a surrounding block of wall
		// but Joe the Average Coder is a defensive coder!
		int ordinal = dir.ordinal();
		if (row + DELTAS[ordinal][0] < 0 || row + DELTAS[ordinal][0] >= maze.getRows()) return false;
		if (col + DELTAS[ordinal][1] < 0 || col + DELTAS[ordinal][1] >= maze.getColumns()) return false;

		switch (dir) {
			case NORTH:
				return !maze.getRoom(row, col).hasNorthWall();
			case SOUTH:
				return !maze.getRoom(row, col).hasSouthWall();
			case EAST:
				return !maze.getRoom(row, col).hasEastWall();
			case WEST:
				return !maze.getRoom(row, col).hasWestWall();
		}

		return false;
	}

	// Iterative Breadth First Search
	private Integer solve(int row, int col) {
		visited[row][col] = true;

		if (row == endRow && col == endCol) {
			return 0;
		}

		LinkedList<Data> data = new LinkedList<>();
		addToQueue(data, row, col, 0); // depth = 0 since we don't care about it here

		while (!data.isEmpty()) {
			Data d = data.getFirst();
			data.removeFirst();

			int direction = d.direction.ordinal();
			int cRow = d.row + DELTAS[direction][0];
			int cCol = d.col + DELTAS[direction][1];

			if (visited[cRow][cCol]) {
				continue;
			}

			visited[cRow][cCol] = true;
			parents[cRow][cCol] = d.direction == Direction.NORTH
					? Direction.SOUTH
					: d.direction == Direction.SOUTH
					? Direction.NORTH
					: d.direction == Direction.EAST
					? Direction.WEST
					: Direction.EAST;

			if (cRow == endRow && cCol == endCol) {
				return flipOnPathAndCalculateLength();
			}

			addToQueue(data, cRow, cCol, 0);
		}

		return null;
	}

	// To be called when the endRoom has been reached
	private Integer flipOnPathAndCalculateLength() {
		int row = endRow;
		int col = endCol;
		int count = 0;

		maze.getRoom(row, col).onPath = true;
		Direction parent = parents[row][col];

		// Parent is null only when it is the original start room
		while (parent != null) {
			int direction = parent.ordinal();
			row = row + DELTAS[direction][0];
			col = col + DELTAS[direction][1];
			maze.getRoom(row, col).onPath = true;
			parent = parents[row][col];
			count++;
		}
		return count;
	}

	// Result are calculated and memoized
	// Only do BFS up to k depth
	@Override
	public Integer numReachable(int k) throws Exception {
		if (results.size() > k) {
			return results.get(k);
		}

		// Check for first time run
		if (results.isEmpty()) {
			// numReachable 0 is always 1
			results.add(1);

			// reset visited
			for (int i = 0; i < maze.getRows(); ++i) {
				for (int j = 0; j < maze.getColumns(); ++j) {
					visited[i][j] = false;
				}
			}

			visited[startRow][startCol] = true;
			addToQueue(queue, startRow, startCol, 1);
		}

		while (!queue.isEmpty()) {
			Data d = queue.getFirst();

			// Break when depth is more than k
			if (d.depth > k) {
				break;
			}

			queue.removeFirst();

			// Add more element to results until results.get(d.depth) can be accessed
			while (results.size() <= d.depth) {
				results.add(0);
			}

			int cRow = d.row + DELTAS[d.direction.ordinal()][0];
			int cCol = d.col + DELTAS[d.direction.ordinal()][1];

			// If already visited then go to the next one
			if (visited[cRow][cCol]) {
				continue;
			}

			results.set(d.depth, results.get(d.depth) + 1);
			visited[cRow][cCol] = true;
			addToQueue(queue, cRow, cCol, d.depth + 1);
		}

		return k >= results.size() ? 0 : results.get(k); // predicate is true when all room to search is exhausted
	}

	public static void main(String[] args) {
		// Do remember to remove any references to ImprovedMazePrinter before submitting
		// your code!
		try {
			Maze maze = Maze.readMaze("maze-empty.txt");
			IMazeSolver solver = new MazeSolver();
			solver.initialize(maze);
			System.out.println(solver.pathSearch(0, 0, 1, 3));
			MazePrinter.printMaze(maze);
			for (int i = 0; i <= 9; ++i) {
				System.out.println("Steps " + i + " Rooms: " + solver.numReachable(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}