package be.kuleuven.cs.distrinet.chameleon.oo.variable;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.scope.LexicalScope;
import be.kuleuven.cs.distrinet.chameleon.core.scope.Scope;
import be.kuleuven.cs.distrinet.chameleon.core.variable.VariableContainer;
import be.kuleuven.cs.distrinet.chameleon.exception.ModelException;
import be.kuleuven.cs.distrinet.chameleon.oo.member.DeclarationWithParametersHeader;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Method;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;

/**
 * @author Marko van Dooren
 */
public class FormalParameter extends RegularVariable {

  public FormalParameter(String name, TypeReference type) {
    super(name, type,null);
  }

	/**
   * @param parameter
   * @return
   */
  public boolean compatibleWith(FormalParameter parameter) throws LookupException {
  	boolean result = false;
  	if((parent() instanceof DeclarationWithParametersHeader) && (parameter != null) && (parameter.parent() instanceof DeclarationWithParametersHeader)) {
    	DeclarationWithParametersHeader header = (DeclarationWithParametersHeader) parent();
    	Method method = header.nearestAncestor(Method.class);
    	//BUG: the following should be parameter.parent(). Fix after removing the parameters.
    	DeclarationWithParametersHeader otherHeader = (DeclarationWithParametersHeader) parent();
    	Method otherMethod = otherHeader.nearestAncestor(Method.class); //REFACTORING
    	result = method.overrides(otherMethod) &&
      method.formalParameters().indexOf(this) == otherMethod.formalParameters().indexOf(parameter); 
  	}
    return result; 
  }

  @Override
protected FormalParameter cloneSelf() {
    return new FormalParameter(name(), null);
  }
  
	@Override
   public Scope scope() throws ModelException {
		return new LexicalScope(nearestAncestor(VariableContainer.class).variableScopeElement());
	}

	@Override
   public FormalParameter actualDeclaration() throws LookupException {
		return this;
	}

}
