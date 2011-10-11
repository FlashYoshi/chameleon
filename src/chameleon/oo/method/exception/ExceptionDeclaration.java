package chameleon.oo.method.exception;

import java.util.Set;

import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.oo.expression.MethodInvocation;

/**
 * @author Marko van Dooren
 */

public abstract class ExceptionDeclaration<E extends ExceptionDeclaration> extends NamespaceElementImpl<E> {

  public ExceptionDeclaration() {
	}


  public abstract boolean compatibleWith(ExceptionClause clause) throws LookupException;

  public abstract E clone();

  public abstract Set getExceptionTypes(MethodInvocation invocation) throws LookupException;

	/**
	 *
	 * @uml.property name="worstCaseExceptionTypes"
	 */
	public abstract Set getWorstCaseExceptionTypes()
		throws LookupException;


  public abstract boolean hasValidAccessibility() throws LookupException;

}
