package be.kuleuven.cs.distrinet.chameleon.support.statement;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.Statement;
import be.kuleuven.cs.distrinet.chameleon.util.Util;

/**
 * @author Marko van Dooren
 */
public class WhileStatement extends IterationStatementWithExpression {

  public WhileStatement(Expression expression, Statement statement) {
    super(statement, expression);
  }

  public WhileStatement clone() {
    return new WhileStatement(condition().clone(), getStatement().clone());
  }
}
