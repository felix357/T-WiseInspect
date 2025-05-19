package org.example.common;

import de.featjar.analysis.sat4j.twise.ConstraintedCoverageComputation;
import de.featjar.analysis.sat4j.twise.CoverageStatistic;
import de.featjar.base.computation.Computations;
import de.featjar.base.data.IntegerList;
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
     * Computes the t-wise coverage statistics for a given set of sampled
     * configurations.
     *
     * <p>
     * This method uses the {@link ConstraintedCoverageComputation} from FeatJAR to
     * calculate how many t-wise feature interactions are covered in the provided
     * sample,
     * based on the CNF representation of the feature model.
     * </p>
     *
     * @param sample         the list of Boolean assignments representing the
     *                       sampled configurations
     * @param computedCNF    the CNF representation of the feature model, used to
     *                       enforce constraints
     * @param samplingConfig the configuration specifying the t-value and sampling
     *                       algorithm
     * @return a {@link CoverageStatistic} object containing details on the t-wise
     *         coverage
     */
    public static CoverageStatistic computeTWiseStatistics(BooleanAssignmentList sample,
            BooleanAssignmentList computedCNF, SamplingConfig samplingConfig) {
        CoverageStatistic statistic = Computations.of(sample)
                .map(ConstraintedCoverageComputation::new)
                .set(ConstraintedCoverageComputation.BOOLEAN_CLAUSE_LIST, computedCNF)
                .set(ConstraintedCoverageComputation.T, new IntegerList(samplingConfig.getT()))
                .compute();
        return statistic;
    }
}
