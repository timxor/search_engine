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
public class SearchTest extends BaseProjectTest {

    public static class ArgumentTest {
        @Rule
        public Timeout globalTimeout = Timeout.seconds(30);

        @Test
        public void testMissingQueryPath() {
            String[] args = {
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString(), //
                    QUERY_FLAG
            };

            checkExceptions("Missing Query Path", args);
        }

        @Test
        public void testMissingExactPath() {
            String[] args = {
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString(), //
                    EXACT_FLAG
            };

            checkExceptions("Missing Exact Path", args);
        }

        @Test
        public void testInvalidQueryPath() {
            String name = Long
                    .toHexString(Double.doubleToLongBits(Math.random()));

            String[] args = {
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString(), //
                    QUERY_FLAG, Paths.get(name).toString()
            };

            checkExceptions("Invalid Query Path", args);
        }

        @Test
        public void testInvalidExactPath() {
            String name = Long
                    .toHexString(Double.doubleToLongBits(Math.random()));

            String[] args = {
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString(), //
                    EXACT_FLAG, Paths.get(name).toString()
            };

            checkExceptions("Invalid Exact Path", args);
        }

        @Test
        public void testDefaultOutput() throws Exception {
            String[] args = {
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString(), //
                    QUERY_FLAG, QUERY_DIR.resolve("simple.txt").toString(), //
                    QUERY_OUT
            };

            Files.deleteIfExists(Paths.get(RESULTS_DEFAULT));
            checkExceptions("Default Search Output", args);

            Assert.assertTrue(
                    errorMessage("Default Search Output", args,
                            "Check that you output to " + RESULTS_DEFAULT
                                    + " if " + "no output path is provided."),
                    Files.isReadable(Paths.get(RESULTS_DEFAULT)));
        }

        @Test
        public void testNoOutput() throws Exception {
            String[] args = {
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString(), //
                    QUERY_FLAG, QUERY_DIR.resolve("simple.txt").toString()
            };

            checkExceptions("No Search Output", args);
        }

        @Test
        public void testEmptyIndex() throws Exception {
            String[] args = {
                    QUERY_FLAG, QUERY_DIR.resolve("simple.txt").toString()
            };

            checkExceptions("Search Empty InvertedIndex", args);
        }

        @Test
        public void testEmptySearch() throws Exception {
            String[] args = {
                    QUERY_OUT
            };

            checkExceptions("Empty InvertedIndex Empty Query", args);
        }
    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class ParialSearchTest {
        @Rule
        public Timeout globalTimeout = Timeout.seconds(120);

        @Test
        public void test01SearchSimple() {
            String name = "search-simple-simple.json";

            String[] args = {
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString(), //
                    QUERY_FLAG, QUERY_DIR.resolve("simple.txt").toString(), //
                    QUERY_OUT, OUTPUT_DIR.resolve(name).toString()
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test02SearchReversed() {
            String name = "search-simple-reversed.json";

            String[] args = {
                    QUERY_OUT, OUTPUT_DIR.resolve(name).toString(), //
                    QUERY_FLAG, QUERY_DIR.resolve("simple.txt").toString(), //
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString()
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test03SearchDuplicates() {
            String name = "search-simple-duplicates.json";

            String[] args = {
                    QUERY_OUT, OUTPUT_DIR.resolve(name).toString(), //
                    QUERY_FLAG, QUERY_DIR.resolve("duplicates.txt").toString(), //
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString()
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test04SearchSimpleComplex() {
            String name = "search-simple-complex.json";

            String[] args = {
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString(), //
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(), //
                    QUERY_OUT, OUTPUT_DIR.resolve(name).toString()
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test05SearchIndexSimple() {
            String name = "search-index-simple.json";

            String[] args = {
                    DIR_FLAG, INDEX_DIR.toString(), //
                    QUERY_FLAG, QUERY_DIR.resolve("simple.txt").toString(), //
                    QUERY_OUT, OUTPUT_DIR.resolve(name).toString()
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test06SearchIndexDuplicates() {
            String name = "search-index-duplicates.json";

            String[] args = {
                    DIR_FLAG, INDEX_DIR.toString(), //
                    QUERY_FLAG, QUERY_DIR.resolve("duplicates.txt").toString(), //
                    QUERY_OUT, OUTPUT_DIR.resolve(name).toString()
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test07SearchIndexComplex() {
            String name = "search-index-complex.json";

            String[] args = {
                    DIR_FLAG, INDEX_DIR.toString(), QUERY_FLAG, //
                    QUERY_DIR.resolve("complex.txt").toString(), //
                    QUERY_OUT, OUTPUT_DIR.resolve(name).toString()
            };

            checkProjectOutput(name, args);
        }
    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class ExactSearchTest {
        @Rule
        public Timeout globalTimeout = Timeout.seconds(120);

        @Test
        public void test01ExactSimpleSimple() {
            String name = "exact-simple-simple.json";

            String[] args = {
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString(), //
                    EXACT_FLAG, QUERY_DIR.resolve("simple.txt").toString(), //
                    QUERY_OUT, OUTPUT_DIR.resolve(name).toString()
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test02ExactRFCsExact() {
            String name = "exact-rfcs-exact.json";

            String[] args = {
                    DIR_FLAG, INDEX_DIR.resolve("rfcs").toString(), //
                    EXACT_FLAG, QUERY_DIR.resolve("exact.txt").toString(), //
                    QUERY_OUT, OUTPUT_DIR.resolve(name).toString()
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test03ExactGutenbergExact() {
            String name = "exact-gutenberg-exact.json";

            String[] args = {
                    DIR_FLAG, INDEX_DIR.resolve("gutenberg").toString(), //
                    EXACT_FLAG, QUERY_DIR.resolve("exact.txt").toString(), //
                    QUERY_OUT, OUTPUT_DIR.resolve(name).toString()
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test04ExactIndexExact() {
            String name = "exact-index-exact.json";

            String[] args = {
                    DIR_FLAG, INDEX_DIR.toString(), EXACT_FLAG, //
                    QUERY_DIR.resolve("exact.txt").toString(), QUERY_OUT, //
                    OUTPUT_DIR.resolve(name).toString()
            };

            checkProjectOutput(name, args);
        }

    }
}
