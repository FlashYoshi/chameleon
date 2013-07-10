package be.kuleuven.cs.distrinet.chameleon.core.lookup;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.DeclarationContainer;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Signature;
import be.kuleuven.cs.distrinet.chameleon.core.relation.WeakPartialOrder;

/**
 * A class of objects that select declarations during lookup.
 * 
 * @author Marko van Dooren
 *
 * @param <D> The type of the declarations selected by this signature selector.
 *            This parameter allows for more specific typing of the result of {@see #selection(Set)},
 *            and consequently of {@see Context#declaration(SignatureSelector)}.
 */
public abstract class DeclarationSelector<D extends Declaration> {
   
	public DeclarationSelector() {
		// Only here to be able to query call hierarchy
	}
	
  /**
   * Return the of the declaration that will be selected by this declaration selector.
   * 
   * This method is provided here such that declaration containers can keep a hashmap as
   * a cache, and quickly select the potential candidate(s). This way, we avoid having to
   * check all members. The most discriminating property of a declaration is usually its name.
   * 
   * @assumption We assume that a declaration selector can only select a declaration with a single
   *             name. If that is no longer the case, this method should return a collection of names.
   *             That makes the caching code more complex, and is slower, so we don't do that for now.
   * 
   * @return
   * @throws LookupException 
   */
  public abstract String selectionName(DeclarationContainer container) throws LookupException;
  
  /**
   * Return the declarations of the given declaration container to which selection is applied.
   * This round-trip allows both the container and the selector to filter the candidates.
   * 
   * The default result is container.declarations(this), but clients cannot rely on that.
   */
  public List<D> declarations(DeclarationContainer container) throws LookupException {
  	return container.declarations(this);
  }
  
  /**
   * Required because 'instanceof D' cannot be used due to type erasure.
   */
 /*@
   @ public behavior
   @
   @ post \result != null;
   @*/
  public abstract Class<D> selectedClass();
  
  /**
   * Only called from within a @link{Cache} to store the cache of this selector.
   * The declaration is best suited for knowing how its selections can be cached.
   * For example, a selector that can select declarations of multiple types
   * needs a different cache than a selector that can only select declarations
   * of a single type. A generic caching scheme would be too inefficient in terms
   * of object creation.
   * 
   * The default implementation does nothing.
   * @param cache
   */
  protected void updateCache(Cache cache, D selection) {
  	
  }
  
  /**
   * Only called from within a @link{Cache} to read the cache of this selector.
   * 
   * The default implementation returns null.
   * @param cache
   * @return
   */
  protected D readCache(Cache cache) {
  	return null;
  }
  
  /**
   * Return the list of declarations in the given set that are selected.
   * 
   * @param declarators
   *        The list containing the declarations that are checked for a match with {@link #selects(Signature)}}.
   * @return
   * @throws LookupException
   */
  public abstract List<D> selection(List<? extends Declaration> declarators) throws LookupException;
  
  /**
   * Return the list of declarations in the given set that are selected.
   * 
   * @param selectionCandidates
   *        The list containing the declarations that are checked for a match with {@link #selects(Signature)}}.
   * @return
   * @throws LookupException
   */
  public abstract List<? extends Declaration> declarators(List<? extends Declaration> selectionCandidates) throws LookupException;

	/**
	 * If the selectionName() of this selector must match declaration.signature().name() when that declaration is selected,
	 * then this method returns true. Otherwise, the method returns false. This method can be used for a String based preselection
	 * such that declaration containers can cache their declarations in a map with the declaration.signature().name() as the key.
	 * 
	 * For super constructor calls in Java, e.g. this method will return false.
	 * @return
	 */
	public boolean usesSelectionName() {
		return true;
	}
	
	/**
	 * Return whether this selector will select the "first" match. With "first" we mean that for example in an inheritance
	 * hierarchy, the selector is able to determine that a declaration in a class will be the final one, regardless of
	 * the declarations in the superclasses. This is the case for any declaration whose signature consists only of a name,
	 * such as fields and classes. This is <b>not</b> the case for methods.
	 * 
	 * This method allows a declaration container to stop the search when a declaration has been found without needlessly
	 * continuing the search in e.g. super declaration containers.
	 * @return
	 */
	public boolean isGreedy() {
		return usesSelectionName();
	}

}
