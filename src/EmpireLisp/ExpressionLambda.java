package EmpireLisp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
public class ExpressionLambda extends Expression implements IApplyable {

    List<String> arguments;
    List<Expression> body;
    Environment environment;

    public ExpressionLambda(Environment environment, ExpressionPair argumentList, ExpressionPair bodyList) {
        this.environment = new Environment(environment);
        this.body = bodyList.toList();
        this.arguments = new ArrayList<>();

        while(argumentList.left != null) {
            if (argumentList.left instanceof ExpressionSymbol) {
                this.arguments.add(((ExpressionSymbol) argumentList.left).symbol);
            }
            else {
                System.out.println("ERROR: Only symbols are allowed in the argument list!"); // TODO: Throw error.
            }

            if (argumentList.right instanceof  ExpressionPair) {
                argumentList = (ExpressionPair) argumentList.right;
            }
            else {
                break;
            }
        }
    }


    @Override
    public String toString() {
        return "compound-procedure";
    }

    @Override
    public Expression eval(Environment environment) {
        return this;
    }

    @Override
    public Expression apply(Environment environment, Expression uncheckedArguments) {
        Expression result = null;

        if (uncheckedArguments instanceof ExpressionPair) {
            ExpressionPair arguments = (ExpressionPair) uncheckedArguments;
            Iterator<String> iterator = this.arguments.iterator();
            while (arguments.left != null && iterator.hasNext()) {
                this.environment.setVariable(iterator.next(), arguments.left);

                if (arguments.right instanceof ExpressionPair) {
                    arguments = (ExpressionPair) arguments.right;
                } else {
                    break;
                }
            }
            // TODO: Check for arity-missmatch!
        }

        for (Expression expression : body) {
            result = expression.eval(this.environment);
        }

        return result;
    }

    @Override
    public boolean isLazyEval() {
        return false;
    }
}
