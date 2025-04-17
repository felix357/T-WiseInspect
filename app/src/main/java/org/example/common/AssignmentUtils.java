package org.example.common;

import java.util.List;
import java.util.stream.Collectors;

import de.featjar.formula.assignment.BooleanAssignment;

public class AssignmentUtils {

    public static List<int[]> convertToIntArrays(List<BooleanAssignment> assignments) {
        return assignments.stream().map(BooleanAssignment::simplify).collect(Collectors.toList());
    }
}
