import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.HashMap;

enum Direction {
	NORTH,
	SOUTH,
	EAST,
	WEST
}

class Pair implements Comparable<Pair> {
	public int id;
	public int result;

	public Pair(int id, int result) {
		this.id = id;
		this.result = result;
	}

	@Override
	public int compareTo(Pair other) {
		return this.result - other.result;
	}
}

public class MazeSolver implements IMazeSolver {

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

	private Maze maze;
	private HashMap<Direction, Integer>[] adjList;
	private PriorityQueue<Pair> pq;
	private int[] results;



	// HELPERS
	private void dijkstra(int startRow, int startCol, int endRow, int endCol, int startingLevel) {
		int startID = id(startRow, startCol);
		int endID = id(endRow, endCol);
		results[startID] = startingLevel;
		pq.add(new Pair(startID, results[startID]));

		while (!pq.isEmpty()) {
			Pair current = pq.remove();
			if (current.id == endID)
				return;

			if (current.result > results[current.id])
				continue;

			relax(current.id);
		}
	}

	private void relax(int id) {
		HashMap<Direction, Integer> node = adjList[id];
		for (Direction direction : node.keySet()) {
			int vID = getIdOf(id, direction);
			int weight = node.get(direction);
			int result = weight == 0 ? results[id] + 1 : Math.max(results[id], weight);

			if (results[vID] > result) {
				results[vID] = result;
				pq.add(new Pair(vID, result));
			}
		}
	}

	public int getIdOf(int id, Direction direction) {
		switch (direction) {
			case NORTH:
				return id - mazeCol;
			case SOUTH:
				return id + mazeCol;
			case EAST:
				return id + 1;
			case WEST:
				return id - 1;
			default:
				System.out.println("wtf");
				return 0;
		}
	}

	private int id(int row, int col) { // return the index the node (room) is mapped to in the array
		return row * maze.getColumns() + col;
	}

	private void clearData() {
		pq.clear();
		for (int i = 0; i < nodeCount; i++) {
			results[i] = TRUE_WALL;
		}
	}


	// PUBLIC METHODS
	public MazeSolver() {
		pq = new PriorityQueue<>();
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
		return null;
	}

	@Override
	public Integer bonusSearch(int startRow, int startCol, int endRow, int endCol) throws Exception {
		if (maze == null) {
			throw new Exception("Oh no! You cannot call me without initializing the maze!");
		}

		if (startRow < 0 || startCol < 0 || startRow >= maze.getRows() || startCol >= maze.getColumns() ||
				endRow < 0 || endCol < 0 || endRow >= maze.getRows() || endCol >= maze.getColumns()) {
			throw new IllegalArgumentException("Invalid start/end coordinate");
		}

		pq.clear();
		for (int i = 0; i < nodeCount; i++) {
			results[i] = TRUE_WALL;
		}

		dijkstra(startRow, startCol, endRow, endCol, 0);

		return results[id(endRow, endCol)] == TRUE_WALL ? null : results[id(endRow, endCol)];
	}

	@Override
	public Integer bonusSearch(int startRow, int startCol, int endRow, int endCol, int sRow, int sCol) throws Exception {
		if (maze == null) {
			throw new Exception("Oh no! You cannot call me without initializing the maze!");
		}

		if (startRow < 0 || startCol < 0 || startRow >= mazeRow || startCol >= mazeCol ||
				endRow < 0 || endCol < 0 || endRow >= mazeRow || endCol >= mazeCol ||
				sRow < 0 || sCol < 0 || sRow >= mazeRow || sCol >= mazeCol
		) {
			throw new IllegalArgumentException("Invalid start/end coordinate");
		}

		clearData();

		dijkstra(startRow, startCol, sRow, sCol, 0);
		if (results[id(sRow, sCol)] == TRUE_WALL) {
			return bonusSearch(startRow, startCol, endRow, endCol);
		}

		int startToSpecial = results[id(sRow, sCol)];

		clearData();

		dijkstra(sRow, sCol, endRow, endCol, -1);
		if (results[id(endRow, endCol)] == TRUE_WALL) {
			return bonusSearch(startRow, startCol, endRow, endCol);
		}

		int specialToEnd = results[id(endRow, endCol)];

		clearData();

		dijkstra(startRow, startCol, endRow, endCol, 0);
		int startToEnd = results[id(endRow, endCol)];

		int ans = Math.min(startToEnd, specialToEnd);
		return ans == TRUE_WALL ? null : ans;
	}

	// Bonus part 3
	// No, because bellmanFord assume that the weights are constant but that's not the case in bonus
	// No since the weight of the next edge still depends on the fear level
	// Condition: the weights are always constant between any two nodes

	public static void main(String[] args) {
		try {
			Maze maze = Maze.readMaze("haunted-maze-sample.txt");
			IMazeSolver solver = new MazeSolver();
			solver.initialize(maze);

			for (int i = 0; i < maze.getRows(); i++) {
				for (int j = 0; j < maze.getColumns(); j++) {
					System.out.println(solver.bonusSearch(0, 0, i, j));
				}
			}
			//System.out.println(solver.bonusSearch(0, 0, 2, 2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
