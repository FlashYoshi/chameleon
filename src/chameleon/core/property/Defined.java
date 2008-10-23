package chameleon.core.property;

import org.rejuse.property.DynamicProperty;
import org.rejuse.property.Property;
import org.rejuse.property.PropertyMutex;

import chameleon.core.declaration.Definition;
import chameleon.core.element.Element;
import chameleon.core.language.Language;

public class Defined extends DynamicProperty<Element> {

  public Defined(String name, Language lang) {
    super(name, lang, new PropertyMutex<Element>());
  }

  public Defined(String name, Language lang, PropertyMutex<Element> mutex) {
    super(name, lang, mutex);
  }
  
  protected Defined(String name, Language lang, PropertyMutex<Element> mutex, Property inverse) {
    super(name, lang, mutex, inverse);
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
  @Override public boolean appliesTo(Element element) {
    return (element instanceof Definition) && ((Definition)element).complete();
  }

//  protected Property<Element> createInverse(String name, Language lang) {
//    return new Defined("not "+name, lang, this) {
//      public boolean appliesTo(Element element) {
//        return (! (element instanceof Definition)) || (!((Definition)element).complete());
//      }
//    };
//  }

}
