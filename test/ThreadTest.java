import java.nio.file.Path;
import java.util.Arrays;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Enclosed.class)
public class ThreadTest extends ProjectTest {

    public static class ArgumentTest {
        @Rule
        public Timeout globalTimeout = Timeout.seconds(30);

        private static String[] getArguments(String threads) {
            return new String[] {
                    INPUT_FLAG, INDEX_DIR.resolve("simple").toString(),
                    QUERY_FLAG, QUERY_DIR.resolve("simple.txt").toString(),
                    THREAD_FLAG, threads
            };
        }

        @Test
        public void testNegativeThreads() {
            checkExceptions("Threads: -1", getArguments("-1"));
        }

        @Test
        public void testZeroThreads() {
            checkExceptions("Threads: 0", getArguments("0"));
        }

        @Test
        public void testFractionThreads() {
            checkExceptions("Threads: 3.14", getArguments("3.14"));
        }

        @Test
        public void testWordThreads() {
            checkExceptions("Threads: fox", getArguments("fox"));
        }

        @Test
        public void testOneThread() {
            checkExceptions("Threads: 1", getArguments("1"));
        }
    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    @RunWith(Parameterized.class)
    public static class OutputTest {
        @Rule
        public Timeout globalTimeout = Timeout.seconds(60);

        @Parameters(name = "{0} Threads")
        public static Iterable<Object[]> data() {
                return Arrays.asList(new Object[][]{
                        {"02"},  // test output with 2  worker threads
                        {"03"},  // test output with 3  worker threads
                        {"05"},  // test output with 5  worker threads
                        {"10"}   // test output with 10 worker threads
                });
        }

        private String numThreads;

        public OutputTest(String numThreads) {
            this.numThreads = numThreads;
        }

        @Test
        public void test01IndexSimple() {
            String name = "index-simple-" + this.numThreads + ".json";

            Path expect = EXPECTED_DIR.resolve("index-simple.json");
            Path actual = OUTPUT_DIR.resolve(name);

            String[] args = {
                    INPUT_FLAG, INDEX_DIR.resolve("simple").toString(),
                    QUERY_FLAG, QUERY_DIR.resolve("simple.txt").toString(),
                    INDEX_FLAG, actual.toString(),
                    THREAD_FLAG, this.numThreads
            };

            checkProjectOutput(name, expect, actual, args);
        }

        @Test
        public void test02IndexComplex() {
            String name = "index-all-" + this.numThreads + ".json";

            Path expect = EXPECTED_DIR.resolve("index-all.json");
            Path actual = OUTPUT_DIR.resolve(name);

            String[] args = {
                    INPUT_FLAG, INDEX_DIR.toString(),
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(),
                    INDEX_FLAG, actual.toString(),
                    THREAD_FLAG, this.numThreads
            };

            checkProjectOutput(name, expect, actual, args);
        }

        @Test
        public void test03SearchSimple() {
            String name = "search-simple-" + this.numThreads + ".json";

            Path expect = EXPECTED_DIR.resolve("search-simple-simple.json");
            Path actual = OUTPUT_DIR.resolve(name);

            String[] args = {
                    INPUT_FLAG, INDEX_DIR.resolve("simple").toString(),
                    QUERY_FLAG, QUERY_DIR.resolve("simple.txt").toString(),
                    RESULTS_FLAG, actual.toString(),
                    THREAD_FLAG, this.numThreads
            };

            checkProjectOutput(name, expect, actual, args);
        }

        @Test
        public void test04SearchComplex() {
            String name = "search-complex-" + this.numThreads + ".json";

            Path expect = EXPECTED_DIR.resolve("search-index-complex.json");
            Path actual = OUTPUT_DIR.resolve(name);

            String[] args = {
                    INPUT_FLAG, INDEX_DIR.toString(),
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(),
                    RESULTS_FLAG, actual.toString(),
                    THREAD_FLAG, this.numThreads
            };

            checkProjectOutput(name, expect, actual, args);
        }
    }
}
