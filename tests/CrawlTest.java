import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(Enclosed.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CrawlTest extends BaseProjectTest {

    /*
     * All of the test links have been changed to versions stored on our local
     * CS web server. This is to reduce the likelihood you will be blocked while
     * testing, but you should still avoid re-running these tests more than
     * necessary.
     */

    public static final String LOCAL_BIRDS = "http://cs.usfca.edu/~cs212/birds/birds.html";
    public static final String LOCAL_YELLOW = "http://cs.usfca.edu/~cs212/birds/yellowthroat.html";

    public static final String LOCAL_RECURSE = "http://cs.usfca.edu/~cs212/recurse/link01.html";

    public static final String LOCAL_GUTENBERG = "http://cs.usfca.edu/~cs212/gutenberg/gutenberg.html";
    public static final String LOCAL_MOBYDICK = "http://cs.usfca.edu/~cs212/gutenberg/2701.htm";

    public static final String LOCAL_LOG4J2 = "http://cs.usfca.edu/~cs212/log4j2/allclasses-noframe.html";
    public static final String LOCAL_HTML40 = "http://cs.usfca.edu/~cs212/wdghtml40/olist.html";

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class Test01Index {
        @Rule
        public Timeout globalTimeout = Timeout.seconds(120);

        @Test
        public void test01IndexBirds() {
            String test = "index-web-birds.json";
            String link = LOCAL_BIRDS;

            String[] args = {
                    URL_FLAG, link, //
                    INDEX_OUT, OUTPUT_DIR.resolve(test).toString()
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test02IndexYellow() {
            String test = "index-web-yellow.json";
            String link = LOCAL_YELLOW;

            String[] args = {
                    URL_FLAG, link, //
                    INDEX_OUT, OUTPUT_DIR.resolve(test).toString()
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test03IndexRecurse() {
            String test = "index-web-recurse.json";
            String link = LOCAL_RECURSE;

            String[] args = {
                    URL_FLAG, link, //
                    INDEX_OUT, OUTPUT_DIR.resolve(test).toString()
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test04IndexMobyDick() {
            String test = "index-web-moby.json";
            String link = LOCAL_MOBYDICK;

            String[] args = {
                    URL_FLAG, link, //
                    INDEX_OUT, OUTPUT_DIR.resolve(test).toString()
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test05IndexGuten() {
            String test = "index-web-guten.json";
            String link = LOCAL_GUTENBERG;

            String[] args = {
                    URL_FLAG, link, //
                    INDEX_OUT, OUTPUT_DIR.resolve(test).toString()
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test06IndexLog4j2() {
            String test = "index-web-log4j2.json";
            String link = LOCAL_LOG4J2;

            String[] args = {
                    URL_FLAG, link, //
                    INDEX_OUT, OUTPUT_DIR.resolve(test).toString()
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test07IndexHTML40() {
            String test = "index-web-html40.json";
            String link = LOCAL_HTML40;

            String[] args = {
                    URL_FLAG, link, //
                    INDEX_OUT, OUTPUT_DIR.resolve(test).toString()
            };

            checkProjectOutput(test, args);
        }
    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class Test02Search {
        @Rule
        public Timeout globalTimeout = Timeout.seconds(120);

        @Test
        public void test01SearchBirds() {
            String test = "search-web-birds.json";
            String link = LOCAL_BIRDS;

            String[] args = {
                    URL_FLAG, link, //
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(), //
                    QUERY_OUT, OUTPUT_DIR.resolve(test).toString()
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test02SearchMobyDick() {
            String test = "search-web-moby.json";
            String link = LOCAL_MOBYDICK;

            String[] args = {
                    URL_FLAG, link, //
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(), //
                    QUERY_OUT, OUTPUT_DIR.resolve(test).toString()
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test03SearchGuten() {
            String test = "search-web-guten.json";
            String link = LOCAL_GUTENBERG;

            String[] args = {
                    URL_FLAG, link, //
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(), //
                    QUERY_OUT, OUTPUT_DIR.resolve(test).toString()
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test04SearchLog4j2() {
            String test = "search-web-log4j2.json";
            String link = LOCAL_LOG4J2;

            String[] args = {
                    URL_FLAG, link, //
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(), //
                    QUERY_OUT, OUTPUT_DIR.resolve(test).toString()
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test05SearchHTML40() {
            String test = "search-web-html40.json";
            String link = LOCAL_HTML40;

            String[] args = {
                    URL_FLAG, link, //
                    QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(), //
                    QUERY_OUT, OUTPUT_DIR.resolve(test).toString()
            };

            checkProjectOutput(test, args);
        }

        @Test
        public void test06SearchExact() {
            String test = "search-web-exact.json";
            String link = LOCAL_GUTENBERG;

            String[] args = {
                    URL_FLAG, link, //
                    EXACT_FLAG, QUERY_DIR.resolve("exact.txt").toString(), //
                    QUERY_OUT, OUTPUT_DIR.resolve(test).toString()
            };

            checkProjectOutput(test, args);
        }
    }
}
