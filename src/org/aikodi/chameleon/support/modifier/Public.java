package org.aikodi.chameleon.support.modifier;



import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.modifier.ModifierImpl;
import org.aikodi.chameleon.core.property.ChameleonProperty;

import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;

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
	protected Public cloneSelf() {
		return new Public();
	}

  @Override
public PropertySet<Element,ChameleonProperty> impliedProperties() {
  	try {
	    return createSet(language().property(PublicProperty.ID));
  	}catch(NullPointerException exc) {
  		return createSet(language().property(PublicProperty.ID));
  	}
  }
}
