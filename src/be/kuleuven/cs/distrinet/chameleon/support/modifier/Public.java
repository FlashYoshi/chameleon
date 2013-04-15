package be.kuleuven.cs.distrinet.chameleon.support.modifier;



import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.ModifierImpl;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;

/**
 * @author Marko van Dooren
 */
public class Public extends ModifierImpl {

  public Public() {
    
  }

//	public boolean atLeastAsVisibleAs(AccessModifier other) {
//		return true;
//	}
//
//  public AccessibilityDomain getAccessibilityDomain(Type type) {
//    return new All();
//  }

	@Override
	public Public clone() {
		return new Public();
	}

  public PropertySet<Element,ChameleonProperty> impliedProperties() {
  	try {
	    return createSet(language().property(PublicProperty.ID));
  	}catch(NullPointerException exc) {
  		return createSet(language().property(PublicProperty.ID));
  	}
  }
}