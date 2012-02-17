package chameleon.aspect.oo.model.pointcut;

import java.util.List;
import java.util.Map;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.oo.expression.NamedTargetExpression;
import chameleon.oo.variable.FormalParameter;

public abstract class AbstractParameterExposurePointcutExpression extends AbstractDynamicPointcutExpression implements ParameterExposurePointcutExpression<Element> {
	
	@Override
	public boolean hasParameter(FormalParameter fp) {
		return indexOfParameter(fp) != -1;
	}

	public int indexOfParameter(FormalParameter fp) {
		if (parameters() == null)
			return -1;	
		
		for (int i = 0; i < parameters().size(); i++) {
			try {
				if (parameters().get(i).getElement() instanceof FormalParameter) {
					FormalParameter param = (FormalParameter) parameters().get(i).getElement();
					
					if (param.signature().name().equals(fp.signature().name()) && param.getType().sameAs(fp.getType()))
						return i;
				}
			} catch (LookupException e) {
				// Ignore
			}
		}
		
		return -1;
	}

//	@Override
//	public ParameterExposurePointcutExpression<?,?> findExpressionFor(FormalParameter fp) {
//		if (hasParameter(fp))
//			return this;
//		
//		return null;
//	}

	@Override
	public void renameParameters(Map<String, String> parameterNamesMap) {
		for (NamedTargetExpression fp : parameters())
			fp.setName(parameterNamesMap.get(fp.name()));
	}
	
	public abstract List<NamedTargetExpression> parameters();
				
	@Override
	public Class<? extends Element> joinPointType() throws LookupException {
		return Element.class;
	}
}
