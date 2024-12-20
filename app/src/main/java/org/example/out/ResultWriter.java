package org.example.out;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ResultWriter {

    public static boolean writeResultToFile(File outputDir, String result) {

        System.out.println(outputDir.getAbsolutePath());

        if (!outputDir.exists() || !outputDir.isFile()) {
            System.out.println("The provided output directory does not exist or is not a file.");
            return false;
        }

        try (FileWriter writer = new FileWriter(outputDir)) {
            writer.write(result);
            System.out.println("Successfully wrote to file: " + outputDir.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to write to file: " + e.getMessage());
            return false;
        }
    }
}