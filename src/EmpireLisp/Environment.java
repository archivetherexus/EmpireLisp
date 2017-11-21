package EmpireLisp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
public class Environment {

    static Expression trueValue = new ExpressionNumber(1);
    static Expression falseValue = new ExpressionPair(null, null);
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
                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH);
            }

            @Override
            public boolean isLazyEval() {
                return true;
            }
        });
        environment.setVariable("+", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment,Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof ExpressionNumber) {
                        ExpressionNumber valueA = (ExpressionNumber) firstPair.left;

                        if (firstPair.right instanceof ExpressionPair) {
                            ExpressionPair secondPair = (ExpressionPair) firstPair.right;

                            if (secondPair.left instanceof ExpressionNumber) {
                                ExpressionNumber valueB = (ExpressionNumber) secondPair.left;

                                return new ExpressionNumber(valueA.number + valueB.number);
                            }
                        }
                    }
                }
                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH);
            }

            @Override
            public boolean isLazyEval() {
                return false;
            }
        });
        environment.setVariable("-", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof ExpressionNumber) {
                        ExpressionNumber valueA = (ExpressionNumber) firstPair.left;

                        if (firstPair.right instanceof ExpressionPair) {
                            ExpressionPair secondPair = (ExpressionPair) firstPair.right;

                            if (secondPair.left instanceof ExpressionNumber) {
                                ExpressionNumber valueB = (ExpressionNumber) secondPair.left;

                                return new ExpressionNumber(valueA.number - valueB.number);
                            }
                        }
                    }
                }
                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH);
            }

            @Override
            public boolean isLazyEval() {
                return false;
            }
        });

        environment.setVariable("*", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof ExpressionNumber) {
                        ExpressionNumber valueA = (ExpressionNumber) firstPair.left;

                        if (firstPair.right instanceof ExpressionPair) {
                            ExpressionPair secondPair = (ExpressionPair) firstPair.right;

                            if (secondPair.left instanceof ExpressionNumber) {
                                ExpressionNumber valueB = (ExpressionNumber) secondPair.left;

                                return new ExpressionNumber(valueA.number * valueB.number);
                            }
                        }
                    }
                }
                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH);
            }

            @Override
            public boolean isLazyEval() {
                return false;
            }
        });

        environment.setVariable("/", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof ExpressionNumber) {
                        ExpressionNumber valueA = (ExpressionNumber) firstPair.left;

                        if (firstPair.right instanceof ExpressionPair) {
                            ExpressionPair secondPair = (ExpressionPair) firstPair.right;

                            if (secondPair.left instanceof ExpressionNumber) {
                                ExpressionNumber valueB = (ExpressionNumber) secondPair.left;

                                return new ExpressionNumber(valueA.number / valueB.number);
                            }
                        }
                    }
                }
                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH);
            }

            @Override
            public boolean isLazyEval() {
                return false;
            }
        });

        environment.setVariable("remainder", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof ExpressionNumber) {
                        ExpressionNumber valueA = (ExpressionNumber) firstPair.left;

                        if (firstPair.right instanceof ExpressionPair) {
                            ExpressionPair secondPair = (ExpressionPair) firstPair.right;

                            if (secondPair.left instanceof ExpressionNumber) {
                                ExpressionNumber valueB = (ExpressionNumber) secondPair.left;

                                return new ExpressionNumber(valueA.number % valueB.number);
                            }
                        }
                    }
                }
                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH);
            }

            @Override
            public boolean isLazyEval() {
                return false;
            }
        });

        environment.setVariable(">", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof ExpressionNumber) {
                        ExpressionNumber valueA = (ExpressionNumber) firstPair.left;

                        if (firstPair.right instanceof ExpressionPair) {
                            ExpressionPair secondPair = (ExpressionPair) firstPair.right;

                            if (secondPair.left instanceof ExpressionNumber) {
                                ExpressionNumber valueB = (ExpressionNumber) secondPair.left;

                                return valueA.number > valueB.number ? trueValue : falseValue;
                            }
                        }
                    }
                }
                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH);
            }

            @Override
            public boolean isLazyEval() {
                return false;
            }
        });

        environment.setVariable(">=", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof ExpressionNumber) {
                        ExpressionNumber valueA = (ExpressionNumber) firstPair.left;

                        if (firstPair.right instanceof ExpressionPair) {
                            ExpressionPair secondPair = (ExpressionPair) firstPair.right;

                            if (secondPair.left instanceof ExpressionNumber) {
                                ExpressionNumber valueB = (ExpressionNumber) secondPair.left;

                                return valueA.number >= valueB.number ? trueValue : falseValue;
                            }
                        }
                    }
                }
                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH);
            }

            @Override
            public boolean isLazyEval() {
                return false;
            }
        });

        environment.setVariable("<", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof ExpressionNumber) {
                        ExpressionNumber valueA = (ExpressionNumber) firstPair.left;

                        if (firstPair.right instanceof ExpressionPair) {
                            ExpressionPair secondPair = (ExpressionPair) firstPair.right;

                            if (secondPair.left instanceof ExpressionNumber) {
                                ExpressionNumber valueB = (ExpressionNumber) secondPair.left;

                                return valueA.number < valueB.number ? trueValue : falseValue;
                            }
                        }
                    }
                }
                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH);
            }

            @Override
            public boolean isLazyEval() {
                return false;
            }
        });

        environment.setVariable("<=", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof ExpressionNumber) {
                        ExpressionNumber valueA = (ExpressionNumber) firstPair.left;

                        if (firstPair.right instanceof ExpressionPair) {
                            ExpressionPair secondPair = (ExpressionPair) firstPair.right;

                            if (secondPair.left instanceof ExpressionNumber) {
                                ExpressionNumber valueB = (ExpressionNumber) secondPair.left;

                                return valueA.number <= valueB.number ? trueValue : falseValue;
                            }
                        }
                    }
                }
                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH);
            }

            @Override
            public boolean isLazyEval() {
                return false;
            }
        });

        environment.setVariable("=", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof ExpressionNumber) {
                        ExpressionNumber valueA = (ExpressionNumber) firstPair.left;

                        if (firstPair.right instanceof ExpressionPair) {
                            ExpressionPair secondPair = (ExpressionPair) firstPair.right;

                            if (secondPair.left instanceof ExpressionNumber) {
                                ExpressionNumber valueB = (ExpressionNumber) secondPair.left;

                                return valueA.number == valueB.number ? trueValue : falseValue;
                            }
                        }
                    }
                }
                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH);
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
                            if (condition.left.eval(environment) == trueValue /*TODO: Use better check. */) {
                                if (condition.right instanceof ExpressionPair) {
                                    return ((ExpressionPair)condition.right).left.eval(environment);
                                }
                                else {
                                    throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, "Bad list!"); // TODO: Use better name!
                                }
                            }
                        }
                        else {
                            throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, "Conditions can only be lists!");
                        }
                    }

                    // No condition matched. Use the last condition as an else. //

                    // All elements have already been checked for arity. //
                    ExpressionPair defaultCondition = (ExpressionPair) conditions.get(conditions.size() - 1);
                    if (defaultCondition.right instanceof ExpressionPair){
                        return ((ExpressionPair)defaultCondition.right).left.eval(environment);
                    }
                    else {
                        throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, "Bad list!"); // TODO: Use better name!
                    }

                }
                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH);
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
