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
public class ThreadTest extends BaseProjectTest {

    public static class ArgumentTest {
        @Rule
        public Timeout globalTimeout = Timeout.seconds(30);

        private static String[] getArguments(String threads) {
            return new String[] {
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString(), //
                    THREAD_NUM, threads
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
        public Timeout globalTimeout = Timeout.seconds(120);

        @Parameters(name = "{0} Threads")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][] {
                    {
                            "02"
                    }, // test output with 2 worker threads
                    {
                            "05"
                    }, // test output with 5 worker threads
                    {
                            "10"
                    } // test output with 10 worker threads
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
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString(), //
                    INDEX_OUT, actual.toString(), //
                    THREAD_NUM, this.numThreads
            };

            checkProjectOutput(name, expect, actual, args);
        }

        @Test
        public void test02IndexComplex() {
            String name = "index-all-" + this.numThreads + ".json";

            Path expect = EXPECTED_DIR.resolve("index-all.json");
            Path actual = OUTPUT_DIR.resolve(name);

            String[] args = {
                    DIR_FLAG, INDEX_DIR.toString(), //
                    INDEX_OUT, actual.toString(), //
                    THREAD_NUM, this.numThreads
            };

            checkProjectOutput(name, expect, actual, args);
        }

        @Test
        public void test03SearchSimple() {
            String name = "search-simple-" + this.numThreads + ".json";

            Path expect = EXPECTED_DIR.resolve("search-simple-simple.json");
            Path actual = OUTPUT_DIR.resolve(name);

            String[] args = {
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString(), //
                    QUERY_FLAG, QUERY_DIR.resolve("simple.txt").toString(), //
                    QUERY_OUT, actual.toString(), //
                    THREAD_NUM, this.numThreads
            };

            checkProjectOutput(name, expect, actual, args);
        }

        @Test
        public void test04SearchComplex() {
            String name = "search-complex-" + this.numThreads + ".json";

            Path expect = EXPECTED_DIR.resolve("search-index-complex.json");
            Path actual = OUTPUT_DIR.resolve(name);

            String[] args = {
                    DIR_FLAG, INDEX_DIR.toString(), //
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(), //
                    QUERY_OUT, actual.toString(), //
                    THREAD_NUM, this.numThreads
            };

            checkProjectOutput(name, expect, actual, args);
        }

        @Test
        public void test05CrawlIndexGuten() {
            String name = "index-web-guten-" + this.numThreads + ".json";

            Path expect = EXPECTED_DIR.resolve("index-web-guten.json");
            Path actual = OUTPUT_DIR.resolve(name);

            String[] args = {
                    URL_FLAG, CrawlTest.LOCAL_GUTENBERG, //
                    INDEX_OUT, actual.toString(), //
                    THREAD_NUM, this.numThreads
            };

            checkProjectOutput(name, expect, actual, args);
        }

        @Test
        public void test06CrawlIndexLog4j2() {
            String name = "index-web-log4j2-" + this.numThreads + ".json";

            Path expect = EXPECTED_DIR.resolve("index-web-log4j2.json");
            Path actual = OUTPUT_DIR.resolve(name);

            String[] args = {
                    URL_FLAG, CrawlTest.LOCAL_LOG4J2, //
                    INDEX_OUT, actual.toString(), //
                    THREAD_NUM, this.numThreads
            };

            checkProjectOutput(name, expect, actual, args);
        }

        @Test
        public void test07CrawlSearchExact() {
            String name = "search-web-exact-" + this.numThreads + ".json";

            Path expect = EXPECTED_DIR.resolve("search-web-exact.json");
            Path actual = OUTPUT_DIR.resolve(name);

            String[] args = {
                    URL_FLAG, CrawlTest.LOCAL_GUTENBERG, //
                    EXACT_FLAG, QUERY_DIR.resolve("exact.txt").toString(), //
                    QUERY_OUT, actual.toString(), //
                    THREAD_NUM, this.numThreads
            };

            checkProjectOutput(name, expect, actual, args);
        }

        @Test
        public void test07CrawlSearchLog4j2() {
            String name = "search-web-log4j2-" + this.numThreads + ".json";

            Path expect = EXPECTED_DIR.resolve("search-web-log4j2.json");
            Path actual = OUTPUT_DIR.resolve(name);

            String[] args = {
                    URL_FLAG, CrawlTest.LOCAL_LOG4J2, //
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(), //
                    QUERY_OUT, actual.toString(), //
                    THREAD_NUM, this.numThreads
            };

            checkProjectOutput(name, expect, actual, args);
        }
    }
}
