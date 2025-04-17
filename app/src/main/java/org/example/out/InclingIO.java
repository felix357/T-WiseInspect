package org.example.out;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.featjar.formula.VariableMap;
import de.featjar.formula.assignment.BooleanAssignment;
import de.featjar.formula.assignment.BooleanAssignmentList;

/**
 * Utility class for managing input and output operations related to the Incling
 * tool.
 * <p>
 * This class facilitates communication between the main application and the
 * Incling JAR
 * by handling the creation of JSON input files and parsing of JSON output
 * files.
 * It abstracts the execution process and data transformation required to
 * interface with Incling.
 */
public class InclingIO {

    /**
     * Executes the Incling JAR file using expected default relative file locations
     * and waits for it to finish.
     * The JAR is expected to consume 'cnf.json' and produce 'results.json'.
     *
     * @return the {@link Path} to the results file (results.json) if the
     *         execution was successful, otherwise null
     */
    public static Path runInclingJar() {
        try {
            Path basePath = Paths.get("").toAbsolutePath();
            Path jarPath = basePath.resolve("app/libs/app-1.0.0-all.jar").normalize();
            Path cnfPath = basePath.resolve("cnf.json").normalize();
            Path resultsPath = basePath.resolve("results.json").normalize();

            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar",
                    jarPath.toString(), cnfPath.toString(), resultsPath.toString());
            processBuilder.inheritIO();

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return resultsPath;
            } else {
                System.err.println("Incling JAR execution failed with exit code: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads a list of BooleanAssignments from a JSON file produced by Incling.
     *
     * @param filePath    the path to the results JSON file
     * @param variableMap the {@link VariableMap} used to interpret literals
     * @return a {@link BooleanAssignmentList} representing all configurations in
     *         the file, or null on failure
     */
    public static BooleanAssignmentList loadAssignmentsFromJson(String filePath, VariableMap variableMap) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Map<String, Object>>>() {
            }.getType();

            List<Map<String, Object>> jsonList = gson.fromJson(reader, listType);

            Collection<BooleanAssignment> booleanAssignments = jsonList.stream()
                    .map(entry -> {
                        List<Object> literalsRaw = (List<Object>) entry.get("literals");

                        List<Integer> literals = literalsRaw.stream()
                                .map(literal -> {
                                    if (literal instanceof Double) {
                                        return ((Double) literal).intValue();

                                    } else if (literal instanceof Integer) {
                                        return (Integer) literal;
                                    }
                                    throw new IllegalArgumentException(
                                            "Unexpected literal type: "
                                                    + literal.getClass());
                                })
                                .collect(Collectors.toList());

                        return new BooleanAssignment(literals);

                    })
                    .collect(Collectors.toList());
            return new BooleanAssignmentList(variableMap, booleanAssignments);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Writes a CNF JSON file in the format expected by the Incling JAR.
     * The file includes assignments as int arrays and variable names.
     *
     * @param assignments a list of int arrays representing clauses
     * @param varNames    the list of variable names used in the feature model
     */
    public static void writeCnfJson(List<int[]> assignments, List<String> varNames) {
        Gson gson = new Gson();
        String assignmentsJson = gson.toJson(assignments);
        String varNamesJson = gson.toJson(varNames);

        try (FileWriter writer = new FileWriter("cnf.json")) {
            writer.write("{\n");
            writer.write("\"assignments\": " + assignmentsJson + ",\n");
            writer.write("\"varNames\": " + varNamesJson + "\n");
            writer.write("}");
            System.out.println("Data written to cnf.json.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
