package chameleon.core.namespacepart;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.Reference;

import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.NamespaceOrType;
import chameleon.core.reference.ElementReference;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 */
public class DemandImport extends Import<DemandImport> {
  
	// TODO: a demand import should only accept a namespace reference.
  public DemandImport(ElementReference<?, ? extends Namespace> ref) {
    setNamespaceOrTypeReference((ElementReference<ElementReference<?, ? extends Namespace>, ? extends Namespace>) ref);
  }

  
  public List children() {
    return Util.createNonNullList(getNamespaceOrTypeReference());
  }

  
	private Reference<DemandImport,ElementReference<?, ? extends Namespace>> _packageOrType = new Reference<DemandImport,ElementReference<?, ? extends Namespace>>(this);

  
  public ElementReference<?, ? extends NamespaceOrType> getNamespaceOrTypeReference() {
    return _packageOrType.getOtherEnd();
  }
  
  public void setNamespaceOrTypeReference(ElementReference<ElementReference<?, ? extends Namespace>, ? extends Namespace> ref) {
  	if(ref != null) {
  		_packageOrType.connectTo(ref.parentLink());
  	}
  	else {
  		_packageOrType.connectTo(null);
  	}
  }
  
  public NamespaceOrType declarationContainer() throws LookupException {
    return getNamespaceOrTypeReference().getElement();
  }
  
  @Override
  public DemandImport clone() {
    return new DemandImport(getNamespaceOrTypeReference().clone());
  }


	@Override
	public List<Declaration> demandImports() throws LookupException {
		return declarationContainer().declarations();
	}


	@Override
	public List<Declaration> directImports() throws LookupException {
		return new ArrayList<Declaration>();
	}


	@Override
	public <D extends Declaration> List<D> demandImports(DeclarationSelector<D> selector) throws LookupException {
		D selected = declarationContainer().localStrategy().lookUp(selector);
		List<D> result = new ArrayList<D>();
		Util.addNonNull(selected, result);
		return result;
		//return declarationContainer().declarations(selector);
	}


	@Override
	public <D extends Declaration> List<D> directImports(DeclarationSelector<D> selector) throws LookupException {
		return new ArrayList<D>();
	}
  


}
