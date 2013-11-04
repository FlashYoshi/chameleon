package be.kuleuven.cs.distrinet.chameleon.core.namespace;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupContext;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespacedeclaration.NamespaceDeclaration;
import be.kuleuven.cs.distrinet.chameleon.core.scope.Scope;
import be.kuleuven.cs.distrinet.chameleon.core.scope.UniversalScope;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Verification;
import be.kuleuven.cs.distrinet.chameleon.util.association.Multi;
import be.kuleuven.cs.distrinet.rejuse.association.Association;

import com.google.common.collect.ImmutableList;

public class RegularNamespace extends NamespaceImpl {
	
	public RegularNamespace(String name) {
		super(name);
	}
	
	/**
	 * SUBNAMESPACES
	 */
	private Multi<Namespace> _namespaces = new Multi<Namespace>(this,"namespaces") {
		@Override
		protected void fireElementAdded(Namespace addedElement) {
			super.fireElementAdded(addedElement);
			registerNamespace(addedElement);
		}
		
		protected void fireElementRemoved(Namespace addedElement) {
			super.fireElementRemoved(addedElement);
			unregisterNamespace(addedElement);
		};
		
		protected void fireElementReplaced(Namespace oldElement, Namespace newElement) {
			super.fireElementReplaced(oldElement, newElement);
			fireElementAdded(newElement);
			fireElementRemoved(oldElement);
		};
	};
	{
		_namespaces.enableCache();
	}

	protected synchronized void addNamespace(Namespace namespace) {
		add(_namespaces,namespace);
		updateCacheNamespaceAdded(namespace);
	}
	
	protected void updateCacheNamespaceAdded(Namespace namespace) {
		flushLocalCache();
	}

	/**
	 * Return all subpackages of this package.
	 */
	public List<Namespace> getSubNamespaces() {
		return _namespaces.getOtherEnds();
	}


	
	/*******************
	 * NAMESPACE PARTS *
	 *******************/

	private Multi<NamespaceDeclaration> _namespaceDeclarations = new Multi<NamespaceDeclaration>(this,"namespace parts");
	{
		_namespaceDeclarations.enableCache();
	}

	public synchronized void addNamespacePart(NamespaceDeclaration namespacePart){
		_namespaceDeclarations.add((Association)namespacePart.namespaceLink());
		flushLocalCache();
	}

	public synchronized List<NamespaceDeclaration> getNamespaceParts(){
		return _namespaceDeclarations.getOtherEnds();
	}
	
	@Override
	public List<NamespaceDeclaration> loadedNamespaceParts() {
		return _namespaceDeclarations.getOtherEnds();
	}

	protected RegularNamespace cloneSelf() {
		return new RegularNamespace(name());
	}
	
	public Scope scope() {
		return new UniversalScope();
	}
	
  public LookupContext lookupContext(Element element) throws LookupException {
  	if(_context == null) {
  		_context = language().lookupFactory().createLexicalLookupStrategy(targetContext(), this);
  	}
		return _context;
  }
  
  private LookupContext _context;
  
	/**
	 * Create a new package with the given name
	 * @param name
	 *        The name of the new package.
	 */
	/*@
	 @ protected behavior
	 @
	 @ post \result != null;
	 @*/
	public Namespace createSubNamespace(String name){
	  Namespace result = new RegularNamespace(name);
	  addNamespace(result);
		return result;
	}

	@Override
	public Verification verifySelf() {
		return Valid.create();
	}

	public Declaration declarator() {
		return this;
	}

	public List<? extends Declaration> locallyDeclaredDeclarations() throws LookupException {
		return declarations();
	}

	@Override
	public boolean complete() {
		return true;
	}
	
	@Override
	public boolean hasSubNamespaces() {
		return _namespaces.size() > 0;
	}

	@Override
	public List<Namespace> getAllSubNamespaces() {
		ImmutableList.Builder<Namespace> builder = ImmutableList.builder();
		builder.addAll(_namespaces.getOtherEnds());
		for(Namespace ns:_namespaces.getOtherEnds()) {
			builder.addAll(ns.getAllSubNamespaces());
		}
		return builder.build();
	}
}
