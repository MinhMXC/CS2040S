import java.util.ArrayList;
import java.util.Random;

// Scoured Github to find a working one
class BellmanFordCorrect {
    public static int INF = 20000000;
    public static int NEGINF = -20000000;
    public ArrayList<ArrayList<IntPair>> adj;
    public int[] dis;

    public BellmanFordCorrect(ArrayList<ArrayList<IntPair>> adjList) {
        this.adj = adjList;
        this.dis = new int[adjList.size()];
    }

    public void computeShortestPaths(int source) {
        dis[source] = 0;
        for (int i = 0; i < dis.length; i++) {
            if (i != source)    dis[i] = BellmanFord.INF;
        }

        for (int j = 1; j <= adj.size() - 1; j++) {
            for (int k = 0; k < adj.size(); k++) {
                for (IntPair pair : adj.get(k)) {
                    if (dis[k] != BellmanFord.INF) {
                        if (dis[pair.first] > pair.second + dis[k] && dis[pair.first] != BellmanFord.NEGINF) {
                            dis[pair.first] = pair.second + dis[k];
                        }
                    }
                }
            }
        }

        for (int k = 0; k < adj.size(); k++) {
            for (IntPair pair : adj.get(k)) {
                if (dis[pair.first] > pair.second + dis[k] && dis[pair.first] != BellmanFord.NEGINF) {
                    for (int j = 1; j <= adj.size(); j++) {
                        for (int n = 0; n < adj.size(); n++) {
                            for (IntPair par : adj.get(n)) {
                                if (dis[n] != BellmanFord.INF && dis[par.first] > par.second + dis[n] && dis[par.first] != BellmanFord.NEGINF) {
                                    dis[par.first] = NEGINF;
                                }
                            }
                        }
                    }
                    return;
                }
            }
        }
    }

    public int getDistance(int node) {
        if (node >= 0 && node < adj.size())   return dis[node];
        else                                  return INF;
    }
}

// Class to test my solution against another known working one
public class GraphGenerator {
    Random rnd;

    public GraphGenerator() {
        rnd = new Random();
    }

    private int randomNumber() {
        return rnd.nextInt(21) - 10;
    }

    private ArrayList<IntPair> randomEdge() {
        ArrayList<IntPair> ans = new ArrayList<>();
        int a = rnd.nextInt(5);
        int b = rnd.nextInt(5);
        for (int i = a; i <= b; i++) {
            ans.add(new IntPair(i, randomNumber()));
        }
        return ans;
    }

    private ArrayList<ArrayList<IntPair>> generate() {
        return new ArrayList<>(){{
            add(randomEdge());
            add(randomEdge());
            add(randomEdge());
            add(randomEdge());
            add(randomEdge());
        }};
    }

    public static void main(String[] args) {
        GraphGenerator gg = new GraphGenerator();

        for (int k =0; k < 1000000; k++) {
            ArrayList<ArrayList<IntPair>> adjList =  gg.generate();

            BellmanFord bmf = new BellmanFord(adjList);
            BellmanFordCorrect bmfc = new BellmanFordCorrect(adjList);

            for (int j = 0; j < 5; j++) {
                bmf.computeShortestPaths(j);
                bmfc.computeShortestPaths(j);

                for (int i = 0; i < 5; i++) {
                    if (bmf.getDistance(i) != bmfc.getDistance(i)) {
                        System.out.println("wrong ans");
                    }
                }
            }
        }
    }
}
