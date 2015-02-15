package be.kuleuven.cs.distrinet.chameleon.support.modifier;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.ModifierImpl;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;

/**
 * @author Marko van Dooren
 */
public class Private extends ModifierImpl {

  public Private() {
    
  }
  
	@Override
	protected Private cloneSelf() {
		return new Private();
	}

	/**
	 * A private element has a private scope, and is not inheritable.
	 */
  @Override
public PropertySet<Element,ChameleonProperty> impliedProperties() {
	  return createSet(language(ObjectOrientedLanguage.class).property(PrivateProperty.ID), language(ObjectOrientedLanguage.class).INHERITABLE.inverse());
  }

}
