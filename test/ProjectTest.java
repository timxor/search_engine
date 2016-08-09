import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;

import org.junit.Assert;

public class ProjectTest {

    // Project Configuration

    public static final Path INPUT_DIR = Paths.get("input");
    public static final Path INDEX_DIR = INPUT_DIR.resolve("index");
    public static final Path QUERY_DIR = INPUT_DIR.resolve("query");

    public static final Path OUTPUT_DIR = Paths.get("output");
    public static final Path EXPECTED_DIR = Paths.get("expected");

    public static final String INPUT_FLAG = "-input";
    public static final String INDEX_FLAG = "-index";
    public static final String QUERY_FLAG = "-query";
    public static final String RESULTS_FLAG = "-results";

    public static final String SEED_FLAG = "-seed";
    public static final String PORT_FLAG = "-port";
    public static final String THREAD_FLAG = "-threads";

    public static final String INDEX_DEFAULT = "index.json";
    public static final String RESULTS_DEFAULT = "results.json";

    public static final int THREAD_DEFAULT = 5;
    public static final int PORT_DEFAULT = 8080;

    /**
     * Checks whether environment setup is correct, with a input and output
     * directory located within the base directory.
     */
    public static boolean isEnvironmentSetup() {
        try {
            Files.createDirectories(OUTPUT_DIR);
        }
        catch (Exception e) {
            return false;
        }

        return Files.isReadable(INPUT_DIR) && Files.isReadable(EXPECTED_DIR) && Files.isReadable(OUTPUT_DIR)
                && Files.isWritable(OUTPUT_DIR);
    }

    /**
     * Checks line-by-line if two files are equal. If one file contains extra
     * blank lines at the end of the file, the two are still considered equal.
     *
     * @param path1
     *            - path to first file to compare with
     * @param path2
     *            - path to second file to compare with
     * @return positive value if two files are equal, negative value if not
     *
     * @throws IOException
     */
    public static int checkFiles(Path path1, Path path2) throws IOException {
        Charset charset = java.nio.charset.StandardCharsets.UTF_8;

        // used to output line mismatch
        int count = 0;

        try (
                BufferedReader reader1 = Files.newBufferedReader(path1, charset);
                BufferedReader reader2 = Files.newBufferedReader(path2, charset);
        ) {
            String line1 = reader1.readLine();
            String line2 = reader2.readLine();

            while (true) {
                count++;

                // compare lines until we hit a null (i.e. end of file)
                if (line1 != null && line2 != null) {
                    // use consistent path separators
                    line1 = line1.replaceAll(Matcher.quoteReplacement(File.separator), "/");
                    line2 = line2.replaceAll(Matcher.quoteReplacement(File.separator), "/");

                    // remove trailing spaces
                    line1 = line1.trim();
                    line2 = line2.trim();

                    // check if lines are equal
                    if (!line1.equals(line2)) {
                        return -count;
                    }

                    // read next lines if we get this far
                    line1 = reader1.readLine();
                    line2 = reader2.readLine();
                }
                else {
                    // discard extra blank lines at end of reader1
                    while (line1 != null && line1.trim().isEmpty()) {
                        line1 = reader1.readLine();
                    }

                    // discard extra blank lines at end of reader2
                    while (line2 != null && line2.trim().isEmpty()) {
                        line2 = reader2.readLine();
                    }

                    if (line1 == line2) {
                        // only true if both are null, otherwise one file had
                        // extra non-empty lines
                        return count;
                    }
                    else {
                        // extra blank lines found in one file
                        return -count;
                    }
                }
            }
        }
    }

    /**
     * Generates an error message, designed for display in a JUnit test.
     *
     * @param test
     *            name of test or relevant output file
     * @param args
     *            test case arguments
     * @param message
     *            debug output for failed JUnit test case
     * @return error message for failed JUnit test case
     */
    public static String errorMessage(String test, String[] args, String message) {
        return String.format("%n" +
                "Test: %s%n" +
                "Args: %s%n" +
                "Message: %n%s%n",
                test, Arrays.toString(args), message);
    }

    /**
     * Checks whether {@link Driver} will run without generating any exceptions.
     * Will print the stack trace if an exception occurs. Designed to be used
     * within an unit test.
     *
     * @param name
     *            - name of test for debugging
     * @param args
     *            - arguments to pass to {@link Driver}
     */
    public static void checkExceptions(String name, String[] args) {
        try {
            Driver.main(args);
        }
        catch (Exception e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));

            Assert.fail(errorMessage(name, args, writer.toString()));
        }
    }

    /**
     * Checks whether {@link Driver} generates the expected output without any
     * exceptions. Will print the stack trace if an exception occurs. Designed
     * to be used within an unit test.
     *
     * @param test
     *            - name of test for debugging (used as filenames as well)
     * @param args
     *            - arguments to pass to {@link Driver}
     */
    public static void checkProjectOutput(String test, String[] args) {
        Path actual = OUTPUT_DIR.resolve(test);
        Path expect = EXPECTED_DIR.resolve(test);

        checkProjectOutput(test, expect, actual, args);
    }

    /**
     * Checks whether {@link Driver} generates the expected output without any
     * exceptions. Will print the stack trace if an exception occurs. Designed
     * to be used within an unit test.
     *
     * @param test
     *            name of tests for debugging
     * @param actual
     *            path to actual output
     * @param expect
     *            path to expected output
     * @param args
     *            arguments to pass to {@link Driver}
     */
    public static void checkProjectOutput(String test, Path expect,
            Path actual, String[] args) {

        try {
            // Remove old actual file (if exists), setup directories if needed
            Files.deleteIfExists(actual);
            Files.createDirectories(actual.getParent());

            // Generate actual output file
            Driver.main(args);

            // Double-check we can read the expected output file
            Assert.assertTrue(errorMessage(test, args,
                    "Unable to read expected output file: " +
                    expect.toString()),
                    Files.isReadable(expect));

            // Double-check we can read the actual output file
            Assert.assertTrue(errorMessage(test, args,
                    "Unable to read actual output file: " +
                    actual.toString()),
                    Files.isReadable(actual));

            // Compare the two files
            int count = checkFiles(actual, expect);

            if (count <= 0) {
                Assert.fail(errorMessage(test, args,
                        "Difference detected on line: " + -count + "."));
            }
        }
        catch (Exception e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));

            Assert.fail(errorMessage(test, args, writer.toString()));
        }
    }
}
