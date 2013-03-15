package chameleon.oo.statement;

import java.util.List;
import java.util.ListIterator;

import be.kuleuven.cs.distrinet.rejuse.java.collections.RobustVisitor;
import chameleon.core.element.ElementImpl;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;

/**
 * @author Marko van Dooren
 */

public abstract class StatementImpl extends ElementImpl implements Statement {


  protected StatementImpl() {
  }

  public abstract StatementImpl clone();

  public CheckedExceptionList getCEL() throws LookupException {
    final CheckedExceptionList cel = getDirectCEL();
    try {
      new RobustVisitor<ExceptionSource>() {
        public Object visit(ExceptionSource element) throws LookupException {
          cel.absorb(((ExceptionSource)element).getCEL());
          return null;
        }

        public void unvisit(ExceptionSource element, Object undo) {
          //NOP
        }
      }.applyTo(children(ExceptionSource.class));
      return cel;
    }
    catch (LookupException e) {
      throw e;
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new Error();
    }
  }

  public CheckedExceptionList getDirectCEL() throws LookupException {
    return new CheckedExceptionList();
  }

  public CheckedExceptionList getAbsCEL() throws LookupException {
    final CheckedExceptionList cel = getDirectAbsCEL();
    try {
      new RobustVisitor() {
        public Object visit(Object element) throws LookupException {
          cel.absorb(((ExceptionSource)element).getAbsCEL());
          return null;
        }

        public void unvisit(Object element, Object undo) {
          //NOP
        }
      }.applyTo(children());
      return cel;
    }
    catch (LookupException e) {
      throw e;
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new Error();
    }
  }

  public CheckedExceptionList getDirectAbsCEL() throws LookupException {
    return getDirectCEL();
  }

  public boolean before(Statement other) {
    StatementListContainer container = getNearestCommonStatementListContainer(other);
    List myParents = ancestors();
    List otherParents = other.ancestors();
    myParents.add(0, this);
    otherParents.add(0, other);
    Statement myAncestor = (Statement)myParents.get(myParents.indexOf(container) - 1);
    Statement otherAncestor = (Statement)otherParents.get(myParents.indexOf(container) - 1);
    return container.getIndexOf(myAncestor) < container.getIndexOf(otherAncestor);
  }

  public StatementListContainer getNearestCommonStatementListContainer(Statement other) {
    List myParents = ancestors();
    List otherParents = other.ancestors();
    ListIterator myIter = myParents.listIterator(myParents.size());
    ListIterator otherIter = otherParents.listIterator(myParents.size());
    Object common = null;
    while ((myIter.hasPrevious()) && (otherIter.hasPrevious())) {
      Object mine = myIter.previous();
      Object others = otherIter.previous();
      if(mine.equals(others)) {
        common = mine;
      }
    }
    if(common instanceof StatementListContainer) {
      return (StatementListContainer)common;
    }
    else {
      return null;
    }
  }
  
  /**
   * The linear lookup strategy of a statement is the lookup strategy used for the element that comes next in a block.
   * For example, the linear lookup strategy of a variable declaration statement includes the declared variable, while its
   * lexical lookup strategy method does not.
   * 
   * Returns the lexical lookup strategy by default.
   * @return
   * @throws LookupException
   */
  public LookupStrategy linearLookupStrategy() throws LookupException {
  	return lexicalLookupStrategy();
  }
}
