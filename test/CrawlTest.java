import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(Enclosed.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CrawlTest extends ProjectTest {

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class Test01LocalIndex {
        @Rule
        public Timeout globalTimeout = Timeout.seconds(60);

        @Test
        public void test01IndexBirds() {
            String test = "index-web-birds.json";
            String link = "http://www.cs.usfca.edu/~sjengle/cs212/crawl/birds.html";

            String[] args = {
                    SEED_FLAG, link,
                    INDEX_FLAG, OUTPUT_DIR.resolve(test).toString(),
                    THREAD_FLAG, String.valueOf(THREAD_DEFAULT)
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test03IndexYellow() {
            String test = "index-web-yellow.json";
            String link = "http://www.cs.usfca.edu/~sjengle/cs212/crawl/yellowthroat.html";

            String[] args = {
                    SEED_FLAG, link,
                    INDEX_FLAG, OUTPUT_DIR.resolve(test).toString(),
                    THREAD_FLAG, String.valueOf(THREAD_DEFAULT)
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test05IndexHolmes() {
            String test = "index-web-holmes.json";
            String link = "http://www.cs.usfca.edu/~sjengle/cs212/gutenberg/1661-h.htm";

            String[] args = {
                    SEED_FLAG, link,
                    INDEX_FLAG, OUTPUT_DIR.resolve(test).toString(),
                    THREAD_FLAG, String.valueOf(THREAD_DEFAULT)
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test07IndexGuten() {
            String test = "index-web-guten.json";
            String link = "http://www.cs.usfca.edu/~sjengle/cs212/crawl/gutenberg.html";

            String[] args = {
                    SEED_FLAG, link,
                    INDEX_FLAG, OUTPUT_DIR.resolve(test).toString(),
                    THREAD_FLAG, String.valueOf(THREAD_DEFAULT)
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test09IndexRecurse() {
            String test = "index-web-recurse.json";
            String link = "http://www.cs.usfca.edu/~sjengle/cs212/recurse/link01.html";

            String[] args = {
                    SEED_FLAG, link,
                    INDEX_FLAG, OUTPUT_DIR.resolve(test).toString(),
                    THREAD_FLAG, String.valueOf(THREAD_DEFAULT)
            };

            checkProjectOutput(test, args);
        }
    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class Test02LocalSearch {
        @Rule
        public Timeout globalTimeout = Timeout.seconds(60);

        @Test
        public void test02SearchBirds() {
            String test = "search-web-birds.json";
            String link = "http://www.cs.usfca.edu/~sjengle/cs212/crawl/birds.html";

            String[] args = {
                    SEED_FLAG, link,
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(),
                    RESULTS_FLAG, OUTPUT_DIR.resolve(test).toString(),
                    THREAD_FLAG, String.valueOf(THREAD_DEFAULT)
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test04SearchYellow() {
            String test = "search-web-yellow.json";
            String link = "http://www.cs.usfca.edu/~sjengle/cs212/crawl/yellowthroat.html";

            String[] args = {
                    SEED_FLAG, link,
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(),
                    RESULTS_FLAG, OUTPUT_DIR.resolve(test).toString(),
                    THREAD_FLAG, String.valueOf(THREAD_DEFAULT)
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test06SearchHolmes() {
            String test = "search-web-holmes.json";
            String link = "http://www.cs.usfca.edu/~sjengle/cs212/gutenberg/1661-h.htm";

            String[] args = {
                    SEED_FLAG, link,
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(),
                    RESULTS_FLAG, OUTPUT_DIR.resolve(test).toString(),
                    THREAD_FLAG, String.valueOf(THREAD_DEFAULT)
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test08SearchGuten() {
            String test = "search-web-guten.json";
            String link = "http://www.cs.usfca.edu/~sjengle/cs212/crawl/gutenberg.html";

            String[] args = {
                    SEED_FLAG, link,
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(),
                    RESULTS_FLAG, OUTPUT_DIR.resolve(test).toString(),
                    THREAD_FLAG, String.valueOf(THREAD_DEFAULT)
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test10SearchRecurse() {
            String test = "search-web-recurse.json";
            String link = "http://www.cs.usfca.edu/~sjengle/cs212/recurse/link01.html";

            String[] args = {
                    SEED_FLAG, link,
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(),
                    RESULTS_FLAG, OUTPUT_DIR.resolve(test).toString(),
                    THREAD_FLAG, String.valueOf(THREAD_DEFAULT)
            };

            checkProjectOutput(test, args);
        }
    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class Test03External {
        @Rule
        public Timeout globalTimeout = Timeout.seconds(60);

        @Test
        public void test01IndexHTMLHelp() {
            String test = "index-web-htmlhelp.json";
            String link = "http://htmlhelp.com/reference/html40/olist.html";

            String[] args = {
                    SEED_FLAG, link,
                    INDEX_FLAG, OUTPUT_DIR.resolve(test).toString(),
                    THREAD_FLAG, String.valueOf(THREAD_DEFAULT)
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test02SearchHTMLHelp() {
            String test = "search-web-htmlhelp.json";
            String link = "http://htmlhelp.com/reference/html40/olist.html";

            String[] args = {
                    SEED_FLAG, link,
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(),
                    RESULTS_FLAG, OUTPUT_DIR.resolve(test).toString(),
                    THREAD_FLAG, String.valueOf(THREAD_DEFAULT)
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test03IndexLog4j() {
            String test = "index-web-log4j.json";
            String link = "http://logging.apache.org/log4j/1.2/apidocs/allclasses-noframe.html";

            String[] args = {
                    SEED_FLAG, link,
                    INDEX_FLAG, OUTPUT_DIR.resolve(test).toString(),
                    THREAD_FLAG, String.valueOf(THREAD_DEFAULT)
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test04SearchLog4j() {
            String test = "search-web-log4j.json";
            String link = "http://logging.apache.org/log4j/1.2/apidocs/allclasses-noframe.html";

            String[] args = {
                    SEED_FLAG, link,
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(),
                    RESULTS_FLAG, OUTPUT_DIR.resolve(test).toString(),
                    THREAD_FLAG, String.valueOf(THREAD_DEFAULT)
            };

            checkProjectOutput(test, args);
        }
    }
}
