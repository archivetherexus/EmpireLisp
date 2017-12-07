package EmpireLisp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A lambda as an Expression.
 *
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

        Iterator<Expression> i = argumentList.iterator();
        while (i.hasNext()) {
            Expression unchecked = i.next();
            if (unchecked instanceof ExpressionSymbol) {
                this.arguments.add(((ExpressionSymbol) unchecked).symbol);
            } else {
                throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, "Only symbols are allowed in the argument value!");
            }
        }

        /*
        while (true) {
            ExpressionPair pair = (ExpressionPair) argumentList;
            if (pair.left instanceof ExpressionSymbol) {
                this.arguments.add(((ExpressionSymbol) pair.left).symbol);
            } else if (!pair.isNil()) {
                throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, "Only symbols are allowed in the argument value!");
            }
            if (pair.right instanceof ExpressionPair) {
                argumentList = pair.right;
            } else {
                break;
            }
        }
        */
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
    public void eval(IEvaluator evaluator, Environment environment, IEvalCallback callback) throws LispException {
        callback.evalCallback(this);
    }

    @Override
    public void apply(IEvaluator evaluator, Environment environment, ExpressionPair arguments, IEvalCallback callback) throws LispException {
        Environment lambdaEnvironment = new Environment(this.parentEnvironment);
        final int argumentsExpected = this.arguments.size();

        class OnDoneCallback {
            void callback() throws LispException {
                Iterator<Expression> iterator = body.iterator();
                iterator.next().eval(evaluator, lambdaEnvironment, new IEvalCallback() {
                    @Override
                    public void evalCallback(Expression result) throws LispException {
                        if (iterator.hasNext()) {
                            iterator.next().eval(evaluator, lambdaEnvironment, this);
                        } else {
                            callback.evalCallback(result);
                        }
                    }
                });
            }
        }
        OnDoneCallback done = new OnDoneCallback();

        Iterator<Expression> iterator1 = arguments.iterator();
        Iterator<String> iterator2 = this.arguments.iterator();
        if (iterator1.hasNext()) {
            iterator1.next().eval(evaluator, environment, new IEvalCallback() {
                @Override
                public void evalCallback(Expression result) throws LispException {
                    if (iterator2.hasNext()) {
                        lambdaEnvironment.setVariable(iterator2.next(), result);
                        if (iterator1.hasNext()) {
                            iterator1.next().eval(evaluator, environment, this);
                        } else {
                            if (!iterator2.hasNext()) {
                                done.callback();
                            } else {
                                int numberOfArguments = arguments.toList().size();
                                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedAmountOfArguments(argumentsExpected, numberOfArguments));
                            }
                        }
                    } else {
                        int numberOfArguments = arguments.toList().size();
                        throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedAmountOfArguments(argumentsExpected, numberOfArguments));
                    }
                }
            });
        } else {
            done.callback();
        }
    }
}
