package chameleon.core.method.exception;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rejuse.association.OrderedReferenceSet;
import org.rejuse.java.collections.RobustVisitor;
import org.rejuse.java.collections.Visitor;
import org.rejuse.predicate.PrimitivePredicate;

import chameleon.core.element.Element;
import chameleon.core.expression.Invocation;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.method.MethodHeader;
import chameleon.core.method.MethodSignature;
import chameleon.core.namespacepart.NamespaceElementImpl;
import chameleon.core.type.Type;

/**
 * @author Marko van Dooren
 */
public class ExceptionClause extends NamespaceElementImpl<ExceptionClause,Method<? extends Method,? extends MethodHeader,? extends MethodSignature>> {

  public ExceptionClause() {
	}

  
public boolean compatibleWith(final ExceptionClause other) throws LookupException {
    if (other == null) {
      return false;
    }
    try {
      return new PrimitivePredicate() {
        public boolean eval(Object o) throws LookupException {
          return ((ExceptionDeclaration)o).compatibleWith(other);
        }
      }.forAll(getDeclarations());
    }
    catch (LookupException e) {
      throw e;
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new Error();
    }
  }

  public Set getExceptionTypes(final Invocation invocation) throws LookupException {
    final Set result = new HashSet();
    try {
      new RobustVisitor() {
        public Object visit(Object element) throws LookupException {
          result.addAll(((ExceptionDeclaration)element).getExceptionTypes(invocation));
          return null;
        }

        public void unvisit(Object element, Object undo) {
          //NOP
        }
      }.applyTo(getDeclarations());
    }
    catch (LookupException e) {
      throw e;
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new Error();
    }

    return result;
  }

  public Set getWorstCaseExceptions() throws LookupException {
    final Set result = new HashSet();
    try {
      new RobustVisitor() {
        public Object visit(Object element) throws LookupException {
          result.addAll(((ExceptionDeclaration)element).getWorstCaseExceptionTypes());
          return null;
        }

        public void unvisit(Object element, Object undo) {
          //NOP
        }
      }.applyTo(getDeclarations());
    }
    catch (LookupException e) {
      throw e;
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new Error();
    }

    return result;
  }

	/**
	 * EXCEPTION DECLARATIONS
	 */

  private OrderedReferenceSet<ExceptionClause,ExceptionDeclaration> _exceptionDeclarations = new OrderedReferenceSet<ExceptionClause,ExceptionDeclaration>(this);

  public void add(ExceptionDeclaration decl) {
    _exceptionDeclarations.add(decl.parentLink());
  }

  public void remove(ExceptionDeclaration decl) {
    _exceptionDeclarations.remove(decl.parentLink());
  }

  public List<ExceptionDeclaration> getDeclarations() {
    return _exceptionDeclarations.getOtherEnds();
  }


  public Type getNearestType() {
    return parent().getNearestType();
  }

  /**
   * @return
   */
  public ExceptionClause clone() {
    final ExceptionClause result = new ExceptionClause();
    new Visitor() {
      public void visit(Object element) {
         result.add(((ExceptionDeclaration)element).clone());
      }
    }.applyTo(getDeclarations());
    return result;
  }

  public boolean hasValidAccessibility() throws LookupException {
    try {
      return new PrimitivePredicate() {
        public boolean eval(Object o) throws LookupException {
          return ((ExceptionDeclaration)o).hasValidAccessibility();
        }
      }.forAll(getDeclarations());
    }
    catch (LookupException e) {
      throw e;
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new Error();
    }
  }

 /*@
   @ public behavior
   @
   @ post \result.equals(getDeclarations());
   @*/
  public List<? extends Element> children() {
    return getDeclarations();
  }

  /**
   * @param done
   * @return
   */
  public boolean isAcyclic(final Set done) throws LookupException {
    try {
      return new PrimitivePredicate() {
        public boolean eval(Object o) throws LookupException {
          ExceptionDeclaration decl = (ExceptionDeclaration)o;
          return decl.isAcyclic(done);
        }
      }.forAll(getDeclarations());
    }
    catch (LookupException e) {
      throw e;
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new Error();
    }
  }
}
