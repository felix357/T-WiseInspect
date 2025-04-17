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

public class SamplingProcessor {

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

    private static BooleanAssignmentList processYasaSampling(BooleanAssignmentList computedCNF, int T) {
        return new YASA(Computations.of(computedCNF)).set(YASA.T, T).compute();
    }

    private static BooleanAssignmentList processUniformSampling(BooleanAssignmentList computedCNF,
            int numberOfSamples) {
        BooleanAssignmentGroups groups = new BooleanAssignmentGroups(computedCNF);
        try (DdnnifeWrapper solver = new DdnnifeWrapper(groups)) {
            return solver.getRandomSolutions(numberOfSamples, new Random().nextLong()).get();
        } catch (Exception e) {
            throw new RuntimeException("Uniform sampling failed", e);
        }
    }

    private static BooleanAssignmentList processInclingSampling(BooleanAssignmentList computedCNF,
            VariableMap variables) {
        List<int[]> assignments = AssignmentUtils.convertToIntArrays(computedCNF.getAll());
        InclingIO.writeCnfJson(assignments, variables.getVariableNames());

        Path result = InclingIO.runInclingJar();
        return result != null ? InclingIO.loadAssignmentsFromJson(result.toString(), variables) : null;
    }
}
