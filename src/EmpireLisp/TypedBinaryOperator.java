package EmpireLisp;

/**
 * A typed binary operator. It will check that the arguments given when apply() is called match the requested type.
 * A binary operator takes in two arguments.
 *
 * @author Tyrerexus
 * @date 11/22/17
 */
@SuppressWarnings({"JavaDoc", "Convert2Lambda"})
public abstract class TypedBinaryOperator<T1 extends Expression, T2 extends Expression> extends ExpressionPrimitive {
    private Class<T1> type1;
    private Class<T2> type2;

    TypedBinaryOperator(Class<T1> type1, Class<T2> type2) {
        this.type1 = type1;
        this.type2 = type2;
    }

    /**
     * Operates on two values that are of the requested type. No additional checks should be needed.
     * All checking is done by apply().
     *
     * @param arg1 The first argument.
     * @param arg2 The second argument.
     * @return The result of operating on arg1 and arg2.
     */
    public abstract Expression operate(T1 arg1, T2 arg2);

    /**
     * This method is called when there is an "arity-miss-match" error.
     * It should return the name of the requested type for arg1.
     *
     * @return The name of the requested type for arg1.
     */
    public abstract String getType1Name();

    /**
     * This method is called when there is an "arity-miss-match" error.
     * It should return the name of the requested type for arg2.
     *
     * @return The name of the requested type for arg2.
     */
    public abstract String getType2Name();

    @SuppressWarnings("unchecked")
    @Override
    public void apply(IEvaluator evaluator, Environment environment, ExpressionPair firstPair, IEvalCallback callback) throws LispException {
        firstPair.left.eval(evaluator, environment, new IEvalCallback() {
            @Override
            public void evalCallback(Expression uncheckedValueA) throws LispException {
                if (type1.isInstance(uncheckedValueA)) {
                    T1 valueA = (T1) uncheckedValueA;

                    if (firstPair.right instanceof ExpressionPair) {
                        ExpressionPair secondPair = (ExpressionPair) firstPair.right;

                        if (!secondPair.left.isNil() && secondPair.right.isNil()) {
                            secondPair.left.eval(evaluator, environment, new IEvalCallback() {
                                @Override
                                public void evalCallback(Expression uncheckedValueB) throws LispException {
                                    if (type2.isInstance(uncheckedValueB)) {
                                        T2 valueB = (T2) uncheckedValueB;
                                        callback.evalCallback(operate(valueA, valueB));
                                    } else {
                                        throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType(getType2Name(), uncheckedValueB.toString()));
                                    }
                                }
                            });
                        } else {
                            int argsReceived = firstPair.toList().size();
                            throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedAmountOfArguments(2, argsReceived));
                        }
                    } else {
                        throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                    }
                } else {
                    throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType(getType1Name(), uncheckedValueA.toString()));
                }
            }
        });
    }
}
