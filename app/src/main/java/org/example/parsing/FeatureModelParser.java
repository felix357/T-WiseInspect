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

public class FeatureModelParser {

    public static IFormula convertXMLToFormula(String modelPath) {
        return load(modelPath, FormulaFormats.getInstance());
    }

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

    public static <T> T load(String modelPath, IFormatSupplier<T> formatSupplier) {
        // Try loading the file directly from the given path (absolute or relative)
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
