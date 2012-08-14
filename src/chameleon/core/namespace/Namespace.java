package chameleon.core.namespace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rejuse.predicate.SafePredicate;
import org.rejuse.predicate.TypePredicate;

import chameleon.core.Config;
import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.declaration.Signature;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.declaration.TargetDeclaration;
import chameleon.core.element.ElementImpl;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LocalLookupStrategy;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.namespacedeclaration.NamespaceDeclaration;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.util.Util;
import chameleon.util.association.Single;

/**
 * <p>Namespaces are a completely logical structure. You do not explicitly create a namespace, but query it using
 * the getOrCreateNamespace method.</p> 
 * 
 * <p>The declarations in a namespace are not stored directly in that namespace. Instead, they are stored in namespace parts.
 * A namespace contains a number of namespace parts. The reason for this decision is that some language, such as C#, allow a programmer
 * to add elements to different namespaces in a single compilation unit (file). Namespace parts can also contain other namespace parts.</p>
 * 
 * <p>For example, if you write a namespace declaration in C#, that will correspond to a namespace part in the model. Thus,
 * "namespace a {class{X} namespace b.c{class Y{} }} namespace d {class Z{}} adds class X to namespace a, lass Y to namespace a.b.c,
 * and class Z to namespace d.</p>
 * 
 * <img src="namespaces.jpg"/>
 * 
 * <p>We chose to unify this structure for all languages. For example, a Java compilation unit with a package declaration
 * will contain a namespace part for the root namespace. A Java compilation with a package declaration contains a namespace 
 * part for that namespace (package).</p>
 *  
 * @author Marko van Dooren
 */

public abstract class Namespace extends ElementImpl implements TargetDeclaration, DeclarationContainer {

	public abstract Namespace clone();

	//SPEED : use hashmap to store the subnamespaces and forbid
	//        adding multiple namespaces with the same name. That is
	//        never useful anyway.
	
	/**
	 * Initialize a new Namespace with the given name.
	 *
	 * @param name
	 *        The name of the new package.
	 */
 /*@
	 @ public behavior
	 @
	 @ pre sig != null;
	 @
	 @ post signature()==sig;
	 @*/
	public Namespace(SimpleNameSignature sig) {
      setSignature(sig);
	}


 /*@
   @ public behavior
   @
   @ pre signature != null;
   @
   @ post signature() = signature; 
   @*/
	public void setSignature(Signature signature) {
		if(! (signature instanceof SimpleNameSignature)) {
			throw new ChameleonProgrammerException("A namespace must have a simple name signature. The argument is of type "+
		                                         (signature == null ? "null type" : signature.getClass().getName()));
		}
	  set(_signature,(SimpleNameSignature)signature);
	}
	
	public void setName(String name) {
		setSignature(new SimpleNameSignature(name));
	}

		  
	  /**
	   * Return the signature of this member.
	   */
	  public SimpleNameSignature signature() {
	    return _signature.getOtherEnd();
	  }
		  
	  private Single<SimpleNameSignature> _signature = new Single<SimpleNameSignature>(this,true);

	  /**
	   * The name of a namespace is the name of its signature.
	   */
	 /*@
	   @ public behavior
	   @
	   @ post \result == signature().getName();
	   @*/
	  public String name() {
		  return signature().name();
	  }
	  
	/**
	 * Return the fully qualified name of this package. This is the concatenation of the
	 * parent namespaces starting at the root. In between the names of two namespaces, a
	 * "." character is placed.
	 */
 /*@
	 @ public behavior
	 @
	 @ post (getParent() == null) || getParent().getName().equals("") ==>
	 @        \result == getName();
	 @ post (getParent() != null) && (! getParent().getName().equals("")) ==>
	 @        \result == getParent().getFullyQualifiedName() + "." + getName();
	 @*/
	public String getFullyQualifiedName() {
		Namespace nearestAncestor = nearestAncestor(Namespace.class);
		return ((parent() == null || nearestAncestor.name().equals("")) ? "" : nearestAncestor.getFullyQualifiedName() + ".") + name();
	}
	
	public String toString() {
		return getFullyQualifiedName();
	}

	/**************
	 * PACKAGEPART
	 **************/

	/**
	 * Add a namespace part to this namespace. A namespace part adds elements to its namespace.
	 */
 /*@
   @ public behavior
   @
   @ pre namespacepart != null;
   @
   @ post getNamespaceParts().contains(namespacepart);
   @*/
	public abstract void addNamespacePart(NamespaceDeclaration namespacePart);

	/**
	 * Return all namespace parts attached to this namespace.
	 */
 /*@
   @ public behavior
   @
   @ post \result != null;
   @*/
	public abstract List<NamespaceDeclaration> getNamespaceParts();

	/**
	 * Return the root namespace of this metamodel instance.
	 */
 /*@
	 @ public behavior
	 @
	 @ post getParent() != null ==> \result == getParent().defaultNamespace();
	 @ post getParent() == null ==> \result == this;
	 @*/
	public Namespace defaultNamespace() {
		if (parent() == null) {
			return this;
		}
		else {
			return nearestAncestor(Namespace.class).defaultNamespace();
		}
	}

	/**
	 * Return the subnamespaces of this namespace.
	 */
 /*@
   @ public behavior
   @
   @ post \result != null;
   @*/
	public abstract List<Namespace> getSubNamespaces();

	/**
	 * <p>Return the namespace with the fullyqualified name that
	 * equals the fqn of this namespace concatenated with the
	 * given name.</p>
	 *
	 * <p>If the namespace does not exist yet, it will be created.</p>
	 *
	 * @param name
	 *        The qualified name relative to this namespace
	 */
 /*@
   @ public behavior
   @
   @ pre name != null;
   @
   @ post (! this == defaultNamespace()) ==> \result.getFullyQualifiedName().equals(getFullyQualifiedName() + "." + name);
   @ post (this == defaultNamespace()) ==> \result.getFullyQualifiedName().equals(name);
   @
   @ signals (LookupException) (* There are multiple subnamespaces with the given name. *) 
   @*/
	public synchronized Namespace getOrCreateNamespace(final String name) throws LookupException {
		if ((name == null) || name.equals("")) {
			return this;
		}
		final String current = Util.getFirstPart(name);
		final String next = Util.getSecondPart(name); //rest
		Namespace currentPackage = getSubNamespace(current);
		if(currentPackage == null) {
			currentPackage = createNamespace(current);
		}
		return currentPackage.getOrCreateNamespace(next);
	}

	protected abstract Namespace createNamespace(String name);
	
	/**
	 * Return the direct subpackage with the given short name.
	 *
	 * @param name
	 *        The short name of the package to be returned.
	 */
 /*@
	 @ public behavior
	 @
	 @ post getSubNamespaces().contains(\result);
	 @ post \result.getName().equals(name);
	 @
	 @ signals (LookupException) (* There are multiple namespaces with the given name. *);
	 @*/
	public Namespace getSubNamespace(final String name) throws LookupException {
		List<Namespace> packages = getSubNamespaces();

		new SafePredicate<Namespace>() {
			public boolean eval(Namespace o) {
				return o.name().equals(name);
			}
		}.filter(packages);
		if (packages.isEmpty()) {
			return null;
		}
		else if(packages.size() == 1){
			return (Namespace)packages.iterator().next();
		}
		else {
			throw new LookupException("Namespace "+getFullyQualifiedName()+ " contains "+packages.size()+" sub namespaces with name "+name);
		}

	}



//	/**
//	 * Return the set of all types in this package and all of its subpackages.
//	 */
//	/*@
//	 @ public behavior
//	 @
//	 @ post \result != null;
//	 @ post \result.containsAll(getTypes());
//	 @ post (\forall Namespace sub; getSubNamespaces().contains(sub);
//	 @         \result.containsAll(sub.getAllTypes()));
//	 @*/
//	public List<Type> getAllTypes() {
//		final List<Type> result = getTypes();
//		new Visitor() {
//			public void visit(Object element) {
//				result.addAll(((Namespace)element).getAllTypes());
//			}
//		}.applyTo(getSubNamespaces());
//		return result;
//	}

	public <T extends Declaration> List<T> allDeclarations(Class<T> kind) throws LookupException {
  	final List<T> result = declarations(kind);
  	for(Namespace ns:getSubNamespaces()) {
		  result.addAll(ns.allDeclarations(kind));
  	}
 	  return result;
	}

		/***********
		 * CONTEXT *
		 ***********/
		
	public LocalLookupStrategy targetContext() {
		return language().lookupFactory().createTargetLookupStrategy(this);
	}
	
	public LookupStrategy localStrategy() {
		return language().lookupFactory().createLocalLookupStrategy(this);
	}

	public List<Declaration> declarations() throws LookupException {
		List<Declaration> result = new ArrayList<Declaration>();
		result.addAll(getSubNamespaces());
		for(NamespaceDeclaration part: getNamespaceParts()) {
			result.addAll(part.declarations());
		}
		return result;
	}
	
	@Override
	public synchronized void flushLocalCache() {
		_declarationCache = null;
	}
	
	private synchronized void ensureLocalCache() throws LookupException {
		if(_declarationCache == null) {
			List<Declaration> declarations = declarations();
		  _declarationCache = new HashMap<String, List<Declaration>>();
		  for(Declaration declaration: declarations) {
		  	String name = declaration.signature().name();
				List<Declaration> list = cachedDeclarations(name);
		  	boolean newList = false;
		  	if(list == null) {
		  		list = new ArrayList<Declaration>();
		  		newList = true;
		  	}
		  	// list != null
		  	list.add(declaration);
		  	if(newList) {
		  		_declarationCache.put(name, list);
		  	}
		  }
		}
	}
	
	protected synchronized List<Declaration> cachedDeclarations(String name) {
		if(_declarationCache != null) {
		  return _declarationCache.get(name);
		} else {
			return null;
		}
	}
	
	protected synchronized void storeCache(String name, List<Declaration> declarations) {
		if(_declarationCache == null) {
			_declarationCache = new HashMap<String, List<Declaration>>();
		}
		_declarationCache.put(name, declarations);
	}

	private Map<String,List<Declaration>> _declarationCache;
	
	public <D extends Declaration> List<D> declarations(DeclarationSelector<D> selector) throws LookupException {
//		return selector.selection(declarations());
		if(selector.usesSelectionName()) {
			List<? extends Declaration> list = null;
			if(Config.cacheDeclarations()) {
				ensureLocalCache();
				synchronized(this) {
				  list = _declarationCache.get(selector.selectionName(this));
				}
			} else {
				list = declarations();
			}
			if(list == null) {
				list = Collections.EMPTY_LIST;
			}
			return selector.selection(Collections.unmodifiableList(list));
		} else {
			return selector.selection(declarations());
		}
	}
	
	public <T extends Declaration> List<T> declarations(Class<T> kind) throws LookupException {
    return new TypePredicate<Declaration,T>(kind).filterReturn(declarations());
  }
	

	public NamespaceAlias alias(SimpleNameSignature sig) {
		return new NamespaceAlias(sig,this);
	}

	public Namespace selectionDeclaration() {
		return this;
	}
	
	public Namespace actualDeclaration() {
		return this;
	}
	
}