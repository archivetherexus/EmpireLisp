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
 * An Environment contains a HashMap of Expressions that can be accessed through a string value.
 * An Environment can also contain a parent Environment which is used for
 * when a variable is not found in the child Environment.
 * <p>
 * This class also a makeStandardEnvironment() method that will create an environment
 * containing all the standard procedures and constants.
 * <p>
 * The class also contains a standardEnvironmentTest() method that can be used to check the sanity of all the
 * procedures (that implement IUnitTestable) defined in makeStandardEnvironment().
 *
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

    /**
     * An abstraction layer for the internal HashMap.
     *
     * @param name  The name of the variable you want to set.
     * @param value The new value of the variable.
     */
    @SuppressWarnings("WeakerAccess")
    public void setVariable(String name, Expression value) {
        map.put(name, value);
    }

    /**
     * An abstraction layer for the internal HashMap.
     * OBS: If there is a parent Environment and the value is not found by it's name
     * in the HasMap it will be searched for in the parent Environment.
     *
     * @param name The name of the variable to "find"
     * @return The found value.
     * @throws LispException Throws a "unbound-variable" error if the value is not found.
     */
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

    /**
     * Creates and populates an environment with all the standard procedures and constants.
     *
     * @return The created standard environment.
     */
    public static Environment makeStandardEnvironment() {
        Environment environment = new Environment(null);

        /* Constants non-procedures: */

        environment.setVariable("true", trueValue);
        environment.setVariable("else", trueValue);
        environment.setVariable("false", falseValue);
        environment.setVariable("nil", nilValue);


        /* Operator classes: */

        /* Naming convention for the operator classes:
         * Add a "Safe" prefix if the class extends IUnitTestable
         * Then use "Typed" if the class checks that the arguments are correct, else use "Untyped".
         * Then use "Unary, Binary, Ternary, etc..." depending on how many arguments the class takes.
         * Finally add an "Operator" suffix.
         *
         * Example:
         * SafeTypedBinaryOperator
         *     This means:
         *     - The operator has a unit test.
         *     - The operator checks the type.
         *     - The operator takes two arguments.
         *     - The operator is an... operator...
         */

        abstract class SafeTypedBinaryOperator<T1 extends Expression, T2 extends Expression> extends TypedBinaryOperator<T1, T2> implements IUnitTestable {
            SafeTypedBinaryOperator(Class<T1> type1, Class<T2> type2) {
                super(type1, type2);
            }
        }

        abstract class SafeUntypedBinaryOperator extends ExpressionPrimitive implements IUnitTestable {
            abstract Expression operate(Expression a, Expression B);

            @Override
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair firstPair, IEvalCallback callback) throws LispException {
                firstPair.left.eval(evaluator, environment, new IEvalCallback() {
                    @Override
                    public void evalCallback(Expression valueA) throws LispException {
                        if (firstPair.right instanceof ExpressionPair) {
                            ExpressionPair secondPair = (ExpressionPair) firstPair.right;

                            if (!secondPair.left.isNil() && secondPair.right.isNil()) {
                                secondPair.left.eval(evaluator, environment, new IEvalCallback() {
                                    @Override
                                    public void evalCallback(Expression valueB) throws LispException {
                                        callback.evalCallback(operate(valueA, valueB));
                                    }
                                });
                            } else {
                                int argsReceived = firstPair.toList().size();
                                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedAmountOfArguments(2, argsReceived));
                            }
                        } else {
                            throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                        }

                    }
                });
            }
        }

        abstract class SafeUntypedUnaryOperator extends ExpressionPrimitive implements IUnitTestable {
            abstract Expression operate(Expression value);

            @Override
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair firstPair, IEvalCallback callback) throws LispException {
                firstPair.left.eval(evaluator, environment, new IEvalCallback() {
                    @Override
                    public void evalCallback(Expression valueA) throws LispException {
                        if (firstPair.right.isNil()) {
                            callback.evalCallback(operate(valueA));

                        } else {
                            int argsReceived = firstPair.toList().size();
                            throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedAmountOfArguments(2, argsReceived));
                        }

                    }
                });
            }
        }

        abstract class SafeExpressionPrimitive extends ExpressionPrimitive implements IUnitTestable {
        }


        /* Procedures: */

        environment.setVariable("and", new SafeUntypedBinaryOperator() {
            @Override
            public Expression operate(Expression arg1, Expression arg2) {
                return (!arg1.equals(falseValue)) && (!arg2.equals(falseValue)) ? trueValue : falseValue;
            }

            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(and true true)", "true") &&
                        environment.evalTest("(and true false)", "false") &&
                        environment.evalTest("(and false true)", "false") &&
                        environment.evalTest("(and false false)", "false");
            }
        });

        environment.setVariable("or", new SafeUntypedBinaryOperator() {
            @Override
            public Expression operate(Expression arg1, Expression arg2) {
                return (!arg1.equals(falseValue)) | (!arg2.equals(falseValue)) ? trueValue : falseValue;
            }

            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(or true true)", "true") &&
                        environment.evalTest("(or true false)", "true") &&
                        environment.evalTest("(or false true)", "true") &&
                        environment.evalTest("(or false false)", "false");
            }
        });

        environment.setVariable("not", new SafeUntypedUnaryOperator() {
            @Override
            Expression operate(Expression value) {
                return value.equals(falseValue) ? trueValue : falseValue;
            }

            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(not true)", "false") &&
                        environment.evalTest("(not false)", "true");
            }
        });

        environment.setVariable("+", new SafeTypedBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
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

        environment.setVariable("-", new SafeTypedBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
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

        environment.setVariable("*", new SafeTypedBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
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

        environment.setVariable("/", new SafeTypedBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
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

        environment.setVariable("remainder", new SafeTypedBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
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

        environment.setVariable("=", new SafeTypedBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
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

        environment.setVariable(">", new SafeTypedBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
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

        environment.setVariable(">=", new SafeTypedBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
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

        environment.setVariable("<", new SafeTypedBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
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

        environment.setVariable("<=", new SafeTypedBinaryOperator<ExpressionNumber, ExpressionNumber>(ExpressionNumber.class, ExpressionNumber.class) {
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
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair firstPair, IEvalCallback callback) throws LispException {
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
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair firstPair, IEvalCallback callback) throws LispException {
                if (firstPair.right instanceof ExpressionPair) {
                    Expression argumentList = firstPair.left;
                    ExpressionPair bodyList = (ExpressionPair) firstPair.right;
                    callback.evalCallback(new ExpressionLambda(environment, argumentList, bodyList));
                } else {
                    throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("pair", firstPair.right.toString()));
                }
            }
        });

        environment.setVariable("quote", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(quote hello!)", "(quote hello!)");
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair arguments, IEvalCallback callback) throws LispException {
                callback.evalCallback(arguments.left);
            }
        });

        environment.setVariable("length", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(length \"Hello\")", "5") &&
                        environment.evalTest("(length (cons 4 (cons 5 (cons 6 nil))))", "3");
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair firstPair, IEvalCallback callback) throws LispException {
                if (firstPair.left instanceof ISequence) {
                    ISequence valueA = (ISequence) firstPair.left;
                    callback.evalCallback(valueA.getLength());
                } else {
                    throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("sequence", firstPair.left.toString()));
                }
            }
        });

        environment.setVariable("map", new SafeExpressionPrimitive() {
            @Override
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair firstPair, IEvalCallback callback) throws LispException {
                if (firstPair.right instanceof ExpressionPair) {
                    ExpressionPair secondPair = (ExpressionPair) firstPair.right;
                    firstPair.left.eval(evaluator, environment, new IEvalCallback() {
                        @Override
                        public void evalCallback(Expression functionResult) throws LispException {
                            if (functionResult instanceof IApplicable) {
                                IApplicable function = (IApplicable) functionResult;
                                secondPair.left.eval(evaluator, environment, new IEvalCallback() {
                                    @Override
                                    public void evalCallback(Expression sequenceResult) throws LispException {
                                        if (sequenceResult instanceof ISequence) {
                                            ExpressionPair listResult = new ExpressionPair(Environment.nilValue, Environment.nilValue);
                                            class Head {
                                                ExpressionPair value = listResult;
                                            }
                                            Head head = new Head();
                                            Iterator<Expression> iterator = ((ISequence) sequenceResult).iterator();
                                            if (iterator.hasNext()) {
                                                function.apply(evaluator, environment, new ExpressionPair(iterator.next(), Environment.nilValue), new IEvalCallback() {
                                                    @Override
                                                    public void evalCallback(Expression result) throws LispException {
                                                        head.value.left = result;
                                                        if (iterator.hasNext()) {
                                                            head.value.right = new ExpressionPair(Environment.nilValue, Environment.nilValue);
                                                            head.value = (ExpressionPair) head.value.right;
                                                            function.apply(evaluator, environment, new ExpressionPair(iterator.next(), Environment.nilValue), this);
                                                        } else {
                                                            callback.evalCallback(listResult);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });

                            } else {
                                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("applicable", firstPair.left.toString()));
                            }
                        }
                    });
                } else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                }
            }

            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(map (lambda (x) (+ 1 x)) (cons 1 (cons 2 (cons 3 nil))))", "(cons 2 (cons 3 (cons 4 nil)))");
            }
        });

        environment.setVariable("at", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(at \"Hello\" 0)", "" + (int) 'H') &&
                        environment.evalTest("(at (cons 1 (cons 2 nil)) 1)", "2") &&
                        environment.evalTest("(at (cons 1 (cons 2 nil)) 0)", "1");
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair firstPair, IEvalCallback callback) throws LispException {
                if (firstPair.right instanceof ExpressionPair) {
                    ExpressionPair secondPair = (ExpressionPair) firstPair.right;
                    firstPair.left.eval(evaluator, environment, new IEvalCallback() {
                        @Override
                        public void evalCallback(Expression resultValueA) throws LispException {
                            if (resultValueA instanceof ISequence) {
                                ISequence valueA = (ISequence) resultValueA;
                                secondPair.left.eval(evaluator, environment, new IEvalCallback() {
                                    @Override
                                    public void evalCallback(Expression resultValueB) throws LispException {
                                        if (resultValueB instanceof ExpressionNumber) {
                                            Expression result = valueA.atIndex((ExpressionNumber) resultValueB);
                                            callback.evalCallback(result);

                                        } else {
                                            throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("number", secondPair.left.toString()));
                                        }
                                    }
                                });
                            } else {
                                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("sequence", firstPair.left.toString()));
                            }
                        }
                    });
                } else {
                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                }
            }
        });

        environment.setVariable("concat", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(concat \"Hello\" \"World\")", "\"HelloWorld\"") &&
                        environment.evalTest("(concat (cons 1 (cons 2 nil)) (cons 3 (cons 4 nil)))", "(cons 1 (cons 2 (cons 3 (cons 4 nil))))");
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair firstPair, IEvalCallback callback) throws LispException {
                if (firstPair.right instanceof ExpressionPair) {
                    ExpressionPair secondPair = (ExpressionPair) firstPair.right;
                    firstPair.left.eval(evaluator, environment, new IEvalCallback() {
                        @Override
                        public void evalCallback(Expression resultValueA) throws LispException {
                            if (resultValueA instanceof ISequence) {
                                ISequence valueA = (ISequence) resultValueA;
                                secondPair.left.eval(evaluator, environment, new IEvalCallback() {
                                    @Override
                                    public void evalCallback(Expression resultValueB) throws LispException {
                                        if (resultValueB instanceof ISequence) {
                                            ISequence valueB = (ISequence) resultValueB;
                                            ISequence result = valueA.concatenate(valueB);
                                            if (result instanceof Expression) {
                                                callback.evalCallback((Expression) result);
                                            } else {
                                                throw new LispException(LispException.ErrorType.INTERNAL_ERROR);
                                            }
                                        } else {
                                            throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("sequence", secondPair.left.toString()));
                                        }
                                    }
                                });
                            } else {
                                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("sequence", firstPair.left.toString()));
                            }
                        }
                    });
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
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair firstPair, IEvalCallback callback) throws LispException {
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
            }
        });

        environment.setVariable("car", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(car (cons 1 2))", "1");
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair arguments, IEvalCallback callback) throws LispException {
                arguments.left.eval(evaluator, environment, new IEvalCallback() {
                    @Override
                    public void evalCallback(Expression result) throws LispException {
                        if (result instanceof ExpressionPair) {
                            callback.evalCallback(((ExpressionPair) result).left);
                        } else {
                            throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("pair", result.toString()));
                        }
                    }
                });
            }
        });

        environment.setVariable("cdr", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(cdr (cons 1 2))", "2");
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair arguments, IEvalCallback callback) throws LispException {
                arguments.left.eval(evaluator, environment, new IEvalCallback() {
                    @Override
                    public void evalCallback(Expression result) throws LispException {
                        if (result instanceof ExpressionPair) {
                            callback.evalCallback(((ExpressionPair) result).right);
                        } else {
                            throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("pair", result.toString()));
                        }
                    }
                });
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
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair firstPair, IEvalCallback callback) throws LispException {
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
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair firstPair, IEvalCallback callback) throws LispException {
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
            }
        });

        environment.setVariable("define", new SafeExpressionPrimitive() {
            @Override
            public boolean selfTest(Environment environment) {
                return environment.evalTest("(define x (+ 7 7))", "14") &&
                        environment.evalTest("((lambda () (define x 6) (+ 2 x)))", "8");
            }

            @Override
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair firstPair, IEvalCallback callback) throws LispException {
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
            }
        });

        return environment;
    }

    /**
     * Runs two expressions in the current environment and checks if they are equal.
     *
     * @param what   The first expression to evaluate.
     * @param expect The second expression to evaluate.
     * @return True if the expressions evaluated values are equal.
     */
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

    /**
     * The tests uses the makeStandardEnvironment() method to create an environment.
     * All IUnitTestable values in this environment are then tested using selfTest().
     * If a selfTest() returns false an exception will be thrown.
     *
     * @throws LispException Will throw a "unit-test-failure" error if some unit test returns false.
     */
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
