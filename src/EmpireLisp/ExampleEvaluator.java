package EmpireLisp;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This is an example of an evaluator. Use it if you are too lazy to create your own.
 *
 * @author Tyrerexus
 * @date 12/10/17
 */
@SuppressWarnings({"unused", "Convert2Lambda"})
public class ExampleEvaluator {

    /**
     * The current environment of the evaluator.
     */
    public Environment environment;

    /**
     * The callback that is called when an evaluation is finished.
     */
    public IEvalCallback callback;

    private Parser parser;

    private int maxEvaluations;

    private Queue<Expression> unevaluatedCode = new LinkedList<>();

    private class CodeEvaluator implements IEvaluator {
        private Expression expressionStash = null;
        private Environment environmentStash = null;
        private IEvalCallback callbackStash = null;

        @Override
        public boolean continueEvaluation() {
            return maxEvaluations-- > 0;
        }

        @Override
        public void stashEvaluation(Expression expression, Environment environment, IEvalCallback callback) {
            //System.out.println("Stashing: " + expression);
            expressionStash = expression;
            environmentStash = environment;
            callbackStash = callback;
        }

        private boolean hasStash() {
            return expressionStash != null;
        }

        private void resumeEvaluation() throws LispException {
            maxEvaluations = 1;
            Expression expression = expressionStash;
            Environment environment = environmentStash;
            IEvalCallback callback = callbackStash;
            expressionStash = null;
            environmentStash = null;
            callbackStash = null;
            expression.eval(this, environment, callback);
        }
    }

    private CodeEvaluator evaluator;

    /**
     * Constructor for the evaluator. No other configuration is needed after this. Just run eval() and run()!
     *
     * @param maxEvaluations How many evaluations is run() allowed to perform per call?
     */
    public ExampleEvaluator(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
        evaluator = new CodeEvaluator();
        parser = new Parser();
        environment = Environment.makeStandardEnvironment();
        callback = new IEvalCallback() {
            @Override
            public void evalCallback(Expression result) throws LispException {
                System.out.println(result);
            }
        };
    }

    /**
     * Setter for how many evaluations is run() allowed to perform per call.
     *
     * @param maxEvaluations The new value of maxEvaluations
     */
    public void setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
    }

    /**
     * Returns true if the evaluator has nothing to do.
     *
     * @return False if the evaluator is not finished evaluating.
     */
    public boolean done() {
        return unevaluatedCode.isEmpty() && !evaluator.hasStash();
    }

    /**
     * Tells the evaluator to evaluate this code when run() is called.
     *
     * @param code The code to evaluate.
     * @throws LispException If an error occurred while parsing the code.
     */
    public void eval(String code) throws LispException {
        unevaluatedCode.add(parser.parseExpression(Parser.fromString(code)));
    }

    /**
     * Tells the evaluator to evaluate this expression when run() is called.
     *
     * @param expression The expression to evaluate.
     */
    public void eval(Expression expression) {
        unevaluatedCode.add(expression);
    }

    /**
     * Evaluates expressions that were given by calling eval().
     * It may also resume paused evaluations.
     *
     * @throws LispException If an error occurred while running.
     */
    public void run() throws LispException {
        if (evaluator.hasStash()) {
            evaluator.resumeEvaluation();
        } else {
            if (!unevaluatedCode.isEmpty()) {
                unevaluatedCode.poll().eval(evaluator, environment, callback);
            }
        }
    }
}
