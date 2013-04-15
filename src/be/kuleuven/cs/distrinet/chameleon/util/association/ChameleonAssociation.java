package be.kuleuven.cs.distrinet.chameleon.util.association;

import be.kuleuven.cs.distrinet.rejuse.association.IAssociation;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.validation.VerificationResult;

public interface ChameleonAssociation<T> extends IAssociation<Element, T> {

	public void cloneTo(ChameleonAssociation<T> o);
	
	public VerificationResult verify();
}