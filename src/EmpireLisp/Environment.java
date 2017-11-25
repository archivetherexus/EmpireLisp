package EmpireLisp;

import java.io.ByteArrayInputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public class Environment {


    @SuppressWarnings("WeakerAccess")
    public static Expression trueValue = new ExpressionNumber(1);

    @SuppressWarnings("WeakerAccess")
    public static Expression falseValue = new ExpressionPair(null, null);

    @SuppressWarnings("WeakerAccess")
    public static Expression nilValue = falseValue;

    private Map<String, Expression> map = new HashMap<>();

    private Environment parent;

    @SuppressWarnings("WeakerAccess")
    public Environment(Environment parent) {
        this.parent = parent;
    }

    @SuppressWarnings("WeakerAccess")
    public Expression getVariable(String name) throws LispException {
        Expression result = map.get(name);
        if (result != null) {
            return result;
        } else {
            if (parent != null) {
                return parent.getVariable(name);
            } else {
                throw new LispException(LispException.ErrorType.UNBOUND_VARIABLE, name);
            }
        }
    }

    public static Environment makeStandardEnvironment() {
        Environment environment = new Environment(null);

        environment.setVariable("true", trueValue);
        environment.setVariable("else", trueValue);
        environment.setVariable("false", falseValue);

        abstract class SafeProcedureBinaryOperator<T1 extends Expression, T2 extends Expression> extends ProcedureBinaryOperator<T1, T2> implements IUnitTestable {
            SafeProcedureBinaryOperator(Class<T1> type1, Class<T2> type2) {
                super(type1, type2);
            }
        }

        abstract class SafeExpressionPrimitive extends ExpressionPrimitive implements IUnitTestable {
        }

        environment.setVariable("+", new SafeProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(+ 20 20)", "40");
            }

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

        environment.setVariable("-", new SafeProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(- 20 20)", "0");
            }

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

        environment.setVariable("*", new SafeProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(* 20 20)", "400");
            }

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

        environment.setVariable("/", new SafeProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(/ 20 20)", "1");
            }

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

        environment.setVariable("remainder", new SafeProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(remainder 20 20)", "0");
            }

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

        environment.setVariable("=", new SafeProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(= 2 2)", "true") &&
                        environment.evalTest("(= 2 1)", "false");
            }

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

        environment.setVariable(">", new SafeProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(> 2 2)", "false") &&
                        environment.evalTest("(> 2 1)", "true") &&
                        environment.evalTest("(> 1 2)", "false");
            }

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

        environment.setVariable(">=", new SafeProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(>= 2 2)", "true") &&
                        environment.evalTest("(>= 2 1)", "true") &&
                        environment.evalTest("(>= 1 2)", "false");
            }

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

        environment.setVariable("<", new SafeProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(< 2 2)", "false") &&
                        environment.evalTest("(< 2 1)", "false") &&
                        environment.evalTest("(< 1 2)", "true");
            }

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

        environment.setVariable("<=", new SafeProcedureBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(<= 2 2)", "true") &&
                        environment.evalTest("(<= 2 1)", "false") &&
                        environment.evalTest("(<= 1 2)", "true");
            }

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

        environment.setVariable("eq", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(cons 1 2)", "(cons 1 2)");
            }

            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;
                    if (firstPair.right instanceof ExpressionPair) {
                        ExpressionPair secondPair = (ExpressionPair) firstPair.right;
                        Expression valueA = firstPair.left;
                        Expression valueB = secondPair.left;
                        return valueA.equals(valueB) ? trueValue : falseValue;
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

        environment.setVariable("lambda", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("((lambda (x) x) 4)", "4");
            }

            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof ExpressionPair && firstPair.right instanceof ExpressionPair) {
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

        environment.setVariable("quote", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(quote hello!)", "(quote hello!)");
            }

            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;
                    return firstPair.left;
                }
                throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
            }

            @Override
            public boolean isLazyEval() {
                return true;
            }
        });

        environment.setVariable("length", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(length \"Hello\")", "5");
            }

            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof ISequence) {
                        ISequence valueA = (ISequence) firstPair.left;
                        return valueA.getLength();
                    }
                    else {
                        throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("sequence", firstPair.left.toString()));
                    }
                }
                throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
            }

            @Override
            public boolean isLazyEval() {
                return true;
            }
        });

        environment.setVariable("at", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(at \"Hello\" 0)", "" + (int)'H');
            }

            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;
                    if (firstPair.right instanceof ExpressionPair) {
                        ExpressionPair secondPair = (ExpressionPair) firstPair.right;
                        if (firstPair.left instanceof ISequence) {
                            ISequence valueA = (ISequence) firstPair.left;
                            if (secondPair.left instanceof ExpressionNumber) {
                                ExpressionNumber valueB = (ExpressionNumber) secondPair.left;
                                return valueA.atIndex(valueB);
                            }
                            else {
                                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("sequence", secondPair.left.toString()));
                            }
                        }
                        else {
                            throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("sequence", firstPair.left.toString()));
                        }
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

        environment.setVariable("concat", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(concat \"Hello\" \"World\")", "\"HelloWorld\"");
            }

            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;
                    if (firstPair.right instanceof ExpressionPair) {
                        ExpressionPair secondPair = (ExpressionPair) firstPair.right;
                        if (firstPair.left instanceof ISequence) {
                            ISequence valueA = (ISequence) firstPair.left;
                            if (secondPair.left instanceof ISequence) {
                                ISequence valueB = (ISequence) secondPair.left;
                                ISequence result = valueA.concatenate(valueB);
                                if (result instanceof Expression) {
                                    return (Expression) result;
                                }
                                else {
                                    throw new LispException(LispException.ErrorType.INTERNAL_ERROR);
                                }
                            }
                            else {
                                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("sequence", secondPair.left.toString()));
                            }
                        }
                        else {
                            throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("sequence", firstPair.left.toString()));
                        }
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

        environment.setVariable("cons", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(cons 1 2)", "(cons 1 2)");
            }

            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;
                    if (firstPair.right instanceof ExpressionPair) {
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

        environment.setVariable("car", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(car (cons 1 2))", "1");
            }

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

        environment.setVariable("cdr", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(cdr (cons 1 2))", "2");
            }

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

        environment.setVariable("if", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(if false 14 (+ 21 21))", "42") &&
                        environment.evalTest("(if true 14 (+ 21 21))", "14") &&
                        environment.evalTest("(if true (+ 7 7) 42)", "14");
            }

            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.right instanceof ExpressionPair) {
                        ExpressionPair secondPair = (ExpressionPair) firstPair.right;

                        if (secondPair.right instanceof ExpressionPair) {
                            ExpressionPair thirdPair = (ExpressionPair) secondPair.right;

                            Expression testExpression = firstPair.left;
                            Expression thenExpression = secondPair.left;
                            Expression elseExpression = thirdPair.left;

                            if (testExpression.eval(environment).isTrue()) {
                                return thenExpression.eval(environment);
                            } else {
                                return elseExpression.eval(environment);
                            }
                        } else {
                            throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                        }
                    } else {
                        throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                    }
                } else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                }
            }

            @Override
            public boolean isLazyEval() {
                return true;
            }
        });

        environment.setVariable("cond", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(cond ((= 1 0) 32) (else 64))", "64") &&
                        environment.evalTest("(cond ((= 1 1) 32) (else 64))", "32") &&
                        environment.evalTest("(cond ((= 9 1) 99) ((= 1 1) 32) (else 64))", "32");
            }

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
                                    return ((ExpressionPair) condition.right).left.eval(environment);
                                } else {
                                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                                }
                            }
                        } else {
                            throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, "Conditions can only be lists!");
                        }
                    }

                    // No condition matched. Use the last condition as an else. //
                    return nilValue; // TODO: What to do?
                } else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                }
            }

            @Override
            public boolean isLazyEval() {
                return true;
            }
        });

        environment.setVariable("define", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(define x (+ 7 7))", "14") &&
                        environment.evalTest("((lambda () (define x 6) (+ 2 x)))", "8");
            }

            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof ExpressionSymbol) {
                        ExpressionSymbol symbol = (ExpressionSymbol) firstPair.left;
                        if (firstPair.right instanceof ExpressionPair) {
                            Expression value = ((ExpressionPair) firstPair.right).left.eval(environment);
                            environment.setVariable(symbol.symbol, value);
                            return value;
                        } else {
                            throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                        }
                    } else {
                        throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, "Define can only use symbols as keys.");
                    }
                } else {
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

    @SuppressWarnings("WeakerAccess")
    public void setVariable(String name, Expression value) {
        map.put(name, value);
    }

    private boolean evalTest(String what, String expect) {
        try {
            String charset = StandardCharsets.UTF_8.name();
            Parser parser = new Parser();
            Environment environment = new Environment(this);
            PushbackInputStream stream1 = new PushbackInputStream(new ByteArrayInputStream(what.getBytes(charset)));
            PushbackInputStream stream2 = new PushbackInputStream(new ByteArrayInputStream(expect.getBytes(charset)));

            Expression expression1 = parser.parseExpression(stream1).eval(environment);
            Expression expression2 = parser.parseExpression(stream2).eval(environment);

            return expression1.equals(expression2);
        } catch (UnsupportedEncodingException | LispException e) {
            e.printStackTrace();
            return false;
        }
    }

    static public void standardEnvironmentTest() throws LispException {
        Environment standardEnvironment = makeStandardEnvironment();
        for (Map.Entry<String, Expression> values : standardEnvironment.map.entrySet()) {
            if (values.getValue() instanceof IUnitTestable) {
                if (!((IUnitTestable) values.getValue()).selfTest(standardEnvironment)) {
                    throw new LispException(LispException.ErrorType.UNIT_TEST_FAILURE, values.getKey());
                }
            }
        }
    }
}
