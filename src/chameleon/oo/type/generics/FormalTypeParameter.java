package chameleon.oo.type.generics;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.OrderedMultiAssociation;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.ConstructedType;
import chameleon.oo.type.IntersectionTypeReference;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.util.CreationStackTrace;
import chameleon.util.Pair;

/**
 * This class represents generic parameters as used in Java and C#.
 * 
 * @author Marko van Dooren
 */
public class FormalTypeParameter extends TypeParameter<FormalTypeParameter> {

	public FormalTypeParameter(SimpleNameSignature signature) {
		super(signature);
	}
	
  
	@Override
	public FormalTypeParameter clone() {
		FormalTypeParameter result = new FormalTypeParameter(signature().clone());
		for(TypeConstraint constraint: constraints()) {
			result.addConstraint(constraint.clone());
		}
		return result;
	}
	
	/**
	 * Resolving a generic parameter results in a constructed type whose bound
	 * is the upper bound of this generic parameter as defined by the upperBound method.
	 */
	public Type selectionDeclaration() throws LookupException {
//		String x = nearestAncestor(Type.class).getFullyQualifiedName()+"."+signature();
//		if(x.equals("org.rejuse.property.Property.E")) {
//			System.out.println("Selection declaration of " + x);
//		}
		Type constructedType = createSelectionType();
		constructedType.setUniParent(parent());
		return constructedType;
	}


	protected Type createSelectionType() throws LookupException {
		return new ConstructedType(signature().clone(),upperBound(),this);
	}
	
	public Type resolveForRoundTrip() throws LookupException {
  	Type result = createLazyAlias();
  	result.setUniParent(parent());
  	return result;
	}


	protected Type createLazyAlias() {
		return new LazyTypeAlias(signature().clone(), this);
	}
	
	public List<Element> children() {
		List<Element> result = new ArrayList<Element>();
		result.add(signature());
		result.addAll(constraints());
		return result;
	}
	
	private OrderedMultiAssociation<FormalTypeParameter,TypeConstraint> _typeConstraints = new OrderedMultiAssociation<FormalTypeParameter,TypeConstraint>(this);
	
	public List<TypeConstraint> constraints() {
		return _typeConstraints.getOtherEnds();
	}
	
	public void addConstraint(TypeConstraint constraint) {
		if(constraint != null) {
			_typeConstraints.add(constraint.parentLink());
		}
	}
	
	public TypeReference upperBoundReference() {
		List<TypeConstraint> constraints = constraints();
		int size = constraints.size();
		TypeReference result;
		ObjectOrientedLanguage language = language(ObjectOrientedLanguage.class);
		if(size == 0) {
		  result = language.createTypeReferenceInDefaultNamespace(language.getDefaultSuperClassFQN());
		} else if(size == 1) {
			result = constraints.get(0).upperBoundReference();
		} else {
			result = language.createIntersectionReference(constraints.get(0).upperBoundReference(), constraints.get(1).upperBoundReference());
			for(int i=2; i<size;i++) {
				((IntersectionTypeReference)result).add(constraints.get(i).upperBoundReference());
			}
			result.setUniParent(this);
		}
		return result;
	}

	public Type upperBound() throws LookupException {
		List<TypeConstraint> constraints = constraints();
		Type result;
		int size = constraints.size();
		if(size == 0) {
			result = language(ObjectOrientedLanguage.class).getDefaultSuperClass();
		} else {
			result = constraints.get(0).upperBound();
			for(int i = 1; i < size; i++) {
				result = result.intersection(constraints.get(i).upperBound());
			}
		}
		return result;
	}

	public FormalTypeParameter alias(SimpleNameSignature signature) {
		throw new ChameleonProgrammerException();
	}


//	@Override
//	public boolean compatibleWith(TypeParameter other) throws LookupException {
//		return equals(other);
//	}


	@Override
	public Type lowerBound() throws LookupException {
		List<TypeConstraint> constraints = constraints();
		Type result;
		int size = constraints.size();
		if(size == 0) {
			result = language(ObjectOrientedLanguage.class).getNullType();
		} else {
			result = constraints.get(0).lowerBound();
			for(int i = 1; i < size; i++) {
				result = result.union(constraints.get(i).lowerBound());
			}
		}
		return result;
	}


	@Override
	public boolean uniSameAs(Element other) throws LookupException {
		if(origin() == this) {
			if(other == other.origin()) {
			  return this == other;
			} else {
				return uniSameAs(other.origin());
			}
		} else {
			return origin().sameAs(other);
		}
//		boolean result = false;
//		if(other instanceof FormalTypeParameter) {
//			result = signature().sameAs(((FormalTypeParameter) other).signature());
//			if(result) {
//				Element parent = nearestAncestor(TypeParameterBlock.class).parent();
//				Element otherParent = other.nearestAncestor(TypeParameterBlock.class).parent();
//				result = parent.sameAs(otherParent);
//			}
//		}
//		return result;
	}

  @Override
  public int hashCode() {
		if(origin() == this) {
			return super.hashCode();
		} else {
			return origin().hashCode();
		}

  }


	@Override
	public boolean sameValueAs(TypeParameter otherParam) throws LookupException {
		return sameAs(otherParam);
	}


	@Override
	public boolean sameValueAs(TypeParameter otherParam, List<Pair<TypeParameter, TypeParameter>> trace) throws LookupException {
		return sameAs(otherParam);
	}
}
