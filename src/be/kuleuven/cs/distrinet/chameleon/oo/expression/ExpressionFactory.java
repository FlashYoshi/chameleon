package be.kuleuven.cs.distrinet.chameleon.oo.expression;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceTarget;
import be.kuleuven.cs.distrinet.chameleon.core.reference.SimpleReference;
import be.kuleuven.cs.distrinet.chameleon.plugin.LanguagePluginImpl;
import be.kuleuven.cs.distrinet.chameleon.support.expression.ConditionalExpression;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.RegularMethodInvocation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.infix.InfixOperatorInvocation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.postfix.PostfixOperatorInvocation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.prefix.PrefixOperatorInvocation;

public class ExpressionFactory extends LanguagePluginImpl {

	public Expression createConditionalExpression(Expression condition, Expression firstOperand, Expression secondOperand) {
		return new ConditionalExpression(condition, firstOperand, secondOperand);
	}

	@Override
	public ExpressionFactory clone() {
		return new ExpressionFactory();
	}

	/**
	 * Create a new invocation based on the name of the invoked method and the target.
	 *  
	 * @param name
	 * @param target
	 * @return
	 */
	public MethodInvocation createInvocation(String name, CrossReferenceTarget target) {
		return new RegularMethodInvocation(name, target);
	}
	
	public InfixOperatorInvocation createInfixOperatorInvocation(String name, CrossReferenceTarget target) {
		return new InfixOperatorInvocation(name, target);
	}

	public PrefixOperatorInvocation createPrefixOperatorInvocation(String name, CrossReferenceTarget target) {
		return new PrefixOperatorInvocation(name, target);
	}

	public PostfixOperatorInvocation createPostfixOperatorInvocation(String name, CrossReferenceTarget target) {
		return new PostfixOperatorInvocation(name, target);
	}

	public <D extends Declaration> SimpleReference<D> createSimpleReference(String fqn,Class<D> kind) {
		return new SimpleReference<>(fqn, kind);
	}

	public NamedTarget createNamedTarget(String fqn) {
		return new NamedTarget(fqn);
	}
}
