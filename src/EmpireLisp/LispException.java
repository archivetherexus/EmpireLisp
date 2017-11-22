package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/21/17
 */
public class LispException extends Exception {
    public static class ErrorMessages {
        public static String ARGUMENTS_MUST_BE_IN_LIST = "Function arguments must be in a valid list.";
        public static String EXPECTED_NUMBER = "Expected number but got:";
    }

    enum ErrorType {
        ARITY_MISS_MATCH ("Arity miss-match"),
        PARSE_ERROR ("Parse error"),
        UNBOUND_VARIABLE ("Unbound variable"),
        INVALID_ARGUMENTS("Invalid arguments"),
        NOT_APPLICABLE("Not applicable");

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

    ErrorType type;

    public LispException (ErrorType type, String message) {
        super(message);
        this.type = type;
    }

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
