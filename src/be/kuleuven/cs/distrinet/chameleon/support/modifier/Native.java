package be.kuleuven.cs.distrinet.chameleon.support.modifier;



import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.ModifierImpl;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;

/**
 * @author Marko van Dooren
 */
public class Native extends ModifierImpl {

  public Native() {
    
  }

	@Override
	public Native clone() {
		return new Native();
	}
	
 /*@
   @ behavior
   @
   @ post \result.contains(language(ObjectOrientedLanguage.class).OVERRIDABLE);
   @ post \result.contains(language(ObjectOrientedLanguage.class).DEFINED);
   @ post \result.size() == 2;
   @*/
  public PropertySet<Element,ChameleonProperty> impliedProperties() {
    return createSet(language(ObjectOrientedLanguage.class).NATIVE);
  }

}
