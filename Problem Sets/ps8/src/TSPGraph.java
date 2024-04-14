import java.util.*;

public class TSPGraph implements IApproximateTSP {
    private class Pair {
        int a;
        int b;

        public Pair(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    // Prim Algorithm
    @Override
    public void MST(TSPMap map) {
        int numOfPoints = map.getCount();
        TreeMapPriorityQueue<Double, Integer> pq = new TreeMapPriorityQueue<>();
        for (int i = 0; i < numOfPoints; i++)
            pq.add(i, Double.MAX_VALUE);
        pq.decreasePriority(0, 0.0);

        HashSet<Integer> set = new HashSet<>();

        while (!pq.isEmpty()) {
            Integer current = pq.extractMin();
            set.add(current);

            for (int i = 0; i < numOfPoints; i++)
                if (!set.contains(i))
                    if (pq.decreasePriority(i, map.pointDistance(current, i)))
                        map.setLink(i, current);
        }
    }

    @Override
    public void TSP(TSPMap map) {
        MST(map);

        // DFS O(V + E) = O(V)
        int numOfPoints = map.getCount();
        HashMap<Integer, ArrayList<Integer>> hm = new HashMap<>();
        for (int i = 0; i < numOfPoints; i++) {
            if (map.getLink(i) != -1) {
                ArrayList<Integer> a = hm.getOrDefault(map.getLink(i), new ArrayList<>());
                a.add(i);
                hm.put(map.getLink(i), a);
            }
        }

        ArrayList<Integer> queue = new ArrayList<>();
        Stack<Pair> stack = new Stack<>();
        for (Integer i : hm.get(0))
            stack.add(new Pair(0, i));

        while (!stack.isEmpty()) {
            Pair current = stack.pop();
            queue.add(current.a);

            if (hm.get(current.b) != null) {
                for (Integer i : hm.get(current.b))
                    stack.add(new Pair(current.b, i));
            } else {
                queue.add(current.b);
            }
        }

        // Shortcut O(2V) = O(V)
        HashSet<Integer> set = new HashSet<>();
        Integer prev = 0;
        for (Integer current : queue) {
            if (!set.contains(current)) {
                set.add(current);
                map.setLink(current, prev);
                prev = current;
            } else {
                continue;
            }
        }
        map.setLink(0, prev); // link the start node to the last valid visited node
    }

    @Override
    public boolean isValidTour(TSPMap map) {
        HashSet<Integer> set = new HashSet<>();
        int current = 0;
        int count = 0;
        while (current != -1 && !set.contains(current)) {
            set.add(current);
            current = map.getLink(current);
            count++;
        }
        return count == map.getCount() && current == 0; // make sure the thing return to 0 at the end
    }

    @Override
    public double tourDistance(TSPMap map) {
        if (!isValidTour(map))
            return -1;

        double cost = map.pointDistance(0, map.getLink(0));
        int current = map.getLink(0);
        while (current != 0) {
            cost += map.pointDistance(current, map.getLink(current));
            current = map.getLink(current);
        }
        return cost;
    }

    public static void main(String[] args) {
        TSPMap map = new TSPMap(args.length > 0 ? args[0] : "fiftypoints.txt");
        TSPGraph graph = new TSPGraph();

        // graph.MST(map);
        graph.TSP(map);
        // System.out.println(graph.isValidTour(map));
        // System.out.println(graph.tourDistance(map));
    }
}
