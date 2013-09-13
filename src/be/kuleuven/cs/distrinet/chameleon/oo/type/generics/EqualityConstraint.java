package be.kuleuven.cs.distrinet.chameleon.oo.type.generics;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;

public class EqualityConstraint extends TypeConstraint {
	
	public EqualityConstraint() {
	}
	
	public EqualityConstraint(TypeReference ref) {
		setTypeReference(ref);
	}

	@Override
	protected EqualityConstraint cloneSelf() {
		return new EqualityConstraint();
	}

	@Override
	public Type lowerBound() throws LookupException {
		return bound().lowerBound();
	}

	@Override
	public boolean matches(Type type) throws LookupException {
		return type.sameAs(bound());
	}

	@Override
	public Type upperBound() throws LookupException {
		return bound().upperBound();
	}

	@Override
	public TypeReference upperBoundReference() {
		return typeReference();
	}

	@Override
	public String toString() {
		return "equals "+toStringTypeReference();
	}

}
