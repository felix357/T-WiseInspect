package org.example;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.example.commands.SamplingExecutionCommand;
import org.example.common.SamplingAlgorithm;
import org.example.common.SamplingConfig;
import org.example.common.SamplingProcessor;
import org.example.common.TWiseCalculator;
import org.example.out.ResultWriter;
import org.example.parsing.FeatureModelParser;
import de.featjar.analysis.sat4j.computation.ComputeCoreDeadMIG;
import de.featjar.analysis.sat4j.twise.CoverageStatistic;
import de.featjar.base.computation.Computations;
import de.featjar.formula.VariableMap;
import de.featjar.formula.assignment.BooleanAssignment;
import de.featjar.formula.assignment.BooleanAssignmentList;
import de.featjar.formula.assignment.ComputeBooleanClauseList;
import de.featjar.formula.computation.ComputeCNFFormula;
import de.featjar.formula.computation.ComputeNNFFormula;
import de.featjar.formula.structure.IFormula;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Main class for performing feature model sampling analysis using
 * various algorithms like YASA, Uniform, and Incling.
 * <p>
 * This commandline application parses a feature model, computes its CNF
 * representation,
 * performs sampling according to the chosen algorithm and parameters,
 * calculates the coverage statistics, and writes the result
 * to a file.
 * </p>
 */
@Command(description = "Main application for sampling Analysis.")
public class SamplingAnalyzer {

        // Configuration for sampling, including algorithm and t-value.
        public static SamplingConfig samplingConfig = new SamplingConfig(SamplingAlgorithm.YASA, 2);
        // Input directory containing the feature model file.
        public static File inputDir;
        // Output directory where result files will be written.
        public static File outputDir;
        // Flag indicating whether a CSV report should be generated
        public static boolean writeCsv = false;

        /**
         * Entry point of the application.
         *
         * @param args the command-line arguments are handled via Picocli
         */
        public static void main(String[] args) {
                CommandLine commandLine = new CommandLine(new SamplingAnalyzer());
                commandLine.addSubcommand(new SamplingExecutionCommand());
                int exitCode = commandLine.execute(args);

                // Parse feature model from XML
                FeatureModelParser.initializeFeatJAR();
                IFormula formula = FeatureModelParser.convertXMLToFormula(inputDir.toPath().toString());

                // Compute CNF representation of the model
                BooleanAssignmentList computedCNF = Computations.of(formula)
                                .map(ComputeNNFFormula::new)
                                .map(ComputeCNFFormula::new)
                                .map(ComputeBooleanClauseList::new)
                                .compute();

                // Extract variable map
                VariableMap variables = computedCNF.getVariableMap();

                // Perform sampling using configured algorithm
                BooleanAssignmentList sample = SamplingProcessor.process(samplingConfig, computedCNF, variables);
                List<BooleanAssignment> assignments = sample.getAll();

                if (SamplingAnalyzer.writeCsv) {
                        try {
                                ResultWriter.writeBatchSummaries(assignments, variables, computedCNF, samplingConfig,
                                                sample,
                                                new File("results.csv"));
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }

                CoverageStatistic statistic = TWiseCalculator.computeTWiseStatistics(sample,
                                computedCNF,
                                samplingConfig);

                // Compute core features
                BooleanAssignment coreAndDead = Computations.of(computedCNF).map(ComputeCoreDeadMIG::new).compute();

                // Write results to output directory
                ResultWriter.writeResultToFile(outputDir,
                                coreAndDead, sample,
                                samplingConfig.getT(),
                                samplingConfig.getSamplingAlgorithm(), statistic, variables);

                System.exit(exitCode);
        }
}