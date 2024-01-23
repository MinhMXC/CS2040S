public class Optional {
    public static int approximationMinimumLoad(int[] jobSizes, int p) {
        if (jobSizes.length == 0 || p <= 0)
            return -1;

        //Allocating jobs to processors
        int[] processors = new int[p];
        for (int i = 0; i < jobSizes.length; i++)
            processors[findLowestLoadedProcessor(processors)] += jobSizes[i];

        //Find the minimum workload needed (i.e. finding the maximum amount of work on one processor)
        int minimum = 0;
        for (int workload: processors)
            if (workload > minimum)
                minimum = workload;

        return minimum;
    }

    private static int findLowestLoadedProcessor(int[] processors) {
        int smallest = processors[0];
        int smallestIndex = 0;

        for (int i = 0; i < processors.length; i++)
            if (smallest > processors[i]) {
                smallestIndex = i;
                smallest = processors[i];
            }

        return smallestIndex;
    }

    public static void main(String[] args) {
        System.out.println(approximationMinimumLoad(new int[]{1, 3, 5, 7, 9, 11, 10, 8, 6, 4}, 1));
    }

    /*
    Proof (CS1231S Time)

    Let L be the load of the LARGEST job size
    Let O be the OPTIMAL minimum possible load
    Let R be result of the above greedy algorithm
    Let S be the sum of all job sizes
    Let p be the number of processors

    1. Note that O >= L, else the largest job cannot be done;
    2. Also note that O >= S / p;
    3. In the above greedy algorithm, the difference between the processor with the highest load
       and the processor with the lowest load can never be more than L. The difference is L when
       the largest job is assigned last to a group of processors that all have the same load. A difference
       larger than L would imply that one of the processor had a lower load than the rest and the allocation
       went wrong;
    4. Notice that the load of the highest load processor = R;
    5. Let M be the load of the processor with the lowest load in the greedy algorithm;
    6. Thus, R - M <= L; (from 3)
             R - M <= O; (from 1)
             R <= O + M;
    7. Notice that M * p <= S (by definition of M) and thus, M <= S / p <= O; (from 2)
    8. Thus, R <= O + M <= O + O <= 2 * O; (proven)
    9. Thus, the above algorithm guarantees a 2-approximation;
    */

}
