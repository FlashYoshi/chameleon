package chameleon.support.expression;

import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.expression.Expression;
import chameleon.util.association.Single;


/**
 * @author Marko van Dooren
 */
public abstract class BinaryExpression extends ExpressionContainingExpression {
  
  public BinaryExpression(Expression first, Expression second) {
    super(first);
    setSecond(second);
  }
  
  public Expression getFirst() {
  	return getExpression();
  }
  
  public void setFirst(Expression expr) {
  	setExpression(expr);
  }

	/**
	 * SECOND
	 */
	private Single<Expression> _second = new Single<Expression>(this);

  
  /**
   * Return the second expression
   */
  public Expression getSecond() {
    return _second.getOtherEnd();
  }
  
  /**
   * Set the second expression
   */
 /*@
   @ public behavior
   @
   @ post getSecond().equals(second); 
   @*/
  public void setSecond(Expression expression) {
    set(_second,expression);
  }
 
	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = Valid.create();
    if(getFirst() == null) {
    	result = result.and(new BasicProblem(this,"The expression has no left-hand side."));
    }
    if(getSecond() == null) {
    	result = result.and(new BasicProblem(this,"The expression has no right-hand side."));
    }
    return result;
	}

}
