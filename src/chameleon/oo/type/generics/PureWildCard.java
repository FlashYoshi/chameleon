package chameleon.oo.type.generics;

import java.util.ArrayList;
import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.Type;

public class PureWildCard extends ActualTypeArgument<PureWildCard> {

	public TypeParameter capture(FormalTypeParameter formal) {
		CapturedTypeParameter newParameter = new CapturedTypeParameter(formal.signature().clone());
		for(TypeConstraint constraint: formal.constraints()) {
			newParameter.addConstraint(constraint.clone());
		}
   return newParameter;
	}

	@Override
	public PureWildCard clone() {
		return new PureWildCard();
	}

	// TypeVariable concept invoeren, en lowerbound,... verplaatsen naar daar? Deze is context sensitive. Hoewel, dat
	// wordt toch nooit direct vergeleken. Er moet volgens mij altijd eerst gecaptured worden, dus dan moet dit inderdaad
	// verplaatst worden. NOPE, niet altijd eerst capturen.
	@Override
	public Type lowerBound() throws LookupException {
		return language(ObjectOrientedLanguage.class).getNullType();
	}

	@Override
	public Type type() throws LookupException {
		return new PureWildCardType(language(ObjectOrientedLanguage.class));
	}

	@Override
	public Type upperBound() throws LookupException {
		return language(ObjectOrientedLanguage.class).getDefaultSuperClass();
	}

	public List<Element> children() {
		return new ArrayList<Element>();
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

}
