package chameleon.core.property;

import org.rejuse.logic.ternary.Ternary;
import org.rejuse.property.DynamicProperty;
import org.rejuse.property.PropertyImpl;
import org.rejuse.property.PropertyMutex;
import org.rejuse.property.PropertySet;
import org.rejuse.property.PropertyUniverse;

import chameleon.core.declaration.Definition;
import chameleon.core.element.Element;
import chameleon.core.language.Language;
import chameleon.core.validation.VerificationResult;

public class Defined extends DynamicChameleonProperty {

  public Defined(String name, Language lang) {
    super(name, lang, new PropertyMutex<ChameleonProperty>(), Definition.class);
  }

  public Defined(String name, Language lang, PropertyMutex<ChameleonProperty> mutex) {
    super(name, lang, mutex,Definition.class);
  }
  
  /**
   * An object is defined if and only if it is a Definition, and
   * it is complete. 
   */
 /*@
   @ behavior
   @
   @ post \result == (element instanceof Definition) && ((Definition)element).complete();
   @*/
  @Override public Ternary appliesTo(Element element) {
  	PropertySet<Element,ChameleonProperty> declared = element.declaredProperties();
  	Ternary result = declared.implies(this);
  	if(result == Ternary.UNKNOWN) {
      result = ((Definition)element).complete();
    	if(result == Ternary.UNKNOWN) {
        result = Ternary.FALSE;
    	}
  	}
    return result;
  }

}
