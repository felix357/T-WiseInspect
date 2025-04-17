package org.example.common;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import org.example.out.InclingIO;

import de.featjar.analysis.ddnnife.solver.DdnnifeWrapper;
import de.featjar.analysis.sat4j.computation.YASA;
import de.featjar.base.computation.Computations;
import de.featjar.formula.VariableMap;
import de.featjar.formula.assignment.BooleanAssignmentGroups;
import de.featjar.formula.assignment.BooleanAssignmentList;

/**
 * Utility class for executing different feature model sampling strategies.
 * <p>
 * Supports the following sampling algorithms:
 * <ul>
 * <li><b>YASA</b> – YASA algorithm</li>
 * <li><b>Uniform</b> – Uniform random sampling algorithm</li>
 * <li><b>Incling</b> – Incling algorithm</li>
 * </ul>
 * <p>
 * Based on the provided {@link SamplingConfig}, this class dispatches to the
 * appropriate sampling method
 * and returns the generated {@link BooleanAssignmentList}.
 */
public class SamplingProcessor {

    /**
     * Dispatches to the appropriate sampling strategy based on the configuration.
     *
     * @param config      The sampling configuration including algorithm, t-value,
     *                    or sample size.
     * @param computedCNF The feature model represented as a BooleanAssignmentList
     *                    (CNF clauses).
     * @param variables   The variable map for resolving variable names and indices.
     * @return A {@link BooleanAssignmentList} containing the sampled
     *         configurations.
     */
    public static BooleanAssignmentList process(SamplingConfig config, BooleanAssignmentList computedCNF,
            VariableMap variables) {
        switch (config.getSamplingAlgorithm()) {
            case YASA:
                return processYasaSampling(computedCNF, config.getT());
            case UNIFORM:
                return processUniformSampling(computedCNF, config.getNumberOfConfigurations());
            case INCLING:
                return processInclingSampling(computedCNF, variables);
            default:
                throw new UnsupportedOperationException("Unsupported sampling algorithm.");
        }
    }

    /**
     * Executes t-wise sampling using the YASA algorithm.
     *
     * @param computedCNF The feature model in CNF form.
     * @param T           The t-value (e.g., 2 for pairwise feature interactions).
     * @return A list of configurations satisfying t-wise coverage.
     */
    private static BooleanAssignmentList processYasaSampling(BooleanAssignmentList computedCNF, int T) {
        return new YASA(Computations.of(computedCNF)).set(YASA.T, T).compute();
    }

    /**
     * Executes uniform random sampling.
     *
     * @param computedCNF     The feature model in CNF form.
     * @param numberOfSamples The number of random configurations to generate.
     * @return A list of randomly sampled valid configurations.
     * @throws RuntimeException if sampling fails due to solver error.
     */
    private static BooleanAssignmentList processUniformSampling(BooleanAssignmentList computedCNF,
            int numberOfSamples) {
        BooleanAssignmentGroups groups = new BooleanAssignmentGroups(computedCNF);
        try (DdnnifeWrapper solver = new DdnnifeWrapper(groups)) {
            return solver.getRandomSolutions(numberOfSamples, new Random().nextLong()).get();
        } catch (Exception e) {
            throw new RuntimeException("Uniform sampling failed", e);
        }
    }

    /**
     * Executes external Incling sampling.
     * <p>
     * Writes the CNF to a JSON file, runs the Incling JAR, and loads the resulting
     * configurations.
     *
     * @param computedCNF The feature model in CNF form.
     * @param variables   The variable map used for resolving variable names.
     * @return A list of configurations sampled by Incling, or {@code null} if
     *         execution fails.
     */
    private static BooleanAssignmentList processInclingSampling(BooleanAssignmentList computedCNF,
            VariableMap variables) {
        List<int[]> assignments = AssignmentUtils.convertToIntArrays(computedCNF.getAll());
        InclingIO.writeCnfJson(assignments, variables.getVariableNames());

        Path result = InclingIO.runInclingJar();
        return result != null ? InclingIO.loadAssignmentsFromJson(result.toString(), variables) : null;
    }
}
