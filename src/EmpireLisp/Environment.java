package EmpireLisp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
public class Environment {

    public static Expression trueValue = new ExpressionNumber(1);
    public static Expression falseValue = new ExpressionPair(null, null);
    static Expression nilValue = falseValue;

    Map<String, Expression> map = new HashMap<>();
    Environment parent;

    public Environment(Environment parent) {
        this.parent = parent;
    };

    public Expression getVariable(String name) throws LispException {
        Expression result = map.get(name);
        if (result != null) {
            return result;
        }
        else {
            if (parent != null) {
                return parent.getVariable(name);
            }
            else {
                throw new LispException(LispException.ErrorType.UNBOUND_VARIABLE, name);
            }
        }
    }

    public static Environment makeStandardEnvironment() {
        Environment environment = new Environment(null);

        environment.setVariable("true", trueValue);
        environment.setVariable("else", trueValue);
        environment.setVariable("false", falseValue);


        environment.setVariable("+", new ProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {

            @Override
            public Expression operate(ExpressionNumber arg1, ExpressionNumber arg2) {
                return new ExpressionNumber(arg1.number + arg2.number);
            }

            @Override
            public String getType1Name() {
                return "number";
            }

            @Override
            public String getType2Name() {
                return "number";
            }
        });

        environment.setVariable("-", new ProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {

            @Override
            public Expression operate(ExpressionNumber arg1, ExpressionNumber arg2) {
                return new ExpressionNumber(arg1.number - arg2.number);
            }

            @Override
            public String getType1Name() {
                return "number";
            }

            @Override
            public String getType2Name() {
                return "number";
            }
        });

        environment.setVariable("*", new ProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {

            @Override
            public Expression operate(ExpressionNumber arg1, ExpressionNumber arg2) {
                return new ExpressionNumber(arg1.number * arg2.number);
            }

            @Override
            public String getType1Name() {
                return "number";
            }

            @Override
            public String getType2Name() {
                return "number";
            }
        });

        environment.setVariable("/", new ProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {

            @Override
            public Expression operate(ExpressionNumber arg1, ExpressionNumber arg2) {
                return new ExpressionNumber(arg1.number / arg2.number);
            }

            @Override
            public String getType1Name() {
                return "number";
            }

            @Override
            public String getType2Name() {
                return "number";
            }
        });

        environment.setVariable("remainder", new ProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {

            @Override
            public Expression operate(ExpressionNumber arg1, ExpressionNumber arg2) {
                return new ExpressionNumber(arg1.number % arg2.number);
            }

            @Override
            public String getType1Name() {
                return "number";
            }

            @Override
            public String getType2Name() {
                return "number";
            }
        });

        environment.setVariable("=", new ProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {

            @Override
            public Expression operate(ExpressionNumber arg1, ExpressionNumber arg2) {
                return arg1.number == arg2.number ? Environment.trueValue : Environment.falseValue;
            }

            @Override
            public String getType1Name() {
                return "number";
            }

            @Override
            public String getType2Name() {
                return "number";
            }
        });

        environment.setVariable(">", new ProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {

            @Override
            public Expression operate(ExpressionNumber arg1, ExpressionNumber arg2) {
                return arg1.number > arg2.number ? Environment.trueValue : Environment.falseValue;
            }

            @Override
            public String getType1Name() {
                return "number";
            }

            @Override
            public String getType2Name() {
                return "number";
            }
        });

        environment.setVariable(">=", new ProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {

            @Override
            public Expression operate(ExpressionNumber arg1, ExpressionNumber arg2) {
                return arg1.number >= arg2.number ? Environment.trueValue : Environment.falseValue;
            }

            @Override
            public String getType1Name() {
                return "number";
            }

            @Override
            public String getType2Name() {
                return "number";
            }
        });

        environment.setVariable("<", new ProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {

            @Override
            public Expression operate(ExpressionNumber arg1, ExpressionNumber arg2) {
                return arg1.number < arg2.number ? Environment.trueValue : Environment.falseValue;
            }

            @Override
            public String getType1Name() {
                return "number";
            }

            @Override
            public String getType2Name() {
                return "number";
            }
        });

        environment.setVariable("<=", new ProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {

            @Override
            public Expression operate(ExpressionNumber arg1, ExpressionNumber arg2) {
                return arg1.number <= arg2.number ? Environment.trueValue : Environment.falseValue;
            }

            @Override
            public String getType1Name() {
                return "number";
            }

            @Override
            public String getType2Name() {
                return "number";
            }
        });

        environment.setVariable("lambda", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof  ExpressionPair && firstPair.right instanceof ExpressionPair) {
                        ExpressionPair argumentList = (ExpressionPair) firstPair.left;
                        ExpressionPair bodyList = (ExpressionPair) firstPair.right;
                        return new ExpressionLambda(environment, argumentList, bodyList);
                    }
                }
                throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
            }

            @Override
            public boolean isLazyEval() {
                return true;
            }
        });

        environment.setVariable("quote", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;
                    if (firstPair.left instanceof ExpressionPair) {
                        return (ExpressionPair) firstPair.left;
                    }
                    else {
                        throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("list", firstPair.left.toString()));
                    }
                }
                throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
            }

            @Override
            public boolean isLazyEval() {
                return true;
            }
        });

        environment.setVariable("cons", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;
                    if (firstPair.right instanceof  ExpressionPair) {
                        return new ExpressionPair(firstPair.left.eval(environment), ((ExpressionPair) firstPair.right).left.eval(environment));
                    }
                    else {
                        throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                    }
                }
                throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
            }

            @Override
            public boolean isLazyEval() {
                return true;
            }
        });

        environment.setVariable("car", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;
                    if (firstPair.left instanceof ExpressionPair) {
                        return ((ExpressionPair) firstPair.left).left;
                    }
                    else {
                        throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("list", firstPair.left.toString()));
                    }
                }
                throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
            }

            @Override
            public boolean isLazyEval() {
                return false;
            }
        });

        environment.setVariable("cdr", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;
                    if (firstPair.left instanceof ExpressionPair) {
                        return ((ExpressionPair) firstPair.left).right;
                    }
                    else {
                        throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("list", firstPair.left.toString()));
                    }
                }
                throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
            }

            @Override
            public boolean isLazyEval() {
                return false;
            }
        });

        environment.setVariable("cond", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    List<Expression> conditions = firstPair.toList();

                    for (Expression uncheckedCondition : conditions) {
                        if (uncheckedCondition instanceof ExpressionPair) {
                            ExpressionPair condition = (ExpressionPair) uncheckedCondition;
                            if (condition.left.eval(environment).isTrue()) {
                                if (condition.right instanceof ExpressionPair) {
                                    return ((ExpressionPair)condition.right).left.eval(environment);
                                }
                                else {
                                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                                }
                            }
                        }
                        else {
                            throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, "Conditions can only be lists!");
                        }
                    }

                    // No condition matched. Use the last condition as an else. //
                    return new ExpressionPair(null, null); // TODO: What to do?
                }
                else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                }
            }

            @Override
            public boolean isLazyEval() {
                return true;
            }
        });

        environment.setVariable("define", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof ExpressionSymbol) {
                        ExpressionSymbol symbol = (ExpressionSymbol) firstPair.left;
                        if (firstPair.right instanceof ExpressionPair) {
                            Expression value = ((ExpressionPair)firstPair.right).left.eval(environment);
                            environment.setVariable(symbol.symbol, value);
                            return value;
                        }
                        else {
                            throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                        }
                    }
                    else {
                        throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, "Define can only use symbols as keys.");
                    }
                }
                else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                }
            }

            @Override
            public boolean isLazyEval() {
                return true;
            }
        });

        return environment;
    }

    public void setVariable(String name, Expression value) {
        map.put(name, value);
    }
}
