package org.example.parsing;

import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.structure.IFormula;
import java.net.URL;

import de.featjar.base.FeatJAR;
import de.featjar.base.computation.Cache;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.base.io.format.IFormat;
import de.featjar.base.io.format.IFormatSupplier;
import de.featjar.base.log.CallerFormatter;
import de.featjar.base.log.Log;
import de.featjar.base.log.TimeStampFormatter;
import de.featjar.base.log.VerbosityFormatter;

public class FeatureModelParser {

    public static IFormula convertXMLToFormula(String modelPath) {
        initializeFeatJAR();

        return load(modelPath, FormulaFormats.getInstance());
    }

    private static void initializeFeatJAR() {
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
        URL systemResource = ClassLoader.getSystemResource(modelPath);
        if (systemResource == null) {
            throw new RuntimeException("Datei nicht gefunden: " + modelPath);
        }
        Result<T> loadResult = IO.load(systemResource, formatSupplier);
        if (!loadResult.isPresent()) {
            throw new RuntimeException("Fehler beim Laden der Datei: " + loadResult.getProblems());
        }
        return loadResult.get();
    }

    public static <T> T load(String modelPath, IFormat<T> format) {
        URL systemResource = ClassLoader.getSystemResource(modelPath);
        if (systemResource == null) {
            throw new RuntimeException("Datei nicht gefunden: " + modelPath);
        }
        return IO.load(systemResource, format).orElseThrow(
                problems -> new RuntimeException("Laden fehlgeschlagen: " + problems));
    }
}
