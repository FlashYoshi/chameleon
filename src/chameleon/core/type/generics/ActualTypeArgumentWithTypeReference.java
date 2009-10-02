package chameleon.core.type.generics;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.core.element.Element;
import chameleon.core.type.TypeReference;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;


public abstract class ActualTypeArgumentWithTypeReference<E extends ActualTypeArgumentWithTypeReference> extends ActualTypeArgument<E> {

	public ActualTypeArgumentWithTypeReference(TypeReference ref) {
		setTypeReference(ref);
	}
	
	public List<? extends Element> children() {
		List<Element> result = new ArrayList<Element>();
		result.add(typeReference());
		return result;
	}

	public TypeReference typeReference() {
		return _type.getOtherEnd();
	}

	public void setTypeReference(TypeReference ref) {
		if(ref == null) {
			_type.connectTo(null);
		} else {
			_type.connectTo(ref.parentLink());
		}
	}

	private SingleAssociation<ActualTypeArgument,TypeReference> _type = new SingleAssociation<ActualTypeArgument,TypeReference>(this);

	@Override
	public VerificationResult verifySelf() {
		if(typeReference() != null) {
		  return Valid.create();
		} else {
			return new MissingTypeReference(this);
		}
	}

	public static class MissingTypeReference extends BasicProblem {

		public MissingTypeReference(Element element) {
			super(element, "Missing type reference");
		}
		
	}

}
