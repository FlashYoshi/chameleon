package chameleon.core.type.generics;

import chameleon.core.language.ObjectOrientedLanguage;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;

public class ExtendsConstraint extends TypeConstraintWithReferences<ExtendsConstraint> {

	public ExtendsConstraint() {
	}
	
	public ExtendsConstraint(TypeReference ref) {
		add(ref);
	}


	@Override
	public boolean matches(Type type) throws LookupException {
		return type.subTypeOf(upperBound());
	}


	@Override
	public ExtendsConstraint cloneThis() {
		return new ExtendsConstraint();
	}


	@Override
	public Type lowerBound() throws LookupException {
		return language(ObjectOrientedLanguage.class).getNullType();
	}


	@Override
	public Type upperBound() throws LookupException {
		return bound();
	}

	
}
