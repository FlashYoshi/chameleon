package chameleon.util.association;

import org.rejuse.association.Association;
import org.rejuse.association.SingleAssociation;

import chameleon.core.element.Element;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;

public class Single<T extends Element> extends SingleAssociation<Element, T> {

	public Single(Element element) {
		super(element);
	}

	public Single(Element element, Association<? extends T, ? super Element> other) {
		super(element, other);
	}
	
	public Single(Element element, boolean mandatory) {
		this(element);
		_mandatory = mandatory;
	}

	public Single(Element element, Association<? extends T, ? super Element> other, boolean mandatory) {
		this(element, other);
		_mandatory = mandatory;
	}
	
	private boolean _mandatory = false;
	
	public boolean mandatory() {
		return _mandatory;
	}
	
	public VerificationResult verify() {
		VerificationResult result = Valid.create();
		if(mandatory()) {
			if(mandatory() && getOtherEnd() == null) {
				result = result.and(new BasicProblem(getObject(), "One " + role() + " was expected, but none is defined."));
			}
		}
		return result;
	}

	public String role() {
		return _role;
	}
	
	public void setRole(String role) {
		_role = role;
	}
	
	private String _role = "element";
	
	@Override
	public T getOtherEnd() {
		T result = super.getOtherEnd();
		if(result == null) {
			result = implicitElement();
		}
		return result;
	}
	
	/**
	 * Return the element that is implicitly connected via this association. The
	 * result should only be non-null when there is no explicitly connected element.
	 * @return
	 */
	public T implicitElement() {
		return null;
	}

	/**
	 * Copy the contents of this association to the given other association.
	 * @param other
	 */
	public void cloneTo(Single<T> other) {
		T otherEnd = getOtherEnd();
		if(otherEnd != null) {
			other.connectTo((Association) otherEnd.clone().parentLink());
		}
	}
	
}
