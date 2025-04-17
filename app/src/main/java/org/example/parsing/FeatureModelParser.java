package org.example.parsing;

import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.structure.IFormula;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import de.featjar.base.FeatJAR;
import de.featjar.base.computation.Cache;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.base.io.format.IFormatSupplier;
import de.featjar.base.log.CallerFormatter;
import de.featjar.base.log.Log;
import de.featjar.base.log.TimeStampFormatter;
import de.featjar.base.log.VerbosityFormatter;

/**
 * Utility class for parsing feature models and initializing the FeatJAR
 * framework.
 * <p>
 * Provides methods for loading feature models from XML files and converting
 * them into {@link IFormula} objects.
 * </p>
 */
public class FeatureModelParser {

    /**
     * Loads and converts a feature model in XML format to an {@link IFormula}.
     *
     * @param modelPath the path to the XML model file
     * @return the parsed feature model as an {@link IFormula}
     * @throws RuntimeException if the file cannot be found or parsed
     */
    public static IFormula convertXMLToFormula(String modelPath) {
        return load(modelPath, FormulaFormats.getInstance());
    }

    /**
     * Initializes the FeatJAR framework with predefined logging and caching
     * settings.
     * <p>
     * - Logs INFO, MESSAGE, DEBUG to stdout<br>
     * - Logs WARNING and ERROR to stderr<br>
     * </p>
     */
    public static void initializeFeatJAR() {
        FeatJAR.configure()
                .log(c -> c.logToSystemOut(Log.Verbosity.MESSAGE, Log.Verbosity.INFO, Log.Verbosity.DEBUG)
                        .logToSystemErr(Log.Verbosity.ERROR, Log.Verbosity.WARNING)
                        .addFormatter(new TimeStampFormatter())
                        .addFormatter(new VerbosityFormatter())
                        .addFormatter(new CallerFormatter()))
                .cache(c -> c.setCachePolicy(Cache.CachePolicy.CACHE_NONE))
                .initialize();
    }

    /**
     * Loads a resource from the file system and parses it using the provided format
     * supplier.
     *
     * @param <T>            the type of object to load (e.g. {@link IFormula})
     * @param modelPath      the absolut path to the model file
     * @param formatSupplier the format supplier for interpreting the file format
     * @return the loaded object of type T
     * @throws RuntimeException if the file cannot be found, accessed, or parsed
     */
    public static <T> T load(String modelPath, IFormatSupplier<T> formatSupplier) {
        // Try loading the file directly from the given absolut path
        File file = new File(modelPath);

        // Check if the file exists and is a valid file
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException("Datei nicht gefunden: " + modelPath);
        }

        try {
            // Convert the file to URL
            URL fileURL = file.toURI().toURL();

            // Load the resource using the provided formatSupplier
            Result<T> loadResult = IO.load(fileURL, formatSupplier);

            if (!loadResult.isPresent()) {
                throw new RuntimeException("Fehler beim Laden der Datei: " + loadResult.getProblems());
            }

            return loadResult.get();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Ungültiger Dateipfad: " + modelPath, e);
        }
    }
}
