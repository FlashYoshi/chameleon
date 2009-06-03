package chameleon.core.expression;

import java.util.List;

import org.rejuse.association.OrderedReferenceSet;
import org.rejuse.association.Reference;
import org.rejuse.java.collections.Visitor;

import chameleon.core.MetamodelException;
import chameleon.core.element.Element;
import chameleon.core.modifier.Modifier;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespacepart.NamespacePartElementImpl;
import chameleon.core.type.Type;
import chameleon.util.Util;

/**
 * A class of elements representing actual parameters. An actual parameter has an expression, and can optionally
 * have modifiers.
 * 
 * Exmaple: ref parameters in C#. 
 * 
 * 
 * @author Marko van Dooren
 * @author Tim Laeremans
 */
public class ActualParameter extends NamespacePartElementImpl<ActualParameter,ActualArgumentList> implements ExpressionContainer<ActualParameter,ActualArgumentList> {



	/**
	 * @param parent
	 * @param target
	 */
	public ActualParameter(Expression expression ) {
        setExpression(expression);
        //DEBUG
        getExpressionLink().lock();
	}

	/**
	 * EXPRESSION
	 */
	private Reference<ActualParameter,Expression> _expression = new Reference<ActualParameter,Expression>(this);

	public Reference getExpressionLink(){
		return _expression;
	}

	public Expression getExpression() {
		return _expression.getOtherEnd();
	}

	public void setExpression(Expression expression) {
		if(expression != null) {
			_expression.connectTo(expression.parentLink());
		} else {
			_expression.connectTo(null);
			throw new Error("Debugging Exception");
		}
	}

	/*************
	 * MODIFIERS *
	 *************/
	
	private OrderedReferenceSet<ActualParameter, Modifier> _modifiers = new OrderedReferenceSet<ActualParameter, Modifier>(this);

	public List<Modifier> getModifiers() {
		return _modifiers.getOtherEnds();
	}

	public void addModifier(Modifier modifier) {
		if ((modifier != null) && (!_modifiers.contains(modifier.parentLink()))) {
			_modifiers.add(modifier.parentLink());
		}
	}

	public void removeModifier(Modifier modifier) {
		_modifiers.remove(modifier.parentLink());
	}

	public boolean is(Modifier modifier) {
		return _modifiers.getOtherEnds().contains(modifier);
	}

	public ActualParameter clone(){
		Expression expr = getExpression().clone();
		final ActualParameter result = new ActualParameter(expr);

		new Visitor<Modifier>() {
			public void visit(Modifier element) {
				result.addModifier(element.clone());
			}
		}.applyTo(getModifiers());

		return result;
	}

	public List<Element> children() {
		List<Element> result = Util.createNonNullList(getExpression());
		result.addAll(getModifiers());
		return result;
	}

	public Type getType() throws MetamodelException{
		return getExpression().getType();
	}

	public Type getNearestType() {
		return this.parent().getNearestType();
	}

	public Namespace getNamespace() {
		return this.parent().getNamespace();
	}

}
