import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(Enclosed.class)
public class SearchTest extends ProjectTest {

    public static class ArgumentTest {
        @Rule
        public Timeout globalTimeout = Timeout.seconds(30);

        @Test
        public void testMissingQueryPath() {
            String[] args = {
                    INPUT_FLAG, INDEX_DIR.resolve("simple").toString(),
                    QUERY_FLAG
            };

            checkExceptions("Missing Query Path", args);
        }

        @Test
        public void testInvalidQueryPath() {
            String name = Long.toHexString(Double.doubleToLongBits(Math.random()));

            String[] args = {
                    INPUT_FLAG, INDEX_DIR.resolve("simple").toString(),
                    QUERY_FLAG, Paths.get(name).toString()
            };

            checkExceptions("Invalid Query Path", args);
        }

        @Test
        public void testDefaultOutput() throws Exception {
            String[] args = {
                    INPUT_FLAG, INDEX_DIR.resolve("simple").toString(),
                    QUERY_FLAG, QUERY_DIR.resolve("simple.txt").toString(),
                    RESULTS_FLAG
            };

            Files.deleteIfExists(Paths.get(RESULTS_DEFAULT));
            checkExceptions("Default Search Output", args);

            Assert.assertTrue(errorMessage("Default Search Output", args,
                    "Check that you output to " + RESULTS_DEFAULT + " if " +
                    "no output path is provided."),
                    Files.isReadable(Paths.get(RESULTS_DEFAULT)));
        }

        @Test
        public void testNoOutput() throws Exception {
            String[] args = {
                    INPUT_FLAG, INDEX_DIR.resolve("simple").toString(),
                    QUERY_FLAG, QUERY_DIR.resolve("simple.txt").toString()
            };

            checkExceptions("No Search Output", args);
        }
    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class OutputTest {
        @Rule
        public Timeout globalTimeout = Timeout.seconds(30);

        @Test
        public void test01SearchSimple() {
            String name = "search-simple-simple.json";

            String[] args = {
                    INPUT_FLAG, INDEX_DIR.resolve("simple").toString(),
                    QUERY_FLAG, QUERY_DIR.resolve("simple.txt").toString(),
                    RESULTS_FLAG, OUTPUT_DIR.resolve(name).toString()
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test02SearchReversed() {
            String name = "search-simple-reversed.json";

            String[] args = {
                    RESULTS_FLAG, OUTPUT_DIR.resolve(name).toString(),
                    QUERY_FLAG, QUERY_DIR.resolve("simple.txt").toString(),
                    INPUT_FLAG, INDEX_DIR.resolve("simple").toString()
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test03SearchSimpleAlphabet() {
            String name = "search-simple-alphabet.json";

            String[] args = {
                    INPUT_FLAG, INDEX_DIR.resolve("simple").toString(),
                    QUERY_FLAG, QUERY_DIR.resolve("alphabet.txt").toString(),
                    RESULTS_FLAG, OUTPUT_DIR.resolve(name).toString()
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test04SearchSimpleComplex() {
            String name = "search-simple-complex.json";

            String[] args = {
                    INPUT_FLAG, INDEX_DIR.resolve("simple").toString(),
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(),
                    RESULTS_FLAG, OUTPUT_DIR.resolve(name).toString()
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test05SearchIndexSimple() {
            String name = "search-index-simple.json";

            String[] args = {
                    INPUT_FLAG, INDEX_DIR.toString(),
                    QUERY_FLAG, QUERY_DIR.resolve("simple.txt").toString(),
                    RESULTS_FLAG, OUTPUT_DIR.resolve(name).toString()
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test06SearchIndexAlphabet() {
            String name = "search-index-alpha.json";

            String[] args = {
                    INPUT_FLAG, INDEX_DIR.toString(),
                    QUERY_FLAG, QUERY_DIR.resolve("alphabet.txt").toString(),
                    RESULTS_FLAG, OUTPUT_DIR.resolve(name).toString()
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test07SearchIndexComplex() {
            String name = "search-index-complex.json";

            String[] args = {
                    INPUT_FLAG, INDEX_DIR.toString(),
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(),
                    RESULTS_FLAG, OUTPUT_DIR.resolve(name).toString()
            };

            checkProjectOutput(name, args);
        }
    }
}
