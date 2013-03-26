package be.kuleuven.cs.distrinet.chameleon.core.lookup;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;

public class CachingLexicalLookupStrategy extends LexicalLookupStrategy {

	public CachingLexicalLookupStrategy(LookupStrategy local, Element element) {
		super(local,element);
	}

	public CachingLexicalLookupStrategy(LookupStrategy local, LookupStrategySelector selector) {
		super(local,selector);
	}

}
