package chameleon.oo.type.generics;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.Association;
import org.rejuse.association.SingleAssociation;
import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.MissingSignature;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReference;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeIndirection;
import chameleon.oo.type.TypeReference;
import chameleon.util.Pair;


public abstract class AbstractInstantiatedTypeParameter<E extends AbstractInstantiatedTypeParameter<E>> extends TypeParameter<E> {

	public AbstractInstantiatedTypeParameter(SimpleNameSignature signature, ActualTypeArgument argument) {
		super(signature);
		setArgument(argument);
	}

	/**
	 * Return a substitution map that specifies which substitutions must be done in the given
	 * element if this type parameter were to be substituted.
	 * @param element The element in which the substitution is to be done.
	 * @return
	 * @throws LookupException
	 */
	public TypeParameterSubstitution substitution(Element<?,?> element) throws LookupException {
		List<CrossReference> crossReferences = 
			 element.descendants(CrossReference.class, 
					              new UnsafePredicate<CrossReference,LookupException>() {
	
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

	public List<Element> children() {
		return new ArrayList<Element>();
	}
	
	private void setArgument(ActualTypeArgument type) {
		_argument = type;
	}
	
	public ActualTypeArgument argument() {
		return _argument;
	}
	
	private ActualTypeArgument _argument;

	public synchronized Type selectionDeclaration() throws LookupException {
		if(_selectionTypeCache == null) {
		  _selectionTypeCache = new ActualType(signature().clone(), argument().type(),this);
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
//		return this;
  	Type result = new LazyTypeAlias(signature().clone(), this);
  	result.setUniParent(parent());
  	return result;
	}

	public static class LazyTypeAlias extends TypeIndirection {

		public LazyTypeAlias(SimpleNameSignature sig, TypeParameter param) {
			super(sig,null);
			_param = param;
		}
		
		public Type aliasedType() {
			try {
				return parameter().upperBound();
			} catch (LookupException e) {
				throw new Error("LookupException while looking for aliasedType of a lazy alias",e);
			}
		}
		
		public TypeParameter parameter() {
			return _param;
		}
		
		private final TypeParameter _param;

		@Override
		public Type clone() {
			return new LazyTypeAlias(signature().clone(), _param);
		}

		public boolean uniSameAs(Type other, List<Pair<TypeParameter, TypeParameter>> trace) throws LookupException {
			return other == this;
		}

		public Declaration declarator() {
			return parameter();
		}
		
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
	public VerificationResult verifySelf() {
		VerificationResult tmp = super.verifySelf();
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

		public int hashCode() {
		return argument().hashCode();
	}

	@Override
	public TypeReference upperBoundReference() throws LookupException {
		return argument().substitutionReference();
	}

}
