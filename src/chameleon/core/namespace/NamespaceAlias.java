package chameleon.core.namespace;

import java.util.List;

import org.rejuse.property.Property;
import org.rejuse.property.PropertySet;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.namespacepart.NamespacePart;
import chameleon.core.property.ChameleonProperty;
import chameleon.core.scope.Scope;
import chameleon.core.scope.ScopeProperty;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.exception.ModelException;
import chameleon.oo.language.ObjectOrientedLanguage;

public class NamespaceAlias extends Namespace {

	public NamespaceAlias(SimpleNameSignature sig, Namespace aliasedNamespace) {
		super(sig);
		_aliasedNamespace = aliasedNamespace;
	}
	
	private final Namespace _aliasedNamespace;

	public Namespace aliasedNamespace() {
		return _aliasedNamespace;
	}

	@Override
	public void addNamespacePart(NamespacePart namespacePart) {
		throw new ChameleonProgrammerException("Trying to add a namespace part to an aliased namespace");
	}

	@Override
	public List<NamespacePart> getNamespaceParts() {
		return aliasedNamespace().getNamespaceParts();
	}

	@Override
	public Namespace clone() {
		return new NamespaceAlias(signature().clone(),aliasedNamespace());
	}

	private PropertySet<Element,ChameleonProperty> myDefaultProperties() {
		return language().defaultProperties(this);
	}

  public PropertySet<Element,ChameleonProperty> defaultProperties() {
    return filterProperties(myDefaultProperties(), aliasedNamespace().defaultProperties());
  }

  public PropertySet<Element,ChameleonProperty> declaredProperties() {
  	return aliasedNamespace().declaredProperties();
  }
	
  public Scope scope() throws ModelException {
  	Scope result = null;
  	ChameleonProperty scopeProperty = property(language().SCOPE_MUTEX);
  	if(scopeProperty instanceof ScopeProperty) {
  		result = ((ScopeProperty)scopeProperty).scope(this);
  	} else if(scopeProperty != null){
  		throw new ChameleonProgrammerException("Scope property is not a ScopeProperty");
  	}
  	return result;
  }
  
  public LookupStrategy lexicalLookupStrategy(Element element) throws LookupException {
  	return aliasedNamespace().lexicalLookupStrategy();
  }

	@Override
	public List<Namespace> getSubNamespaces() {
		return aliasedNamespace().getSubNamespaces();
	}

	@Override
	public Namespace getOrCreateNamespace(String name) throws LookupException {
		return aliasedNamespace().getOrCreateNamespace(name);
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

}
