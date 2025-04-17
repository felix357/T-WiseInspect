package org.example.common;

import java.util.List;
import java.util.stream.Collectors;

import de.featjar.formula.assignment.BooleanAssignment;

/**
 * Utility class for operations related to {@link BooleanAssignment} handling
 * and transformation.
 */
public class AssignmentUtils {

    /**
     * Converts a list of {@link BooleanAssignment} objects into a list of integer
     * arrays.
     * <p>
     * This is especially useful for serializing or interfacing with tools that
     * operate on raw arrays.
     * </p>
     *
     * @param assignments the list of {@code BooleanAssignment} instances to be
     *                    converted
     * @return a list of integer arrays, each representing a simplified feature
     *         assignment
     **/
    public static List<int[]> convertToIntArrays(List<BooleanAssignment> assignments) {
        return assignments.stream().map(BooleanAssignment::simplify).collect(Collectors.toList());
    }
}
