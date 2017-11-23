package EmpireLisp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public class ExpressionLambda extends Expression implements IApplicable {

    private List<String> arguments;
    private List<Expression> body;
    private Environment environment;

    @SuppressWarnings("WeakerAccess")
    public ExpressionLambda(Environment environment, ExpressionPair argumentList, ExpressionPair bodyList) throws LispException {
        this.environment = new Environment(environment);
        this.body = bodyList.toList();
        this.arguments = new ArrayList<>();

        while(argumentList.left != null) {
            if (argumentList.left instanceof ExpressionSymbol) {
                this.arguments.add(((ExpressionSymbol) argumentList.left).symbol);
            }
            else {
                throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, "Only symbols are allowed in the argument list!");
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
    public Expression apply(Environment environment, Expression uncheckedArguments) throws LispException {
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
