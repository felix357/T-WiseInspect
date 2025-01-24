package org.example.common;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import de.featjar.analysis.sat4j.computation.MIGBuilder;
import de.featjar.analysis.sat4j.solver.ModalImplicationGraph;
import de.featjar.analysis.sat4j.twise.CoverageStatistic;
import de.featjar.analysis.sat4j.twise.TWiseCountComputation;
import de.featjar.analysis.sat4j.twise.TWiseCountComputation.CombinationList;
import de.featjar.analysis.sat4j.twise.TWiseCoverageComputation;
import de.featjar.base.computation.Computations;
import de.featjar.base.computation.Progress;
import de.featjar.formula.assignment.BooleanAssignment;
import de.featjar.formula.assignment.BooleanAssignmentList;
import de.featjar.formula.assignment.BooleanClauseList;
import de.featjar.formula.assignment.BooleanSolutionList;

public class TWiseCalculator {
    public static Long computeTWiseCount(BooleanSolutionList result, int tValue, BooleanAssignment variableFilter,
            BooleanClauseList booleanClauseList, List<int[]> combinations) {
        CombinationList combinationList = CombinationList.of(combinations);

        BooleanAssignmentList samplingAlgBoolAssignments = result.toAssignmentList();
        System.out.println(samplingAlgBoolAssignments);
        List<Object> tWiseCountdependencyList = new ArrayList<>();
        tWiseCountdependencyList.add(samplingAlgBoolAssignments); // samples
        tWiseCountdependencyList.add(tValue); // t value
        tWiseCountdependencyList.add(variableFilter); // variable filter
        tWiseCountdependencyList.add(combinationList); // combination filter

        TWiseCountComputation twiseCountComputation = new TWiseCountComputation(Computations.of(booleanClauseList));
        Long number = twiseCountComputation.compute(tWiseCountdependencyList, new Progress()).get();
        return number;
    }

    public static CoverageStatistic computeTWiseStatistics(BooleanClauseList booleanClauseList, BooleanAssignment core,
            BooleanSolutionList result, Integer tValue) {
        BooleanAssignment variableFilter = new BooleanAssignment(new int[] {});
        Duration duration = Duration.ofMinutes(1);
        Long randomSeed = 1234567890123456789L;

        MIGBuilder migBuilder = new MIGBuilder(Computations.of(booleanClauseList));
        List<Object> depList = new ArrayList<>();
        depList.add(booleanClauseList);
        depList.add(core);
        ModalImplicationGraph modalImplGraph = migBuilder.compute(depList, new Progress()).get();

        List<Object> dependencyList = new ArrayList<>();
        dependencyList.add(booleanClauseList); // CNF clauses (check)
        dependencyList.add(core); // core (check)
        dependencyList.add(booleanClauseList); // ASSUMED_CLAUSE_LIST (might be wrong)
        dependencyList.add(duration); // duration check
        dependencyList.add(randomSeed); // randomSeed check
        dependencyList.add(tValue); // tValue check check
        dependencyList.add(modalImplGraph); // modalImplicationGraph (might be wrong)
        dependencyList.add(variableFilter); // variable Filter check
        dependencyList.add(result); // sample check

        TWiseCoverageComputation tWiseCoverageComputation = new TWiseCoverageComputation(
                Computations.of(booleanClauseList));

        CoverageStatistic statistics = tWiseCoverageComputation.compute(dependencyList, new Progress()).get();
        return statistics;
    }
}
