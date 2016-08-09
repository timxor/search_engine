import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class StressTest extends ProjectTest {

    private static final int WARM_RUNS = 3;
    private static final int TIME_RUNS = 5;

    private static final int THREADS = 5;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(60 * TIME_RUNS);

    @Test
    public void testIndexConsistency() {
        ThreadTest.OutputTest tester =
                new ThreadTest.OutputTest(String.valueOf(THREADS));

        for (int i = 0; i < TIME_RUNS; i++) {
            tester.test02IndexComplex();
        }
    }

    @Test
    public void testSearchConsistency() {
        ThreadTest.OutputTest tester =
                new ThreadTest.OutputTest(String.valueOf(THREADS));

        for (int i = 0; i < TIME_RUNS; i++) {
            tester.test04SearchComplex();
        }
    }

    @Test
    public void testRuntime() {
        double singleAverage = benchmark(String.valueOf(1)) / 1000000000.0;
        double threadAverage = benchmark(String.valueOf(THREADS)) / 1000000000.0;

        System.out.printf("%d Threads: %.2f s%n", 1, singleAverage);
        System.out.printf("%d Threads: %.2f s%n", THREADS, threadAverage);
        System.out.printf("  Speedup: %.2f %n", singleAverage / threadAverage);

        Assert.assertTrue(singleAverage - threadAverage > 0);
    }

    private double benchmark(String numThreads) {
        String[] args = {
                INPUT_FLAG, INDEX_DIR.toString(),
                QUERY_FLAG, QUERY_DIR.resolve("complex.txt").toString(),
                THREAD_FLAG, numThreads
        };

        long total = 0;
        long start = 0;

        try {
            for (int i = 0; i < WARM_RUNS; i++) {
                Driver.main(args);
            }

            for (int i = 0; i < TIME_RUNS; i++) {
                start = System.nanoTime();
                Driver.main(args);
                total += System.nanoTime() - start;
            }
        }
        catch (Exception e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));

            Assert.fail(errorMessage("Benchmark: " + numThreads,
                    args, writer.toString()));
        }

        return (double) total / TIME_RUNS;
    }
}
