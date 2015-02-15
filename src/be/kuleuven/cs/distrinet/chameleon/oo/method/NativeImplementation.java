package be.kuleuven.cs.distrinet.chameleon.oo.method;

import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Verification;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.Block;

/**
 * @author Marko van Dooren
 */
public class NativeImplementation extends Implementation {


	@Override
   protected NativeImplementation cloneSelf() {
    return new NativeImplementation();
  }

  @Override
public boolean compatible() {
    return true;
  }
  
  @Override
public Block getBody() {
    return null;
  }

	@Override
	public Verification verifySelf() {
		return Valid.create();
	}

	@Override
	public boolean complete() {
		return true;
	}

}
