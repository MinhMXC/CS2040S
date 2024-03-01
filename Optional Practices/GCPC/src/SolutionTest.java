import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.*;

class Pair {
    int a;
    int b;

    public Pair(int a, int b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return a + " " + b;
    }
}

public class SolutionTest {

    @org.junit.Test
    public void update() {
        Solution soln = new Solution(3);
        assertEquals(2, soln.update(2, 7));
        assertEquals(3, soln.update(3, 5));
        assertEquals(2, soln.update(1, 6));
        assertEquals(1, soln.update(1, 9));
    }

    @org.junit.Test
    public void check() {
        Random r = new Random();
        int SIZE = 100000;
        int TEST_PASS_COUNT = 1000;
        float ra = 0;

        // Store commands of runs where ratio > threshold
        ArrayList<Pair> commands = new ArrayList<>();

        for (int i = 0; i < TEST_PASS_COUNT; i++) {
            long totalTimeSolution = 0;
            long totalTimeIdealSolution = 0;
            Solution s1 = new Solution(SIZE);
            IdealSolution s2 = new IdealSolution(SIZE);

            ArrayList<Pair> current_commands = new ArrayList<>();

            for (int j = 0; j < 1000; j++) {

                int team = r.nextInt(SIZE) + 1;
                int penalty = r.nextInt(SIZE);
                // System.out.printf("%d %d\n", team, penalty);

                current_commands.add(new Pair(team, penalty));

                long startA = System.nanoTime();
                int a = s1.update(team, penalty);
                long endA = System.nanoTime();
                int b = s2.update(team, penalty);
                long endB = System.nanoTime();

                totalTimeSolution += endA - startA;
                totalTimeIdealSolution += endB - endA;

                assertEquals(a, b);
            }

            ra += (float) (totalTimeSolution / totalTimeIdealSolution);
//            if (totalTimeSolution / totalTimeIdealSolution > 10) {
//                if (commands.isEmpty())
//                    commands.addAll(current_commands);
//                System.out.println("Warning: time ratio threshold exceeded");
//            }
        }

        System.out.println("Average Ratio: " + ra / TEST_PASS_COUNT);
        System.out.println(commands);
    }

    @org.junit.Test
    public void randomisedTest() {
        Random r = new Random();
        for (int k = 0; k < 1000; k++) {
            int SIZE = r.nextInt(1000) + 1;
            int TEST_PASS_COUNT = 100;

            for (int i = 0; i < TEST_PASS_COUNT; i++) {

                Solution s1 = new Solution(SIZE);
                IdealSolution s2 = new IdealSolution(SIZE);

                for (int j = 0; j < 100; j++) {
                    int team = r.nextInt(SIZE) + 1;
                    int penalty = r.nextInt(SIZE);
                    // System.out.printf("%d %d\n", team, penalty);

                    int a = s1.update(team, penalty);
                    int b = s2.update(team, penalty);

                    assertEquals(a, b);
                }
            }
        }
    }
}