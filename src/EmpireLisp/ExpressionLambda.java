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
    private Environment parentEnvironment;

    @SuppressWarnings("WeakerAccess")
    public ExpressionLambda(Environment environment, ExpressionPair argumentList, ExpressionPair bodyList) throws LispException {
        this.parentEnvironment = environment;
        this.body = bodyList.toList();
        this.arguments = new ArrayList<>();

        while(argumentList.left != null) {
            if (argumentList.left instanceof ExpressionSymbol) {
                this.arguments.add(((ExpressionSymbol) argumentList.left).symbol);
            }
            else {
                throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, "Only symbols are allowed in the argument value!");
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
    public boolean equals(Expression other) {
        return this == other; // TODO: Is this right?
    }

    @Override
    public void eval(Environment environment, IEvalCallback callback) throws LispException {
        callback.evalCallback(this);
    }

    @Override
    public void apply(Environment environment, Expression uncheckedArguments, IEvalCallback callback) throws LispException {
        Environment lambdaEnvironment = new Environment(this.parentEnvironment);

        int numberOfArguments = 0;
        if (uncheckedArguments instanceof ExpressionPair) {
            ExpressionPair arguments = (ExpressionPair) uncheckedArguments;
            Iterator<String> iterator = this.arguments.iterator();
            while (arguments.left != null) {
                numberOfArguments++;
                if (iterator.hasNext()){
                    lambdaEnvironment.setVariable(iterator.next(), arguments.left);
                }
                if (arguments.right instanceof ExpressionPair) {
                    arguments = (ExpressionPair) arguments.right;
                } else {
                    break;
                }
            }
        }
        else {
            throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
        }
        if (numberOfArguments != arguments.size()) {
            throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedAmountOfArguments(arguments.size(), numberOfArguments));
        }

        Iterator<Expression> iterator = body.iterator();
        iterator.next().eval(lambdaEnvironment, new IEvalCallback() {
            @Override
            public void evalCallback(Expression result) throws LispException {
                if (iterator.hasNext()) {
                    iterator.next().eval(lambdaEnvironment, this);
                }
                else {
                    callback.evalCallback(result);
                }
            }
        });
    }

    @Override
    public boolean isLazyEval() {
        return false;
    }
}
