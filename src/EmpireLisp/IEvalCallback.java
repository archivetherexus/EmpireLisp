package EmpireLisp;

/**
 * This callback is used for when an evaluation has been completed.
 *
 * @author Tyrerexus
 * @date 11/27/17
 */
@SuppressWarnings("JavaDoc")
public interface IEvalCallback {

    /**
     * This method is called when an evaluation has been completed.
     *
     * @param result The result of the evaluation
     * @throws LispException If an error occurred.
     */
    void evalCallback(Expression result) throws LispException;
}
