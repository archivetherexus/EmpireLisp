package EmpireLisp;

/**
 * An abstraction layer for writing lists made out of ExpressionPairs.
 *
 * @author Tyrerexus
 * @date 12/7/17
 */
public class ListWriter {
    private ExpressionPair head;
    private ExpressionPair result = null;

    /**
     * Pushes an expression to the list.
     *
     * @param expression The expression to add to the list
     */
    @SuppressWarnings("WeakerAccess")
    public void push(Expression expression) {
        if (result == null) {
            result = new ExpressionPair(expression, Environment.nilValue);
            head = result;
        } else {
            head.right = new ExpressionPair(expression, Environment.nilValue);
            head = (ExpressionPair) head.right;
        }
    }

    /**
     * Gets the resulting list of the list writer.
     *
     * @return The resulting list of the list writer.
     */
    public ExpressionPair getResult() {
        if (result == null) {
            return Environment.nilValue;
        } else {
            return result;
        }
    }

    // TODO: Write unit-test!
}
