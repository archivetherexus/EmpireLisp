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
public class Parser {
    public String readToken(PushbackInputStream stream) {
        try {
            int character = stream.read();
            if (character != -1) {
                while (Character.isWhitespace(character)) {
                    character = stream.read();
                }

                if (character == '(') {
                    return "(";
                }
                else if (character == ')') {
                    return ")";
                }

                StringBuilder builder = new StringBuilder();
                builder.append((char)character);

                character = stream.read();
                while (character != -1 && !Character.isWhitespace(character) && character != '(' && character != ')') {
                    builder.append((char)character);
                    character = stream.read();
                }
                stream.unread(character);

                return builder.toString();
            }
            else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Expression parseExpression(PushbackInputStream stream) {
        Expression result;
        String token = readToken(stream);

        if (token.equals("(")) {
            result = new ExpressionPair(null, null);
            ExpressionPair head = (ExpressionPair) result;
            Expression expression = parseExpression(stream);
            while (expression != null) {
                head.left = expression;
                head.right = new ExpressionPair(null, null);
                head = (ExpressionPair) head.right;
                expression = parseExpression(stream);
            }
        }
        else if (token.equals(")")) {
            return null;
        }
        else {
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

    public static void readTokenTest() {
        ArrayList<String> expectedList = new ArrayList<String>(){{
            add("(");
            add("hello");
            add("world");
            add("(");
            add("how");
            add("are");
            add("you");
            add(")");
            add(")");
        }};

        ArrayList<String> resultList = new ArrayList<String>();
        Parser parser = new Parser();
        PushbackInputStream stream = fromString("(hello world (how are you))");
        String str = parser.readToken(stream);
        while (str != null) {
            resultList.add(str);
            str = parser.readToken(stream);
        }

        Iterator<String> iter1 = resultList.iterator();
        Iterator<String> iter2 = expectedList.iterator();
        boolean equal = true;
        while(iter1.hasNext() || iter2.hasNext()) {
            if (iter1.hasNext() && iter2.hasNext()) {
                if (!iter1.next().equals(iter2.next())) {
                    equal = false;
                }
            }
            else {
                equal = false;
            }
        }

        if (!equal) {
            throw new RuntimeException("Parser.readTokenTest() failed. Lists are not equal!");
        }
    }

    public static void parseExpressionTest() {
        String expectedString = "(123.0 . ((World . ()) . ()))";
        Parser parser = new Parser();
        PushbackInputStream stream = Parser.fromString("(123 (World))");
        if (!expectedString.equals(parser.parseExpression(stream).toString())) {
            throw new RuntimeException("Parser.parseExpressionTest() failed. Result didn't match the expected string!");
        }
    }
}
