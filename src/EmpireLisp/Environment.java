package EmpireLisp;

import java.io.ByteArrayInputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings({"JavaDoc", "Convert2Lambda"})
public class Environment {


    @SuppressWarnings("WeakerAccess")
    public static Expression trueValue = new ExpressionNumber(1);

    @SuppressWarnings("WeakerAccess")
    public static Expression falseValue = new ExpressionPair(null, null);

    @SuppressWarnings("WeakerAccess")
    public static Expression nilValue = new ExpressionNil();

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
                return environment.evalTest("(+ 20 20)", "40") &&
                        environment.evalTest("(+ (+ 10 10) (+ 10 10))", "40");
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
                return environment.evalTest("(eq 1 2)", "false") &&
                        environment.evalTest("(eq (+ 10 10) (+ 10 10))", "true") &&
                        environment.evalTest("(eq (+ 11 11) (+ 10 10))", "false");
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, Expression arguments, IEvalCallback callback) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;
                    if (firstPair.right instanceof ExpressionPair) {
                        ExpressionPair secondPair = (ExpressionPair) firstPair.right;
                        Expression valueA = firstPair.left;
                        Expression valueB = secondPair.left;
                        valueA.eval(evaluator, environment, new IEvalCallback() {
                            @Override
                            public void evalCallback(Expression resultA) throws LispException {
                                valueB.eval(evaluator, environment, new IEvalCallback() {
                                    @Override
                                    public void evalCallback(Expression resultB) throws LispException {
                                        callback.evalCallback(resultA.equals(resultB) ? trueValue : falseValue);
                                    }
                                });
                            }
                        });
                    } else {
                        throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                    }
                } else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                }
            }
        });

        environment.setVariable("lambda", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("((lambda (x) x) 4)", "4") &&
                        environment.evalTest("((lambda () 12))", "12") &&
                        environment.evalTest("((lambda (x y z) (+ x (+ y z))) 1 2 3)", "6");
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, Expression arguments, IEvalCallback callback) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;
                    if (firstPair.right instanceof ExpressionPair) {
                        Expression argumentList = firstPair.left;
                        ExpressionPair bodyList = (ExpressionPair) firstPair.right;
                        callback.evalCallback(new ExpressionLambda(environment, argumentList, bodyList));
                    } else {
                        throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("pair", firstPair.right.toString()));
                    }
                } else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                }
            }
        });

        environment.setVariable("quote", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(quote hello!)", "(quote hello!)");
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, Expression arguments, IEvalCallback callback) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;
                    callback.evalCallback(firstPair.left);
                } else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                }
            }
        });

        environment.setVariable("length", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(length \"Hello\")", "5");
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, Expression arguments, IEvalCallback callback) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof ISequence) {
                        ISequence valueA = (ISequence) firstPair.left;
                        callback.evalCallback(valueA.getLength());
                    } else {
                        throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("sequence", firstPair.left.toString()));
                    }
                } else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                }
            }
        });

        environment.setVariable("at", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(at \"Hello\" 0)", "" + (int) 'H');
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, Expression arguments, IEvalCallback callback) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;
                    if (firstPair.right instanceof ExpressionPair) {
                        ExpressionPair secondPair = (ExpressionPair) firstPair.right;
                        if (firstPair.left instanceof ISequence) {
                            ISequence valueA = (ISequence) firstPair.left;
                            if (secondPair.left instanceof ExpressionNumber) {
                                ExpressionNumber valueB = (ExpressionNumber) secondPair.left;
                                callback.evalCallback(valueA.atIndex(valueB));
                            } else {
                                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("sequence", secondPair.left.toString()));
                            }
                        } else {
                            throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("sequence", firstPair.left.toString()));
                        }
                    } else {
                        throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                    }
                } else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                }
            }
        });

        environment.setVariable("concat", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(concat \"Hello\" \"World\")", "\"HelloWorld\"");
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, Expression arguments, IEvalCallback callback) throws LispException {
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
                                    callback.evalCallback((Expression) result);
                                } else {
                                    throw new LispException(LispException.ErrorType.INTERNAL_ERROR);
                                }
                            } else {
                                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("sequence", secondPair.left.toString()));
                            }
                        } else {
                            throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("sequence", firstPair.left.toString()));
                        }
                    } else {
                        throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                    }
                } else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                }
            }
        });

        environment.setVariable("cons", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(cons 1 2)", "(cons 1 2)");
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, Expression arguments, IEvalCallback callback) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;
                    if (firstPair.right instanceof ExpressionPair) {
                        firstPair.left.eval(evaluator, environment, new IEvalCallback() {
                            @Override
                            public void evalCallback(Expression valueA) throws LispException {
                                ((ExpressionPair) firstPair.right).left.eval(evaluator, environment, new IEvalCallback() {
                                    @Override
                                    public void evalCallback(Expression valueB) throws LispException {
                                        callback.evalCallback(new ExpressionPair(valueA, valueB));
                                    }
                                });
                            }
                        });
                    } else {
                        throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                    }
                } else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                }
            }
        });

        environment.setVariable("car", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(car (cons 1 2))", "1");
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, Expression arguments, IEvalCallback callback) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ((ExpressionPair) arguments).left.eval(evaluator, environment, new IEvalCallback() {
                        @Override
                        public void evalCallback(Expression result) throws LispException {
                            if (result instanceof ExpressionPair) {
                                callback.evalCallback(((ExpressionPair) result).left);
                            } else {
                                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("pair", result.toString()));
                            }
                        }
                    });
                } else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                }
            }
        });

        environment.setVariable("cdr", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(cdr (cons 1 2))", "2");
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, Expression arguments, IEvalCallback callback) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ((ExpressionPair) arguments).left.eval(evaluator, environment, new IEvalCallback() {
                        @Override
                        public void evalCallback(Expression result) throws LispException {
                            if (result instanceof ExpressionPair) {
                                callback.evalCallback(((ExpressionPair) result).right);
                            } else {
                                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("pair", result.toString()));
                            }
                        }
                    });
                } else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                }
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
            public void apply(IEvaluator evaluator, Environment environment, Expression arguments, IEvalCallback callback) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.right instanceof ExpressionPair) {
                        ExpressionPair secondPair = (ExpressionPair) firstPair.right;

                        if (secondPair.right instanceof ExpressionPair) {
                            ExpressionPair thirdPair = (ExpressionPair) secondPair.right;

                            Expression testExpression = firstPair.left;
                            Expression thenExpression = secondPair.left;
                            Expression elseExpression = thirdPair.left;

                            testExpression.eval(evaluator, environment, new IEvalCallback() {
                                @Override
                                public void evalCallback(Expression result) throws LispException {
                                    if (result.isTrue()) {
                                        thenExpression.eval(evaluator, environment, callback);
                                    } else {
                                        elseExpression.eval(evaluator, environment, callback);
                                    }
                                }
                            });
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
        });

        environment.setVariable("cond", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(cond ((= 1 0) 32) (else 64))", "64") &&
                        environment.evalTest("(cond ((= 1 1) 32) (else 64))", "32") &&
                        environment.evalTest("(cond ((= 9 1) 99) ((= 1 1) 32) (else 64))", "32");
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, Expression arguments, IEvalCallback callback) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    List<Expression> conditions = firstPair.toList();

                    Iterator<Expression> iterator = conditions.iterator();
                    Expression uncheckedCondition = iterator.next();
                    if (uncheckedCondition instanceof ExpressionPair) {

                        class ConditionWrapper {
                            ExpressionPair value = (ExpressionPair) uncheckedCondition;
                        }
                        ConditionWrapper condition = new ConditionWrapper();
                        condition.value.left.eval(evaluator, environment, new IEvalCallback() {
                            @Override
                            public void evalCallback(Expression result) throws LispException {
                                if (result.isTrue()) {
                                    if (condition.value.right instanceof ExpressionPair) {
                                        ((ExpressionPair) condition.value.right).left.eval(evaluator, environment, callback);
                                    } else {
                                        throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                                    }
                                } else if (iterator.hasNext()) {
                                    Expression uncheckedCondition = iterator.next();
                                    if (uncheckedCondition instanceof ExpressionPair) {
                                        condition.value = (ExpressionPair) uncheckedCondition;
                                        condition.value.left.eval(evaluator, environment, this);
                                    } else {
                                        throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                                    }
                                } else {
                                    callback.evalCallback(nilValue); // FIXME: Is this correct?
                                }
                            }
                        });
                    } else {
                        throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                    }
                } else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                }
            }
        });

        environment.setVariable("define", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(define x (+ 7 7))", "14") &&
                        environment.evalTest("((lambda () (define x 6) (+ 2 x)))", "8");
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, Expression arguments, IEvalCallback callback) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof ExpressionSymbol) {
                        ExpressionSymbol symbol = (ExpressionSymbol) firstPair.left;
                        if (firstPair.right instanceof ExpressionPair) {
                            ((ExpressionPair) firstPair.right).left.eval(evaluator, environment, new IEvalCallback() {
                                @Override
                                public void evalCallback(Expression result) throws LispException {
                                    environment.setVariable(symbol.symbol, result);
                                    callback.evalCallback(result);
                                }
                            });
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

            IEvaluator evaluator = new IEvaluator() {
                @Override
                public boolean continueEvaluation() {
                    return true;
                }

                @Override
                public void stashEvaluation(Expression expression, Environment environment, IEvalCallback callback) {

                }
            };

            class Result {
                boolean result = false;
                boolean callback_fired = false;
            }
            Result result = new Result();

            parser.parseExpression(stream1).eval(evaluator, environment, new IEvalCallback() {
                @Override
                public void evalCallback(Expression expression1) throws LispException {
                    parser.parseExpression(stream2).eval(evaluator, environment, new IEvalCallback() {
                        @Override
                        public void evalCallback(Expression expression2) throws LispException {
                            result.result = expression1.equals(expression2);
                            result.callback_fired = true;
                        }
                    });
                }
            });

            if (!result.callback_fired) {
                throw new LispException(LispException.ErrorType.INTERNAL_ERROR, "Callback did not fire!");
            }

            return result.result;
        } catch (UnsupportedEncodingException | LispException e) {
            e.printStackTrace();
            return false;
        }
    }

    static public void standardEnvironmentTest() throws LispException {
        Environment standardEnvironment = makeStandardEnvironment();
        //standardEnvironment.evalTest("((lambda (x) x) 12)", "12");
        standardEnvironment.evalTest("((lambda () 12))", "12");
        for (Map.Entry<String, Expression> values : standardEnvironment.map.entrySet()) {
            if (values.getValue() instanceof IUnitTestable) {
                if (!((IUnitTestable) values.getValue()).selfTest(standardEnvironment)) {
                    throw new LispException(LispException.ErrorType.UNIT_TEST_FAILURE, values.getKey());
                }
            }
        }
    }
}
