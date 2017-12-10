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
        ExampleEvaluator evaluator = new ExampleEvaluator(64);

        /* Print will be our interface to the outer-world. */
        evaluator.environment.setVariable("print", new ExpressionPrimitive() {
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
        evaluator.environment.setVariable("exit", new ExpressionPrimitive() {
            @Override
            public void apply(IEvaluator evaluator, Environment environment, ExpressionPair arguments, IEvalCallback callback) throws LispException {
                running = false;
                callback.evalCallback(Environment.nilValue);
            }
        });

        while (running) {
            try {
                if (evaluator.done()) {
                    System.out.print("> ");
                    evaluator.eval(scanner.nextLine());
                } else {
                    evaluator.run();
                }
            }
            catch (LispException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
