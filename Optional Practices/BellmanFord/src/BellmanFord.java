import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

// Pass private test cases 40% of the time which I believe is due to time limit exceed
// It is a bit slow but I tried to optimised it to the best of my ability
// The runtime cut-off on coursemology is pretty low
public class BellmanFord {
    // DO NOT MODIFY THE TWO STATIC VARIABLES BELOW
    public static int INF = 20000000;
    public static int NEGINF = -20000000;



    // FIELDS
    private ArrayList<HashMap<Integer, Integer>> adjList;
    private HashMap<Integer, Integer> results; // Memoised results for getDistance to run in O(1)



    // HELPERS
    // Relax all edge coming out of a node
    private boolean relaxNode(int a, HashMap<Integer, Integer> hm) {
        boolean res = false;
        for (Integer b : adjList.get(a).keySet()) {
            if (hm.get(b) > hm.get(a) + adjList.get(a).get(b)) {
                hm.put(b,hm.get(a) + adjList.get(a).get(b));
                res = true;
            }
        }
        return res;
    }

    // BFS to find all nodes that is connected to source
    private void breadthFirstSearch(int source, HashSet<Integer> connected) {
        LinkedList<Integer> processing = new LinkedList<>(adjList.get(source).keySet());
        connected.add(source);

        while (!processing.isEmpty()) {
            Integer current = processing.remove();
            if (connected.contains(current))
                continue;

            connected.add(current);
            processing.addAll(adjList.get(current).keySet());
        }
    }



    // PUBLIC METHODS
    public BellmanFord(ArrayList<ArrayList<IntPair>> adjList) {
        this.adjList = new ArrayList<>();
        results = new HashMap<>();

        for (int i = 0; i < adjList.size(); i++) {
            HashMap<Integer, Integer> hm = new HashMap<>();
            ArrayList<IntPair> cur = adjList.get(i);
            for (IntPair pair : cur) {
                hm.put(pair.first, pair.second);
            }

            this.adjList.add(hm);
        }
    }

    // Run bellman ford algorithm
    public void computeShortestPaths(int source) {
        // reset all parameters
        for (int i = 0; i < adjList.size(); i++) {
            results.put(i, INF);
        }
        results.put(source, 0);

        if (source >= adjList.size())
            return;


        HashSet<Integer> connected = new HashSet<>();
        results.put(source, 0);
        breadthFirstSearch(source, connected);

        for (int i = 0; i < connected.size(); i++) {
            boolean changed = false;
            for (Integer node : connected)
                if (results.get(node) != INF)
                    changed = relaxNode(node, results) || changed;

            if (!changed)
                break;
        }

        @SuppressWarnings("unchecked")
        HashMap<Integer, Integer> secondResults = (HashMap<Integer, Integer>) results.clone();

        // Run or a second time to detect negative cycles
        for (int i = 0; i < connected.size(); i++) {
            boolean changed = false;
            for (Integer node : connected)
                if (results.get(node) != INF && results.get(node) != NEGINF)
                    changed = relaxNode(node, secondResults) || changed;

            if (changed) {
                for (Integer key : results.keySet()) {
                    if (results.get(key) != NEGINF && results.get(key) > secondResults.get(key)) {
                        results.put(key, NEGINF);
                        HashSet<Integer> temp = new HashSet<>();
                        breadthFirstSearch(key, temp);
                        for (Integer node : temp) // any node connected to this node is also in a neg cycle
                            results.put(node, NEGINF);
                    }
                }
            } else {
                break;
            }
        }
    }

    public int getDistance(int node) {
        return results.getOrDefault(node, INF);
    }
}