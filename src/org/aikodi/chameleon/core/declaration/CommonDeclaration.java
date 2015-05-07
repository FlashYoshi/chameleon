package org.aikodi.chameleon.core.declaration;

import java.util.List;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.modifier.ElementWithModifiers;
import org.aikodi.chameleon.core.modifier.Modifier;
import org.aikodi.chameleon.core.property.ChameleonProperty;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.exception.ModelException;
import org.aikodi.chameleon.util.Lists;
import org.aikodi.chameleon.util.association.Multi;
import org.aikodi.chameleon.util.association.Single;

import be.kuleuven.cs.distrinet.rejuse.property.Property;
import be.kuleuven.cs.distrinet.rejuse.property.PropertyMutex;
import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;

/**
 * A convenience class for declarations with a fixed signature and optional modifiers.
 * 
 * @author Marko van Dooren
 */
public abstract class CommonDeclaration extends DeclarationImpl implements ElementWithModifiers {

	/**
	 * Instantiate a new common declaration without a signature.
	 */
	public CommonDeclaration() {
	}
	
	/**
	 * Instantiate a new common declaration with the given signature.
	 * 
	 * @param signature The signature of the declaration
	 */
	public CommonDeclaration(Signature signature) {
		setSignature(signature);
	}

	private Single<Signature> _signature = createSignatureLink();

	protected Single<Signature> createSignatureLink() {
	  return new Single<Signature>(this);
	}
	
	@Override
	public Signature signature() {
		return _signature.getOtherEnd();
	}

	@Override
	public void setSignature(Signature signature) {
		set(_signature,signature);
	}
	
	@Override
	public LookupContext targetContext() throws LookupException {
		throw new LookupException("A goal does not contain declarations.");
	}

//	public abstract CommonDeclaration clone();
	
  /*************
   * MODIFIERS *
   *************/
  private Multi<Modifier> _modifiers = new Multi<Modifier>(this);

  /**
   * @return the list of modifiers of this member.
   */
 /*@
   @ behavior
   @
   @ post \result != null;
   @*/
  @Override
public List<Modifier> modifiers() {
    return _modifiers.getOtherEnds();
  }

  /**
   * Add the given modifier to this element.
   * @param modifier The modifier to be added.
   */
 /*@
   @ public behavior
   @
   @ pre modifier != null;
   @
   @ post modifiers().contains(modifier);
   @*/
  @Override
public void addModifier(Modifier modifier) {
  	add(_modifiers,modifier);
  }
  
  /**
   * Remove the given modifier from this element.
   * 
   * @param modifier The modifier that must be removed.
   */
 /*@
   @ public behavior
   @
   @ pre modifier != null;
   @
   @ post ! modifiers().contains(modifier);
   @*/
  @Override
public void removeModifier(Modifier modifier) {
  	remove(_modifiers,modifier);
  }

  /**
   * Check whether this element contains the given modifier.
   * 
   * @param modifier The modifier that is searched.
   */
 /*@
   @ public behavior
   @
   @ pre modifier != null;
   @
   @ post \result == modifiers().contains(modifier);
   @*/
  public boolean hasModifier(Modifier modifier) {
    return _modifiers.getOtherEnds().contains(modifier);
  }

	/**
	 * Return the default properties for this element.
	 * @return
	 */
	protected PropertySet<Element,ChameleonProperty> myDefaultProperties() {
		return language().defaultProperties(this, explicitProperties());
	}
	
}
