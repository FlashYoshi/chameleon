package be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationSelector;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.SelectionResult;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.ElementWithModifiersImpl;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.member.Member;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.util.Lists;
import be.kuleuven.cs.distrinet.chameleon.util.StackOverflowTracer;
import be.kuleuven.cs.distrinet.chameleon.util.association.Single;
import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;

public abstract class AbstractInheritanceRelation extends ElementWithModifiersImpl implements InheritanceRelation {
	
	public AbstractInheritanceRelation(TypeReference ref) {
		setSuperClassReference(ref);
	}
	
	public Type superElement() throws LookupException {
		return superClass();
	}
	
	/**
	 * Return the inherited class.
	 * @return
	 * @throws LookupException
	 */
 /*@
   @ public behavior
   @
   @ post \result == superClassReference().getType();
   @*/
	public Type superClass() throws LookupException {
//			System.out.println("Inheritance relation of class "+fqn+" is going to look up super class ");
			Type result = null;
			try {
		    result = superClassReference().getType();
			} 
			catch(NullPointerException exc) {
				String fullyQualifiedName = "CLASS WITHOUT PARENT!!!";
				if(parent() != null) {
				  fullyQualifiedName = fullyQualifiedName;
				}
				if(superClassReference() == null) {
				  throw new ChameleonProgrammerException("trying to get the super class of an inheritance relation that points to null in class" + fullyQualifiedName,exc);
				} else {
					throw exc;
				}
			}
		  if(result != null) {
		  	return result;
		  } else {
		  	throw new LookupException("Superclass is null",superClassReference());
		  }
	}
	
	/**
	 * Return a reference to the super class of this inheritance relation.
	 * @return
	 */
	public TypeReference superClassReference() {
		return _superClass.getOtherEnd();
	}
	
	/**
	 * Set the type reference that points to the super class of this inheritance relation.
	 */
 /*@
   @ public behavior
   @
   @ post superClassReference() == ref;
   @*/
	public void setSuperClassReference(TypeReference ref) {
		set(_superClass,ref);
	}
	
	/**
	 * Return the set of members inherited through this inheritance relation.
	 * @param <M>
	 * @param <S>
	 * @param <F>
	 * @param kind
	 * @return
	 * @throws LookupException
	 */
	public <M extends Member> 
  void accumulateInheritedMembers(final Class<M> kind, List<M> current) throws LookupException {
		final List<M> potential = potentiallyInheritedMembers(kind);
		removeNonMostSpecificMembers((List)current, potential);
	}

//	public <M extends Member<M,? super Type,S,F>, S extends Signature<S,M>, F extends Member<? extends Member,? super Type,S,F>> 
//  void accumulateInheritedMembers(DeclarationSelector<M> selector, List<M> current) throws LookupException {
	public <X extends Member> 
  void accumulateInheritedMembers(DeclarationSelector<X> selector, List<SelectionResult> current) throws LookupException {
		final List<? extends SelectionResult> potential = potentiallyInheritedMembers(selector);
		removeNonMostSpecificMembers(current, potential);
	}

	protected <M extends Member>
	  void removeNonMostSpecificMembers(List<SelectionResult> current, final List<? extends SelectionResult> potential) throws LookupException {
		final List<SelectionResult> toAdd = Lists.create();
		for(SelectionResult mm: potential) {
			Member m = (Member)mm.finalDeclaration();
			boolean add = true;
			Iterator<? extends SelectionResult> iterCurrent = current.iterator();
			while(add && iterCurrent.hasNext()) {
				Member alreadyInherited = (Member)iterCurrent.next().finalDeclaration();
				// Remove the already inherited member if potentially inherited member m overrides or hides it.
				if((alreadyInherited.sameAs(m) || alreadyInherited.overrides(m) || alreadyInherited.canImplement(m) || alreadyInherited.hides(m))) {
					add = false;
				} else if((!alreadyInherited.sameAs(m)) && (m.overrides(alreadyInherited) || m.canImplement(alreadyInherited) || m.hides(alreadyInherited))) {
					iterCurrent.remove();
				}
			}
			if(add == true) {
				toAdd.add(mm);
			}
		}
		current.addAll(toAdd);
	}

	
//	public <M extends Member<M,? super Type,S,F>, S extends Signature<S,M>, F extends Member<? extends Member,? super Type,S,F>> 
//	        List<M> potentiallyInheritedMembers(final Class<M> kind) throws LookupException {
	public <M extends Member> List<M> potentiallyInheritedMembers(final Class<M> kind) throws LookupException {
		List<M> superMembers = superClass().members(kind);
		removeNonInheritableMembers(superMembers);
    return superMembers;
	}

	public List<Member> potentiallyInheritedMembers() throws LookupException {
		List<Member> superMembers = superClass().members();
		removeNonInheritableMembers(superMembers);
    return superMembers;
	}
	
	public <M extends Member> List<? extends SelectionResult> potentiallyInheritedMembers(
			final DeclarationSelector<M> selector) throws LookupException {
		List<? extends SelectionResult> superMembers = superClass().members(selector);
		removeNonInheritableMembers(superMembers);
		return superMembers;
	}
	
//	public static DeclarationContainerAlias membersInContext(Type type) throws LookupException {
//		DeclarationContainerAlias result = _cache.get(type);
//		if(result == null) {
//		  List<Member> elements = type.localMembers();
//		  result = new DeclarationContainerAlias(type);
//		  for(Member member: elements) {
//			  Member clone = member.clone();
//			  clone.setOrigin(member.origin());
//			  result.add(clone);
//		  }
//		  for(InheritanceRelation inheritanceRelation: type.inheritanceRelations()) {
//			  inheritanceRelation.mergeMembersInContext(result);
//		  }
//		  _cache.put(type, result);
//		}
//		return result;
//	}
	
//	private static Map<Type, DeclarationContainerAlias> _cache = new HashMap<Type, DeclarationContainerAlias>();
	
//	public void mergeMembersInContext(DeclarationContainerAlias accumulator) throws LookupException {
//		DeclarationContainerAlias clonedSuperMemberContainer = membersInContext(superClass()).clone();
//		mergeMembersInContext(accumulator, clonedSuperMemberContainer);
//		accumulator.addSuperContainer(clonedSuperMemberContainer);
//	}
//	
//	protected void mergeMembersInContext(DeclarationContainerAlias accumulator, DeclarationContainerAlias newTree) throws LookupException {
//		List<DeclarationContainerAlias> supers = accumulator.superContainers();
//		for(DeclarationContainerAlias superContainer: supers) {
//			mergeMembersInContext(superContainer, newTree);
//		}
//		mergeAux(accumulator, newTree);
//	}
//	
//	public void mergeAux(DeclarationContainerAlias accumulator, DeclarationContainerAlias newTree) throws LookupException {
//		List<DeclarationContainerAlias> supers = newTree.superContainers();
//		for(DeclarationContainerAlias superContainer: supers) {
//			mergeAux(accumulator, superContainer);
//		}
//		mergeLocal(accumulator, newTree);
//	}
//	public void mergeLocal(DeclarationContainerAlias accumulator, DeclarationContainerAlias newTree) throws LookupException {
//		for(Declaration superDeclaration: newTree.declarations()) {
//			Member superMember;
//			if(superDeclaration instanceof Member) {
//			  superMember = (Member) superDeclaration;
//			} else {
//				superMember = (Member) ((DeclarationAlias) superDeclaration).aliasedDeclaration();
//			}	
//			for(Declaration processedDeclaration: accumulator.declarations()) {
//				Member processedMember;
//				if(processedDeclaration instanceof Member) {
//				  processedMember = (Member) processedDeclaration;
//				} else {
//					processedMember = (Member) ((DeclarationAlias) processedDeclaration).aliasedDeclaration();
//				}	
//				if((processedMember.origin().equals(superMember.origin())) || processedMember.equals(superMember) || processedMember.overrides(superMember) || processedMember.sameAs(superMember) || processedMember.canImplement(superMember) || processedMember.hides(superMember)) {
//					// Make superDeclaration an alias, or update the alias.
//				  DeclarationAlias alias = new DeclarationAlias(superDeclaration.signature().clone(), processedMember);
//				  DeclarationContainerAlias superContainer = (DeclarationContainerAlias) superDeclaration.parent();
//				  superContainer.remove(superDeclaration);
//				  superContainer.add(alias);
//				  break;
//				}
//				else if((!processedMember.equals(superMember)) && (superMember.overrides(processedMember) || superMember.canImplement(processedMember) || superMember.hides(processedMember))) {
//					// Make processedDeclaration an alias, or update the alias.
//				  DeclarationAlias alias = new DeclarationAlias(processedDeclaration.signature().clone(), superMember);
//				  DeclarationContainerAlias processedContainer = (DeclarationContainerAlias) processedDeclaration.parent();
//				  processedContainer.remove(processedDeclaration);
//				  processedContainer.add(alias);
//				}
//			}
//		}
//	}
//	
//	public void XXmergeMembersInContext(DeclarationContainerAlias accumulator) throws LookupException {
//		DeclarationContainerAlias superMemberContainer = membersInContext(superClass());
//		for(Declaration superDeclaration: superMemberContainer.allDeclarations()) {
//			Member superMember;
//			if(superDeclaration instanceof Member) {
//			  superMember = (Member) superDeclaration;
//			} else {
//				superMember = (Member) ((DeclarationAlias) superDeclaration).aliasedDeclaration();
//			}	
//			for(Declaration processedDeclaration: accumulator.allDeclarations()) {
//				Member processedMember;
//				if(processedDeclaration instanceof Member) {
//				  processedMember = (Member) processedDeclaration;
//				} else {
//					processedMember = (Member) ((DeclarationAlias) processedDeclaration).aliasedDeclaration();
//				}	
//				if(processedMember.equals(superMember) || processedMember.overrides(superMember) || processedMember.sameAs(superMember) || processedMember.canImplement(superMember) || processedMember.hides(superMember)) {
//					// Make superDeclaration an alias, or update the alias.
//				  DeclarationAlias alias = new DeclarationAlias(superDeclaration.signature().clone(), processedMember);
//				  DeclarationContainerAlias superContainer = (DeclarationContainerAlias) superDeclaration.parent();
//				  superContainer.remove(superDeclaration);
//				  superContainer.add(alias);
//				  break;
//				}
//				else if((!processedMember.equals(superMember)) && (superMember.overrides(processedMember) || superMember.canImplement(processedMember) || superMember.hides(processedMember))) {
//					// Make processedDeclaration an alias, or update the alias.
//				  DeclarationAlias alias = new DeclarationAlias(processedDeclaration.signature().clone(), superMember);
//				  DeclarationContainerAlias processedContainer = (DeclarationContainerAlias) processedDeclaration.parent();
//				  processedContainer.remove(processedDeclaration);
//				  processedContainer.add(alias);
//				}
//				
//			}
//		}
//		accumulator.addSuperContainer(superMemberContainer);
//	}

  /**
   * Remove members that are not inheritable.
   */
 /*@
   @ public behavior
   @
   @ (\forall M m; members.contains(m); \old(members()).contains(m) && m.is(language(ObjectOrientedLanguage.class).INHERITABLE == Ternary.TRUE);
   @*/
	private <M extends Declaration> void removeNonInheritableMembers(List<? extends SelectionResult> members) throws LookupException {
		Iterator<? extends SelectionResult> superIter = members.iterator();
		while(superIter.hasNext()) {
			SelectionResult r = superIter.next();
			Declaration member = r.finalDeclaration();
			Ternary temp = member.is(language(ObjectOrientedLanguage.class).INHERITABLE);
			if (temp == Ternary.UNKNOWN) {
				temp = member.is(language(ObjectOrientedLanguage.class).INHERITABLE);
				throw new LookupException("For one of the members of super type " + superClass().getFullyQualifiedName()
						+ " it is unknown whether it is inheritable or not. Member type: " + member.getClass().getName());
			} else {
				if (temp == Ternary.FALSE) {
					superIter.remove();
				}
			}
		}
	}
	
//	public <M extends Member<M,? super Type,S,F>, S extends Signature<S,M>, F extends Member<? extends Member,? super Type,S,F>> 
//	        M transform(M member) throws MetamodelException {
//		M result = member.clone();
//		// 1) SUBSTITUTE GENERIC PARAMETERS, OR USE TRICK CONTAINER?
//		//   1.a) WE NEED A TRICK CONTAINER (The superclass) IN WHICH THE PARAMETERS ARE SUBSTITUTED
//	}
	
	private Single<TypeReference> _superClass = new Single<TypeReference>(this);


}
