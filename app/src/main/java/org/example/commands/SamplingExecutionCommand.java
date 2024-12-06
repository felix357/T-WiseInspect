package org.example.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import org.example.SamplingAnalyzer;
import org.example.common.SamplingAlgorithm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.concurrent.Callable;

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
            "--sampling-algorithm" }, description = "The sampling algorithm to use (YASA, ICPL, Chvatal).", required = true)
    private SamplingAlgorithm algorithm;

    @Override
    public Integer call() throws Exception {
        // 1. Input File Processing
        if (!inputFile.exists() || !inputFile.canRead()) {
            System.err.println("Error: The file " + inputFile.getAbsolutePath() + " does not exist or cannot be read.");
            return 1;
        }

        System.out.println("Reading file: " + inputFile.getAbsolutePath());
        String content = Files.readString(inputFile.toPath());
        System.out.println("File content:\n" + content);

        // 2. Algorithm Processing
        System.out.println("Selected algorithm: " + algorithm);
        switch (algorithm) {
            case YASA:
                SamplingAnalyzer.samplingConfig.setSamplingAlgorithm(algorithm);
                processYASA();
                break;
            case ICPL:
                processICPL();
                break;
            case CHVATAL:
                processChvatal();
                break;
        }

        // 3. Output Handling
        String result = "Processing result using " + algorithm + " algorithm";
        if (outputFile != null) {
            try {
                writeToFile(outputFile, result);
                System.out.println("Result written to: " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
                return 1;
            }
        } else {
            printToConsole(result);
        }

        return 0;
    }

    private void processYASA() {
        System.out.println("Processing with YASA sampling algorithm...");
    }

    private void processICPL() {
        System.out.println("Processing with ICPL sampling algorithm...");
    }

    private void processChvatal() {
        System.out.println("Processing with Chvatal sampling algorithm...");
    }

    private void printToConsole(String result) {
        System.out.println(result);
    }

    private void writeToFile(File file, String result) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(result);
        }
    }
}
