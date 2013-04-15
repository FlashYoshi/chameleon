package be.kuleuven.cs.distrinet.chameleon.aspect.core.model.pointcut.expression;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.aspect.core.model.pointcut.pattern.DeclarationPattern;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;

public class WithinPointcutExpression extends DeclarationPointcutExpression<Element>{

	public WithinPointcutExpression(DeclarationPattern pattern, Class<? extends Declaration> type) {
		super(pattern);
		_type = type;
	}

	@Override
	public Class<? extends Element> joinPointType() throws LookupException {
		return Element.class;
	}

	@Override
	public WithinPointcutExpression clone() {
		return new WithinPointcutExpression(pattern().clone(), type());
	}

	@Override
	protected Declaration declaration(Element joinpoint) throws LookupException {
		return (Declaration) joinpoint.nearestAncestor(type());
	}
	
	public Class<? extends Declaration> type() {
		return _type;
	}

	private Class<? extends Declaration> _type;
}