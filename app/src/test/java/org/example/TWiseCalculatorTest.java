package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.example.common.TWiseCalculator;
import org.junit.jupiter.api.Test;

import de.featjar.analysis.sat4j.twise.CoverageStatistic;
import de.featjar.formula.VariableMap;
import de.featjar.formula.assignment.BooleanAssignment;
import de.featjar.formula.assignment.BooleanAssignmentList;

public class TWiseCalculatorTest {

        @Test
        void testBooleanAssignmentListCreation() {
                BooleanAssignment a1 = new BooleanAssignment(+1, -2, -3, -4, +5, +6, -7, +8, -9, -10);
                BooleanAssignment a2 = new BooleanAssignment(+1, +2, +3, -4, -5, -6, -7, -8, -9, -10);
                BooleanAssignment a3 = new BooleanAssignment(+1, +2, +3, +4, +5, +6, +7, +8, +9, -10);
                BooleanAssignment a4 = new BooleanAssignment(+1, +2, -3, +4, -5, +6, -7, +8, +9, +10);
                BooleanAssignment a5 = new BooleanAssignment(+1, +2, +3, +4, +5, +6, +7, +8, +9, +10);
                BooleanAssignment a6 = new BooleanAssignment(+1, +2, +3, +4, -5, -6, -7, -8, -9, -10);
                BooleanAssignment a7 = new BooleanAssignment(+1, +2, +3, +4, -5, +6, +7, -8, -9, +10);
                BooleanAssignment a8 = new BooleanAssignment(+1, -2, -3, -4, -5, +6, +7, -8, +9, +10);
                BooleanAssignment a9 = new BooleanAssignment(+1, -2, -3, -4, +5, -6, -7, -8, -9, -10);

                VariableMap variableMap = VariableMap
                                .of(Arrays.asList("Email", "Functions", "Forward", "Reply", "Addressbook",
                                                "Encryption", "Sign", "Verify", "Encryp", "Decrypt"));

                BooleanAssignmentList sample = new BooleanAssignmentList(variableMap,
                                Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, a9));

                BooleanAssignment b1 = new BooleanAssignment(+1);
                BooleanAssignment b2 = new BooleanAssignment(-2, +1);
                BooleanAssignment b3 = new BooleanAssignment(-3, +2);
                BooleanAssignment b4 = new BooleanAssignment(-4, +2);
                BooleanAssignment b5 = new BooleanAssignment(-2, +3, +4);
                BooleanAssignment b6 = new BooleanAssignment(-5, +1);
                BooleanAssignment b7 = new BooleanAssignment(-6, +1);
                BooleanAssignment b8 = new BooleanAssignment(-7, +6);
                BooleanAssignment b9 = new BooleanAssignment(-8, +6);
                BooleanAssignment b10 = new BooleanAssignment(-9, +6);
                BooleanAssignment b11 = new BooleanAssignment(-10, +6);
                BooleanAssignment b12 = new BooleanAssignment(-6, +7, +8, +9, +10);

                BooleanAssignmentList computedCNF = new BooleanAssignmentList(variableMap,
                                Arrays.asList(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12));

                BooleanAssignment core = new BooleanAssignment(+1);

                CoverageStatistic coverageStatistic = TWiseCalculator.computeTWiseStatistics(computedCNF, core, sample,
                                variableMap,
                                2);

                assertEquals(1.0, coverageStatistic.coverage());

        }
}
