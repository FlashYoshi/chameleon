package chameleon.oo.language;

import java.util.List;

import org.rejuse.association.SingleAssociation;
import org.rejuse.junit.Revision;
import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.declaration.TargetDeclaration;
import chameleon.core.element.Element;
import chameleon.core.language.LanguageImpl;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategyFactory;
import chameleon.core.namespace.Namespace;
import chameleon.core.property.ChameleonProperty;
import chameleon.core.property.Defined;
import chameleon.core.property.DynamicChameleonProperty;
import chameleon.core.property.StaticChameleonProperty;
import chameleon.core.reference.CrossReference;
import chameleon.core.relation.EquivalenceRelation;
import chameleon.core.relation.StrictPartialOrder;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.oo.member.Member;
import chameleon.oo.method.Method;
import chameleon.oo.type.DerivedType;
import chameleon.oo.type.IntersectionTypeReference;
import chameleon.oo.type.Parameter;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.ActualTypeArgument;
import chameleon.oo.type.generics.TypeParameter;
import chameleon.oo.variable.Variable;
import chameleon.oo.variable.VariableDeclarator;
import chameleon.util.Pair;

public abstract class ObjectOrientedLanguage extends LanguageImpl {
	
	//TODO document the properties. This is becoming complicated without an explanation.
	public final StaticChameleonProperty INHERITABLE;
	public final StaticChameleonProperty OVERRIDABLE;
	public final ChameleonProperty EXTENSIBLE;
	public final ChameleonProperty REFINABLE;
	public final DynamicChameleonProperty DEFINED;
	/**
	 * The inverse of DEFINED.
	 */
	public final ChameleonProperty ABSTRACT;
	public final StaticChameleonProperty INSTANCE;
	public final ChameleonProperty CLASS;
	public final ChameleonProperty CONSTRUCTOR;
	public final ChameleonProperty DESTRUCTOR;
	public final ChameleonProperty REFERENCE_TYPE;
	public final ChameleonProperty VALUE_TYPE;
	public final ChameleonProperty NATIVE;
	public final ChameleonProperty INTERFACE;	

	public ObjectOrientedLanguage(String name, Revision version) {
		this(name,null,version);
	}

	public ObjectOrientedLanguage(String name, LookupStrategyFactory factory, Revision version) {
		super(name, factory,version);
		// 1) Create the properties.
  	INHERITABLE = new StaticChameleonProperty("inheritable",this,Declaration.class);
  	OVERRIDABLE = new StaticChameleonProperty("overridable",this,Declaration.class);
  	EXTENSIBLE = new StaticChameleonProperty("extensible", this,Declaration.class);
  	REFINABLE = new StaticChameleonProperty("refinable", this,Declaration.class);
  	DEFINED = new Defined("defined",this);
  	DEFINED.addValidElementType(Variable.class);
//  	ABSTRACT = DEFINED.inverse();
  	ABSTRACT = new StaticChameleonProperty("abstract", this, Declaration.class);
  	INSTANCE = new StaticChameleonProperty("instance",this,Declaration.class);
  	INSTANCE.addValidElementType(VariableDeclarator.class);
  	CLASS = INSTANCE.inverse();
    CLASS.setName("class");
    CONSTRUCTOR = new StaticChameleonProperty("constructor", this,Method.class);
    DESTRUCTOR = new StaticChameleonProperty("destructor", this,Method.class);
  	REFERENCE_TYPE = new StaticChameleonProperty("reference type", this, Type.class);
  	VALUE_TYPE = REFERENCE_TYPE.inverse();
  	NATIVE = new StaticChameleonProperty("native", this, Type.class);
		INTERFACE = new StaticChameleonProperty("interface", this, Type.class);

  	//2) Add relations between the properties.
    OVERRIDABLE.addImplication(INHERITABLE);
    OVERRIDABLE.addImplication(REFINABLE);
    EXTENSIBLE.addImplication(REFINABLE);
    NATIVE.addImplication(DEFINED);
    INTERFACE.addImplication(ABSTRACT);
    ABSTRACT.addImplication(DEFINED.inverse());
	}

  public abstract TypeReference createTypeReference(String fqn);
  
  public abstract TypeReference createTypeReference(Type type);
  
  public abstract TypeReference createTypeReference(CrossReference<? extends TargetDeclaration> target, String name);
  
  public abstract TypeReference createTypeReference(CrossReference<? extends TargetDeclaration> target, SimpleNameSignature signature);
  
  public abstract IntersectionTypeReference createIntersectionReference(TypeReference first, TypeReference second);
  
  // NEEDS_NS
  public TypeReference createTypeReferenceInNamespace(String fqn, Namespace namespace) {
	  TypeReference typeRef = createTypeReference(fqn);
	  typeRef.setUniParent(namespace);
	  return typeRef;
  }
  
  public abstract <P extends Parameter> DerivedType createDerivedType(Class<P> kind, List<P> parameters, Type baseType);
  
  public abstract DerivedType createDerivedType(Type baseType, List<ActualTypeArgument> typeArguments) throws LookupException;
  
  // NEEDS_NS
	public synchronized Type getDefaultSuperClass(Namespace root) throws LookupException {
//		Type result = _defaultSuperClass;
//		if(result == null) {
			TypeReference typeRef = createTypeReferenceInNamespace(getDefaultSuperClassFQN(),root);
			Type result = typeRef.getType();
//			_defaultSuperClass = result;
//			if (result==null) {
//				throw new LookupException("Default super class "+getDefaultSuperClassFQN()+" not found.");
//			}
//		}
		return result;
	}
	
//	private Type _defaultSuperClass;

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
	public abstract Type getNullInvocationException(Namespace ns) throws LookupException;

	/**
	 * Return the exception representing the top class of non-fatal runtime exceptions. This doesn't include
	 * errors.
	 *
	 * @param defaultPackage The root package in which the exception type should be found.
	 */
	public abstract Type getUncheckedException(Namespace ns) throws LookupException;

	/**
	 * Check whether the given type is an exception.
	 */
	public abstract boolean isException(Type type) throws LookupException;

	/**
	 * Check whether the given type is a checked exception.
	 */
	public abstract boolean isCheckedException(Type type) throws LookupException;

	public abstract boolean upperBoundNotHigherThan(Type first, Type second, List<Pair<Type, TypeParameter>> trace) throws LookupException;

	public abstract Type getNullType(Namespace root);

	/**
	 * Return the relation that determines when a member overrides another
	 */
	public abstract WeakPartialOrder<Type> subtypeRelation();
	
//	/**
//	 * Return the relation that determines when a member overrides another
//	 */
//	public abstract StrictPartialOrder<Member> overridesRelation();

//	/**
//	 * Return the relation that determines when a member hides another
//	 */
//	public abstract StrictPartialOrder<Member> hidesRelation();

	/**
	 * Return the relation that determines when a member implements another.
	 */
	public abstract StrictPartialOrder<Member> implementsRelation();

	public abstract Type voidType(Namespace ns) throws LookupException;

	/**
	 * Return the type that represents the boolean type in this language.
	 */
	public abstract Type booleanType(Namespace ns) throws LookupException;

	/**
	 * Return the type that represents class cast exceptions in this language.
	 */
	public abstract Type classCastException(Namespace ns) throws LookupException;

	/**
	 * Return the relation that determines when a member is equivalent to another.
	 */
	public abstract EquivalenceRelation<Member> equivalenceRelation();

	public Type findType(String fqn, Namespace ns) throws LookupException {
		TypeReference ref = createTypeReferenceInNamespace(fqn,ns);
		return ref.getType();
	}

	public void replace(TypeReference replacement, final Declaration declarator, TypeReference in) throws LookupException {
		UnsafePredicate<TypeReference, LookupException> predicate = new UnsafePredicate<TypeReference, LookupException>() {
			@Override
			public boolean eval(TypeReference object) throws LookupException {
				return object.getDeclarator().sameAs(declarator);
			}
		};
		List<TypeReference> crefs = in.descendants(TypeReference.class,predicate);
		if(predicate.eval(in)) {
			crefs.add(in);
		}
		
		for(TypeReference cref: crefs) {
			TypeReference clonedReplacement = replacement.clone();
			TypeReference substitute = createNonLocalTypeReference(clonedReplacement, replacement.parent());
			
//			TypeReference substitute;
//			if(replacement.isDerived()) {
//				Element oldParent = replacement.parent();
//				replacement.setUniParent(null);
//				substitute = createNonLocalTypeReference(replacement,oldParent);
//			} else {
//				substitute = createNonLocalTypeReference(replacement);
//			}

			
			SingleAssociation crefParentLink = cref.parentLink();
			crefParentLink.getOtherRelation().replace(crefParentLink, substitute.parentLink());
		}
	}
	
	public TypeReference createNonLocalTypeReference(TypeReference tref) {
		return createNonLocalTypeReference(tref, tref.parent());
	}

	public abstract TypeReference createNonLocalTypeReference(TypeReference tref, Element lookupParent);
	
	public abstract <E extends Element> E replace(TypeReference replacement, Declaration declarator, E in, Class<E> kind) throws LookupException;
}
