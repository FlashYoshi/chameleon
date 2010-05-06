package chameleon.oo.type.generics;

import org.rejuse.association.SingleAssociation;

import chameleon.core.element.Element;
import chameleon.core.element.ElementImpl;
import chameleon.core.lookup.LookupException;
import chameleon.exception.ModelException;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;

public abstract class TypeConstraint<E extends TypeConstraint> extends ElementImpl<E,Element> {

	public TypeConstraint() {
//		setTypeReference(typeRef);
	}
	
//	public void setTypeReference(TypeReference typeRef) {
//		if(typeRef != null) {
//			_typeRef.connectTo(typeRef.parentLink());
//		} else {
//			_typeRef.connectTo(null);
//		}
//	}
//	
//	public TypeReference typeReference() {
//		return _typeRef.getOtherEnd();
//	}
//	
//	public Type type() throws MetamodelException {
//		return typeReference().getType();
//	}
	
	public abstract boolean matches(Type type) throws LookupException;
	
	@Override
	public abstract E clone();

//	public List<? extends Element> children() {
//		List<Element> result = new ArrayList<Element>();
//		result.add(typeReference());
//		return result;
//	}

	/**
	 * Return the upper bound on the type that this type constraint imposes.
	 * 
	 * @return
	 * @throws LookupException 
	 */
	public abstract Type upperBound() throws LookupException;
	
	public abstract TypeReference upperBoundReference();
	
	/**
	 * Return the lower bound on the type that this type constraint imposes.
	 * 
	 * @return
	 * @throws LookupException 
	 */
	public abstract Type lowerBound() throws LookupException;
	
}
