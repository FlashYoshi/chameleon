package chameleon.core.method;

import org.rejuse.property.PropertySet;

import chameleon.core.element.Element;
import chameleon.core.method.exception.ExceptionClause;
import chameleon.core.property.ChameleonProperty;
import chameleon.core.type.TypeReference;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.exception.ChameleonProgrammerException;

public class MethodAlias<E extends MethodAlias<E,H,S>, H extends MethodHeader<H, E, S>, S extends MethodSignature> extends Method<E,H,S,Method> {

	public MethodAlias(H header, Method aliasedMethod) {
		super(header);
		_aliasedMethod = aliasedMethod;
	}
	
	private Method _aliasedMethod;
	
	public Method aliasedMethod() {
		return _aliasedMethod;
	}
	
  public Method origin() {
  	Method<?,?,?,? extends Method> aliased = aliasedMethod();
  	return aliased.origin();
  }


	@Override
	protected E cloneThis() {
		return (E) new MethodAlias(header().clone(),aliasedMethod());
	}

	@Override
	public ExceptionClause getExceptionClause() {
		return aliasedMethod().getExceptionClause();
	}

	@Override
	public Implementation implementation() {
		return aliasedMethod().implementation();
	}

	@Override
	public TypeReference getReturnTypeReference() {
		return aliasedMethod().getReturnTypeReference();
	}

	@Override
	public boolean sameKind(Method other) {
		return aliasedMethod().sameKind(other);
	}

	@Override
	public void setExceptionClause(ExceptionClause clause) {
		throw new ChameleonProgrammerException("Trying to set the exception clause of a method alias.");
	}

	@Override
	public void setImplementation(Implementation implementation) {
		throw new ChameleonProgrammerException("Trying to set the implementation of a method alias.");
	}

  public PropertySet<Element,ChameleonProperty> defaultProperties() {
    return filterProperties(myDefaultProperties(), aliasedMethod().defaultProperties());
  }
	
  public PropertySet<Element,ChameleonProperty> declaredProperties() {
    return filterProperties(myDeclaredProperties(), aliasedMethod().declaredProperties());
  }

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

}
