package chameleon.support.expression;

import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReferenceTarget;
import chameleon.oo.expression.Expression;


/**
 * @author Marko van Dooren
 */
public class ConditionalOrExpression extends ConditionalBooleanExpression<ConditionalOrExpression> {

  /**
   * @param first
   * @param second
   */
  public ConditionalOrExpression(Expression first, Expression second) {
    super(first, second);
  }
  
  public ConditionalOrExpression clone() {
    Expression first = getFirst().clone();
    Expression second = getSecond().clone();
    return new ConditionalOrExpression(first, second);
  }

}
