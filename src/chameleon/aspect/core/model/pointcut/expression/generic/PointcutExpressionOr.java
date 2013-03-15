package chameleon.aspect.core.model.pointcut.expression.generic;

import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;
import chameleon.aspect.core.model.pointcut.expression.MatchResult;
import chameleon.aspect.core.model.pointcut.expression.PointcutExpression;
import chameleon.aspect.oo.model.pointcut.ParameterExposurePointcutExpression;
import chameleon.aspect.oo.model.pointcut.RuntimePointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public class PointcutExpressionOr extends PointcutExpressionDual<Element> implements RuntimePointcutExpression<Element> {

	public PointcutExpressionOr(PointcutExpression expression1, PointcutExpression expression2) {
		super(expression1, expression2);
	}

	/**
	 * 	{@inheritDoc}
	 * 
	 * 	We can't just return a single expression if it matches - e.g. this example:
 	 * 	(callAnnotated(Deprecated) && if(false) ) || (call(void hrm.Person.doubleTest()) && if(true)) 
	 * 
	 *  Suppose both static pointcut expressions match and we only return the first one - this will cause the weaver not to weave, which is wrong		
	 */
	@Override
	public MatchResult match(Element joinpoint) throws LookupException {
		MatchResult r1 = MatchResult.noMatch();
		MatchResult r2 = MatchResult.noMatch();

		if(expression1() instanceof RuntimePointcutExpression) {
			r1 = new MatchResult(expression1(),joinpoint);
		} else {
		  r1 = expression1().matches(joinpoint);
		}
		if(expression2() instanceof RuntimePointcutExpression) {
			r2 = new MatchResult(expression2(),joinpoint);
		} else {
	    r2 = expression2().matches(joinpoint);
		}
		
		MatchResult result = MatchResult.noMatch();
		if (r1.isMatch() && r2.isMatch()) {
			result = new MatchResult<Element>(this, joinpoint);
		}	else if (r1.isMatch()) {
			result = r1;
		} else if (r2.isMatch()) {
			result = r2;
		}
		return result;
	}

	@Override
	public PointcutExpressionOr clone() {
		return new PointcutExpressionOr(expression1().clone(), expression2().clone());
	}

	@Override
	public Class<? extends Element> joinPointType() throws LookupException {
		return Element.class;
	}

	/**
	 * 	{@inheritDoc}
	 */	
	@Override
	public PointcutExpression<?> without(SafePredicate<PointcutExpression<?>> filter) {	
		PointcutExpression<?> left = expression1().without(filter);
		PointcutExpression<?> right = expression2().without(filter);
		if (left == null && right == null)
			return null;
		if (left == null && right != null)
			return right;
		if (left != null && right == null)
			return left;
		
		return new PointcutExpressionOr(left, right);
	}
}