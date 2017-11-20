import EmpireLisp.*;

import java.io.*;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
public class Main {

    public static void main(String[] args) throws UnsupportedEncodingException {
        Parser.readTokenTest();
        Parser.parseExpressionTest();

        Parser parser = new Parser();
        Environment environment = new Environment(null);
        environment.setVariable("lambda", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;

                    if (firstPair.left instanceof  ExpressionPair && firstPair.right instanceof ExpressionPair) {
                        ExpressionPair argumentList = (ExpressionPair) firstPair.left;
                        ExpressionPair bodyList = (ExpressionPair) firstPair.right;
                        return new ExpressionLambda(environment, argumentList, bodyList);
                    }
                }
                System.out.println("ERROR: Arity miss-match!"); // TODO: Throw error!
                return null;
            }

            @Override
            public boolean isLazyEval() {
                return true;
            }
        });
        environment.setVariable("+", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment,Expression arguments) {
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
                System.out.println("ERROR: Arity miss-match!"); // TODO: Throw error!
                return null;
            }

            @Override
            public boolean isLazyEval() {
                return false;
            }
        });
        environment.setVariable("-", new ExpressionPrimitive() {
            @Override
            public Expression apply(Environment environment, Expression arguments) {
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
                System.out.println("ERROR: Arity miss-match!"); // TODO: Throw error!
                return null;
            }

            @Override
            public boolean isLazyEval() {
                return false;
            }
        });
        //Expression expression = parser.parseExpression(Parser.fromString("(- ( + 2 ( + 4 4 )) 1)"));
        Expression expression = parser.parseExpression(Parser.fromString("((lambda (x y) 33 (+ x y)) 42 8)"));
        String result = expression.eval(environment).toString();
        System.out.println(result);
    }

}
