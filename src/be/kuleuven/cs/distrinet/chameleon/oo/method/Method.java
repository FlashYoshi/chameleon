package be.kuleuven.cs.distrinet.chameleon.oo.method;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LocalLookupContext;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.Modifier;
import be.kuleuven.cs.distrinet.chameleon.oo.method.exception.ExceptionClause;
import be.kuleuven.cs.distrinet.chameleon.oo.method.exception.TypeExceptionDeclaration;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.Block;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.ExceptionTuple;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.rejuse.java.collections.Visitor;
import be.kuleuven.cs.distrinet.rejuse.predicate.AbstractPredicate;

/**
 * A class of methods.
 * 
 * A method has a header, which contains its return type, throws clause, name, arguments, ... 
 * A method can also have an implementation, which can be a native implementation, or a regular implementation.
 * 
 * @author Marko van Dooren
 *
 * @param <E>
 * @param <H>
 * @param <S>
 * @param <M>
 */
public abstract class Method extends DeclarationWithParameters {

	/**
	 * Initialize a new method with the given header.
	 * @param header
	 */
 /*@
   @ public behavior
   @
   @ post header() == header;
   @*/ 
	public Method(MethodHeader header) {
		super(header);
	}
	
	public Type declarationType() throws LookupException {
		return returnType();
	}
	
	public boolean complete() {
	  return implementation() != null && implementation().complete();
	}

	/******************
	 * IMPLEMENTATION *
	 ******************/
	
	public abstract Implementation implementation();

	public abstract void setImplementation(Implementation implementation);

	/****/

	/********
	 * MISC *
	 ********/

	/**
	 * Return the exception types that this method throw in a worst case scenario. Subtypes
	 * are not necessarily included.
	 */
	/*@
	 @ public behavior
	 @
	 @ post \result.equals(getExceptionClause().getWorstCaseExceptions());
	 @*/
	public Set getWorstCaseExceptions() throws LookupException {
		return getExceptionClause().getWorstCaseExceptions();
	}

	/**
	 * Return the checked exceptions that can be thrown by the
	 * <b>body</b> of this method if exceptions anchors are <b>not</b> taken into account.
	 * If this method has no body, the result will be empty.
	 */
	/*@
	 @ public behavior
	 @
	 @ post \result != null;
	 @ post getBody() == null ==> \result.isEmpty();
	 @*/
//	public Set getAbsoluteThrownCheckedExceptions() throws MetamodelException {
//		throw new Error("Implements exception anchors again");
////		Set excs = new HashSet();
////		Block body = getBody();
////		if(body != null) {
////			Collection declarations = body.getAbsCEL().getDeclarations();
////			final ExceptionClause exceptionClause = new ExceptionClause();
////			new Visitor() {
////				public void visit(Object element) {
////					exceptionClause.add((ExceptionDeclaration)element);
////				}
////			}.applyTo(declarations);
////
////			StubExceptionClauseContainer stub = new StubExceptionClauseContainer(this, exceptionClause, lexicalContext());
////			excs = exceptionClause.getWorstCaseExceptions();
////		}
////		return excs;
//	}

	/***************
	 * RETURN TYPE *
	 ***************/

	public abstract TypeReference returnTypeReference();
	
	public abstract void setReturnTypeReference(TypeReference type);

	/**
	 * Return the type of this method.
	 */
	/*@
	 @ public behavior
	 @
	 @ post \result == getReturnTypeReference().getType();
	 @*/
	public Type returnType() throws LookupException {
		if(returnTypeReference() != null) {
		  return returnTypeReference().getType();
		} else {
			throw new LookupException("Return type reference of method is null");
		}
	}

	/**
	 * Check whether or not the implementation of this method is compatible with the exception clause
	 * of this method.
	 */
	/*@
	 @ public behavior
	 @
	 @ post \result == (getImplementation() == null) || getImplementation().compatible();
	 @*/
	public boolean hasCompatibleImplementation() throws LookupException {
		return (implementation() == null) || implementation().compatible();
	}

	/**
	 * Check whether or not all checked exceptions caught in catch blocks can actually be thrown.
	 */
	/*@
	 @ public behavior
	 @
	 @ post \result == (getImplementation() == null) || getImplementation().hasValidCatchClauses();
	 @*/
	public boolean hasValidCatchClauses() throws LookupException {
		return (implementation() == null) || implementation().hasValidCatchClauses();
	}

  public abstract ExceptionClause getExceptionClause();

  public abstract void setExceptionClause(ExceptionClause clause);

	/**
	 * Check whether or not all elements of the exception are at least as visible as this method.
	 */
	/*@
	 @ public behavior
	 @
	 @ post \result == getExceptionClause().hasValidAccessibility();
	 @*/
	public boolean hasValidExceptionClauseAccessibility() throws LookupException {
		return getExceptionClause().hasValidAccessibility();
	}

//	/**
//	 * Check whether or not this method has an acyclic exception graph.
//	 */
//	/*@
//	 @ public behavior
//	 @
//	 @ post \result == hasAcyclicExceptionGraph(new HashSet());
//	 @*/
//	public boolean hasAcyclicExceptionGraph() throws LookupException {
//		return hasAcyclicExceptionGraph(new HashSet());
//	}
//
//	/**
//	 * @param done
//	 * @return
//	 */
//	public boolean hasAcyclicExceptionGraph(Set done) throws LookupException {
//		if(done.contains(this)) {
//			return false;
//		}
//		else {
//			Set newDone = new HashSet(done);
//			newDone.add(this);
//			return getExceptionClause().isAcyclic(newDone);
//		}
//	}

	/**
	 * Check whether or not the exception clause of this method is compatible with the
	 * exception clauses of all super methods.
	 * @return
	 * @throws LookupException
	 */
	public boolean hasValidOverridingExceptionClause() throws LookupException {
		try {
			List methods = directlyOverriddenMembers();
			return new AbstractPredicate() {
				public boolean eval(Object o) throws LookupException {
					Method method = (Method)o;
					return getExceptionClause().compatibleWith(method.getExceptionClause());
				}
			}.forAll(methods);
		}
		catch (LookupException e) {
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Error();
		}
	}

	/**
	 * Return the body of this method.
	 */
	public Block body() {
		if(implementation() == null) {
			return null;
		}
		else {
			return implementation().getBody();
		}
	}

	public Set getDirectlyThrownExceptions() throws LookupException {
		Block block = body();
		Set result = new HashSet();
		if (block != null) {
			List pairs = block.getCEL().getPairs();
			Iterator iter = pairs.iterator();
			while (iter.hasNext()) {
				ExceptionTuple pair = (ExceptionTuple)iter.next();
				if (pair.getDeclaration() instanceof TypeExceptionDeclaration) {
					result.add(pair.getException());
				}
			}
		}
		return result;
	}
  
  public LocalLookupContext targetContext() throws LookupException {
  	return returnType().targetContext();
  }
  
	public abstract boolean sameKind(Method other);

	/**
	 * For debugging purposes because Eclipse detail formatters simply don't work.
	 */
	public String toString() {
		return header().toString();
	}

}
