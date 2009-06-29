package chameleon.core.variable;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.OrderedReferenceSet;
import org.rejuse.association.Reference;
import org.rejuse.java.collections.Visitor;
import org.rejuse.logic.ternary.Ternary;
import org.rejuse.property.Property;
import org.rejuse.property.PropertySet;

import chameleon.core.MetamodelException;
import chameleon.core.context.LookupException;
import chameleon.core.context.TargetContext;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.expression.ExpressionContainer;
import chameleon.core.modifier.Modifier;
import chameleon.core.statement.CheckedExceptionList;
import chameleon.core.statement.ExceptionSource;
import chameleon.core.type.Type;
import chameleon.core.type.TypeDescendant;
import chameleon.core.type.TypeReference;
import chameleon.util.Util;

public abstract class RegularVariable<E extends RegularVariable<E,P>, P extends DeclarationContainer & TypeDescendant> 
       extends VariableImpl<E,P> implements ExpressionContainer<E,P>, ExceptionSource<E,P> {

	public RegularVariable(SimpleNameSignature sig, TypeReference typeRef, Expression init) {
		super(sig);
    setTypeReference(typeRef);
    setInitialization(init);
	}

	/**
	 * TYPE
	 */
	private Reference<Variable,TypeReference> _typeReference = new Reference<Variable,TypeReference>(this);


  public TypeReference getTypeReference() {
    return _typeReference.getOtherEnd();
  }

  public void setTypeReference(TypeReference type) {
    _typeReference.connectTo(type.parentLink());
  }

	/**
	 * INITIALIZATION EXPRESSION 
	 */
  
	private Reference<RegularVariable,Expression> _init = new Reference<RegularVariable,Expression>(this);

	public Expression getInitialization() {
    return _init.getOtherEnd();
  }
  
  public void setInitialization(Expression expr) {
    if(expr != null) {
      _init.connectTo(expr.parentLink());
    }
    else {
      _init.connectTo(null);
    }
  }
  
  public CheckedExceptionList getCEL() throws LookupException {
    if(getInitialization() != null) {
      return getInitialization().getCEL();
    }
    else {
      return new CheckedExceptionList(getNamespace().language());
    }
  }

  public CheckedExceptionList getAbsCEL() throws LookupException {
    if(getInitialization() != null) {
      return getInitialization().getAbsCEL();
    }
    else {
      return new CheckedExceptionList(getNamespace().language());
    }
  }

 /*@
   @ also public behavior
   @
   @ post \result.containsAll(modifiers());
   @ post getTypeReference() != null ==> \result.contains(getTypeReference());
   @ post getInitialization() != null ==> \result.contains(getInitialization());
   @ post signature() != null ==> \result.contains(signature());
   @*/
  public List<Element> children() {
    List result = new ArrayList<Element>();
    Util.addNonNull(getInitialization(), result);
    result.addAll(modifiers());
    Util.addNonNull(signature(), result);
    Util.addNonNull(getInitialization(), result);
    return result;
  }
	/*************
	 * MODIFIERS *
	 *************/
	
	private OrderedReferenceSet<Variable, Modifier> _modifiers = new OrderedReferenceSet<Variable, Modifier>(this);

	public List<Modifier> modifiers() {
		return _modifiers.getOtherEnds();
	}

	public void addModifier(Modifier modifier) {
		if ((modifier != null) && (!_modifiers.contains(modifier.parentLink()))) {
			_modifiers.add(modifier.parentLink());
		}
	}
	
	public void addAllModifiers(List<Modifier> modifiers) {
		for(Modifier modifier: modifiers) {
			addModifier(modifier);
		}
	}

	public void removeModifier(Modifier modifier) {
		_modifiers.remove(modifier.parentLink());
	}

	public boolean hasModifier(Modifier modifier) {
		return _modifiers.getOtherEnds().contains(modifier);
	}


  /**
   * Return the name of this variable.
   */
  public String getName() {
    return signature().getName();
  }

  public Type getType() throws LookupException {
    Type result = getTypeReference().getType();
    if(result != null) {
      return result;
    }
    else {
      getTypeReference().getType();
      throw new LookupException("getType on regular variable returned null.");
    }
  }

  /*@
  @ also public behavior
  @
  @ post \result == getParent().getNearestType();
  @*/
 public Type getNearestType() {
   return parent().getNearestType();
 }

 public E clone() {
   final E result = cloneThis();
   new Visitor<Modifier>() {
     public void visit(Modifier element) {
       result.addModifier(element.clone());
     }
   }.applyTo(modifiers());
   return result;
 }

 protected abstract E cloneThis();

 public Ternary is(Property<Element> property) {
   PropertySet<Element> declared = declaredProperties();
   if((property).appliesTo(this)) {
     declared.add(property);
   }
   return declared.implies(property);
 }

 public PropertySet<Element> declaredProperties() {
   PropertySet<Element> result = new PropertySet<Element>();
   for(Modifier modifier:modifiers()) {
     result.addAll(modifier.impliedProperties());
   }
   return result;
 }
 
 public TargetContext targetContext() throws LookupException {
   return getType().targetContext();
 }


 public Variable resolve() {
 	return this;
 }

}
