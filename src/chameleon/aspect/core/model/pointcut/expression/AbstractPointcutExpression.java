package chameleon.aspect.core.model.pointcut.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rejuse.predicate.Not;
import org.rejuse.predicate.SafePredicate;
import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.variable.FormalParameter;

/**
 * 	An abstract class with a default implementation for most methods of a pointcut expression.
 * 
 * 	@author Jens De Temmerman
 *  @author Marko van Dooren
 *
 * 	@param <E>
 * 	@param <T>
 */
public abstract class AbstractPointcutExpression<E extends AbstractPointcutExpression<E,J>,J extends Element> extends NamespaceElementImpl<E> implements PointcutExpression<E,J> {
	/**
	 *  {@inheritDoc}
	 */
	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}
	
	/**
	 *  {@inheritDoc}
	 */
	@Override
	public abstract E clone();
		
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public final PointcutExpression<?,?> retainOnly(final SafePredicate<PointcutExpression<?,?>> filter) {
		return without(new SafePredicate<PointcutExpression<?,?>>() {

			@Override
			public boolean eval(PointcutExpression<?,?> object) {
				return ! filter.eval(object);
			}
		});
	}

	/**
	 *	{@inheritDoc}
	 */
	@Override
	public PointcutExpression<?,?> without(final Class<? extends PointcutExpression> type) {	
		SafePredicate<PointcutExpression<?,?>> filter = new SafePredicate<PointcutExpression<?,?>>() {
			@Override
			public boolean eval(PointcutExpression<?,?> object) {
				return type.isInstance(object);
			}
		};
		return without(filter);
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public PointcutExpression<?,?> without(SafePredicate<PointcutExpression<?,?>> filter) {
		PointcutExpression<?,?> result = null;
		if (!filter.eval(this)) {
			result = clone();
			result.setOrigin(origin());
		}
		return result;
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public List<PointcutExpression<?,?>> toPostorderList() {
		return Collections.<PointcutExpression<?,?>>singletonList(this);
	}
	
//	/**
//	 *	{@inheritDoc}
//	 */
//	@Override
//	public boolean hasParameter(FormalParameter fp) {
//		return false;
//	}
	
	/**
	 *	{@inheritDoc}
	 */
	@Override
	public final List<MatchResult> joinpoints(CompilationUnit compilationUnit) throws LookupException {
		List<MatchResult> result = new ArrayList<MatchResult>();
		List<? extends Element> joinPoints = compilationUnit.descendants(joinPointType());
		for (Element joinPoint : joinPoints) {
			MatchResult match = matches(joinPoint);
			if (match.isMatch()) {
				result.add(match);
			}
		}
		return result; 
	}
	
	@Override
	public final MatchResult matches(Element<?> joinpoint) throws LookupException {
		if(joinPointType().isInstance(joinpoint)) {
			return match((J)joinpoint);
		} else {
			return MatchResult.noMatch();
		}
	}
	
	protected abstract MatchResult match(J joinpoint) throws LookupException;
}