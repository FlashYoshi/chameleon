package chameleon.core.type.generics;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.oo.language.ObjectOrientedLanguage;

public class SuperWildCardType extends WildCardType {

	public SuperWildCardType(Type lowerBound) throws LookupException {
		super(new SimpleNameSignature("? super "+lowerBound.getName()), lowerBound.language(ObjectOrientedLanguage.class).getDefaultSuperClass(), lowerBound);
	}
	
}
