package be.kuleuven.cs.distrinet.chameleon.analysis.predicate;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;

public class IsBinary extends UniversalPredicate<Element,Nothing> {

	public IsBinary() {
		super(Element.class);
	}

	@Override
	public boolean uncheckedEval(Element element) throws Nothing {
		return element.view().isBinary(element);
	} 

}
