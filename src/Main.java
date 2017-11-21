import EmpireLisp.*;

import java.io.*;
import java.util.Scanner;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
public class Main {

    public static void main(String[] args) throws UnsupportedEncodingException {
        Parser.readTokenTest();
        Parser.parseExpressionTest();
        Scanner scanner = new Scanner(System.in);

        try {
            Parser parser = new Parser();
            Environment environment = Environment.makeStandardEnvironment();

            //Expression expression = parser.parseExpression(Parser.fromString("(- ( + 2 ( + 4 4 )) 1)"));
            /*Expression expression = parser.parseExpression(Parser.fromString("((lambda (x y) 33 (+ x y)) 42 8)"));
            Expression expression2 = parser.parseExpression(Parser.fromString("(+ (lambda (x) 1) 1)"));
            String result = expression.eval(environment).toString();
            String result2 = expression2.eval(environment).toString();
            System.out.println(result);
            System.out.println(result2);*/
            while (true) {
                System.out.print("> ");
                Expression read = parser.parseExpression(Parser.fromString(scanner.nextLine()));
                System.out.println("Read: " + read);
                Expression result = read.eval(environment);
                System.out.println(result);
            }
        }
        catch (LispException e) {
            System.out.println(e.getMessage());
        }
    }

}
