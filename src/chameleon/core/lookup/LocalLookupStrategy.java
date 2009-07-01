package chameleon.core.lookup;

import java.util.HashSet;
import java.util.Set;

import chameleon.core.MetamodelException;
import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;

/**
 * 
 * @author Marko van Dooren
 */
public class LocalLookupStrategy<E extends DeclarationContainer> extends LookupStrategy {

	public LocalLookupStrategy(E element) {
		_element = element;
	}
	
	private E _element;

	/**
	 * Return the element referenced by this collector
	 * @return
	 */
	public E element() {
	  return _element; 
	}

	/**
	 * Return the declarations declared on-demand by the referenced element.
	 * 
	 * The default implementation returns an empty set.
	 * @throws LookupException 
	 */
  public Set<Declaration> demandDeclarations() throws LookupException {
  	return new HashSet<Declaration>();
  }
  
	/**
	 * Return the declarations directly declared by the referenced element.
	 * @throws LookupException 
	 */
  public Set<Declaration> directDeclarations() throws LookupException {
  	Set<Declaration> result = new HashSet<Declaration>();
  	Set<Declaration> tmp = element().declarations();
  	for(Declaration decl: tmp) { // does not compile when inlined, stupid Java
  		result.add(decl.resolve());
  	}
  	return result;
  }


  /**
   * Return those declarations of this declaration container that are selected
   * by the given signature selector. First the direct declarations are searched.
   * If that yields no results, the on-demand declarations are searched.
   * 
   * @param <T> The type of the arguments selected by the given signature selector. This type
   *            shoud be inferred automatically.
   * @param selector
   * @return
   * @throws LookupException
   */
  public <T extends Declaration> Set<T> declarations(DeclarationSelector<T> selector) throws LookupException {
    Set<T> result = selector.selection(directDeclarations());
    // Only use on-demand declarations when no direct declarations are found.
    if(result.isEmpty()) {
    	result = selector.selection(demandDeclarations());
    }
    return result;
  }

	@Override
	public <T extends Declaration> T lookUp(DeclarationSelector<T> selector) throws LookupException {
	  Set<T> tmp = declarations(selector);
	  int size = tmp.size();
	  if(size == 1) {
	    return tmp.iterator().next();
	  } else if (size == 0) {
	    return null;
	  } else {
	    throw new LookupException("Multiple matches found in "+element().toString() + " using selector "+selector.toString(),selector);
	  }

	}

}
