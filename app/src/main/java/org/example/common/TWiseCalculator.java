package org.example.common;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.featjar.analysis.sat4j.computation.MIGBuilder;
import de.featjar.analysis.sat4j.computation.TWiseCombinations.TWiseCombinationsList;
import de.featjar.analysis.sat4j.solver.ModalImplicationGraph;
import de.featjar.analysis.sat4j.twise.CoverageStatistic;
import de.featjar.analysis.sat4j.twise.TWiseCountComputation;
import de.featjar.analysis.sat4j.twise.TWiseCountComputation.CombinationList;
import de.featjar.analysis.sat4j.twise.TWiseCoverageComputation;
import de.featjar.base.computation.Computations;
import de.featjar.base.computation.Progress;
import de.featjar.formula.VariableMap;
import de.featjar.formula.assignment.BooleanAssignment;
import de.featjar.formula.assignment.BooleanAssignmentList;

/**
 * Utility class for calculating t-wise coverage statistics in feature model
 * sampling.
 * <p>
 * Provides methods to:
 * <ul>
 * <li>Compute the number of t-wise combinations covered by a sample</li>
 * <li>Evaluate the overall t-wise coverage statistics of a given sample</li>
 * </ul>
 */
public class TWiseCalculator {

    /**
     * Computes the number of valid t-wise combinations that are covered by the
     * given sample.
     *
     * @param result            The sample containing Boolean assignments
     *                          (configurations).
     * @param tValue            The value of 't' (e.g., 2 for pairwise).
     * @param variableFilter    A filter used to limit the variables included in the
     *                          combinations.
     * @param booleanClauseList The list of clauses representing the feature model
     *                          in CNF.
     * @param combinations      A list of variable index combinations to evaluate.
     * @return The number of covered t-wise combinations that satisfy the
     *         constraints.
     */
    public static Long computeTWiseCount(BooleanAssignmentList result, int tValue, BooleanAssignment variableFilter,
            BooleanAssignmentList booleanClauseList, List<int[]> combinations) {
        CombinationList combinationList = CombinationList.of(combinations);

        List<Object> tWiseCountdependencyList = new ArrayList<>();
        tWiseCountdependencyList.add(result); // samples
        tWiseCountdependencyList.add(tValue); // t value
        tWiseCountdependencyList.add(variableFilter); // variable filter
        tWiseCountdependencyList.add(combinationList); // combination filter

        TWiseCountComputation twiseCountComputation = new TWiseCountComputation(Computations.of(booleanClauseList));
        Long number = twiseCountComputation.compute(tWiseCountdependencyList, new Progress()).get();
        return number;
    }

    /**
     * Computes detailed t-wise coverage statistics for a sample, including covered,
     * uncovered, and invalid combinations.
     *
     * @param booleanAssignmentList The full list of feature model constraints in
     *                              CNF.
     * @param core                  The core assignment representing mandatory
     *                              features.
     * @param result                The generated sample of configurations.
     * @param variables             The variable map for resolving feature names and
     *                              indices.
     * @param tValue                The desired level of interaction coverage (e.g.,
     *                              2 for pairwise).
     * @return A {@link CoverageStatistic} containing coverage metrics for the
     *         sample.
     */
    public static CoverageStatistic computeTWiseStatistics(BooleanAssignmentList booleanAssignmentList,
            BooleanAssignment core, BooleanAssignmentList result, VariableMap variables, Integer tValue) {
        BooleanAssignment variableFilter = new BooleanAssignment(new int[] {});
        Duration duration = Duration.ofMinutes(2);
        Random random = new Random();
        Long randomSeed = random.nextLong(Long.MAX_VALUE);

        MIGBuilder migBuilder = new MIGBuilder(Computations.of(booleanAssignmentList));
        List<Object> depList = new ArrayList<>();
        depList.add(booleanAssignmentList);
        depList.add(core);
        ModalImplicationGraph modalImplGraph = migBuilder.compute(depList, new Progress()).get();

        List<Integer> s = variables.getVariableIndices();
        BooleanAssignment var = new BooleanAssignment(s);
        TWiseCombinationsList combinationsList = new TWiseCombinationsList(var, tValue);

        List<Object> dependencyList = new ArrayList<>();
        dependencyList.add(booleanAssignmentList);
        dependencyList.add(core);
        dependencyList.add(booleanAssignmentList); // ASSUMED_CLAUSE_LIST
        dependencyList.add(duration);
        dependencyList.add(randomSeed);
        dependencyList.add(result);
        dependencyList.add(tValue);
        dependencyList.add(combinationsList);
        dependencyList.add(modalImplGraph);
        dependencyList.add(variableFilter);

        TWiseCoverageComputation tWiseCoverageComputation = new TWiseCoverageComputation(
                Computations.of(booleanAssignmentList));

        CoverageStatistic statistics = tWiseCoverageComputation.compute(dependencyList, new Progress()).get();
        return statistics;
    }
}
