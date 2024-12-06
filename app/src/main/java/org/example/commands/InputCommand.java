package org.example.commands;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.Callable;

@Command(name = "example", mixinStandardHelpOptions = true, version = "1.0", description = "An application that processes an input file using a specified sampling method.")
public class InputCommand implements Callable<Integer> {

    @Option(names = { "-f", "--file" }, description = "The path to the input file.", required = true)
    private File inputFile;

    @Override
    public Integer call() throws Exception {
        if (!inputFile.exists() || !inputFile.canRead()) {
            System.err.println("Error: The file " + inputFile.getAbsolutePath() + " does not exist or cannot be read.");
            return 1;
        }

        System.out.println("Reading file: " + inputFile.getAbsolutePath());
        String content = Files.readString(inputFile.toPath());
        System.out.println("File content:");
        System.out.println(content);

        return 0;
    }

    // Test cli command
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: example --file <input_file>");
            new CommandLine(new InputCommand()).usage(System.out);
            System.exit(1);
        }

        int exitCode = new CommandLine(new InputCommand()).execute(args);
        System.exit(exitCode);
    }
}
