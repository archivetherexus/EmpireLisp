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
    public ExpressionLambda(Environment environment, Expression argumentList, ExpressionPair bodyList) throws LispException {
        this.parentEnvironment = environment;
        this.body = bodyList.toList();
        this.arguments = new ArrayList<>();

        if (argumentList instanceof ExpressionPair) {
            while (true) {
                ExpressionPair pair = (ExpressionPair) argumentList;
                if (pair.left instanceof ExpressionSymbol) {
                    this.arguments.add(((ExpressionSymbol) pair.left).symbol);
                } else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, "Only symbols are allowed in the argument value!");
                }
                if (pair.right instanceof ExpressionPair) {
                    argumentList = pair.right;
                }
                else {
                    break;
                }
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
        final int argumentsExpected = this.arguments.size();

        class OnDoneCallback {
            void callback() throws LispException {
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
        }
        OnDoneCallback done = new OnDoneCallback();

        if (uncheckedArguments instanceof ExpressionPair) {
            Iterator<Expression> iterator1 = ((ExpressionPair) uncheckedArguments).iterator();
            Iterator<String> iterator2 = arguments.iterator();
            iterator1.next().eval(environment, new IEvalCallback() {
                @Override
                public void evalCallback(Expression result) throws LispException {
                    if (iterator2.hasNext()) {
                        lambdaEnvironment.setVariable(iterator2.next(), result);
                        if (iterator1.hasNext()) {
                            iterator1.next().eval(environment, this);
                        } else {
                            if (!iterator2.hasNext()) {
                                done.callback();
                            }
                            else {
                                int numberOfArguments = ((ExpressionPair) uncheckedArguments).toList().size();
                                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedAmountOfArguments(argumentsExpected, numberOfArguments));
                            }
                        }
                    }
                    else {
                        int numberOfArguments = ((ExpressionPair) uncheckedArguments).toList().size();
                        throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedAmountOfArguments(argumentsExpected, numberOfArguments));
                    }
                }
            });
        }
        else if (uncheckedArguments.isNil()) {
            done.callback();
        }
        else {
            throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
        }
    }
}
