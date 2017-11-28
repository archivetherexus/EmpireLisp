package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/22/17
 */
@SuppressWarnings("JavaDoc")
public abstract class ProcedureBinaryOperator<T1 extends Expression, T2 extends Expression> extends ExpressionPrimitive {
    private Class<T1> type1;
    private Class<T2> type2;

    ProcedureBinaryOperator(Class<T1> type1, Class<T2> type2) {
        this.type1 = type1;
        this.type2 = type2;
    }

    public abstract Expression operate(T1 arg1, T2 arg2);
    public abstract String getType1Name();
    public abstract String getType2Name();

    @SuppressWarnings("unchecked")
    @Override
    public void apply(Environment environment, Expression arguments, IEvalCallback callback) throws LispException {
        if (arguments instanceof ExpressionPair) {
            ExpressionPair firstPair = (ExpressionPair) arguments;

            if (type1.isInstance(firstPair.left)) {
                T1 valueA = (T1) firstPair.left;

                if (firstPair.right instanceof ExpressionPair) {
                    ExpressionPair secondPair = (ExpressionPair) firstPair.right;

                    if (secondPair.left != null && (secondPair.right == null || secondPair.right.isNil())) {
                        if (type2.isInstance(secondPair.left)) {
                            T2 valueB = (T2) secondPair.left;
                            callback.evalCallback(operate(valueA, valueB));
                        } else {
                            throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType(getType2Name(), secondPair.left.toString()));
                        }
                    }
                    else {
                        int argsReceived = ((ExpressionPair) arguments).toList().size();
                        throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedAmountOfArguments(2, argsReceived));
                    }
                }
                else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                }
            }
            else {
                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType(getType1Name(), firstPair.left.toString()));
            }
        }
        else {
            throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
        }
    }

    @Override
    public boolean isLazyEval() {
        return false;
    }
}
