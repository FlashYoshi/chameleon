package chameleon.core.type;

import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.member.Member;
import chameleon.core.modifier.Modifier;
import chameleon.core.modifier.ModifierContainer;
import chameleon.core.namespace.NamespaceElement;
import chameleon.core.statement.CheckedExceptionList;

/**
 * A class of elements that can be direct children of a type.
 * 
 * @author Marko van Dooren
 *
 * @param <E> The type of the element itself
 * @param <P> The type of the parent of the element
 */
public interface TypeElement<E extends TypeElement<E,P>, P extends Element> extends NamespaceElement<E, P>, ModifierContainer<E, P> {

  public abstract E clone();
  
  /**
   * Return the set of members introduced into the parent type (if any) of this type element.
   */
 /*@
   @ public behavior
   @
   @ post \result != null; 
   @*/
  public List<? extends Member> getIntroducedMembers();
  
  /**
   * Return the modifiers of this type element.
   */
 /*@
   @ public behavior
   @
   @ post \result != null;
   @*/
  public List<Modifier> modifiers();
  
  /**
   * Add the given modifier to this type element.
   */
 /*@
   @ public behavior
   @
   @ pre modifier != null;
   @
   @ post modifiers().contains(modifier);
   @*/
  public void addModifier(Modifier modifier);
  
  /**
   * Remove the given modifier from this type element.
   */
 /*@
   @ public behavior
   @
   @ pre modifier != null;
   @
   @ post ! modifiers().contains(modifier);
   @*/
  public void removeModifier(Modifier modifier);
  
 /*@
   @ public behavior
   @
   @ pre modifiers != null;
   @
   @ post modifiers().containsAll(modifiers);
   @*/
  public void addModifiers(List<Modifier> modifiers);

	public abstract CheckedExceptionList getCEL() throws LookupException;
	
	public abstract CheckedExceptionList getAbsCEL() throws LookupException;
    
}
