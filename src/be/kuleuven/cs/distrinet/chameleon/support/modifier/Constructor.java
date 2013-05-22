package be.kuleuven.cs.distrinet.chameleon.support.modifier;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.ModifierImpl;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;

/**
 * A class of modifiers marking a method as being a constructor.
 * 
 * @author Marko van Dooren
 */
public class Constructor extends ModifierImpl {
	
	  public Constructor() {
		  
	  }

		@Override
		protected Constructor cloneSelf() {
			return new Constructor();
		}

		/**
		 * A constructor modifier assigns the language().CONSTRUCTOR property to
		 * an element. Subclasses can add additional properties.
		 */
	 /*@
	   @ public behavior
	   @
	   @ post \result.contains(language().CONSTRUCTOR);
	   @*/
    public PropertySet<Element,ChameleonProperty> impliedProperties() {
      return createSet(language(ObjectOrientedLanguage.class).CONSTRUCTOR);
    }

}
