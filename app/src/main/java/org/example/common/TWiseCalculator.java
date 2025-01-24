package org.example.common;

import java.util.ArrayList;
import java.util.List;

import de.featjar.analysis.sat4j.twise.TWiseCountComputation;
import de.featjar.analysis.sat4j.twise.TWiseCountComputation.CombinationList;
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
}
