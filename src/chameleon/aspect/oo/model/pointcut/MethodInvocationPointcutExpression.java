package chameleon.aspect.oo.model.pointcut;

import chameleon.aspect.core.model.pointcut.expression.AbstractPointcutExpression;
import chameleon.core.lookup.LookupException;
import chameleon.oo.expression.MethodInvocation;

public abstract class MethodInvocationPointcutExpression<E extends MethodInvocationPointcutExpression<E>> extends AbstractPointcutExpression<E,MethodInvocation> {

	@Override
	public Class<? extends MethodInvocation> joinPointType() throws LookupException {
		return MethodInvocation.class;
	}
}
