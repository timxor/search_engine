package com.timsiwula.searchengineapp;

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
public class IndexTest extends BaseProjectTest {

    public static class EnvironmentTest {
        @Test
        public void testEnvironment() {
            String warning = "Check your environment setup for the correct directory structure.";

            Assert.assertTrue(errorMessage("Environment Setup", null, warning),
                    isEnvironmentSetup());
        }
    }

    @FixMethodOrder(MethodSorters.JVM)
    public static class ArgumentTest {

        @Rule
        public Timeout globalTimeout = Timeout.seconds(30);

        @Test
        public void testNoArguments() {
            checkExceptions("No Arguments", new String[] {});
        }

        @Test
        public void testBadArguments() {
            String[] args = {
                    "hello", "world"
            };

            checkExceptions("Bad Arguments", args);
        }

        @Test
        public void testMissingDirectory() {
            String[] args = {
                    DIR_FLAG
            };

            checkExceptions("Missing Directory", args);
        }

        @Test
        public void testInvalidDirectory() {
            String dir = Long
                    .toHexString(Double.doubleToLongBits(Math.random()));

            String[] args = {
                    DIR_FLAG, dir
            };

            checkExceptions("Invalid Directory", args);
        }

        @Test
        public void testNoOutput() {
            String[] args = {
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString()
            };

            checkExceptions("No InvertedIndex Output", args);
        }

        @Test
        public void testDefaultOutput() throws Exception {
            String[] args = {
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString(), //
                    INDEX_OUT
            };

            Files.deleteIfExists(Paths.get(INDEX_DEFAULT));
            checkExceptions("Default InvertedIndex Output", args);

            String warning = String.format(
                    "Check that you output to %s if no output path is provided.",
                    INDEX_DEFAULT);

            Assert.assertTrue(
                    errorMessage("Default InvertedIndex Output", args, warning),
                    Files.isReadable(Paths.get(INDEX_DEFAULT)));
        }
    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class OutputTest {
        @Rule
        public Timeout globalTimeout = Timeout.seconds(360);

        @Test
        public void test01IndexSimple() {
            String name = "index-simple.json";

            String[] args = {
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString(), //
                    INDEX_OUT, OUTPUT_DIR.resolve(name).toString()
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test02IndexSimpleReversed() {
            String name = "index-simple-reversed.json";

            String[] args = {
                    INDEX_OUT, OUTPUT_DIR.resolve(name).toString(), //
                    DIR_FLAG, INDEX_DIR.resolve("simple").toString(),
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test03IndexRFCs() {
            String name = "index-rfcs.json";

            String[] args = {
                    INDEX_OUT, OUTPUT_DIR.resolve(name).toString(), //
                    DIR_FLAG, INDEX_DIR.resolve("rfcs").toString(),
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test04IndexGutenberg() {
            String name = "index-gutenberg.json";

            String[] args = {
                    INDEX_OUT, OUTPUT_DIR.resolve(name).toString(), //
                    DIR_FLAG, INDEX_DIR.resolve("gutenberg").toString(),
            };

            checkProjectOutput(name, args);
        }

        @Test
        public void test05IndexAll() {
            String name = "index-all.json";

            String[] args = {
                    INDEX_OUT, OUTPUT_DIR.resolve(name).toString(), //
                    DIR_FLAG, INDEX_DIR.toString(),
            };

            checkProjectOutput(name, args);
        }
    }
}
