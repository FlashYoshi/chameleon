package chameleon.aspect.oo.model.pointcut;

import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;
import chameleon.aspect.core.model.pointcut.expression.MatchResult;
import chameleon.aspect.core.model.pointcut.expression.staticexpression.within.WithinPointcutExpressionDeprecated;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.util.Util;
import chameleon.util.association.Single;

public class WithinTypePointcutExpression<E extends WithinTypePointcutExpression<E>> extends WithinPointcutExpressionDeprecated {
	
	private Single<TypeReference> _typeReference = new Single<TypeReference>(this);
	private Single<SubtypeMarker> _subtypeMarker = new Single<SubtypeMarker>(this);

	public void setSubtypeMarker(SubtypeMarker marker) {
		set(_subtypeMarker, marker);
	}
	
	public boolean hasSubtypeMarker() {
		return subtypeMarker() != null;
	}
	
	public SubtypeMarker subtypeMarker() {
		return _subtypeMarker.getOtherEnd();
	}
	
	public TypeReference typeReference() {
		return _typeReference.getOtherEnd();
	}
	
	public void setTypeReference(TypeReference typeReference) {
		set(_typeReference, typeReference);
	}

	@Override
	public MatchResult match(Element joinpoint) throws LookupException {
		@SuppressWarnings("unchecked")
		boolean noMatch = joinpoint.ancestors(Type.class, new SafePredicate<Type>() {

			@Override
			public boolean eval(Type object) {
				try {
					if (hasSubtypeMarker())
						return object.assignableTo(typeReference().getType());
					else
						return object.sameAs(typeReference().getType());
				} catch (LookupException e) {
					// Shouldn't occur with normale usage, only due to a bug
					e.printStackTrace();
					return false;
				}
			}
		}).isEmpty();
		
		if (noMatch)
			return MatchResult.noMatch();
		else
			return new MatchResult<Element>(this, joinpoint);
	}

	@Override
	public E clone() {
		WithinTypePointcutExpression<E> clone = new WithinTypePointcutExpression<E>();
		clone.setSubtypeMarker(Util.cloneOrNull(subtypeMarker()));
		clone.setTypeReference(Util.cloneOrNull(typeReference()));

		return (E) clone;
	}
}