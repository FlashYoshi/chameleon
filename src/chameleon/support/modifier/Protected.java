package chameleon.support.modifier;

import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;
import chameleon.core.element.Element;
import chameleon.core.modifier.ModifierImpl;
import chameleon.core.property.ChameleonProperty;

/**
 * @author Marko van Dooren
 */
public class Protected extends ModifierImpl {

	
	
  public Protected() {
    
  }
  
	@Override
	public Protected clone() {
		return new Protected();
	}

	public PropertySet<Element,ChameleonProperty> impliedProperties() {
		return createSet(language().property(ProtectedProperty.ID));
	}


}
