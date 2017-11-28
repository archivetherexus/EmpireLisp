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

    private int parenthesesCount = 0;

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

    public Expression parseExpression(PushbackInputStream stream) throws LispException {
        Expression result;
        String token = readToken(stream);

        if (token == null) {
            throw new LispException(LispException.ErrorType.PARSE_ERROR, "Missing right-angel parentheses!");
        } else if (token.equals("(")) {
            parenthesesCount++;
            Expression expression = parseExpression(stream);
            if (expression != null) {
                result = new ExpressionPair(expression, Environment.nilValue);
                ExpressionPair head = (ExpressionPair) result;
                while (true) {
                    Expression expression2 = parseExpression(stream);
                    if (expression2 == null) {
                        break;
                    } else {
                        head.right = new ExpressionPair(expression2, Environment.nilValue);
                        head = (ExpressionPair) head.right;
                    }
                }
            } else {
                return Environment.nilValue;
            }
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

    public static PushbackInputStream fromString(String string) {
        try {
            return new PushbackInputStream(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8.name())));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

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

    public static void parseExpressionTest() {
        try {
            String expectedString = "(123.0 . ((world . nil) . nil))";
            Parser parser = new Parser();
            PushbackInputStream stream = Parser.fromString("(123 (WoRlD))");

            if (!expectedString.equals(parser.parseExpression(stream).toString())) {
                throw new LispException(LispException.ErrorType.UNIT_TEST_FAILURE, "Parser.parseExpressionTest() failed. Result didn't match the expected string!");
            }
        } catch (LispException e) {
            e.printStackTrace();
        }
    }
}
