package chameleon.core.reference;

import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

/**
 * This interface represents the concept of a cross references in the model. For proper
 * functioning, every cross reference (method call, variable reference, type reference,...) must
 * implement this interface. 
 * 
 * @author Marko van Dooren
 * @author Tim Vermeiren
 * 
 * <D> The type of the declaration that is referenced by this cross reference.
 */
public interface CrossReference<D extends Declaration> extends Element, CrossReferenceTarget {
	
	/**
	 * Return the element referenced by this cross-reference.
	 * 
	 * @throws LookupException
	 * A LookupException is thrown if the model cannot guarantee to
	 * find a correct result. This happens at some point during
	 * the lookup, a required element cannot be found, or if 
	 * there are multiple equivalent candidates.
	 */
	public D getElement() throws LookupException;

	/**
	 * Return the element that declared the element that is returned
	 * by getElement(). In most cases, both methods return the same element,
	 * but for variable, getElement() returns the variable, whereas 
	 * getDeclarator() may return a variable declarator. The same goes for
	 * type names that refer to type a parameter. In this case getElement()
	 * returns the type, and getDeclarator() returns the type parameter.
	 * 
	 * This method is used e.g. in IDEs to jump to the definition of an element in the source code.
	 * @return
	 * @throws LookupException
	 */
	public Declaration getDeclarator() throws LookupException;

}
