package chameleon.core.method;

import chameleon.core.lookup.LookupException;
import chameleon.core.namespacepart.NamespaceElementImpl;
import chameleon.core.statement.Block;
import chameleon.core.type.Type;

/**
 * @author Marko van Dooren
 */

public abstract class Implementation<E extends Implementation> extends NamespaceElementImpl<E,Method<? extends Method, ? extends MethodHeader,? extends MethodSignature>> {

  public Implementation() {
    }

  public final Type getNearestType() {
    return parent().getNearestType();
  }


	//  public abstract Set getAllStatements();

  /**
   * @return
   */
  public abstract E clone();

  /**
   * Check if the implementation exception clause of the method body 
   * conforms to the exception clause of the parent method.
   * @return
   */
  public abstract boolean compatible() throws LookupException;

  /**
   * Check whether or not all catch blocks in the implementation are useful. If a catch block
   * catches a checked exception that can never be thrown in the try statement, that catch block
   * is useless and should be removed.
   */
  public boolean hasValidCatchClauses() throws LookupException {
    return true;
  }

  /**
   * Return the body of this implementation.
   */
	public abstract Block getBody();


}
