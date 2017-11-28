package EmpireLisp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tyrerexus
 * @date 11/20/17
 */
@SuppressWarnings("JavaDoc")
public class ExpressionPair extends Expression {
    @SuppressWarnings("WeakerAccess")
    public Expression left;

    @SuppressWarnings("WeakerAccess")
    public Expression right;

    @SuppressWarnings("WeakerAccess")
    public ExpressionPair(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        if (left != null || right != null) {
            return "(" + (left != null ? left.toString() : "") + " . " + (right != null ? right.toString() : "") + ")";
        }
        else {
            return "()";
        }
    }

    @Override
    public boolean equals(Expression other) {
        if (other instanceof ExpressionPair) {
            ExpressionPair otherPair = (ExpressionPair) other;
            if (this.left == null && this.right == null) {
                return otherPair.left == null && otherPair.right == null;
            }
            else if (this.left == null) {
                return otherPair.left == null && this.right.equals(otherPair.right);
            }
            else if (this.right == null) {
                return this.left.equals(otherPair.left) && otherPair.right == null;
            }
            else {
                return this.left.equals(otherPair.left) && this.right.equals(otherPair.right);
            }
        }
        else {
            return false;
        }
    }

    @Override
    public void eval(Environment environment, IEvalCallback callback) throws LispException {
        if (this.right instanceof ExpressionPair) {
            ExpressionPair pair = this;
            this.left.eval(environment, new IEvalCallback() {
                @Override
                public void evalCallback(Expression fun) throws LispException {
                    if (fun instanceof IApplicable) {
                        if (!((IApplicable)fun).isLazyEval()) {
                            ExpressionPair result = new ExpressionPair(null, null);
                            class ListWrapper {
                                ExpressionPair value = (ExpressionPair) pair.right;
                            }
                            final ListWrapper list = new ListWrapper();
                            class HeadWrapper {
                                ExpressionPair value = result;
                            }
                            final HeadWrapper head = new HeadWrapper();

                            if (pair.right instanceof ExpressionPair) {
                                if (((ExpressionPair) pair.right).left != null) {
                                    ((ExpressionPair) pair.right).left.eval(environment, new IEvalCallback() {
                                        @Override
                                        public void evalCallback(Expression argument) throws LispException {
                                            head.value.left = argument;
                                            head.value.right = new ExpressionPair(null, null);
                                            head.value = (ExpressionPair) head.value.right;

                                            if (list.value.right instanceof ExpressionPair) {
                                                list.value = (ExpressionPair) list.value.right;
                                                if (list.value.left != null) {
                                                    list.value.left.eval(environment, this);
                                                } else {
                                                    ((IApplicable) fun).apply(environment, result, callback);
                                                }
                                            } else {
                                                throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                                            }
                                        }
                                    });
                                }
                                else {
                                    ((IApplicable) fun).apply(environment, result, callback);
                                }
                            }
                        }
                        else {
                            ((IApplicable)fun).apply(environment, pair.right, callback);
                        }
                    }
                    else {
                        throw new LispException(LispException.ErrorType.NOT_APPLICABLE,"\"" + fun.toString() + "\" is not applicable");
                    }
                }
            });
        }
        else {
            throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public List<Expression> toList() {
        List<Expression> result = new ArrayList<>();
        ExpressionPair list = this;
        while(list.left != null) {
            result.add(list.left);

            if (list.right instanceof  ExpressionPair) {
                list = (ExpressionPair) list.right;
            }
            else {
                break;
            }
        }
        return result;
    }
}
