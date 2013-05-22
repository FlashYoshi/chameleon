package be.kuleuven.cs.distrinet.chameleon.oo.type.generics;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;

public class SuperConstraint extends TypeConstraint {

	private SuperConstraint() {
		
	}
	
	public SuperConstraint(TypeReference ref) {
		setTypeReference(ref);
	}
	
	@Override
	public boolean matches(Type type) throws LookupException {
		return upperBound().subTypeOf(type);
	}

	@Override
	protected SuperConstraint cloneSelf() {
		return new SuperConstraint();
	}

	@Override
	public Type lowerBound() throws LookupException {
		return bound();
	}

	@Override
	public Type upperBound() throws LookupException {
		return language(ObjectOrientedLanguage.class).getDefaultSuperClass(view().namespace());
	}

	@Override
	public TypeReference upperBoundReference() {
		ObjectOrientedLanguage language = language(ObjectOrientedLanguage.class);
		return language.createTypeReference(language.getDefaultSuperClassFQN());
	}

}
