package chameleon.oo.type.generics;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.MissingSignature;
import chameleon.core.declaration.Signature;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.scope.LexicalScope;
import chameleon.core.scope.Scope;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.exception.ModelException;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.util.Pair;

/**
 * A class representing type parameters. These can be formal type parameters or instantiated type parameters.
 * 
 * @author Marko van Dooren
 *
 * @param <E>
 */
public abstract class TypeParameter<E extends TypeParameter<E>> extends NamespaceElementImpl<E, Element> implements Declaration<E,Element,SimpleNameSignature,Type>{
	
	public TypeParameter(SimpleNameSignature signature) {
		setSignature(signature);
	}

	public abstract Type selectionDeclaration() throws LookupException;
	
	public abstract E clone();
	
	/**
	 * Check whether this type parameter is equal to the other type parameter. 
	 * 
	 * Equality for type parameters means that for each assignment of actual types, both parameters
	 * must refer to the same type. 
	 */
  @Override
	public abstract boolean uniSameAs(Element other) throws LookupException;

	public void setSignature(Signature signature) {
  	if(signature instanceof SimpleNameSignature) {
  		if(signature != null) {
  			_signature.connectTo(signature.parentLink());
  		} else {
  			_signature.connectTo(null);
  		}
  	} else {
  		throw new ChameleonProgrammerException("Setting wrong type of signature. Provided: "+(signature == null ? null :signature.getClass().getName())+" Expected SimpleNameSignature");
  	}
  }
  
  /**
   * Return the signature of this member.
   */
  public SimpleNameSignature signature() {
    return _signature.getOtherEnd();
  }
  
  private SingleAssociation<TypeParameter, SimpleNameSignature> _signature = new SingleAssociation<TypeParameter, SimpleNameSignature>(this);

  public Type actualDeclaration() throws LookupException {
  	throw new ChameleonProgrammerException();
  }


//	public boolean compatibleWith(TypeParameter other) throws LookupException {
//		Type myType = nearestAncestor(Type.class);
//		Type otherType = (Type) other.nearestAncestor(Type.class);
//		String x = myType.getFullyQualifiedName()+"."+signature().name();
//		String y = otherType.getFullyQualifiedName() + "." + other.signature().name();
//		if(x.equals("chameleon.core.declaration.Signature.E")) {
//			System.out.println("Hebbes");
//		}
//		return sameAs(other) || upperBound().subTypeOf(other.upperBound()) && other.lowerBound().subTypeOf(lowerBound());
//	}
//	
  
  private static int count;
	public boolean compatibleWith(TypeParameter other,List<Pair<Type, TypeParameter>> trace) throws LookupException {
		List<Pair<Type, TypeParameter>> slowTrace = new ArrayList<Pair<Type, TypeParameter>>(trace);
//		List<Pair<Type, TypeParameter>> slowTrace = trace;
		boolean result = sameAs(other);
		if(! result) {
		 result = upperBound().upperBoundNotHigherThan(other.upperBound(),slowTrace);
		 if(result) {
			 result = other.lowerBound().upperBoundNotHigherThan(lowerBound(),slowTrace);
		 }
		}
		return result;
	}

	public boolean canBeAssigned(ActualTypeArgument typeArgument) throws LookupException {
		return lowerBound().subTypeOf(typeArgument.lowerBound()) && typeArgument.upperBound().subTypeOf(upperBound());
	}
	
	public abstract Type upperBound() throws LookupException;
	
	public abstract TypeReference upperBoundReference() throws LookupException;
	
	public abstract Type lowerBound() throws LookupException;

	public Scope scope() throws ModelException {
		return new LexicalScope(nearestAncestor(Type.class));
	}

	public abstract Declaration resolveForRoundTrip() throws LookupException;

	@Override
	public VerificationResult verifySelf() {
		if(signature() != null) {
		  return Valid.create();
		} else {
			return new MissingSignature(this); 
		}
	}

	public abstract boolean sameValueAs(TypeParameter otherParam) throws LookupException;

	public abstract boolean sameValueAs(TypeParameter otherParam, List<Pair<TypeParameter, TypeParameter>> trace) throws LookupException;

  public Declaration declarator() {
  	return this;
  }
}
