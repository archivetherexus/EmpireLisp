import EmpireLisp.*;

import java.io.*;
import java.util.Scanner;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public class Main {

    static boolean running = true;

    public static void main(String[] args) throws UnsupportedEncodingException, LispException {


        Parser.readTokenTest();
        Parser.parseExpressionTest();
        Environment.standardEnvironmentTest();

        Scanner scanner = new Scanner(System.in);


        Parser parser = new Parser();
        Environment environment = Environment.makeStandardEnvironment();
        environment.setVariable("print", new ExpressionPrimitive() {
            @Override
            public void apply(Environment environment, Expression arguments, IEvalCallback callback) throws LispException {
                if (arguments instanceof ExpressionPair) {
                    ExpressionPair firstPair = (ExpressionPair) arguments;
                    if (firstPair.left instanceof ExpressionString) {
                        ExpressionString valueA = (ExpressionString) firstPair.left;
                        System.out.println(valueA.string);
                        callback.evalCallback(Environment.nilValue);
                    }
                    else {
                        throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType("string", firstPair.left.toString()));
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
        });

        environment.setVariable("exit", new ExpressionPrimitive() {
            @Override
            public void apply(Environment environment, Expression arguments, IEvalCallback callback) throws LispException {
                running = false;
                callback.evalCallback(Environment.nilValue);
            }

            @Override
            public boolean isLazyEval() {
                return false;
            }
        });

        while (running) {
            try {
                System.out.print("> ");
                Expression read = parser.parseExpression(Parser.fromString(scanner.nextLine()));
                if (read != null) {
                    read.eval(environment, new IEvalCallback() {
                        @Override
                        public void evalCallback(Expression result) throws LispException {
                            if (!result.isNil()) {
                                System.out.println(result);
                            }
                        }
                    });
                }
                else {
                    System.err.println("parseExpression() returned null!");
                }
            }
            catch (LispException e) {
                System.err.println(e.getMessage());
            }
        }
    }

}
