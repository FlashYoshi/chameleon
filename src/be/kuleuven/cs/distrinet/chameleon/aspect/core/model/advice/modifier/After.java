package be.kuleuven.cs.distrinet.chameleon.aspect.core.model.advice.modifier;

import be.kuleuven.cs.distrinet.chameleon.aspect.core.model.advice.property.AfterProperty;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.ModifierImpl;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;
import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;

public class After extends ModifierImpl {

	@Override
	public PropertySet<Element, ChameleonProperty> impliedProperties() {
		return createSet(language().property(AfterProperty.ID));
	}

	@Override
	protected After cloneSelf() {
		return new After();
	}
}
