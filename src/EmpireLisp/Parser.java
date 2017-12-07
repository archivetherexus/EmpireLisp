package EmpireLisp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public class Parser {

    /**
     * How many parentheses have been read?
     */
    private int parenthesesCount = 0;

    /**
     * The tokenizer of the parser. Reads token by token from the stream.
     *
     * @param stream The stream to read from.
     * @return The token read. Returns null if no tokens are left on the stream.
     * @throws LispException Can throw a "unterminated-string" error if necessary.
     */
    @SuppressWarnings("WeakerAccess")
    public String readToken(PushbackInputStream stream) throws LispException {
        try {
            int character = stream.read();

            while (Character.isWhitespace(character)) {
                character = stream.read();
            }
            if (character == ';') {
                character = stream.read();
                while (character != '\n' && character != -1 && character != 255) {
                    character = stream.read();
                }
                character = stream.read();
            }

            if (character == 255 || character == -1) {
                return null;
            } else if (character == '(') {
                return "(";
            } else if (character == ')') {
                return ")";
            } else if (character == '"') {
                StringBuilder builder = new StringBuilder();
                builder.append((char) character);
                character = stream.read();
                while (character != '"') {
                    if (character == '\'') {
                        character = stream.read();
                        builder.append((char) character);
                    } else if (character == 255 || character == -1) {
                        throw new LispException(LispException.ErrorType.PARSE_ERROR, "Unterminated string!");
                    } else {
                        builder.append((char) character);
                    }
                    character = stream.read();
                }
                return builder.toString();
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append((char) character);

                character = stream.read();
                while (character != 255 && character != -1 && !Character.isWhitespace(character) && character != '(' && character != ')') {
                    builder.append((char) character);
                    character = stream.read();
                }
                stream.unread(character);

                return builder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parses an expression by reading tokens from readToken() and converting these tokens
     * from their string representation into an expression.
     *
     * @param stream The stream that will be passed onto readToken()
     * @return The parsed expression.
     * @throws LispException May throw a "missing-parentheses" error if necessary.
     */
    public Expression parseExpression(PushbackInputStream stream) throws LispException {
        Expression result;
        String token = readToken(stream);

        if (token == null) {
            throw new LispException(LispException.ErrorType.PARSE_ERROR, "Missing right-angel parentheses!");
        } else if (token.equals("(")) {
            parenthesesCount++;
            ListWriter list = new ListWriter();
            for (Expression e = parseExpression(stream); e != null; e = parseExpression(stream)) {
                list.push(e);
            }
            result = list.getResult();
            /*
            Expression expression = parseExpression(stream);
            if (expression != null) {
                ListWriter list = new ListWriter();
                list.push(expression);
                while (true) {
                    Expression expression2 = parseExpression(stream);
                    if (expression2 == null) {
                        break;
                    } else {
                        list.push(expression2);
                    }
                }
                result = list.getResult();
            } else {
                return Environment.nilValue;
            }
            */
        } else if (token.equals(")")) {
            if (parenthesesCount > 0) {
                parenthesesCount--;
                return null;
            } else {
                throw new LispException(LispException.ErrorType.PARSE_ERROR, "Missing left-angel parentheses!");
            }
        } else if (token.charAt(0) == '\"') {
            return new ExpressionString(token.substring(1));
        } else {
            try {
                result = new ExpressionNumber(Double.parseDouble(token));
            } catch (NumberFormatException nfe) {
                result = new ExpressionSymbol(token);
            }
        }
        return result;
    }

    /**
     * Helper method:
     * Converts a string into a PushbackInputStream that can be used by parseExpression() and readToken().
     *
     * @param string The string to convert.
     * @return The new stream.
     */
    public static PushbackInputStream fromString(String string) {
        try {
            return new PushbackInputStream(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8.name())));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This tests the tokenizer by creating a stream from testString.
     * And then pulling out tokens using readToken() while making sure that it matches the expectedList.
     * It will throw an exception if the expectedList does not match the tokens extracted by using readToken().
     *
     * @param testString   The string to test.
     * @param expectedList The expected "tokenization" of that list.
     * @throws LispException May throw a "unit-test-failure" error if the lists do not match.
     */
    private static void readTokenTestList(String testString, ArrayList<String> expectedList) throws LispException {

        ArrayList<String> resultList = new ArrayList<>();
        Parser parser = new Parser();
        PushbackInputStream stream = fromString(testString);
        String str = parser.readToken(stream);
        while (str != null) {
            resultList.add(str);
            str = parser.readToken(stream);
        }

        Iterator<String> iterator1 = resultList.iterator();
        Iterator<String> iterator2 = expectedList.iterator();
        boolean equal = true;
        while (iterator1.hasNext() || iterator2.hasNext()) {
            if (iterator1.hasNext() && iterator2.hasNext()) {
                if (!iterator1.next().equals(iterator2.next())) {
                    equal = false;
                }
            } else {
                equal = false;
                break;
            }
        }

        if (!equal) {
            throw new LispException(LispException.ErrorType.UNIT_TEST_FAILURE, "Parser.readTokenTest() failed. Lists are not equal!");
        }
    }

    /**
     * Tests if the tokenizer works properly.
     * It does so by using the private method readTokenTestList().
     *
     * @throws LispException May throw a "unit-test-failure" error if a unit-test fails.
     */
    public static void readTokenTest() throws LispException {
        readTokenTestList("(hello world (how are you?))", new ArrayList<String>() {{
            add("(");
            add("hello");
            add("world");
            add("(");
            add("how");
            add("are");
            add("you?");
            add(")");
            add(")");
        }});

        readTokenTestList("( + 2 ( + 4 4 ) )", new ArrayList<String>() {{
            add("(");
            add("+");
            add("2");
            add("(");
            add("+");
            add("4");
            add("4");
            add(")");
            add(")");
        }});

        readTokenTestList("( \"Hello () World!\" 123)", new ArrayList<String>() {{
            add("(");
            add("\"Hello () World!");
            add("123");
            add(")");
        }});
        readTokenTestList("Hello World ; This is a comment\n!", new ArrayList<String>() {{
            add("Hello");
            add("World");
            add("!");
        }});
    }

    /**
     * Tests if the parseExpression() method is sane.
     * It does so by running parseExpression() on a string to get an Expression.
     * It then uses this Expression to run toString() on it to check against another string.
     *
     * @throws LispException May throw a "unit-test-failure" error if a unit-test fails.
     */
    public static void parseExpressionTest() throws LispException {
        String expectedString = "(123.0 . ((world . nil) . nil))";
        Parser parser = new Parser();
        PushbackInputStream stream = Parser.fromString("(123 (WoRlD))");

        if (!expectedString.equals(parser.parseExpression(stream).toString())) {
            throw new LispException(LispException.ErrorType.UNIT_TEST_FAILURE, "Parser.parseExpressionTest() failed. Result didn't match the expected string!");
        }
    }
}
