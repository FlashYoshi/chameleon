package chameleon.aspect.oo.weave.infrastructure;

import chameleon.core.lookup.LookupException;
import chameleon.oo.statement.Block;
import chameleon.oo.statement.Statement;

public class AfterExpression extends AdvisedExpressionFactory {

	public AfterExpression(ExpressionInfrastructureFactory factory) {
		super(factory);
	}

	public Block createBody() throws LookupException {
		Block innerAdviceBody = new Block();
		/*
		 * 	Add the return statement
		 */
		innerAdviceBody.addStatement(ooFactory().createReturn(factory().getNextExpression()));

		/*
		 * 	Wrap that in a try { } finally { } statement
		 */
		Block adviceBody = new Block();
		adviceBody.addStatement(createTryStatement(innerAdviceBody));

		return adviceBody;
	}

	protected Statement createTryStatement(Block tryBody) throws LookupException {
		return ooFactory().createTryFinally(tryBody, factory().getAdvice().body().clone());
	}

}
