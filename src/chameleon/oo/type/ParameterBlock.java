package chameleon.oo.type;

import java.util.List;

import org.rejuse.association.Association;

import chameleon.core.declaration.Declaration;
import chameleon.core.element.ElementImpl;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.util.association.Multi;

public class ParameterBlock<T extends Parameter> extends ElementImpl {

	public ParameterBlock(Class<T> parameterType) {
		_parameterType = parameterType;
	}
	
	public Class<T> parameterType() {
		return _parameterType;
	}
	
	private Class<T> _parameterType;
	
	private Multi<T> _parameters = new Multi<T>(this);
	
	public List<T> parameters() {
		return _parameters.getOtherEnds();
	}
	
	public int nbTypeParameters() {
		return _parameters.size();
	}
	
	/**
	 * Indices start at 1.
	 */
	public T parameter(int index) {
		return _parameters.elementAt(index);
	}
	
	public void add(T parameter) {
		add(_parameters,parameter);
	}

	public void remove(T parameter) {
		remove(_parameters,parameter);
	}
	
	public void replace(T oldParameter, T newParameter) {
		if((oldParameter != null) && (newParameter != null)){
			_parameters.replace((Association)oldParameter.parentLink(), (Association)newParameter.parentLink());
		}
	}

	@Override
	public ParameterBlock<T> clone() {
		ParameterBlock<T> result = cloneThis();
		for(T parameter: parameters()) {
			result.add((T)parameter.clone());
		}
		return result;
	}

	public ParameterBlock<T> cloneThis() {
		return new ParameterBlock<T>(parameterType());
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

	public List<? extends Declaration> declarations() throws LookupException {
		return parameters();
	}

	public <D extends Declaration> List<D> declarations(DeclarationSelector<D> selector) throws LookupException {
		return selector.selection(declarations());
	}
	
}
