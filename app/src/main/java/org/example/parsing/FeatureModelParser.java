package org.example.parsing;

import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.io.dimacs.FormulaDimacsParser;
import de.featjar.formula.structure.IFormula;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashSet;

import de.featjar.base.FeatJAR;
import de.featjar.base.computation.Cache;
/*import de.featjar.formula.structure.IFormula;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File; */
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.base.io.format.IFormat;
import de.featjar.base.io.format.IFormatSupplier;
import de.featjar.base.log.CallerFormatter;
import de.featjar.base.log.Log;
import de.featjar.base.log.TimeStampFormatter;
import de.featjar.base.log.VerbosityFormatter;

//import de.featjar.base.io.NonEmptyLineIterator;

public class FeatureModelParser {

    public static void main(String[] args) {

        initializeFeatJAR();

        String modelPath = "example_input.xml";

        // Beispiel: Datei "example-model.json" aus dem resources-Ordner laden

        // Laden mit einem spezifischen Format
        IFormula formula = loadFormula(modelPath);

        LinkedHashSet<String> variables = formula.getVariableNames();
        System.out.println("Variables: " + variables);

        String a = formula.getName();

        // Ausgabe der geladenen Formel
        System.out.println(a);
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

    public static IFormula loadFormula(String modelPath) {
        return load(modelPath, FormulaFormats.getInstance());
    }
}

/*
 * public static void main(String[] args) {
 * 
 * // so geht es innerhalb von featjar
 * FormulaDimacsParser parser = new FormulaDimacsParser();
 * // IFormula formula = parser.parse(new NonEmptyLineIterator(dataStream));
 * 
 * String modelPath = "./input.xml";
 * URL systemResource = ClassLoader.getSystemResource(modelPath);
 * if (systemResource == null) {
 * // fail(modelPath);
 * System.err.println("e");
 * }
 * 
 * FormulaFormats format = FormulaFormats.getInstance();
 * 
 * Result<IFormula> load = IO.load(systemResource, format);
 * System.err.println(load);
 * 
 * }
 * }
 */
