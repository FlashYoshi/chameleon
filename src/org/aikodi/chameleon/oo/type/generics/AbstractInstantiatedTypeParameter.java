package org.aikodi.chameleon.oo.type.generics;

import java.util.List;

import org.aikodi.chameleon.core.declaration.MissingSignature;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.CrossReference;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.util.Pair;

import be.kuleuven.cs.distrinet.rejuse.predicate.Predicate;


public abstract class AbstractInstantiatedTypeParameter extends TypeParameter {

	public AbstractInstantiatedTypeParameter(String name, ActualTypeArgument argument) {
		super(name);
		setArgument(argument);
	}

	/**
	 * Return a substitution map that specifies which substitutions must be done in the given
	 * element if this type parameter were to be substituted.
	 * @param element The element in which the substitution is to be done.
	 * @return
	 * @throws LookupException
	 */
	public TypeParameterSubstitution substitution(Element element) throws LookupException {
		List<CrossReference> crossReferences = 
			 element.descendants(CrossReference.class, 
					              new Predicate<CrossReference,LookupException>() {
	
													@Override
													public boolean eval(CrossReference object) throws LookupException {
//														return object.getElement().sameAs(selectionDeclaration());
														return object.getDeclarator().sameAs(AbstractInstantiatedTypeParameter.this);
													}
				 
			                  });
		
//		for(CrossReference cref: crossReferences) {
//			SingleAssociation parentLink = cref.parentLink();
//			Association childLink = parentLink.getOtherRelation();
//			TypeReference namedTargetExpression = argument().substitutionReference().clone();
//			childLink.replace(parentLink, namedTargetExpression.parentLink());
//		}
		return new TypeParameterSubstitution(this, crossReferences);
	}

	private void setArgument(ActualTypeArgument type) {
		_argument = type;
	}
	
	public ActualTypeArgument argument() {
		return _argument;
	}
	
	private ActualTypeArgument _argument;

	@Override
   public synchronized Type selectionDeclaration() throws LookupException {
		if(_selectionTypeCache == null) {
		  _selectionTypeCache = new InstantiatedParameterType(name(), argument().type(),this);
		}
		return _selectionTypeCache;
	}

	@Override
	public synchronized void flushLocalCache() {
		super.flushLocalCache();
		_selectionTypeCache = null;
	}

	private Type _selectionTypeCache;

	
	@Override
	public Type resolveForRoundTrip() throws LookupException {
  	Type result = new LazyInstantiatedAlias(name(), this);
  	result.setUniParent(parent());
  	return result;
	}

	public TypeParameter capture(FormalTypeParameter formal, List<TypeConstraint> accumulator) {
		return argument().capture(formal,accumulator);
	}
	
	@Override
	public Type lowerBound() throws LookupException {
		return argument().type();
	}

	@Override
	public Type upperBound() throws LookupException {
		return argument().type();
	}
	
	@Override
	public Verification verifySelf() {
		Verification tmp = super.verifySelf();
		if(argument() != null) {
		  return tmp;
		} else {
			return tmp.and(new MissingSignature(this)); 
		}
	}

	@Override
	public boolean uniSameAs(Element other) throws LookupException {
		return other == this;
//		boolean result = false;
//		if(other instanceof InstantiatedTypeParameter) {
//		 result = argument().sameAs(((InstantiatedTypeParameter)other).argument());
//		}
//		return result;
	}
	
//	@Override
//	public boolean sameValueAs(TypeParameter other) throws LookupException {
//		boolean result = false;
//		if(other instanceof AbstractInstantiatedTypeParameter) {
//			result = argument().sameAs(((InstantiatedTypeParameter)other).argument());
//		}
//		return result;
//	}

	@Override
	public boolean sameValueAs(TypeParameter other, List<Pair<TypeParameter, TypeParameter>> trace) throws LookupException {
		boolean result = false;
		if(other instanceof AbstractInstantiatedTypeParameter) {
			result = argument().sameAs(((InstantiatedTypeParameter)other).argument(), trace);
		}
		return result;
	}

		@Override
      public int hashCode() {
		return argument().hashCode();
	}

	@Override
	public TypeReference upperBoundReference() throws LookupException {
		return argument().substitutionReference();
	}

	@Override
	public String toString() {
		return argument().toString();
	}
}
