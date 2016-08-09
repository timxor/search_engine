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
public class IndexTest extends ProjectTest {

    public static class EnvironmentTest {
        @Test
        public void testEnvironment() {
            Assert.assertTrue(errorMessage("Environment Setup", null,
                    "Check your environment setup for the correct" +
                    "directory structure."),
                    isEnvironmentSetup());
        }
    }

    public static class ArgumentTest {
        @Rule
        public Timeout globalTimeout = Timeout.seconds(30);

        @Test
        public void testNoArguments() {
            checkExceptions("No Arguments", new String[] {});
        }

        @Test
        public void testBadArguments() {
            checkExceptions("Bad Arguments", new String[] {
                    "hello", "world"
            });
        }

        @Test
        public void testMissingDirectory() {
            String[] args = {INPUT_FLAG};
            checkExceptions("Missing Directory", args);
        }

        @Test
        public void testInvalidDirectory() {
            String dir = Long.toHexString(Double.doubleToLongBits(Math.random()));
            String[] args = {INPUT_FLAG, dir};
            checkExceptions("Invalid Directory", args);
        }

        @Test
        public void testDefaultOutput() throws Exception {
            String[] args = {
                    INPUT_FLAG, INDEX_DIR.resolve("simple").toString(),
                    INDEX_FLAG
            };

            Files.deleteIfExists(Paths.get(INDEX_DEFAULT));
            checkExceptions("Default Index Output", args);

            Assert.assertTrue(errorMessage("Default Index Output", args,
                    "Check that you output to " + INDEX_DEFAULT + " if " +
                    "no output path is provided."),
                    Files.isReadable(Paths.get(INDEX_DEFAULT)));
        }

        @Test
        public void testNoOutput() {
            String[] args = {
                    INPUT_FLAG, INDEX_DIR.resolve("simple").toString()
            };

            checkExceptions("No Index Output", args);
        }
    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class OutputTest {
        @Rule
        public Timeout globalTimeout = Timeout.seconds(30);

        @Test
        public void test01IndexSimple() {
            String name = "index-simple.json";

            String[] args = {
                    INPUT_FLAG, INDEX_DIR.resolve("simple").toString(),
                    INDEX_FLAG, OUTPUT_DIR.resolve(name).toString()
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test02IndexSimpleReversed() {
            String name = "index-simple-reversed.json";

            String[] args = {
                    INDEX_FLAG, OUTPUT_DIR.resolve(name).toString(),
                    INPUT_FLAG, INDEX_DIR.resolve("simple").toString(),
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test03IndexRFCs() {
            String name = "index-rfcs.json";

            String[] args = {
                    INDEX_FLAG, OUTPUT_DIR.resolve(name).toString(),
                    INPUT_FLAG, INDEX_DIR.resolve("rfcs").toString(),
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test04IndexGutenberg() {
            String name = "index-gutenberg.json";

            String[] args = {
                    INDEX_FLAG, OUTPUT_DIR.resolve(name).toString(),
                    INPUT_FLAG, INDEX_DIR.resolve("gutenberg").toString(),
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test05IndexAll() {
            String name = "index-all.json";

            String[] args = {
                    INDEX_FLAG, OUTPUT_DIR.resolve(name).toString(),
                    INPUT_FLAG, INDEX_DIR.toString(),
            };

            checkProjectOutput(name, args);
        }
    }
}
