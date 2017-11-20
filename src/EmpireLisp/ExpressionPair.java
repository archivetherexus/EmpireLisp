package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
public class ExpressionPair extends Expression {
    public Expression left;
    public Expression right;

    public ExpressionPair(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        if (left != null || right != null) {
            return "(" + (left != null ? left.toString() : "") + " . " + (right != null ? right.toString() : "") + ")";
        }
        else {
            return "()";
        }
    }
}
