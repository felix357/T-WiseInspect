package org.example.out;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.example.common.SamplingAlgorithm;

import de.featjar.formula.assignment.BooleanClause;
import de.featjar.formula.assignment.BooleanSolution;
import de.featjar.formula.assignment.BooleanSolutionList;

public class ResultWriter {

    public static boolean writeResultToFile(File outputDir, BooleanSolutionList booleanSolutionList,
            ArrayList<BooleanClause> bc, int t, SamplingAlgorithm samplingAlgorithm) {

        if (!outputDir.exists() || !outputDir.isFile()) {
            System.out.println("The provided output directory does not exist or is not a file.");
            return false;
        }

        System.out.println("Wringing results to: " + outputDir.getAbsolutePath());

        try (FileWriter writer = new FileWriter(outputDir)) {

            writer.write("Sampling Alg: " + samplingAlgorithm + ", t = " + t);

            writer.write("\n" + booleanSolutionList.toString());

            int numberOfSamples = booleanSolutionList.size();

            writer.write("\nNumber of Samples: " + numberOfSamples);

            // Get relevant BooleanClauses and relevant numbers
            ArrayList<Integer> relevantNumbers = getRelevantNumbers(bc);

            writer.write("\nRelevant features: " + relevantNumbers);

            HashMap<String, Integer> entries = determineEntriesT2(relevantNumbers);

            // Process number of solutions per interaction
            updateEntriesWithNumberOfSolutions(booleanSolutionList, entries);

            // Calculate coverage and write it
            double coverage = determineCoverage(entries);
            writer.write("\nCoverage: " + coverage);

            // Write down all interactions and include the number of occurrences.
            writeEntriesToFile(entries, writer);

            System.out.println("Successfully wrote to file: " + outputDir.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to write to file: " + e.getMessage());
            return false;
        }

    }

    // determines all relevant entries for t = 2
    private static HashMap<String, Integer> determineEntriesT2(ArrayList<Integer> relevantNumbers) {
        HashMap<String, Integer> entries = new HashMap<>();

        for (int i = 0; i < relevantNumbers.size(); i++) {
            for (int j = i + 1; j < relevantNumbers.size(); j++) {
                String a = relevantNumbers.get(i).toString() + "," + relevantNumbers.get(j).toString();
                entries.put(a, 0);
                String b = "-" + relevantNumbers.get(i).toString() + "," + relevantNumbers.get(j).toString();
                entries.put(b, 0);
                String c = relevantNumbers.get(i).toString() + "," + "-" + relevantNumbers.get(j).toString();
                entries.put(c, 0);
                String d = "-" + relevantNumbers.get(i).toString() + "," + "- " + relevantNumbers.get(j).toString();
                entries.put(d, 0);
            }
        }
        return entries;
    }

    private static double determineCoverage(HashMap<String, Integer> entries) {
        AtomicInteger count = new AtomicInteger(0);

        entries.forEach((key, value) -> {
            if (value > 0) {
                count.incrementAndGet();
            }
        });

        return (double) count.get() / (double) entries.size();
    }

    private static void writeEntriesToFile(HashMap<String, Integer> entries, FileWriter writer) throws IOException {
        StringBuilder sb = new StringBuilder();
        entries.forEach((key, value) -> sb.append(String.format("\n%s: %d", key, value)));
        writer.write(sb.toString());
    }

    private static void updateEntriesWithNumberOfSolutions(BooleanSolutionList booleanSolutionList,
            HashMap<String, Integer> entries) {
        for (BooleanSolution solution : booleanSolutionList) {
            for (int i = 0; i < solution.size(); i++) {
                for (int j = i + 1; j < solution.size(); j++) {
                    String key = solution.get(i) + "," + solution.get(j);
                    entries.computeIfPresent(key, (k, v) -> v + 1);
                }
            }
        }
    }

    private static ArrayList<Integer> getRelevantNumbers(ArrayList<BooleanClause> bc) {
        ArrayList<Integer> relevantNumbers = new ArrayList<>();

        // Collect non-singleton clauses and their values
        for (BooleanClause clause : bc) {
            if (clause.size() != 1) {
                int[] nonZeroValues = clause.getNonZeroValues();
                for (int value : nonZeroValues) {
                    relevantNumbers.add(value);
                }
            }
        }
        return relevantNumbers;
    }

}