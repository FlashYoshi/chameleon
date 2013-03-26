package be.kuleuven.cs.distrinet.chameleon.support.modifier;

import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.ModifierImpl;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;


/**
 * @author Marko van Dooren
 */
public class Final extends ModifierImpl {

  public Final() {
  }

	@Override
	public Final clone() {
		return new Final();
	}

 /*@
   @ behavior
   @
   @ post \result.contains(language().OVERRIDABLE.inverse());
   @ post \result.contains(language().DEFINED);
   @ post \result.size() == 2;
   @*/
  public PropertySet<Element,ChameleonProperty> impliedProperties() {
    return createSet(language(ObjectOrientedLanguage.class).REFINABLE.inverse(),language(ObjectOrientedLanguage.class).DEFINED);
  }

}
