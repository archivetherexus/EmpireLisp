import EmpireLisp.*;

import java.io.*;
import java.util.Scanner;

/**
 * This is an example of how to use this interpreter.
 * It contains a basic REPL.
 *
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public class Main {

    private static boolean running = true;

    @SuppressWarnings("Convert2Lambda")
    public static void main(String[] args) throws UnsupportedEncodingException, LispException {

        /* Run some unit-tests. */
        Parser.readTokenTest();
        Parser.parseExpressionTest();
        Environment.standardEnvironmentTest();

        /* Start the REPL. */
        Scanner scanner = new Scanner(System.in);
        REPL(scanner);

    }

    static void REPL(Scanner scanner) {
        Parser parser = new Parser();
        Environment environment = Environment.makeStandardEnvironment();

        /* Print will be our interface to the outer-world. */
        environment.setVariable("print", new ExpressionPrimitive() {
            @Override
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair firstPair, IEvalCallback callback) throws LispException {
                firstPair.left.eval(evaluator, environment, new IEvalCallback() {
                    @Override
                    public void evalCallback(Expression result) throws LispException {
                        if (result instanceof ExpressionString) {
                            System.out.println(((ExpressionString) result).string);
                            callback.evalCallback(Environment.nilValue);
                        } else {
                            throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("string", firstPair.left.toString()));
                        }
                    }
                });
            }
        });

        /* Allow us to exit this REPL. */
        environment.setVariable("exit", new ExpressionPrimitive() {
            @Override
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair arguments, IEvalCallback callback) throws LispException {
                running = false;
                callback.evalCallback(Environment.nilValue);
            }
        });

        class MainEvaluator implements IEvaluator {

            private Expression expressionStash   = null;
            private Environment environmentStash = null;
            private IEvalCallback callbackStash  = null;

            private int maxEvaluations = 1;

            @Override
            public boolean continueEvaluation() {
                return maxEvaluations-- > 0;
            }

            @Override
            public void stashEvaluation(Expression expression, Environment environment, IEvalCallback callback) {
                //System.out.println("Stashing: " + expression);
                expressionStash = expression;
                environmentStash = environment;
                callbackStash = callback;
            }

            private boolean hasStash() {
                return expressionStash != null;
            }

            private void resumeEvaluation() throws LispException {
                maxEvaluations = 1;
                Expression expression = expressionStash;
                Environment environment = environmentStash;
                IEvalCallback callback = callbackStash;
                expressionStash  = null;
                environmentStash = null;
                callbackStash    = null;
                expression.eval(this, environment, callback);
            }
        }

        MainEvaluator evaluator = new MainEvaluator();

        while (running) {
            try {
                System.out.print("> ");
                Expression read = parser.parseExpression(Parser.fromString(scanner.nextLine()));
                if (read != null) {
                    read.eval(evaluator, environment, new IEvalCallback() {
                        @Override
                        public void evalCallback(Expression result) throws LispException {
                            if (!result.isNil()) {
                                System.out.println(result);
                            }
                        }
                    });
                    while(evaluator.hasStash()) {
                        evaluator.resumeEvaluation();
                    }
                }
                else {
                    throw new RuntimeException("parseExpression() returned null!");
                }
            }
            catch (LispException e) {
                System.err.println(e.getMessage());
            }
        }
    }

}
