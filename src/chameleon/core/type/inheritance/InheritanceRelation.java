package chameleon.core.type.inheritance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.rejuse.association.Reference;
import org.rejuse.logic.ternary.Ternary;

import chameleon.core.declaration.Signature;
import chameleon.core.element.ChameleonProgrammerException;
import chameleon.core.element.Element;
import chameleon.core.element.ElementImpl;
import chameleon.core.language.ObjectOrientedLanguage;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.DummyLookupStrategy;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.member.Member;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;

public abstract class InheritanceRelation<E extends InheritanceRelation> extends ElementImpl<E,Type> {
	
	private static Logger logger = Logger.getLogger("lookup.inheritance");
	
	public Logger lookupLogger() {
		return logger;
	}
	
	public abstract E clone();

	public InheritanceRelation(TypeReference ref) {
		setSuperClassReference(ref);
	}
	
	public List<? extends Element> children() {
		List<Element> result = new ArrayList<Element>();
		result.add(superClassReference());
		return result;
	}
	
	/**
	 * Return the inherited class.
	 * @return
	 * @throws LookupException
	 */
 /*@
   @ public behavior
   @
   @ post \result == superClassReference().getType();
   @*/
	public Type superClass() throws LookupException {
			lookupLogger().debug("Inheritance relation of class "+parent().getFullyQualifiedName()+" is going to look up super class " + superClassReference().getName());
			Type result = null;
			try {
		    result = superClassReference().getType();
			} 
			catch(NullPointerException exc) {
				if(superClassReference() == null) {
				  throw new ChameleonProgrammerException("trying to get the super class of an inheritance relation that points to null in class" + parent().getFullyQualifiedName(),exc);
				} else {
					throw exc;
				}
			}
		  if(result != null) {
		  	return result;
		  } else {
		  	throw new LookupException("Superclass is null",superClassReference());
		  }
	}
	
	/**
	 * Return the inherited type, if this relation also introduces a subtype relation.
	 * @return
	 * @throws LookupException
	 */
 /*@
   @ public behavior
   @
   @ post \result == null || \result == superClass();
   @*/
	public abstract Type superType() throws LookupException;
	
	/**
	 * Return a reference to the inherited class.
	 * @return
	 */
	public TypeReference superClassReference() {
    return _superClass.getOtherEnd();
	}
	
 /*@
   @ public behavior
   @
   @ post superClassReference() == ref;
   @*/
	public void setSuperClassReference(TypeReference ref) {
		if(ref != null) {
			_superClass.connectTo(ref.parentLink());
		} else {
			_superClass.connectTo(null);
		}
	}
	
	/**
	 * Return the set of members inherited through this inheritance relation.
	 * @param <M>
	 * @param <S>
	 * @param <F>
	 * @param kind
	 * @return
	 * @throws LookupException
	 */
	public <M extends Member<M,? super Type,S,F>, S extends Signature<S,M>, F extends Member<? extends Member,? super Type,S,F>> 
  void accumulateInheritedMembers(final Class<M> kind, List<M> current) throws LookupException {
		final List<M> toAdd = new ArrayList<M>();
		final List<M> potential = potentiallyInheritedMembers(kind);
		removeNonMostSpecificMembers(current, toAdd, potential);
	}

	public <M extends Member<M,? super Type,S,F>, S extends Signature<S,M>, F extends Member<? extends Member,? super Type,S,F>> 
  void accumulateInheritedMembers(DeclarationSelector<M> selector, List<M> current) throws LookupException {
		final List<M> toAdd = new ArrayList<M>();
		final List<M> potential = potentiallyInheritedMembers(selector);
		removeNonMostSpecificMembers(current, toAdd, potential);
	}

	private <M extends Member<M,? super Type,S,F>, S extends Signature<S,M>, F extends Member<? extends Member,? super Type,S,F>>
	  void removeNonMostSpecificMembers(List<M> current, final List<M> toAdd, final List<M> potential) throws LookupException {
		for(M m: potential) {
			boolean add = true;
			Iterator<M> iterCurrent = current.iterator();
			while(add && iterCurrent.hasNext()) {
				M alreadyInherited = iterCurrent.next();
				// Remove the already inherited member if potentially inherited member m overrides or hides it.
				if((alreadyInherited != m) && (m.overrides(alreadyInherited) || m.canImplement(alreadyInherited) || m.hides(alreadyInherited))) {
					iterCurrent.remove();
				}
				if(add == true && ((alreadyInherited == m) || alreadyInherited.overrides(m) || alreadyInherited.equivalentTo(m) || alreadyInherited.canImplement(m) || alreadyInherited.hides(m))) {
					add = false;
				}
			}
			if(add == true) {
				toAdd.add(m);
			}
		}
		current.addAll(toAdd);
	}

	
	public <M extends Member<M,? super Type,S,F>, S extends Signature<S,M>, F extends Member<? extends Member,? super Type,S,F>> 
	        List<M> potentiallyInheritedMembers(final Class<M> kind) throws LookupException {
		List<M> superMembers = superClass().members(kind);
		removeNonInheritableMembers(superMembers);
    return superMembers;
	}

	public <M extends Member<M, ? super Type, S, F>, S extends Signature<S, M>, F extends Member<?, ? super Type, S, F>> List<M> potentiallyInheritedMembers(
			final DeclarationSelector<M> selector) throws LookupException {
		List<M> superMembers = superClass().members(selector);
		removeNonInheritableMembers(superMembers);
		return superMembers;
	}

  /**
   * Remove members that are not inheritable.
   */
 /*@
   @ public behavior
   @
   @ (\forall M m; members.contains(m); \old(members()).contains(m) && m.is(language(ObjectOrientedLanguage.class).INHERITABLE == Ternary.TRUE);
   @*/
	private <M extends Member<M, ? super Type, S, F>, S extends Signature<S, M>, F extends Member<?, ? super Type, S, F>> void removeNonInheritableMembers(List<M> members) throws LookupException {
		Iterator<M> superIter = members.iterator();
		while(superIter.hasNext()) {
			M member = superIter.next();
			Ternary temp = member.is(language(ObjectOrientedLanguage.class).INHERITABLE);
			if (temp == Ternary.UNKNOWN) {
				temp = member.is(language(ObjectOrientedLanguage.class).INHERITABLE);
				throw new LookupException("For one of the members of super type " + superClass().getFullyQualifiedName()
						+ " it is unknown whether it is inheritable or not. Member type: " + member.getClass().getName());
			} else {
				if (temp == Ternary.FALSE) {
					superIter.remove();
				}
			}
		}
	}
	
//	public <M extends Member<M,? super Type,S,F>, S extends Signature<S,M>, F extends Member<? extends Member,? super Type,S,F>> 
//	        M transform(M member) throws MetamodelException {
//		M result = member.clone();
//		// 1) SUBSTITUTE GENERIC PARAMETERS, OR USE TRICK CONTAINER?
//		//   1.a) WE NEED A TRICK CONTAINER (The superclass) IN WHICH THE PARAMETERS ARE SUBSTITUTED
//	}
	
	private Reference<InheritanceRelation,TypeReference> _superClass = new Reference<InheritanceRelation, TypeReference>(this);

}
