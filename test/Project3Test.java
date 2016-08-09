import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    IndexTest.class,
    SearchTest.class,
    ThreadTest.class,
    StressTest.class
})
public class Project3Test {
    /*
     * To be eligible for code review for project 3, you must pass
     * this test suite on the lab computers **except** for StressTest!
     */
}
