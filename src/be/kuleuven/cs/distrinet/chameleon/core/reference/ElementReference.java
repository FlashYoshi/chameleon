package be.kuleuven.cs.distrinet.chameleon.core.reference;

import java.lang.ref.SoftReference;

import be.kuleuven.cs.distrinet.chameleon.core.Config;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Signature;
import be.kuleuven.cs.distrinet.chameleon.util.association.Single;

/**
 * 
 * 
 * @author Marko van Dooren
 */
public abstract class ElementReference<D extends Declaration> extends CrossReferenceImpl<D> implements CrossReferenceWithName<D> {

//	private static Logger logger = Logger.getLogger("lookup.elementreference");
	
//	public Logger lookupLogger() {
//		return logger;
//	}
	
	public ElementReference() {
	}
	
//	public abstract ElementReference<D> clone();

	/*@
   @ public behavior
   @
   @ pre name != null;
   @
   @ post getName().equals(name);
   @*/
	public ElementReference(Signature signature) {
		setSignature(signature);
	}
	
  private Single<Signature> _signature = new Single<Signature>(this);

  /**
   * Return the signature of this element reference.
   */
  public Signature signature() {
  	return _signature.getOtherEnd();
  }
  
  public String name() {
  	return signature().name();
  }
  
 /*@
   @ public behavior
   @
   @ pre name != null;
   @
   @ post getName() == name;
   @*/
  public void setSignature(Signature signature) {
  	set(_signature, signature);
  }
  
	@Override
	public final void setName(String name) {
		signature().setName(name);
	}
  
  private SoftReference<D> _cache;
  
  @Override
  public synchronized void flushLocalCache() {
  	super.flushLocalCache();
  	_cache = null;
  }
  
  protected synchronized D getCache() {
  	D result = null;
  	if(Config.cacheElementReferences() == true) {
  	  result = (_cache == null ? null: _cache.get());
  	}
    return result;
  }
  
  protected synchronized void setCache(D value) {
    	if(Config.cacheElementReferences() == true) {
    		_cache = new SoftReference<D>(value);
    	}
  }
}
