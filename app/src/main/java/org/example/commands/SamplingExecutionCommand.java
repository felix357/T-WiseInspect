package org.example.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import org.example.SamplingAnalyzer;
import org.example.common.SamplingAlgorithm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Callable;

/**
 * Command-line interface for executing the sampling analysis process.
 * <p>
 * This command accepts input and output file paths along with sampling
 * parameters, and applies the selected sampling algorithm
 *
 * <p>
 * Usage example:
 * 
 * <pre>{@code java -jar app.jar process -i input.xml -s YASA -t 3 -o output.txt}</pre>
 * </p>
 */
@Command(name = "process", mixinStandardHelpOptions = true, version = "1.0", description = "An application that processes an input file, applies a sampling algorithm, and writes results to an output file.")
public class SamplingExecutionCommand implements Callable<Integer> {

    // Input file option
    @Option(names = { "-i", "--input-file" }, description = "The path to the input file.", required = true)
    private File inputFile;

    // Output file option
    @Option(names = { "-o",
            "--output" }, description = "Path to the output file. If not provided, the result will be printed to the console.")
    private File outputFile;

    // Sampling algorithm option
    @Option(names = { "-s",
            "--sampling-algorithm" }, description = "The sampling algorithm to use (YASA, UNIFORM, INCLING, ICPL, CHVARTAL).", required = true)
    private SamplingAlgorithm algorithm;

    // Number of configurations to generate for UNIFORM sampling.
    @Option(names = { "-c",
            "--configurations" }, description = "The number of configurations for UNIFORM sampling.", defaultValue = "10000")
    private int numberOfConfigurations;

    // Optional parameter for t-value (default value: 2)
    @Option(names = { "-t",
            "--t-value" }, description = "The t-value for t-wise sampling (default is 2).", defaultValue = "2")
    private int tValue;

    // Optional flag to write CSV summaries
    @Option(names = { "-csv" }, description = "If set, writes CSV summary output to 'results.csv'.")
    private boolean writeCsv;

    /**
     * Executes the sampling process and initializes the {@link SamplingAnalyzer}
     * with the provided configuration.
     * 
     * @return exit code: {@code 0} for success, {@code 1} for failure
     */
    @Override
    public Integer call() throws Exception {

        // Construct relevant path
        File modifiedFile = new File(System.getProperty("user.dir"), inputFile.getName());

        // 1. Input File Processing
        if (!modifiedFile.exists() || !modifiedFile.canRead()) {
            System.err.println(
                    "Error: The file " + modifiedFile.getAbsolutePath() + " does not exist or cannot be read.");
            return 1;
        }

        SamplingAnalyzer.inputDir = modifiedFile;

        System.out.println("Selected algorithm: " + algorithm);
        SamplingAnalyzer.samplingConfig.setSamplingAlgorithm(algorithm);
        SamplingAnalyzer.samplingConfig.setT(tValue);
        SamplingAnalyzer.writeCsv = writeCsv;

        // set specific values of sampling config for specific SamplingAlgorithm
        if (algorithm == SamplingAlgorithm.INCLING) {
            SamplingAnalyzer.samplingConfig.setT(2);
        }

        SamplingAnalyzer.samplingConfig.setNumberOfConfigurations(numberOfConfigurations);
        System.out.println("Using " + numberOfConfigurations + " as maximum number of configurations");

        // 3. Output Handling
        String result = "Processing result using " + algorithm + " algorithm";
        if (outputFile != null) {
            try {
                SamplingAnalyzer.outputDir = this.outputFile;
                writeToFile(outputFile, result);
                System.out.println("Result written to: " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
                return 1;
            }
        } else {
            System.out.println(result);
        }

        return 0;
    }

    /**
     * Writes the sampling info to a specified file.
     *
     * @param file   the output file to write to
     * @param result the content to write
     * @throws IOException if the file cannot be written
     */
    private void writeToFile(File file, String result) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(result);
        }
    }
}
