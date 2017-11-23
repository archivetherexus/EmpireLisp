package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/21/17
 */
@SuppressWarnings("JavaDoc")
public class LispException extends Exception {

    @SuppressWarnings("WeakerAccess")
    public static class ErrorMessages {
        public static String ARGUMENTS_MUST_BE_IN_LIST = "Function arguments must be in a valid list.";
        public static String expectedType(String type, String got) {
            return "Expected " + type + " but got: " + got;
        }
    }

    enum ErrorType {
        ARITY_MISS_MATCH ("Arity miss-match"),
        PARSE_ERROR ("Parse error"),
        UNBOUND_VARIABLE ("Unbound variable"),
        INVALID_ARGUMENTS("Invalid arguments"),
        NOT_APPLICABLE("Not applicable"),
        UNIT_TEST_FAILURE("Unit test failure");

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

    public LispException (ErrorType type, String message) {
        super(message);
        this.type = type;
    }

    @SuppressWarnings("unused")
    public LispException (ErrorType type) {
        super("");
        this.type = type;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (message.equals("")) {
            return type.getName();
        }
        else {
            return type.getName() + ": " + super.getMessage();
        }
    }
}
