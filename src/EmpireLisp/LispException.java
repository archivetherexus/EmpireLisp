package EmpireLisp;

/**
 * The main exception class of this interpreter.
 * This class also contains standard error messages that can be used for error-reporting.
 *
 * @author Tyrerexus
 * @date 11/21/17
 */
@SuppressWarnings("JavaDoc")
public class LispException extends Exception {

    /**
     * This class contains some standard error-messages that can be used for error-reporting.
     */
    @SuppressWarnings("WeakerAccess")
    public static class ErrorMessages {
        public static String ARGUMENTS_MUST_BE_IN_LIST = "Function arguments must be in a valid list.";

        public static String expectedType(String type, String got) {
            return "Expected " + type + " but got: " + got;
        }

        public static String expectedAmountOfArguments(int expected, int got) {
            return "Expected " + expected + " arguments but got " + got;
        }
    }

    /**
     * All possible error types that can be thrown.
     */
    public enum ErrorType {
        ARITY_MISS_MATCH("Arity miss-match"),
        PARSE_ERROR("Parse error"),
        UNBOUND_VARIABLE("Unbound variable"),
        INVALID_ARGUMENTS("Invalid arguments"),
        NOT_APPLICABLE("Not applicable"),
        UNIT_TEST_FAILURE("Unit test failure"),
        INTERNAL_ERROR("Internal error"),
        ARRAY_OUT_OF_BOUNDS("Array out of bounds");

        private String name;

        ErrorType(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return this.getName();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public ErrorType type;

    public LispException(ErrorType type, String message) {
        super(message);
        this.type = type;
    }

    @SuppressWarnings("unused")
    public LispException(ErrorType type) {
        super("");
        this.type = type;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (message.equals("")) {
            return type.getName();
        } else {
            return type.getName() + ": " + super.getMessage();
        }
    }
}
