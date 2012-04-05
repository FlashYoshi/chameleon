package chameleon.aspect.oo.model.pointcut.method;

import java.util.Collections;
import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.element.ElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;

public class AnnotationReference extends ElementImpl {
	
	private String referencedName;
	
	public AnnotationReference(String reference) {
		setReferencendName(reference);
	}
	
	public String referencendName() {
		return referencedName;
	}

	private void setReferencendName(String reference) {
		this.referencedName = reference;
	}

	@Override
	public AnnotationReference clone() {
		return new AnnotationReference(referencendName());
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}
}