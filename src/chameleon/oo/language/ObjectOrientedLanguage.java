package chameleon.oo.language;

import chameleon.core.declaration.Declaration;
import chameleon.core.language.Language;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategyFactory;
import chameleon.core.member.Member;
import chameleon.core.method.Method;
import chameleon.core.property.ChameleonProperty;
import chameleon.core.property.Defined;
import chameleon.core.property.StaticChameleonProperty;
import chameleon.core.relation.EquivalenceRelation;
import chameleon.core.relation.StrictPartialOrder;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;

public abstract class ObjectOrientedLanguage extends Language {
	
	public final ChameleonProperty INHERITABLE;
	public final ChameleonProperty OVERRIDABLE;
	public final ChameleonProperty EXTENSIBLE;
	public final ChameleonProperty REFINABLE;
	public final ChameleonProperty DEFINED;
	public final ChameleonProperty INSTANCE;
	public final ChameleonProperty CLASS;
	public final ChameleonProperty CONSTRUCTOR;
	public final ChameleonProperty DESTRUCTOR;
	public final ChameleonProperty REFERENCE_TYPE;
	public final ChameleonProperty VALUE_TYPE;
	public final ChameleonProperty NATIVE;
	
	public ObjectOrientedLanguage(String name) {
		this(name,null);
	}

	public ObjectOrientedLanguage(String name, LookupStrategyFactory factory) {
		super(name, factory);
		// 1) Create the properties.
  	INHERITABLE = new StaticChameleonProperty("inheritable",this,Declaration.class);
  	OVERRIDABLE = new StaticChameleonProperty("overridable",this,Declaration.class);
  	EXTENSIBLE = new StaticChameleonProperty("extensible", this,Declaration.class);
  	REFINABLE = new StaticChameleonProperty("refinable", this,Declaration.class);
  	DEFINED = new Defined("defined",this);
  	INSTANCE = new StaticChameleonProperty("instance",this,Declaration.class);
  	CLASS = INSTANCE.inverse();
    CLASS.setName("class");
    CONSTRUCTOR = new StaticChameleonProperty("constructor", this,Method.class);
    DESTRUCTOR = new StaticChameleonProperty("destructor", this,Method.class);
  	REFERENCE_TYPE = new StaticChameleonProperty("reference type", this, Type.class);
  	VALUE_TYPE = REFERENCE_TYPE.inverse();
  	NATIVE = new StaticChameleonProperty("native", this, Type.class);
  	
  	//2) Add relations between the properties.
    OVERRIDABLE.addImplication(INHERITABLE);
    OVERRIDABLE.addImplication(REFINABLE);
    EXTENSIBLE.addImplication(REFINABLE);
    NATIVE.addImplication(DEFINED);
}

  protected final class DummyTypeReference extends TypeReference {
	  public DummyTypeReference(String qn) {
		  super(qn);
		  setUniParent(defaultNamespace());
    }
  }

	public Type getDefaultSuperClass() throws LookupException {
		  TypeReference typeRef = new DummyTypeReference(getDefaultSuperClassFQN());
	    Type result = typeRef.getType();
	    if (result==null) {
	        throw new LookupException("Default super class "+getDefaultSuperClassFQN()+" not found.");
	    }
	    return result;
	}

	/**
	 * Return the fully qualified name of the class that acts as the default
	 * super class.
	 */
	public abstract String getDefaultSuperClassFQN();

	/**
	 * Return the exception thrown by the language when an invocation is done on a 'null' or 'void' target.
	 *
	 * @param defaultPackage The root package in which the exception type should be found.
	 */
	public abstract Type getNullInvocationException() throws LookupException;

	/**
	 * Return the exception representing the top class of non-fatal runtime exceptions. This doesn't include
	 * errors.
	 *
	 * @param defaultPackage The root package in which the exception type should be found.
	 */
	public abstract Type getUncheckedException() throws LookupException;

	/**
	 * Check whether the given type is an exception.
	 */
	public abstract boolean isException(Type type) throws LookupException;

	/**
	 * Check whether the given type is a checked exception.
	 */
	public abstract boolean isCheckedException(Type type) throws LookupException;

	public abstract Type getNullType();

	/**
	 * Return the relation that determines when a member overrides another
	 */
	public abstract WeakPartialOrder<Type> subtypeRelation();

	/**
	 * Return the relation that determines when a member overrides another
	 */
	public abstract StrictPartialOrder<Member> overridesRelation();

	/**
	 * Return the relation that determines when a member hides another
	 */
	public abstract StrictPartialOrder<Member> hidesRelation();

	/**
	 * Return the relation that determines when a member implements another.
	 */
	public abstract StrictPartialOrder<Member> implementsRelation();

	public abstract Type voidType() throws LookupException;

	/**
	 * Return the type that represents the boolean type in this language.
	 */
	public abstract Type booleanType() throws LookupException;

	/**
	 * Return the type that represents class cast exceptions in this language.
	 */
	public abstract Type classCastException() throws LookupException;

	/**
	 * Return the relation that determines when a member is equivalent to another.
	 */
	public abstract EquivalenceRelation<Member> equivalenceRelation();

	public Type findType(String fqn) throws LookupException {
		TypeReference ref = new TypeReference(fqn);
		ref.setUniParent(defaultNamespace());
		return ref.getType();
	}


//  protected void initProperties() {
//  	INHERITABLE = new StaticProperty<Element>("inheritable",this);
//  	OVERRIDABLE = new StaticProperty<Element>("overridable",this);
//  	EXTENSIBLE = new StaticProperty<Element>("extensible", this);
//  	REFINABLE = new StaticProperty<Element>("refinable", this);
//  	DEFINED = new Defined("defined",this);
//  	INSTANCE = new StaticProperty<Element>("instance",this);
//  	CLASS = INSTANCE.inverse();
//    CLASS.setName("class");
//    CONSTRUCTOR = new StaticProperty<Element>("constructor", this);
//    DESTRUCTOR = new StaticProperty<Element>("destructor", this);
//  	REFERENCE_TYPE = new StaticProperty<Element>("reference type", this);
//  	VALUE_TYPE = REFERENCE_TYPE.inverse();
//    OVERRIDABLE.addImplication(INHERITABLE);
//    OVERRIDABLE.addImplication(REFINABLE);
//    EXTENSIBLE.addImplication(REFINABLE);
//  }

}
