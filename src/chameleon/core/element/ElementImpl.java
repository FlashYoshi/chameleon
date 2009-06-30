package chameleon.core.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rejuse.association.Reference;
import org.rejuse.logic.ternary.Ternary;
import org.rejuse.predicate.PrimitiveTotalPredicate;
import org.rejuse.predicate.TypePredicate;
import org.rejuse.property.Property;
import org.rejuse.property.PropertyMutex;
import org.rejuse.property.PropertySet;

import chameleon.core.MetamodelException;
import chameleon.core.context.Context;
import chameleon.core.context.LookupException;
import chameleon.core.language.Language;
import chameleon.core.tag.Tag;

/**
 * @author Marko van Dooren
 * @author Koen Vanderkimpen
 */
public abstract class ElementImpl<E extends Element, P extends Element> implements Element<E,P> {

	  public ElementImpl() {
//	  	_state = createState();
	  }
	  
	  /*********
	   * STATE *
	   *********/
	  
//	  protected abstract S createState();
//	  
//	  private final S _state;
//
//	  protected final S getState() {
//	  	return _state;
//	  }
	  
//		void setElement(E element) {
//			_element = element;
//		}
		
		/***********
		 * ELEMENT *
		 ***********/
		
//		private E _element;
//		
//		public final E getElement() {
//			return _element;
//		}

	  
	  
		
		/**************
		 * DECORATORS *
		 **************/
		
	  // initialization of this Map is done lazy.
	  private Map<String, Tag> _decorators;
	  
	  public Tag tag(String name) {
	  	if(_decorators != null) {
	      return _decorators.get(name);
	  	} else {
	  		//lazy init has not been performed yet.
	  		return null;
	  	}
	  }

	  public void removeTag(String name) {
	  	if(_decorators != null) {
	     Tag old = _decorators.get(name);
	     if((old != null) && (old.getElement() != this)){
	    	 old.setElement(null,name);
	     }
	     _decorators.remove(name);
	  	}
	  }

	  public void setTag(Tag decorator, String name) {
	  	//Lazy init of hashmap
		  if (_decorators==null) {
	      _decorators = new HashMap<String, Tag>();
	    }
		  Tag old = _decorators.get(name); 
		  if(old != decorator) {
	      if((decorator != null) && (decorator.getElement() != this)) {
	  	    decorator.setElement(this,name);
	      }
	      if (old != null) {
	    	    old.setElement(null,name);
	      }
	  	  _decorators.put(name, decorator);
	    }
	  }

	  public Collection<Tag> tags() {
	  	if(_decorators == null) {
	  		return new ArrayList();
	  	} else {
	  	  return _decorators.values();
	  	}
	  }

	  public boolean hasTag(String name) {
	  	if(_decorators == null) {
	  		return false;
	  	} else {
	      return _decorators.get(name) != null;
	  	}
	  }

	  public boolean hasTags() {
	  	if(_decorators == null) {
	  		return false;
	  	} else {
	      return _decorators.size() > 0;
	  	}
	  }

	  /********************
	   * ORIGINAL ELEMENT *
	   ********************/
	  
	  public E getOriginal() {
	  	if(_parentLink == null) {
	  		return _original;
	  	} else {
	  		throw new ChameleonProgrammerException("Invoking getOriginal() on real source element");
	  	}
	  }
	  
	  private E _original;
	  
	  /**********
	   * PARENT *
	   **********/
	  
	  // WORKING AROUND LACK OF MULTIPLE INHERITANCE
	  
	  // THESE VARIABLES MUST NOT BE USED BOTH
	  //
	  // IF _parentLink IS NULL, THE ELEMENT IS NOT LEXICAL,
	  // IN WHICH CASE _parent PROVIDES THE UNIDIRECTIONAL ASSOCIATION
	  // WITH THE PARENT. IN THAT CASE, _original IS SET TO THE ELEMENT
	  // OF WHICH THIS ELEMENT IS A DERIVED ELEMENT
	  private Reference<E,P> _parentLink = new Reference<E,P>((E) this);

	  /**
	   * This is the undirectional association with the parent in case this element is derived.
	   */
	  private P _parent;
	  
	  /**
	   * Return the bidirectional link to the parent in case the element IS NOT derived.
	   * DO NOT USE THIS TO OBTAIN THE PARENT
	   * 
	   * @throws ChameleonProgrammerException
	   *    The method is invoked on a derived element. 
	   */
	  public final Reference<E,P> parentLink() {
	  	if(_parentLink != null) {
	      return _parentLink;
	  	} else {
	  		throw new ChameleonProgrammerException("Invoking getParentLink() on automatic derivation");
	  	}
	  }
	  
	  /**
	   * Return the parent of this element
	   */
	  public final P parent() {
	  	if(_parentLink != null) {
	      return _parentLink.getOtherEnd();
	  	} else {
	  		return _parent;
	  	}
	  }
	  
	  /**
	   * Check if this element is derived or not.
	   * 
	   * @return True if this element is derived, false otherwise.
	   */
	  public boolean isDerived() {
	  	return _parent == null;
	  }
	  
	  public void disconnect() {
	  	if(_parentLink != null) {
	  		_parentLink.connectTo(null);
	  	} else {
	  		_parent = null;
	  	}
	  }
	  
	  public final void setUniParent(P parent) {
	  	if(_parentLink != null) {
	  		_parentLink.connectTo(null);
	  	}
	  	_parentLink = null;
	  	_parent = parent;
	  }
	  
    public final List<Element> descendants() {
        return descendants(Element.class);
    }

    public final <T extends Element> List<T> descendants(Class<T> c) {
    	List<Element> tmp = (List<Element>) children();
    	if(tmp == null) {
    		throw new ChameleonProgrammerException("children() returns null for " + getClass().getName());
    	}
    	new TypePredicate<Element,T>(c).filter(tmp);
      List<T> result = (List<T>)tmp;
      for (Element e : children()) {
      	if(e == null) {
      		throw new ChameleonProgrammerException("children of " + getClass().getName() +" contains null.");
      	}
        result.addAll(e.descendants(c));
      }
      return result;
    }

    public final List<Element> ancestors() {
        if (parent()!=null) {
            List<Element> result = parent().ancestors();
            result.add(0, parent());
            return result;
        } else {
            return new ArrayList<Element>();
        }
    }

    public <T extends Element> T nearestAncestor(Class<T> c) {
    	Element el = parent();
    	while ((el != null) && (! c.isInstance(el))){
    		el = el.parent();
    	}
    	return (T) el;
    }
    
    public abstract E clone();
    
    public Language language() {
      if(parent() != null) {
        return parent().language();
      } else {
        return null;
      }
    }
    
    /**
     * @see Element#lexicalContext(Element) 
     */
    public Context lexicalContext(Element child) throws LookupException {
    	P parent = parent();
    	if(parent != null) {
        return parent.lexicalContext(this);
    	} else {
    		throw new LookupException("Going to the parent context when there is no parent.");
    	}
    }

    /**
     * @see Element#lexicalContext() 
     */
    public final Context lexicalContext() throws LookupException {
    	try {
        return parent().lexicalContext(this);
    	} catch(NullPointerException exc) {
    		if(parent() == null) {
    			throw new LookupException("Requesting the lexical context of an element without a parent: " +getClass().getName());
    		} else {
    			throw exc;
    		}
    	}
    }
    
    public PropertySet<Element> properties() {
    	PropertySet<Element> result = declaredProperties();
    	result.addAll(defaultProperties());
    	return result;
    }
    
    public PropertySet<Element> defaultProperties() {
    	return language().defaultProperties(this);
    }
    
    public PropertySet<Element> declaredProperties() {
    	return new PropertySet<Element>();
    }
    

    public Ternary is(Property<Element> property) {
    	// First get the declared properties.
      PropertySet<Element> properties = properties();
      // Add the given property if it dynamically applies to this element.
      if((property).appliesTo(this)) {
        properties.add(property);
      }
      // Check if the resulting property set implies the given property.
      return properties.implies(property);
    }
   
    public Property<Element> property(PropertyMutex<Element> mutex) throws MetamodelException {
    	List<Property<Element>> properties = new ArrayList<Property<Element>>();
    	for(Property<Element> p : properties().properties()) {
    		if(p.mutex() == mutex) {
    			properties.add(p);
    		}
    	}
    	if(properties.size() == 1) {
    		return properties.get(0);
    	} else {
    		throw new MetamodelException("Element has "+properties.size()+" properties for the mutex "+mutex);
    	}
    }

		protected PropertySet<Element> filterProperties(PropertySet<Element> overriding, PropertySet<Element> base) {
			Set<Property<Element>> baseProperties = base.properties();
			final Set<Property<Element>> overridingProperties = overriding.properties();
		  new PrimitiveTotalPredicate<Property<Element>>() {
				@Override
				public boolean eval(final Property<Element> aliasedProperty) {
					return new PrimitiveTotalPredicate<Property<Element>>() {
						@Override
						public boolean eval(Property<Element> myProperty) {
							return !aliasedProperty.contradicts(myProperty);
						}
					}.forAll(overridingProperties);
				}
		  	
		  }.filter(baseProperties);
		  baseProperties.addAll(overridingProperties);
		  return new PropertySet<Element>(baseProperties);
		}

}
