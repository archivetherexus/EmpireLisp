package EmpireLisp;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * A pair as an Expression.
 *
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public class ExpressionPair extends Expression implements ISequence {
    @SuppressWarnings("WeakerAccess")
    public Expression left;

    @SuppressWarnings("WeakerAccess")
    public Expression right;

    @SuppressWarnings("WeakerAccess")
    public ExpressionPair(Expression left, Expression right) {
        super();

        this.left = left;
        this.right = right;
    }

    @Override
    public void serializeCode(Writer output) throws IOException {
        Iterator<Expression> i = iterator();
        if (i.hasNext()) {
            output.write('(');
            do {
                i.next().serializeCode(output);
                if (i.hasNext()) {
                    output.write(' ');
                } else {
                    output.write(')');
                }
            } while (i.hasNext());
        }
        else {
            output.write("()");
        }
    }

    @Override
    public void serializeExpression(HashSet<Long> completedIDs, Writer output) throws IOException {
        left.serializeExpression(completedIDs, output);
        right.serializeExpression(completedIDs, output);
        String self = "(cons " + ID_NUMBER_PREFIX + left.expressionID + " " + ID_NUMBER_PREFIX + right.expressionID + ")";
        registerSelf(completedIDs, output, self);
    }

    @Override
    public String toString() {
        if (left != null || right != null) {
            return "(" + (left != null ? left.toString() : "") + " . " + (right != null ? right.toString() : "") + ")";
        } else {
            return "()";
        }
    }

    @Override
    public boolean equals(Expression other) {
        if (other instanceof ExpressionPair) {
            ExpressionPair otherPair = (ExpressionPair) other;
            if (this.left == null && this.right == null) {
                return otherPair.left == null && otherPair.right == null;
            } else if (this.left == null) {
                return otherPair.left == null && this.right.equals(otherPair.right);
            } else if (this.right == null) {
                return this.left.equals(otherPair.left) && otherPair.right == null;
            } else {
                return this.left.equals(otherPair.left) && this.right.equals(otherPair.right);
            }
        } else {
            return false;
        }
    }

    @SuppressWarnings("Convert2Lambda")
    @Override
    public void eval(IEvaluator evaluator, Environment environment, IEvalCallback callback) throws LispException {
        Expression operator = this.left;
        if (this.right instanceof ExpressionPair) {
            ExpressionPair operand = (ExpressionPair) this.right;

            // Evaluate the operator. //
            if (evaluator.continueEvaluation()) {
                operator.eval(evaluator, environment, new IEvalCallback() {
                    @Override
                    public void evalCallback(Expression operator) throws LispException {
                        if (operator instanceof IApplicable) {
                            ((IApplicable) operator).apply(evaluator, environment, operand, callback);

                        } else {
                            throw new LispException(LispException.ErrorType.NOT_APPLICABLE, "\"" + operator.toString() + "\" is not applicable");
                        }
                    }
                });
            } else {
                evaluator.stashEvaluation(this, environment, callback);
            }
        } else {
            throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public List<Expression> toList() {
        List<Expression> result = new ArrayList<>();
        ExpressionPair list = this;
        while (list.left != null) {
            result.add(list.left);

            if (list.right instanceof ExpressionPair) {
                list = (ExpressionPair) list.right;
            } else {
                break;
            }
        }
        return result;
    }

    @SuppressWarnings("WeakerAccess")
    public Iterator<Expression> iterator() {
        ExpressionPair pair = this;
        return new Iterator<Expression>() {
            ExpressionPair head = pair;
            boolean end = pair.isNil();

            @Override
            public boolean hasNext() {
                return !end;
            }

            @Override
            public Expression next() {
                Expression value = head.left;
                if (head.right instanceof ExpressionPair) {
                    head = (ExpressionPair) head.right;
                    if (head.isNil()) {
                        end = true;
                    }
                    return value;
                } else if (head.isNil()) {
                    return head;
                } else {
                    throw new RuntimeException("Invalid list structure!");
                }
            }
        };
    }

    @Override
    public ExpressionNumber getLength() {
        Iterator i = iterator();
        int length = 0;
        while (i.hasNext()) {
            i.next();
            length++;
        }
        return new ExpressionNumber(length);
    }

    @Override
    public Expression atIndex(ExpressionNumber index) throws LispException {
        double at = index.number;
        Iterator<Expression> i = iterator();
        Expression value;
        for (value = null; at >= 0 && i.hasNext(); at--) {
            value = i.next();
        }
        if (at != -1 || value == null) {
            throw new LispException(LispException.ErrorType.ARRAY_OUT_OF_BOUNDS);
        }
        return value;
    }

    @Override
    public ISequence concatenate(ISequence other) {
        Iterator<Expression> myIterator = iterator();
        Iterator<Expression> theirIterator = other.iterator();
        ListWriter list = new ListWriter();

        while (myIterator.hasNext()) {
            list.push(myIterator.next());
        }

        while (theirIterator.hasNext()) {
            list.push(theirIterator.next());
        }

        return list.getResult();
    }
}
