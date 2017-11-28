package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/27/17
 */
@SuppressWarnings("JavaDoc")
public interface IEvalCallback {
    void evalCallback(Expression result) throws LispException;
}
