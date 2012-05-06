package chameleon.util.association;

import java.util.Collections;
import java.util.List;

import org.rejuse.association.Association;
import org.rejuse.association.OrderedMultiAssociation;

import chameleon.core.element.Element;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;

public class Multi<T extends Element> extends OrderedMultiAssociation<Element, T> implements ChameleonAssociation<T> {

	public Multi(Element element) {
		super(element);
		_max = -1;
	}
	
	public Multi(Element element, int min, int max) {
		this(element);
		if(min > max) {
			throw new IllegalArgumentException();
		}
		_min=min;
		_max=max;
	}
	
	public Multi(Element element, String role) {
		this(element);
		setRole(role);
	}
	
	public Multi(Element element, int min, int max, String role) {
		this(element,min,max);
		setRole(role);
	}
	
	public int min() {
		return _min;
	}
	
	public int max() {
		return _max;
	}
	
	private int _min;
	private int _max;
	
	public boolean isConstrained() {
		return _max < 1;
	}
	
	public VerificationResult verify() {
		VerificationResult result = Valid.create();
		if(isConstrained()) {
			int size = size();
			if(size < min()) {
				result = result.and(new BasicProblem(getObject(), "At least " + min()+" " + role() + " were expected, but only "+size+ " are defined."));
			}
			if(size > max()) {
				result = result.and(new BasicProblem(getObject(), "At most " + max()+" " + role() +" were expected, but "+size+ " are defined."));
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
	
	private String _role = "elements";

	protected List<T> explicitElements() {
		return super.getOtherEnds();
	}
	
	protected List<T> implicitElements() {
		return Collections.EMPTY_LIST;
	}
	
	@Override
	public List<T> getOtherEnds() {
		List<T> result = explicitElements();
		result.addAll(implicitElements());
		return result;
	}
	
	@Override
	public void cloneTo(ChameleonAssociation<T> o) {
		Multi<T> other = (Multi<T>) o;
		for(T t: explicitElements()) {
			other.add((Association)t.clone().parentLink());
		}
	}

}
