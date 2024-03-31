import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.HashMap;

public class MazeSolver implements IMazeSolver {
	enum Direction {
		NORTH,
		SOUTH,
		EAST,
		WEST
	}

	private static final int TRUE_WALL = Integer.MAX_VALUE;
	private static final int EMPTY_SPACE = 0;
	private static final List<Function<Room, Integer>> WALL_FUNCTIONS = Arrays.asList(
			Room::getNorthWall,
			Room::getEastWall,
			Room::getWestWall,
			Room::getSouthWall
	);
	private static final int[][] DELTAS = new int[][] {
			{ -1, 0 }, // North
			{ 1, 0 }, // South
			{ 0, 1 }, // East
			{ 0, -1 }, // West
	};



	// FIELDS

	// These fields seem useless since we can use getter for it
	// but do you know using getters takes 3 times longer than direct field?
	private int nodeCount;
	private int mazeRow;
	private int mazeCol;
	private int startRow = -1;
	private int startCol = -1;

	private Maze maze;
	private HashMap<Direction, Integer>[] adjList;
	private int[] results;



	// HELPERS
	private void bellmanFord() {
		for (int a = 0; a < nodeCount; a++) {
			boolean changed = false;

			for (int i = 0; i < mazeRow; i++) {
				for (int j = 0; j < mazeCol; j++) {
					int cId = id(i, j);
					if (results[cId] == TRUE_WALL)
						continue;

					HashMap<Direction, Integer> current = adjList[cId];
					for (Direction edge : current.keySet()) {
						int id = id(i + DELTAS[edge.ordinal()][0], j + DELTAS[edge.ordinal()][1]);
						int weight = current.get(edge) == 0 ? 1 : current.get(edge);
						if (results[id] > results[cId] + weight) {
							results[id] = results[cId] + weight;
							changed = true;
						}
					}
				}
			}

			if (!changed)
				return;
		}
	}

	private int id(int row, int col) { // return the index the node (room) is mapped to in the array
		return row * maze.getColumns() + col;
	}


	// PUBLIC METHODS
	public MazeSolver() {
	}

	@Override
	public void initialize(Maze maze) {
		this.maze = maze;
		this.mazeRow = maze.getRows();
		this.mazeCol = maze.getColumns();
		this.nodeCount = mazeRow * mazeCol;
		this.adjList = new HashMap[nodeCount];
		results = new int[nodeCount];

		for (int i = 0; i < maze.getRows(); i++) {
			for (int j = 0; j < maze.getColumns(); j++) {
				HashMap<Direction, Integer> temp = new HashMap<>();
				if (maze.getRoom(i, j).getNorthWall() != TRUE_WALL)
					temp.put(Direction.NORTH, maze.getRoom(i, j).getNorthWall());
				if (maze.getRoom(i, j).getSouthWall() != TRUE_WALL)
					temp.put(Direction.SOUTH, maze.getRoom(i, j).getSouthWall());
				if (maze.getRoom(i, j).getEastWall() != TRUE_WALL)
					temp.put(Direction.EAST, maze.getRoom(i, j).getEastWall());
				if (maze.getRoom(i, j).getWestWall() != TRUE_WALL)
					temp.put(Direction.WEST, maze.getRoom(i, j).getWestWall());
				adjList[id(i, j)] = temp;

				results[id(i, j)] = TRUE_WALL;
			}
		}
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

		if (this.startRow != startRow || this.startCol != startCol) {
			this.startRow = startRow;
			this.startCol = startCol;

			for (int i = 0; i < nodeCount; i++)
				results[i] = TRUE_WALL;
			results[id(startRow, startCol)] = 0;

			bellmanFord();
		}

		int id = id(endRow, endCol);
		return results[id] == TRUE_WALL ? null : results[id];
	}

	@Override
	public Integer bonusSearch(int startRow, int startCol, int endRow, int endCol) throws Exception {
		// TODO: Find minimum fear level given new rules.
		return null;
	}

	@Override
	public Integer bonusSearch(int startRow, int startCol, int endRow, int endCol, int sRow, int sCol) throws Exception {
		// TODO: Find minimum fear level given new rules and special room.
		return null;
	}

	public static void main(String[] args) {
		try {
			Maze maze = Maze.readMaze("maze-empty.txt");
			IMazeSolver solver = new MazeSolver();
			solver.initialize(maze);

//			for (int i = 0; i < maze.getRows(); i++) {
//				for (int j = 0; j < maze.getColumns(); j++) {
//					System.out.println(solver.pathSearch(0, 1, i, j));
//				}
//			}
			System.out.println(solver.pathSearch(0, 1, 0, 0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
