package chameleon.oo.type.generics;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.Type;

public class ExtendsWildcardType extends WildCardType {

	public ExtendsWildcardType(Type upperBound) {
		super(new SimpleNameSignature("? extends "+upperBound.getName()), upperBound,upperBound.language(ObjectOrientedLanguage.class).getNullType());
	}

	public Type bound() {
		return upperBound();
	}

	@Override
	public String getFullyQualifiedName() {
		return "? extends "+upperBound().getFullyQualifiedName();
	}


}
