package EmpireLisp;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public Expression eval(Environment environment) {
        if (this.right instanceof ExpressionPair) {
            Expression fun = this.left.eval(environment);
            ExpressionPair result;
            if (fun instanceof IApplyable) {
                if (!((IApplyable)fun).isLazyEval()) {
                    ExpressionPair list = (ExpressionPair) this.right;
                    result = new ExpressionPair(null, null);
                    ExpressionPair head = result;
                    while (list.left != null) {
                        head.left = list.left.eval(environment);
                        head.right = new ExpressionPair(null, null);
                        head = (ExpressionPair) head.right;

                        if (list.right instanceof ExpressionPair) {
                            list = (ExpressionPair) list.right;
                        } else {
                            break;
                        }
                    }
                }
                else {
                    result = (ExpressionPair) this.right;
                }

                return ((IApplyable)fun).apply(environment, result);
            }
            else {
                System.out.println("ERROR: List is not applyable"); // TODO: Throw error.
                return null;
            }
        }
        else {
            return this;
        }
    }

    public List<Expression> toList() {
        List<Expression> result = new ArrayList<Expression>();
        ExpressionPair list = this;
        while(list.left != null) {
            result.add(list.left);

            if (list.right instanceof  ExpressionPair) {
                list = (ExpressionPair) list.right;
            }
            else {
                break;
            }
        }
        return result;
    }
}
