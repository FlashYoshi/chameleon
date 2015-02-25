package org.aikodi.chameleon.support.expression;

import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.util.association.Single;

public abstract class ExpressionContainingExpression extends Expression {

	public ExpressionContainingExpression(Expression expr) {
		setExpression(expr);
	}
	
	/**
	 * FIRST
	 */
  
	private Single<Expression> _first = new Single<Expression>(this);

  /**
   * Return the first expression
   */
  public Expression getExpression() {
    return _first.getOtherEnd();
  }

  /**
   * Set the first expression
   */
 /*@
   @ public behavior
   @
   @ post getFirst().equals(first); 
   @*/
  public void setExpression(Expression expression) {
    set(_first,expression);
  }


}
