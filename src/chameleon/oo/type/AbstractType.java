package chameleon.oo.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.rejuse.java.collections.TypeFilter;
import org.rejuse.logic.ternary.Ternary;
import org.rejuse.predicate.TypePredicate;

import chameleon.core.Config;
import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.Definition;
import chameleon.core.declaration.MissingSignature;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.language.Language;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LocalLookupStrategy;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.lookup.LookupStrategySelector;
import chameleon.core.member.FixedSignatureMember;
import chameleon.core.member.Member;
import chameleon.core.modifier.Modifier;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.core.statement.CheckedExceptionList;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.generics.TypeParameter;
import chameleon.oo.type.inheritance.InheritanceRelation;
import chameleon.util.Pair;

/**
 * <p>A class representing types in object-oriented programs.</p>
 *
 * <p>A class contains <a href="Member.html">members</a> as its content.</p>
 *
 * @author Marko van Dooren
 */
public abstract class AbstractType extends FixedSignatureMember<Type,Element,SimpleNameSignature,Type> 
                implements Type {
 
	
	public Class<SimpleNameSignature> signatureType() {
		return SimpleNameSignature.class;
	}
	
	public Type declarationType() {
		return this;
	}
	
	private List<? extends Declaration> _declarationCache = null;
	
	private List<? extends Declaration> declarationCache() {
		if(_declarationCache != null && Config.cacheDeclarations()) {
		  return new ArrayList<Declaration>(_declarationCache);
		} else {
			return null;
		}
	}
	
  @Override
  public void flushLocalCache() {
  	super.flushLocalCache();
  	_declarationCache = null;
  }

	private void setDeclarationCache(List<? extends Declaration> cache) {
		if(Config.cacheDeclarations()) {
		  _declarationCache = new ArrayList<Declaration>(cache);
		}
	}
	
	
    /**
     * Initialize a new Type.
     */
   /*@
     @ public behavior
     @
     @ pre sig != null;
 	   @
     @ post signature() == sig;
     @ post parent() == null;
     @*/
    public AbstractType(SimpleNameSignature sig) {
        setSignature(sig);
    }
    

  	/* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#getName()
		 */

  	/*@
  	 @ public behavior
  	 @
  	 @ post \result != null;
  	 @*/
  	public String getName() {
  		SimpleNameSignature signature = signature();
			return (signature != null ? signature.name() : null);
  	}

    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#getFullyQualifiedName()
		 */
   /*@
     @ public behavior
     @
     @ getPackage().getFullyQualifiedName().equals("") ==> \result == getName();
     @ ! getPackage().getFullyQualifiedName().equals("") == > \result.equals(getPackage().getFullyQualifiedName() + getName());
     @*/
    public String getFullyQualifiedName() {
        String prefix;
        Type nearest = nearestAncestor(Type.class);
        if(nearest != null) {
        	prefix = nearest.getFullyQualifiedName();
        } else {
          prefix = getNamespace().getFullyQualifiedName();
        }
        return (prefix.equals("") ? "" : prefix+".")+getName();
    }

    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#targetContext()
		 */
    
    
    public LookupStrategy targetContext() throws LookupException {
    	Language language = language();
    	if(language != null) {
			  return language.lookupFactory().createTargetLookupStrategy(this);
    	} else {
    		throw new LookupException("Element of type "+getClass().getName()+" is not connected to a language. Cannot retrieve target context.");
    	}
    }
    
    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#localStrategy()
		 */
    public LookupStrategy localStrategy() throws LookupException {
    	return targetContext();
    }
    
    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#lexicalLookupStrategy(chameleon.core.element.Element)
		 */
    public LookupStrategy lexicalLookupStrategy(Element element) throws LookupException {
    	if(inheritanceRelations().contains(element)) {
    		Element parent = parent();
    		if(parent != null) {
    			return lexicalParametersLookupStrategy();
//    		  return parent().lexicalContext(this);
    		} else {
    			throw new LookupException("Parent of type is null when looking for the parent context of a type.");
    		}
    	} else {
    	  return lexicalMembersLookupStrategy();
    	  
    	  //language().lookupFactory().createLexicalContext(this,targetContext());
    	}
    }
    
    protected LookupStrategy lexicalMembersLookupStrategy() throws LookupException {
    	LookupStrategy result = _lexicalMembersLookupStrategy;
    	// Lazy initialization
    	if(result == null) {
    		Language language = language();
    		if(language == null) {
    			throw new LookupException("Parent of type "+signature().name()+" is null.");
    		}
				_lexicalMembersLookupStrategy = language.lookupFactory().createLexicalLookupStrategy(targetContext(), this, 
    			new LookupStrategySelector(){
					
						public LookupStrategy strategy() throws LookupException {
	    	  		return lexicalParametersLookupStrategy();
						}
					}); 
    		result = _lexicalMembersLookupStrategy;
    	}
    	return result;
    }
    
    protected LookupStrategy _lexicalMembersLookupStrategy;
    
    protected LookupStrategy lexicalParametersLookupStrategy() {
    	LookupStrategy result = _lexicalParametersLookupStrategy;
    	// lazy initialization
    	if(result == null) {
    		_lexicalParametersLookupStrategy = language().lookupFactory().createLexicalLookupStrategy(_localInheritanceLookupStrategy, this);
    		result = _lexicalParametersLookupStrategy;
    	}
    	return result;
    }
    
    protected LookupStrategy _lexicalParametersLookupStrategy;
    
    protected LocalInheritanceLookupStrategy _localInheritanceLookupStrategy = new LocalInheritanceLookupStrategy(this);
    
  	protected class LocalInheritanceLookupStrategy extends LocalLookupStrategy<Type> {
  	  public LocalInheritanceLookupStrategy(Type element) {
  			super(element);
  		}

  	  @Override
  	  @SuppressWarnings("unchecked")
  	  public <D extends Declaration> List<D> declarations(DeclarationSelector<D> selector) throws LookupException {
  	    return selector.selection(parameters());
  	  }
  	}

  	/* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#parameters()
		 */
  	public abstract List<TypeParameter> parameters();
  	
  	/* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#parameter(int)
		 */
  	public abstract TypeParameter parameter(int index);
  	
  	/* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#nbTypeParameters()
		 */
  	public abstract int nbTypeParameters();
  	
  	/* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#addParameter(chameleon.oo.type.generics.TypeParameter)
		 */
  	public abstract void addParameter(TypeParameter parameter);
  	
  	/* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#replaceParameter(chameleon.oo.type.generics.TypeParameter, chameleon.oo.type.generics.TypeParameter)
		 */
  	public abstract void replaceParameter(TypeParameter oldParameter, TypeParameter newParameter);

  	/* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#replaceAllParameter(java.util.List)
		 */
  	public abstract void replaceAllParameter(List<TypeParameter> newParameters);
  	
    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#getIntroducedMembers()
		 */
    
    public List<Member> getIntroducedMembers() {
      List<Member> result = new ArrayList<Member>();
      result.add(this);
      return result;
    }
    
    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#complete()
		 */
    public Ternary complete() {
			try {
	    	List<Member> members = localMembers(Member.class);
	    	// Only check for actual definitions
	    	new TypePredicate<Element,Definition>(Definition.class).filter(members);
	    	Iterator<Member> iter = members.iterator();
	    	Ternary result = Ternary.TRUE;
	    	while(iter.hasNext()) {
	    		result = result.and(iter.next().is(language(ObjectOrientedLanguage.class).DEFINED));
	    	}
	      return result;
			} catch (LookupException e) {
				return Ternary.UNKNOWN;
			}
    }
    

    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#getType()
		 */
    public Type getType() {
        return this;
    }

  	/***********
  	 * MEMBERS *
  	 ***********/

    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#add(chameleon.oo.type.TypeElement)
		 */
   /*@
     @ public behavior
     @
     @ pre element != null;
     @
     @ post directlyDeclaredElements().contains(element);
     @*/
  	public abstract void add(TypeElement element) throws ChameleonProgrammerException;
  	
    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#remove(chameleon.oo.type.TypeElement)
		 */
   /*@
     @ public behavior
     @
     @ pre element != null;
     @
     @ post ! directlyDeclaredElements().contains(element);
     @*/
  	public abstract void remove(TypeElement element) throws ChameleonProgrammerException;
  	
    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#addAll(java.util.Collection)
		 */
   /*@
     @ public behavior
     @
     @ pre elements != null;
     @ pre !elements.contains(null);
     @
     @ post directlyDeclaredElements().containsAll(elements);
     @*/
  	public void addAll(Collection<? extends TypeElement> elements) throws ChameleonProgrammerException {
  		for(TypeElement element: elements) {
  			add(element);
  		}
  	}

    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#getDirectSuperTypes()
		 */

    public List<Type> getDirectSuperTypes() throws LookupException {
            final ArrayList<Type> result = new ArrayList<Type>();
            for(InheritanceRelation element:inheritanceRelations()) {
              Type type = element.superType();
              if (type!=null) {
                result.add(type);
              }
            }
            return result;
    }

    public List<Type> getDirectSuperClasses() throws LookupException {
      final ArrayList<Type> result = new ArrayList<Type>();
      for(InheritanceRelation element:inheritanceRelations()) {
        result.add(element.superClass());
      }
      return result;
}

    public void accumulateAllSuperTypes(Set<Type> acc) throws LookupException {
    	List<Type> temp =getDirectSuperTypes();
//    	acc.addAll(temp);
//    	for(Type type:temp) {
//    		  type.accumulateAllSuperTypes(acc);
//    	}
    	for(Type type:temp) {
    		if(! acc.contains(type)) {
    			acc.add(type);
    		  type.accumulateAllSuperTypes(acc);
    		}
    	}
    }
    
    public Set<Type> getAllSuperTypes() throws LookupException {
    	if(_superTypeCache == null) {
    		_superTypeCache = new HashSet<Type>();
    		accumulateAllSuperTypes(_superTypeCache);
    	}
    	Set<Type>  result = new HashSet<Type>(_superTypeCache);
    	return result;
    }
    
//    public Set<Type> getAllSuperTypes() throws LookupException {
//    	Set<Type> result = new HashSet<Type>();
//    	accumulateAllSuperTypes(result);
//    	return result;
//    }

    
    private Set<Type> _superTypeCache;

    
    //TODO: rename to properSubTypeOf
    
    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#subTypeOf(chameleon.oo.type.Type)
		 */
    public boolean subTypeOf(Type other) throws LookupException {
    	ObjectOrientedLanguage language = language(ObjectOrientedLanguage.class);
			WeakPartialOrder<Type> subtypeRelation = language.subtypeRelation();
			return subtypeRelation.contains(this, other);
//    	  Collection superTypes = getAllSuperTypes(); 
//        return superTypes.contains(other);
    }
    
//    /* (non-Javadoc)
//		 * @see chameleon.oo.type.Tajp#uniSameAs(chameleon.core.element.Element)
//		 */
//    @Override
//    public boolean uniSameAs(Element other) throws LookupException {
//    	return other == this;
//    }
    
    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#assignableTo(chameleon.oo.type.Type)
		 */
   /*@
     @ public behavior
     @
     @ post \result == equals(other) || subTypeOf(other);
     @*/
    public boolean assignableTo(Type other) throws LookupException {
    	boolean equal = equals(other);
    	boolean subtype = subTypeOf(other);
    	return (equal || subtype);
    }

    
  	/* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#inheritanceRelations()
		 */
   /*@
     @ public behavior
     @
     @ post \result != null;
     @*/
  	public abstract List<InheritanceRelation> inheritanceRelations();
  	
//  	/* (non-Javadoc)
//		 * @see chameleon.oo.type.Tajp#directSuperTypes()
//		 */
//   /*@
//     @ public behavior
//     @
//     @ post \result != null;
//     @ post (\forall InheritanceRelation relation; inheritanceRelations().contains(relation) ;
//     @             \result.contains(relation.superType()));
//     @*/
//  	public List<Type> directSuperTypes() throws LookupException {
//  	  List<Type> result = new ArrayList<Type>();
//  		for(InheritanceRelation relation: inheritanceRelations()) {
//  			Type superType = relation.superType();
//  			if(superType != null) {
//  				result.add(superType);
//  			}
//  		}
//  		return result;
//  	}

  	/* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#addInheritanceRelation(chameleon.oo.type.inheritance.InheritanceRelation)
		 */
   /*@
     @ public behavior
     @
     @ pre relation != null;
     @ post inheritanceRelations().contains(relation);
     @*/
  	public abstract void addInheritanceRelation(InheritanceRelation relation) throws ChameleonProgrammerException;
    
  	/* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#removeInheritanceRelation(chameleon.oo.type.inheritance.InheritanceRelation)
		 */
   /*@
     @ public behavior
     @
     @ pre relation != null;
     @ post ! inheritanceRelations().contains(relation);
     @*/
  	public abstract void removeInheritanceRelation(InheritanceRelation relation) throws ChameleonProgrammerException;
  	
  	/* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#removeAllInheritanceRelations()
		 */
  	public void removeAllInheritanceRelations() {
  		for(InheritanceRelation relation: inheritanceRelations()) {
  			removeInheritanceRelation(relation);
  		}
  	}
  	
    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#localMembers(java.lang.Class)
		 */
    public <T extends Member> List<T> localMembers(final Class<T> kind) throws LookupException {
      return (List<T>) new TypeFilter(kind).retain(localMembers());
    }
    
    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#localMembers()
		 */
    public abstract List<Member> localMembers() throws LookupException;
    
    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#directlyDeclaredMembers(java.lang.Class)
		 */
    public <T extends Member> List<T> directlyDeclaredMembers(Class<T> kind) {
      return (List<T>) new TypeFilter(kind).retain(directlyDeclaredMembers());
    }
    
    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#directlyDeclaredMembers()
		 */
    public  List<Member> directlyDeclaredMembers() {
  		List<Member> result = new ArrayList<Member>();
      for(TypeElement m: directlyDeclaredElements()) {
        result.addAll(m.declaredMembers());
      }
      return result;
    }
    
    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#members(chameleon.core.lookup.DeclarationSelector)
		 */
    public <D extends Member> List<D> members(DeclarationSelector<D> selector) throws LookupException {

  		// 1) All defined members of the requested kind are added.
  		List<D> result = localMembers(selector);

  		// 2) Fetch all potentially inherited members from all inheritance relations
  		for (InheritanceRelation rel : inheritanceRelations()) {
  				rel.accumulateInheritedMembers(selector, result);
  		}
  		// The selector must still apply its order to the candidates.
  		//selector.applyOrder(result);
  		
  		return selector.selection(result);
    }
    
/*    public <D extends Member> List<D> members(DeclarationSelector<D> selector) throws LookupException {
    	System.out.println("MEMBERS of: "+getFullyQualifiedName());
  		DeclarationContainerAlias alias = InheritanceRelation.membersInContext(this);
  		List<Declaration> declarations = alias.allDeclarations();
  		List<Member> result = new ArrayList<Member>();
  		for(Declaration declaration:declarations) {
  			if(
  					 (declaration.is(language(ObjectOrientedLanguage.class).INHERITABLE) == Ternary.TRUE || 
  							 declaration.nearestAncestor(Type.class) == this
  					 )  && 
  				(! (declaration instanceof DeclarationAlias))) {
  				result.add((Member) declaration);
  			}
  		}
  		return selector.selection(result);
    }*/

    
    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#localMembers(chameleon.core.lookup.DeclarationSelector)
		 */
    @SuppressWarnings("unchecked")
    public abstract <D extends Member> List<D> localMembers(DeclarationSelector<D> selector) throws LookupException;

    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#members()
		 */
    public List<Member> members() throws LookupException {
      return members(Member.class);
    }
    
//    public <M extends Member> Set<M> potentiallyInheritedMembers(final Class<M> kind) throws MetamodelException {
//  		final Set<M> result = new HashSet<M>();
//			for (InheritanceRelation rel : inheritanceRelations()) {
//				result.addAll(rel.potentiallyInheritedMembers(kind));
//			}
//  		return result;
//    }
//
    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#members(java.lang.Class)
		 */
    public <M extends Member> List<M> members(final Class<M> kind) throws LookupException {

		// 1) All defined members of the requested kind are added.
		final List<M> result = new ArrayList(localMembers(kind));

		// 2) Fetch all potentially inherited members from all inheritance relations
		for (InheritanceRelation rel : inheritanceRelations()) {
				rel.accumulateInheritedMembers(kind, result);
		}
		return result;
	}

    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#clone()
		 */



    public abstract Type clone();

   /*@
     @ also public behavior
     @
     @ post \result.containsAll(getSuperTypeReferences());
     @ post \result.containsAll(getMembers());
     @ post \result.containsAll(getModifiers());
     @*/
    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#children()
		 */
    public List<Element> children() {
        List<Element> result = super.children();
        result.addAll(inheritanceRelations());
//        result.addAll(directlyDeclaredElements());
        return result;
    }

    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#directlyDeclaredElements()
		 */
    public abstract List<? extends TypeElement> directlyDeclaredElements();

    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#getCEL()
		 */

    public CheckedExceptionList getCEL() throws LookupException {
        CheckedExceptionList cel = new CheckedExceptionList();
        for(TypeElement el : localMembers()) {
        	cel.absorb(el.getCEL());
        }
        return cel;
    }

    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#getAbsCEL()
		 */
    public CheckedExceptionList getAbsCEL() throws LookupException {
      CheckedExceptionList cel = new CheckedExceptionList();
      for(TypeElement el : localMembers()) {
      	cel.absorb(el.getAbsCEL());
      }
      return cel;
    }

    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#declarations()
		 */
    public List<? extends Declaration> declarations() throws LookupException {
    	List<? extends Declaration> result = declarationCache();
    	if(result == null) {
    		result = members();
    		setDeclarationCache(result);
    	}
    	return result;
    }
    /* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#declarations(chameleon.core.lookup.DeclarationSelector)
		 */
    public <D extends Declaration> List<D> declarations(DeclarationSelector<D> selector) throws LookupException {
    	return (List<D>) members((DeclarationSelector<? extends Member>)selector);
    }

  	protected void copyContents(Type from) {
  		copyContents(from, false);
  	}

  	protected void copyContents(Type from, boolean link) {
  		for(InheritanceRelation relation : from.inheritanceRelations()) {
        InheritanceRelation clone = relation.clone();
        if(link) {
        	clone.setOrigin(relation);
        }
				addInheritanceRelation(clone);
  		}
      copyEverythingExceptionInheritanceRelations(from, link);
  	}

		protected void copyEverythingExceptionInheritanceRelations(Type from, boolean link) {
			for(Modifier mod : from.modifiers()) {
      	Modifier clone = mod.clone();
        if(link) {
        	clone.setOrigin(mod);
        }
				addModifier(clone);
      }
      for(TypeElement el : from.directlyDeclaredElements()) {
        TypeElement clone = el.clone();
        if(link) {
        	clone.setOrigin(el);
        }
				add(clone);
      }
      for(TypeParameter par : from.parameters()) {
      	TypeParameter clone = par.clone();
        if(link) {
        	clone.setOrigin(par);
        }
				addParameter(clone);
      }
		}
  
  	/* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#alias(chameleon.core.declaration.SimpleNameSignature)
		 */
  	public Type alias(SimpleNameSignature sig) {
      return new TypeAlias(sig,this);
  	}

  	/* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#intersection(chameleon.oo.type.Type)
		 */
  	public Type intersection(Type type) throws LookupException {
  		return type.intersectionDoubleDispatch(this);
  	}
  	
  	public Type intersectionDoubleDispatch(Type type) throws LookupException {
  		Type result = new IntersectionType(this,type);
  		result.setUniParent(parent());
  		return result;
  	}

  	public Type intersectionDoubleDispatch(IntersectionType type) throws LookupException {
  		IntersectionType result = type.clone();
  		result.addType(type);
  		return result;
  	}

  	public Type union(Type type) throws LookupException {
  		return type.unionDoubleDispatch(this);
  	}
  	
  	public Type unionDoubleDispatch(Type type) throws LookupException {
  		Type result = new UnionType(this,type);
  		result.setUniParent(parent());
  		return result;
  	}
  	
  	public Type unionDoubleDispatch(UnionType type) throws LookupException {
  		UnionType result = type.clone();
  		result.addType(type);
  		return result;
  	}

		/* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#replace(chameleon.oo.type.TypeElement, chameleon.oo.type.TypeElement)
		 */
		public abstract void replace(TypeElement oldElement, TypeElement newElement);

		/* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#baseType()
		 */
		public abstract Type baseType();

		/* (non-Javadoc)
		 * @see chameleon.oo.type.Tajp#verifySelf()
		 */
		@Override
		public VerificationResult verifySelf() {
			if(signature() != null) {
			  return Valid.create();
			} else {
				return new MissingSignature(this); 
			}
		}

		public boolean upperBoundNotHigherThan(Type other, List<Pair<TypeParameter, TypeParameter>> trace) throws LookupException {
//			List<Pair<TypeParameter, TypeParameter>> slowTrace = new ArrayList<Pair<TypeParameter, TypeParameter>>(trace);
			List<Pair<TypeParameter, TypeParameter>> slowTrace = trace;
  	ObjectOrientedLanguage language = language(ObjectOrientedLanguage.class);
			return language.upperBoundNotHigherThan(this, other, slowTrace);
		}

		public Type upperBound() {
			return this;
		}
		
		public Type lowerBound() {
			return this;
		}

}


