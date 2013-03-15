package chameleon.support.member.simplename;

import java.util.List;

import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;
import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReferenceWithName;
import chameleon.core.reference.CrossReferenceTarget;
import chameleon.oo.expression.MethodInvocation;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.lookup.SimpleNameCrossReferenceWithArgumentsSelector;
import chameleon.oo.method.Method;
import chameleon.oo.type.Type;

public abstract class SimpleNameMethodInvocation<D extends Method> extends MethodInvocation<D> implements CrossReferenceWithName<D> {

  public SimpleNameMethodInvocation(CrossReferenceTarget target, String name) {
    super(target);
    setName(name);
  }
  
  protected Type actualType() throws LookupException {
    try {
			Method method = getElement();
			if (method != null) {
			  return method.returnType();
			}
			else {
			  getElement();
			  throw new LookupException("Could not find method of constructor invocation", this);
			}
		} catch (LookupException e) {
//			e.printStackTrace();
//			getMethod();
			throw e;
		}
  }



  
  /********
   * NAME *
   ********/

  private String _methodName;

  public String name() {
    return _methodName;
  }

  public void setName(String method) {
    _methodName = method;
  }

//  public D getMethod() throws LookupException {
//	    D result = lexicalContext().lookUp(selector());
//	    if(result == null) {
//	      throw new LookupException();
//	    }
//	    return result;
//  }


  public abstract class SimpleNameMethodSelector extends SimpleNameCrossReferenceWithArgumentsSelector<D> {
  	
//  	private int _nameHash = SimpleNameMethodInvocation.this._methodName.hashCode();
    
  	@Override
    public boolean selectedRegardlessOfName(D declaration) throws LookupException {
  		boolean result = declaration.is(language(ObjectOrientedLanguage.class).CONSTRUCTOR) != Ternary.TRUE;
  		if(result) {
  			result = super.selectedRegardlessOfName(declaration);
  		}

  		return result;
    }
  	
  	@Override
  	public int nbActualParameters() {
  		return SimpleNameMethodInvocation.this.nbActualParameters();
  	}
  	
  	@Override
  	public List<Type> getActualParameterTypes() throws LookupException {
  		return SimpleNameMethodInvocation.this.getActualParameterTypes();
  	}
  	
  	public String name() {
  		return SimpleNameMethodInvocation.this.name();
  	}
  }
}
