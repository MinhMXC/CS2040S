import java.util.ArrayList;
import java.util.HashSet;

public class IdealSolution {
    class Score implements Comparable<Score> {
        int a;
        int b;

        public Score(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public int compareTo(Score other) {
            if (other == this)
                return 0;

            return this.a != other.a
                    ? this.a - other.a
                    : other.b - this.b;
        }

        public void update(int b) {
            this.a++;
            this.b += b;
        }
    }

    HashSet<Score> above;
    Score[] scores;

    public IdealSolution(int numTeams) {
        above = new HashSet<>();
        scores = new Score[numTeams];
        for (int i = 0; i < numTeams; i++)
            scores[i] = new Score(0, 0);
    }

    public int update(int team, long penalty) {
        scores[team - 1].update((int) penalty);
        if (team - 1 == 0) {
            ArrayList<Score> toBeRemove = new ArrayList<>();
            for (Score t : above)
                if (scores[0].compareTo(t) >= 0)
                    toBeRemove.add(t);
            for (Score t : toBeRemove)
                above.remove(t);
        }

        if (scores[team - 1].compareTo(scores[0]) > 0)
            above.add(scores[team - 1]);

        return above.size() + 1;
    }

    public static void main(String[] args) {
    }
}
