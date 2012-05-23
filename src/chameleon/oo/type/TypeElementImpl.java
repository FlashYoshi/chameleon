package chameleon.oo.type;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.property.Property;
import org.rejuse.property.PropertyMutex;

import chameleon.core.lookup.LookupException;
import chameleon.core.modifier.ElementWithModifiersImpl;
import chameleon.core.modifier.Modifier;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.exception.ModelException;
import chameleon.oo.member.Member;

/**
 * Support class for member-like elements that can be the direct children of a type.
 * 
 * @author Marko van Dooren
 */
public abstract class TypeElementImpl extends ElementWithModifiersImpl implements TypeElement {
  

//	public CheckedExceptionList getCEL() throws LookupException {
//	  return new CheckedExceptionList();	
//	}
//	
//	public CheckedExceptionList getAbsCEL() throws LookupException {
//		return new CheckedExceptionList();
//	}

	public abstract TypeElementImpl clone();

	public List<? extends Member> declaredMembers() {
    try {
			return getIntroducedMembers();
		} catch (LookupException e) {
			throw new ChameleonProgrammerException("This should not happen. Element of class "+this.getClass().getName()+" threw a lookup exception in getIntroducedMembers. This exception ended up in declaredMembers. But if that is the case, then declaredMembers must be overridden to provide a proper definition.", e);
		}
  }

  public List<Modifier> modifiers(PropertyMutex mutex) throws ModelException {
  	Property property = property(mutex);
  	List<Modifier> result = new ArrayList<Modifier>();
  	for(Modifier mod: modifiers()) {
  		if(mod.impliesTrue(property)) {
  			result.add(mod);
  		}
  	}
  	return result;
  }
}
