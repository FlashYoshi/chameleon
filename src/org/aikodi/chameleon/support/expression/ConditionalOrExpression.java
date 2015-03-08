package org.aikodi.chameleon.support.expression;

import org.aikodi.chameleon.oo.expression.Expression;


/**
 * @author Marko van Dooren
 */
public class ConditionalOrExpression extends ConditionalBooleanExpression {

  /**
   * @param first
   * @param second
   */
  public ConditionalOrExpression(Expression first, Expression second) {
    super(first, second);
  }
  
  @Override
protected ConditionalOrExpression cloneSelf() {
    return new ConditionalOrExpression(null,null);
  }

}
