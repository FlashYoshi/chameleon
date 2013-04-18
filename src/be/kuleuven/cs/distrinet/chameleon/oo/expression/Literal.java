package be.kuleuven.cs.distrinet.chameleon.oo.expression;

import java.util.HashSet;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.core.validation.BasicProblem;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Verification;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;


/**
 * @author Marko van Dooren
 */
public abstract class Literal extends Expression {
  
  public Literal(String value) {
    setValue(value);
  }
  
  /**
   * VALUE
   */
  
  private String _value;
  
  public String getValue() {
    return _value;
  }

  public void setValue(String value) {
  	_value = value;
  }
  
  /*@
    @ also public behavior
    @
    @ post \result.isEmpty(); 
    @*/
  public Set<Type> getDirectExceptions() {
    return new HashSet<Type>();
  }
  
	@Override
	public Verification verifySelf() {
		Verification result = Valid.create();
		if(getValue() == null) {
			result = result.and(new BasicProblem(this, "The value of the literal is missing."));
		}
		return result;
	}

}
