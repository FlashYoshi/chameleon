package chameleon.aspect.oo.model.pointcut;

import java.util.Collections;
import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;

public class SubtypeMarker extends NamespaceElementImpl {

	@Override
	public List<? extends Element> children() {
		return Collections.emptyList();
	}

	@Override
	public SubtypeMarker clone() {
		return new SubtypeMarker();
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

}
