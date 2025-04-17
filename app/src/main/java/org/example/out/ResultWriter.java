package org.example.out;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.example.common.SamplingAlgorithm;

import de.featjar.analysis.sat4j.twise.CoverageStatistic;
import de.featjar.base.data.IntegerList;
import de.featjar.formula.VariableMap;
import de.featjar.formula.assignment.BooleanAssignment;
import de.featjar.formula.assignment.BooleanAssignmentList;

/**
 * Utility class for writing sampling results and coverage statistics
 * to a file.
 */
public class ResultWriter {

    /**
     * Writes sampling results and statistics to a specified output file.
     *
     * @param outputDir           the output file to write to
     * @param coreAndDeadFeatures a {@link BooleanAssignment} representing core and
     *                            dead features
     * @param sample              the sample result as a list of configurations
     * @param t                   the t-value used
     * @param samplingAlgorithm   the sampling algorithm used
     * @param coverageStatistic   the computed coverage statistics
     * @param variableMap         map containing feature variable names
     * @return {@code true} if the write operation succeeded, {@code false}
     *         otherwise
     */
    public static boolean writeResultToFile(File outputDir, BooleanAssignment coreAndDeadFeatures,
            BooleanAssignmentList sample, int t, SamplingAlgorithm samplingAlgorithm,
            CoverageStatistic coverageStatistic,
            VariableMap variableMap) {

        if (!outputDir.exists() || !outputDir.isFile()) {
            System.out.println("The provided output directory does not exist or is not a file.");
            return false;
        }

        System.out.println("Wringing results to: " + outputDir.getAbsolutePath());

        try (FileWriter writer = new FileWriter(outputDir)) {

            writer.write("Sampling Alg: " + samplingAlgorithm + ", t = " + t);

            writer.write("\nSample: " + sample.toString());

            int numberOfSamples = sample.size();

            writer.write("\nNumber of Configurations: " + numberOfSamples);

            ArrayList<Integer> features = getFeatures(sample);

            writer.write("\nFeatures: " + variableMap);

            long numberOfInvalidFeatures = coverageStatistic.invalid();

            // write coverage
            writer.write(
                    "\nT-Wise Combinations: Covered: " + coverageStatistic.covered() + "; " + "Uncovered: "
                            + coverageStatistic.uncovered()
                            + "; " + "Invalid: " + numberOfInvalidFeatures);
            writer.write("\nCoverage: " + coverageStatistic.coverage() + "\n");

            // Write down all interactions and include the number of occurrences for t = 2.
            if (t == 2) {
                HashMap<String, Integer> entries = determineEntriesT2(features, sample, true);

                // Process number of solutions per interaction
                updateEntriesWithNumberOfSolutions(sample, entries);

                writeEntriesToFile(entries, writer, numberOfInvalidFeatures);
                printFeatureInteractionsCoveredExactlyOnce(entries, writer, variableMap);
            }

            System.out.println("Successfully wrote to file: " + outputDir.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to write to file: " + e.getMessage());
            return false;
        }

    }

    /**
     * Computes all possible 2-wise feature combinations (positive and negative)
     * and initializes their counts to 0.
     *
     * @param features                           list of features
     * @param sample                             configurations to validate
     *                                           combinations against
     * @param considerInvalidFeatureInteractions if true, all combinations are
     *                                           included
     * @return a map of 2-wise feature interaction strings to their initial
     *         frequency count
     *         across configurations
     */
    private static HashMap<String, Integer> determineEntriesT2(ArrayList<Integer> features,
            BooleanAssignmentList sample, boolean considerInvalidFeatureInteractions) {
        HashMap<String, Integer> entries = new HashMap<>();

        for (int i = 0; i < features.size(); i++) {
            for (int j = i + 1; j < features.size(); j++) {
                String bothTrue = features.get(i).toString() + "," + features.get(j).toString();
                String secondTrue = "-" + features.get(i).toString() + "," + features.get(j).toString();
                String firstTrue = features.get(i).toString() + "," + "-" + features.get(j).toString();
                String bothFalse = "-" + features.get(i).toString() + "," + "-" + features.get(j).toString();

                if (considerInvalidFeatureInteractions
                        || combinationExistsInSample(sample, features.get(i), features.get(j))) {
                    entries.put(bothTrue, 0);
                }
                if (considerInvalidFeatureInteractions
                        || combinationExistsInSample(sample, -features.get(i), features.get(j))) {
                    entries.put(secondTrue, 0);
                }
                if (considerInvalidFeatureInteractions
                        || combinationExistsInSample(sample, features.get(i), -features.get(j))) {
                    entries.put(firstTrue, 0);
                }
                if (considerInvalidFeatureInteractions
                        || combinationExistsInSample(sample, -features.get(i), -features.get(j))) {
                    entries.put(bothFalse, 0);
                }
            }
        }
        return entries;
    }

    /**
     * Checks if a combination of two features exists in any configuration.
     *
     * @param sample   the list of configurations
     * @param feature1 the first feature
     * @param feature2 the second feature
     * @return true if the combination exists in at least one configuration
     */
    private static boolean combinationExistsInSample(BooleanAssignmentList sample, int feature1,
            int feature2) {
        for (BooleanAssignment configuration : sample) {
            if (configuration.contains(feature1) && configuration.contains(feature2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Writes the frequency of covered feature interactions to the result file.
     *
     * @param entries                 map of interactions and counts
     * @param writer                  file writer to write the output
     * @param numberOfInvalidFeatures the number of invalid features
     * @throws IOException if writing fails
     */
    private static void writeEntriesToFile(HashMap<String, Integer> entries, FileWriter writer,
            long numberOFInvalidFeatures)
            throws IOException {

        // Count the number of entries by value
        HashMap<Integer, Long> valueCounts = new HashMap<>();
        for (Integer value : entries.values()) {
            valueCounts.put(value, valueCounts.getOrDefault(value, (long) 0) + 1);
        }

        // Reduce number of not covered features by the number of invalid features.
        valueCounts.put(0, valueCounts.get(0) - numberOFInvalidFeatures);

        // Build a summary string
        StringBuilder sb = new StringBuilder();
        sb.append("Feature interaction coverage (t=2):\n");
        valueCounts.forEach(
                (value, count) -> sb
                        .append(String.format("Covered: %d Number of Feature Interactions: %d\n", value, count)));

        // Write the summary to the file
        writer.write(sb.toString());
    }

    /**
     * Writes a list of feature interactions that were covered exactly once.
     *
     * @param entries     map of interactions and counts
     * @param writer      file writer to write the output
     * @param variableMap map used to resolve feature names
     * @throws IOException if writing fails
     */
    private static void printFeatureInteractionsCoveredExactlyOnce(HashMap<String, Integer> entries,
            FileWriter writer,
            VariableMap variableMap)
            throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("List of feature interactions covered exactly once:\n");

        for (Map.Entry<String, Integer> entry : entries.entrySet()) {
            if (entry.getValue() == 1) {
                String key = entry.getKey();

                String[] parts = key.split(",");
                int number1 = Integer.parseInt(parts[0]);
                int number2 = Integer.parseInt(parts[1]);

                IntegerList indices = new IntegerList(Arrays.asList(number1, number2));
                String featureInteraction = variableMap.getVariableNames(indices).toString();

                stringBuilder.append(featureInteraction + ", ");
            }
        }

        writer.write(stringBuilder.toString());
    }

    /**
     * Updates the map of 2-wise feature combinations with actual occurrence counts
     * from the sample.
     *
     * @param sample  the list of configurations
     * @param entries the map of combinations to be updated
     */
    private static void updateEntriesWithNumberOfSolutions(BooleanAssignmentList sample,
            HashMap<String, Integer> entries) {
        for (BooleanAssignment config : sample) {
            for (int i = 0; i < config.size(); i++) {
                for (int j = i + 1; j < config.size(); j++) {
                    String key = config.get(i) + "," + config.get(j);
                    entries.computeIfPresent(key, (k, v) -> v + 1);
                }
            }
        }
    }

    /**
     * Extracts a list of feature variable IDs from the sample.
     *
     * @param booleanSolutionList list of sampled configurations
     * @return a list of integer feature IDs
     */
    private static ArrayList<Integer> getFeatures(BooleanAssignmentList booleanSolutionList) {
        int[] features = booleanSolutionList.getAll().get(0).getAbsoluteValues();

        ArrayList<Integer> relevantFeatures = new ArrayList<>();
        for (int i : features) {
            relevantFeatures.add(i);
        }
        return relevantFeatures;
    }

}