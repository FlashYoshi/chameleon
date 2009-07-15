/*
 * Copyright 2000-2004 the Jnome development team.
 *
 * @author Marko van Dooren
 * @author Nele Smeets
 * @author Kristof Mertens
 * @author Jan Dockx
 *
 * This file is part of Jnome.
 *
 * Jnome is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Jnome is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Jnome; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package chameleon.core.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rejuse.association.Reference;
import org.rejuse.java.collections.Visitor;

import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.reference.CrossReference;
import chameleon.core.statement.CheckedExceptionList;
import chameleon.core.type.Type;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 * 
 * @param <D> The type of declaration invoked by this invocation.
 */

public abstract class Invocation<E extends Invocation,D extends Method> extends Expression<E> implements CrossReference<E,Element> {

  public Invocation(InvocationTarget target) {
	  setTarget(target);
	  _parameters.connectTo(new ActualArgumentList().parentLink());
  }
  
  public abstract DeclarationSelector<D> selector();
  
//  /**
//   * <p>The accessibility domain is the intersection of the accessibility domains of:</p>
//   * <ul>
//   *   <li>The invoked method</li>
//   *   <li>The target (if it is not null)</li>
//   *   <li>The actual parameters</li>
//   * </ul>
//   */
//  public AccessibilityDomain getAccessibilityDomain() throws MetamodelException {
//    AccessibilityDomain result = getMethod().getAccessibilityDomain();
//    if(getTarget() != null) {
//      result = result.intersect(getTarget().getAccessibilityDomain());
//    }
//    Iterator iter = getActualParameters().iterator();
//    while(iter.hasNext() ) {
//      result = result.intersect(((Expression)iter.next()).getAccessibilityDomain());
//    }
//    return result;
//  }

	/**
	 * TARGET
	 * 
	 */
	private Reference<Invocation,InvocationTarget> _target = new Reference<Invocation,InvocationTarget>(this);


  public InvocationTarget getTarget() {
    return _target.getOtherEnd();
  }

  public void setTarget(InvocationTarget target) {
    if (target != null) {
      _target.connectTo(target.parentLink());
    }
    else {
      _target.connectTo(null);
    }
  }

	/**
	 * ACTUAL PARAMETERS
	 */
 private Reference<Invocation,ActualArgumentList> _parameters = new Reference<Invocation,ActualArgumentList>(this);
 
 public ActualArgumentList actualArgumentList() {
	 return _parameters.getOtherEnd();
 }

  public void addArgument(ActualArgument parameter) {
  	actualArgumentList().addParameter(parameter);
  }
  
  public void addAllArguments(List<ActualArgument> parameters) {
  	for(ActualArgument parameter: parameters) {
  		addArgument(parameter);
  	}
  }

  public void removeParameter(ActualArgument parameter) {
  	actualArgumentList().removeParameter(parameter);
  }

  public List<ActualArgument> getActualParameters() {
    return actualArgumentList().getActualParameters();
  }

 /*@
   @ public behavior
   @
   @ post \result == getActualParameters().size;
   @*/
  public int nbActualParameters() {
  	return actualArgumentList().nbActualParameters();
  }
  
  public List<Type> getActualParameterTypes() throws LookupException {
	    List<ActualArgument> params = getActualParameters();
	    final List<Type> result = new ArrayList();
	    for(ActualArgument param:params) {
        Type type = param.getType();
        if (type != null) {
      	  result.add(type);
        }
        else {
          //Type ttt = ((ActualParameter)param).getType(); //DEBUG
          throw new LookupException("Cannot determine type of expression");
        }
	    }
	    return result;
	}

 /*@
   @ also public behavior 
   @
   @ post \result.containsAll(getMethod().getExceptionClause().getExceptionTypes(this));
   @ post (getLanguage().getUncheckedException(getPackage().getDefaultPackage()) != null) ==>
   @      result.contains(getLanguage().getUncheckedException(getPackage().getDefaultPackage());
   @*/  
  public Set getMethodExceptions() throws LookupException {
    Set result = getMethod().getExceptionClause().getExceptionTypes(this);
    Type rte = language().getUncheckedException();
    if (rte != null) {
      result.add(rte);
    }
    return result;
  }
  
 /*@
   @ also public behavior 
   @
   @ post \result.containsAll(getMethodExceptions());
   @ post (getLanguage().getNullInvocationException(getPackage().getDefaultPackage()) != null) ==>
   @      result.contains(getLanguage().getNullInvocationException(getPackage().getDefaultPackage());
   @*/  
  public Set getDirectExceptions() throws LookupException {
    Set result = getMethodExceptions();
    if(getTarget() != null) {
      Util.addNonNull(language().getNullInvocationException(), result);
    }
    return result;
  }
  
 /*@
   @ also public behavior
   @
   @ post \result.contains(actualArgumentList());
   @ post getTarget() != null ==> \result.contains(getTarget());
   @*/  
  public List<Element> children() {
    List<Element> result = new ArrayList<Element>();
    result.add(actualArgumentList());
    Util.addNonNull(getTarget(), result);
    return result;
  }

	//  public Set getDirectExceptions() throws NotResolvedException {
	//    Set result = getMethodExceptions();
	//    Type npe = getLanguage().getNullInvocationException(getPackage().getDefaultPackage());
	//    if(npe != null) {
	//      result.add(npe);
	//    }
	//    result.addAll(getTarget().getDirectExceptions());
	//    Iterator iter = getActualParameters().iterator();
	//    while(iter.hasNext()) {
	//      result.addAll(((Expression)iter.next()).getDirectExceptions());
	//    }
	//    return result;
	//  }

	/**
	 * Return the method invoked by this invocation.
	 * 
	 * @uml.property name="method"
	 * @uml.associationEnd 
	 * @uml.property name="method" multiplicity="(0 1)"
	 */
	/*@
	 @ public behavior
	 @
	 @ post \result != null;
	 @
	 @ signals (NotResolvedException) (* The method could not be found *);
	 @*/
//	public abstract D getMethod() throws MetamodelException;
  public D getMethod() throws LookupException {
  	InvocationTarget target = getTarget();
  	D result;
  	if(target == null) {
      result = lexicalContext().lookUp(selector());
  	} else {
  		result = getTarget().targetContext().lookUp(selector());
  	}
		if (result == null) {
			//repeat lookup for debugging purposes.
	  	if(target == null) {
	      result = lexicalContext().lookUp(selector());
	  	} else {
	  		result = getTarget().targetContext().lookUp(selector());
	  	}
			throw new LookupException("Method returned by invocation is null", this);
		}
    return result;
}

  public D getElement() throws LookupException {
  	return getMethod();
  }
  
  /**
   * Return a clone of this invocation without target or parameters.
   */
 /*@
   @ protected behavior
   @
   @ post \result != null;
   @*/
  protected abstract E cloneInvocation(InvocationTarget target);
  
  public E clone() {
    InvocationTarget target = null;
    if(getTarget() != null) {
      target = getTarget().clone();
    }
    final E result = cloneInvocation(target);
    new Visitor() {
      public void visit(Object element) {
        result.addArgument(((ActualArgument)element).clone());
      }
    }.applyTo(getActualParameters());
    return result;
  }

//  public void substituteParameter(String name, Expression expr) throws MetamodelException {
//    if(getTarget()!= null) {
//      getTarget().substituteParameter(name, expr);
//    }
//  }
  
  public CheckedExceptionList getDirectCEL() throws LookupException {
    throw new Error();
  }
  
  public CheckedExceptionList getDirectAbsCEL() throws LookupException {
    throw new Error();
  }
}
