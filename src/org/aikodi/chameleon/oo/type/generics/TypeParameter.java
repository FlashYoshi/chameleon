package org.aikodi.chameleon.oo.type.generics;

import java.util.ArrayList;
import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.declaration.MissingSignature;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LocalLookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.scope.LexicalScope;
import org.aikodi.chameleon.core.scope.Scope;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.exception.ModelException;
import org.aikodi.chameleon.oo.type.Parameter;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeFixer;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.util.Pair;

/**
 * A class representing type parameters. These can be formal type parameters or instantiated type parameters.
 * 
 * @author Marko van Dooren
 *
 * @param <E>
 */
public abstract class TypeParameter extends Parameter implements ElementWithTypeBounds {
	
	public TypeParameter(String name) {
		super(name);
	}
	
	/**
	 * Check whether this type parameter is equal to the other type parameter. 
	 * 
	 * Equality for type parameters means that for each assignment of actual types, both parameters
	 * must refer to the same type. 
	 */
  @Override
	public abstract boolean uniSameAs(Element other) throws LookupException;

  @Override
public Type actualDeclaration() throws LookupException {
  	throw new ChameleonProgrammerException();
  }

	public abstract Declaration resolveForRoundTrip() throws LookupException;

//	public final TypeParameter cloneForStub() throws LookupException {
//		return (TypeParameter) clone();
//	}
//	
	public boolean compatibleWith(TypeParameter other,TypeFixer trace) throws LookupException {
		TypeFixer slowTrace = trace.clone();
		boolean result = sameAs(other);
		if(! result) {
			final Type upperBound = upperBound();
      final Type otherUpperBound = other.upperBound();
      result = upperBound.upperBoundNotHigherThan(otherUpperBound,slowTrace);
			if(result) {
				final Type otherLowerBound = other.lowerBound();
        final Type lowerBound = lowerBound();
        result = otherLowerBound.upperBoundNotHigherThan(lowerBound,slowTrace);
			}
		}
		return result;
	}

	public abstract TypeReference upperBoundReference() throws LookupException;
	
	@Override
   public Scope scope() throws ModelException {
		return new LexicalScope(nearestAncestor(Type.class));
	}

	@Override
	public Verification verifySelf() {
		if(name() != null) {
		  return Valid.create();
		} else {
			return new MissingSignature(this); 
		}
	}

	public abstract boolean sameValueAs(TypeParameter otherParam, List<Pair<TypeParameter, TypeParameter>> trace) throws LookupException;

	@Override
   public String toString() {
		return name();
	}

  @Override
public LocalLookupContext<?> targetContext() throws LookupException {
  	return upperBound().targetContext();
  }

  @Override
  public abstract Type selectionDeclaration() throws LookupException;
  
}
