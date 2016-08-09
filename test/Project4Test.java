import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    IndexTest.class,
    SearchTest.class,
    ThreadTest.class,
    StressTest.class,
    CrawlTest.class
})
public class Project4Test {
    /*
     * To be eligible for code review for project 4, you must pass
     * this test suite on the lab computers.
     */
}
