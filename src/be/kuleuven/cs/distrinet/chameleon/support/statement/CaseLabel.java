package be.kuleuven.cs.distrinet.chameleon.support.statement;

import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.VerificationResult;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.util.association.Single;

/**
 * @author Marko van Dooren
 */
public class CaseLabel extends SwitchLabel {
  
  public CaseLabel(Expression expr) {
    setExpression(expr);
  }

	/**
	 * EXPRESSION
	 */
	private Single<Expression> _expression = new Single<Expression>(this,true);

  
  public Expression getExpression() {
    return _expression.getOtherEnd();
  }
  
  public void setExpression(Expression expression) {
    set(_expression,expression);
  }

  public void removeExpression(Expression expr) {
    if(getExpression() == expr) {
      _expression.connectTo(null);
    }
  }

  public CaseLabel clone() {
    return new CaseLabel(getExpression().clone());
  }

	@Override
	public VerificationResult verifySelf() {
		return checkNull(getExpression(), "The case label has no expression", Valid.create());
	}

}