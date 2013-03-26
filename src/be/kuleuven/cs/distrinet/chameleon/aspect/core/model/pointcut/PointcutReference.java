package be.kuleuven.cs.distrinet.chameleon.aspect.core.model.pointcut;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationSelector;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupStrategy;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceWithArguments;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceTarget;
import be.kuleuven.cs.distrinet.chameleon.core.validation.VerificationResult;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.NamedTargetExpression;
import be.kuleuven.cs.distrinet.chameleon.oo.lookup.SimpleNameCrossReferenceWithArgumentsSelector;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.FormalParameter;

public class PointcutReference extends CrossReferenceWithArguments implements CrossReference<Pointcut> {

	private String _pointcutName;

	public String name() {
		return _pointcutName;
	}

	public void setName(String method) {
		_pointcutName = method;
	}

	public PointcutReference(String name) {
		setName(name);
	}

	@Override
	public Pointcut getElement() throws LookupException {
		return (Pointcut) super.getElement();
	}
	
	@Override
	public DeclarationSelector<Declaration> selector() throws LookupException {
		return new SimpleNamePointcutSelector();
	}

	public class SimpleNamePointcutSelector<D extends Pointcut> extends SimpleNameCrossReferenceWithArgumentsSelector<D> {

		@Override
		public String name() {
			return PointcutReference.this.name();
		}

		@Override
		public int nbActualParameters() {
			return PointcutReference.this.nbActualParameters();
		}

		@Override
		public List<Type> getActualParameterTypes() throws LookupException {
			return PointcutReference.this.getActualParameterTypes();
		}

		@Override
		public Class<D> selectedClass() {
			return (Class<D>) Pointcut.class;
		}
		
		
	}


	@Override
	public VerificationResult verifySelf() {
		return super.verifySelf();
	}
	

	public boolean hasParameter(FormalParameter fp) {
		return indexOfParameter(fp) != -1;
	}

	public int indexOfParameter(FormalParameter fp) {
		int index = 0;
		
		for (Expression param : getActualParameters()) {
			if (!(param instanceof NamedTargetExpression))
				continue;
			
			if (((NamedTargetExpression) param).name().equals(fp.getName()))
				return index;
			
			index++;
		}
			
		
		return -1;
	}

	@Override
	public PointcutReference clone() {
		CrossReferenceTarget target = null;
		if (getTarget() != null) {
			target = getTarget().clone();
		}
		PointcutReference result = new PointcutReference(name());
		result.setTarget(target);
		for (Expression element : getActualParameters()) {
			result.addArgument(element.clone());
		}
		for (ActualTypeArgument arg : typeArguments()) {
			result.addArgument(arg.clone());
		}
		return result;
	}

	@Override
	public LookupStrategy targetContext() throws LookupException {
		return getElement().targetContext();
	}
}
