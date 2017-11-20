import EmpireLisp.Parser;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
public class Main {

    public static void main(String[] args) throws UnsupportedEncodingException {
        Parser.readTokenTest();
        Parser.parseExpressionTest();
    }

}
