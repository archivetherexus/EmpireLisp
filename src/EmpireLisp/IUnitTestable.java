package EmpireLisp;

/**
 * Any object that implements this interface can be unit-tested by the method in the Environment class automatically.
 *
 * @author Tyrerexus
 * @date 11/23/17
 */
@SuppressWarnings("JavaDoc")
public interface IUnitTestable {

    /**
     * This method should return false if the unit-test failed.
     *
     * @param environment The environment to run the unit-test inside of.
     * @return False on failure.
     */
    boolean selfTest(Environment environment);
}
