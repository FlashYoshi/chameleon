package chameleon.support.expression;

import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.expression.Expression;
import chameleon.util.association.Single;

public abstract class TernaryExpression extends BinaryExpression {

	public TernaryExpression(Expression first, Expression second, Expression third) {
		super(first,second);
		setThird(third);
	}
	
	/**
	 * THIRD
	 */
	private Single<Expression> _third = new Single<Expression>(this);

  public Expression getThird() {
    return _third.getOtherEnd();
  }

  public void setThird(Expression expression) {
  	set(_third,expression);
  }

	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = super.verifySelf();
    if(getThird() == null) {
    	result = result.and(new BasicProblem(this,"The expression has no third expression."));
    }
    return result;
	}

}
