package chameleon.core.namespacedeclaration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.rejuse.predicate.TypePredicate;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.element.Element;
import chameleon.core.element.ElementImpl;
import chameleon.core.language.Language;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LocalLookupStrategy;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.lookup.LookupStrategyFactory;
import chameleon.core.lookup.LookupStrategySelector;
import chameleon.core.namespace.Namespace;
import chameleon.core.reference.SimpleReference;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.util.association.Multi;
import chameleon.util.association.Single;
/**
 * A namespace part adds its declarations to a namespace. Different namespace parts in different compilation units
 * can contribute to the same namespace.
 * 
 * @author Marko van Dooren
 * @author Tim Laeremans
 */
public class NamespaceDeclaration extends ElementImpl implements DeclarationContainer {

  static {
    excludeFieldName(NamespaceDeclaration.class,"_namespaceLink");
  }
  
	private final class DefaultNamespaceSelector implements LookupStrategySelector {
		public LookupStrategy strategy() throws LookupException {
			// 5 SEARCH IN DEFAULT NAMESPACE
			return NamespaceDeclaration.this.getDefaultNamespace().targetContext();
		}
	}

	protected class ImportLocalDemandContext extends LocalLookupStrategy<NamespaceDeclaration> {
	  public ImportLocalDemandContext(NamespaceDeclaration element) {
			super(element);
		}

	  @Override
	  public <D extends Declaration> List<D> declarations(DeclarationSelector<D> selector) throws LookupException {
	    List<D> result = new ArrayList<D>();
			List<? extends Import> imports = imports();
			ListIterator<? extends Import> iter = imports.listIterator(imports.size());
			// If the selector found a match, we stop.
			// We must iterate in reverse.
			while(result.isEmpty() && iter.hasPrevious()) {
				Import imporT = iter.previous();
		    result.addAll(imporT.demandImports(selector));
	    }
	    return result;
	  }
	}
	
	protected class ImportLocalDirectContext extends LocalLookupStrategy<NamespaceDeclaration> {
		private ImportLocalDirectContext(NamespaceDeclaration element) {
			super(element);
		}

		@Override
		public <D extends Declaration> List<D> declarations(DeclarationSelector<D> selector) throws LookupException {
			List<D> result = new ArrayList<D>();
			List<? extends Import> imports = imports();
			ListIterator<? extends Import> iter = imports.listIterator(imports.size());
			// If the selector found a match, we stop.
			// We must iterate in reverse.
			while(result.isEmpty() && iter.hasPrevious()) {
				Import imporT = iter.previous();
				result.addAll(imporT.directImports(selector));
			}
		  return result;
		}
	}

	protected class DemandImportStrategySelector implements LookupStrategySelector {
		public LookupStrategy strategy() {
			// 4 SEARCH DEMAND IMPORTS
			return _importDemandContext;
		}
	}

	protected class CurrentNamespaceStrategySelector implements LookupStrategySelector {
		public LookupStrategy strategy() throws LookupException {
			// 3 SEARCH IN CURRENT NAMESPACE
			LookupStrategy currentNamespaceStrategy = namespace().localStrategy();
			return language().lookupFactory().createLexicalLookupStrategy(
					 currentNamespaceStrategy, NamespaceDeclaration.this, _demandImportStrategySelector)
			;
		}
	}

	protected class DirectImportStrategySelector implements LookupStrategySelector {
		public LookupStrategy strategy() {
			// 2 SEARCH IN DIRECT IMPORTS
			return _importDirectContext;
		}
	}

	/**
	 * Create a new namespace part the adds elements to the given namespace.
	 * @param namespace
	 */
 /*@
   @ public behavior
   @
   @ pre namespace != null;
   @
   @ post getDeclaredNamespace() == namespace
   @*/ 
	public NamespaceDeclaration(SimpleReference<Namespace> ref) {
    setNamespaceReference(ref);
	}
	
	public void setNamespaceReference(SimpleReference<Namespace> ref) {
		set(_ref, ref);
	}
	
	private Single<SimpleReference<Namespace>> _ref = new Single<>(this,true);
	
	public LookupStrategy lexicalLookupStrategy(Element child) throws LookupException {
		if(imports().contains(child) || child == namespaceReference()) {
			return getDefaultNamespace().targetContext();
		} else {
			return getLexicalContext();
		}
	}
	
	private LookupStrategy getLexicalContext() {
		if(_lexicalContext == null) {
			initContexts();
		}
		return _lexicalContext;
	}
	
	private void initContexts() {
    // This must be executed after the namespace is set, so it cannot be in the initialization.
    _typeLocalContext = language().lookupFactory().createTargetLookupStrategy(this);
    _importLocalDirectContext = new ImportLocalDirectContext(this);
//    _importLocalDemandContext = language().lookupFactory().wrapLocalStrategy(new ImportLocalDemandContext(this),this);
    _importLocalDemandContext = new ImportLocalDemandContext(this);
		_importDirectContext = language().lookupFactory().createLexicalLookupStrategy(_importLocalDirectContext, this, _currentNamespaceStrategySelector);
		_importDemandContext = language().lookupFactory().createLexicalLookupStrategy(_importLocalDemandContext, this, _defaultNamespaceSelector);
    // 1 SEARCH IN NAMESPACEPART
		_lexicalContext = language().lookupFactory().createLexicalLookupStrategy(localContext(), this, _directImportStrategySelector); 
	}

  private DirectImportStrategySelector _directImportStrategySelector = new DirectImportStrategySelector();

	private CurrentNamespaceStrategySelector _currentNamespaceStrategySelector = new CurrentNamespaceStrategySelector();

	private DemandImportStrategySelector _demandImportStrategySelector = new DemandImportStrategySelector();

	private DefaultNamespaceSelector _defaultNamespaceSelector = new DefaultNamespaceSelector();
	
	private LookupStrategy _importLocalDirectContext;
	
	private LookupStrategy _importDirectContext;
	
	private LookupStrategy _importLocalDemandContext;
	
	private LookupStrategy _importDemandContext;
	
	public LookupStrategy localContext() {
		if(_typeLocalContext == null) {
			initContexts();
		}
		return _typeLocalContext;
	}
	
	private LookupStrategy _lexicalContext;
	
	public String getName(){
		return namespace().name();
	}

	public String getFullyQualifiedName() {
		return namespace().getFullyQualifiedName();
	}

	public NamespaceDeclaration getNearestNamespacePart() {
		return this;
	}
	
	/**
	 * NAMESPACEPARTS
	 */
	public List<NamespaceDeclaration> namespaceParts() {
		return _subNamespaceParts.getOtherEnds();
	}

	public void addNamespacePart(NamespaceDeclaration pp) {
		add(_subNamespaceParts,pp);
	}

	public void removeNamespacePart(NamespaceDeclaration pp) {
		remove(_subNamespaceParts,pp);
	}

	/**
	 * Recursively disconnect this namespace declaration and all descendant namespace declarations
	 * from their namespaces. 
	 */
	public void nonRecursiveDisconnect() {
		// 1) Set the lexical parent to null.
		super.nonRecursiveDisconnect();
//		if(Config.DEBUG) {
//			if(namespace() != null) {
//			  System.out.println("Disconnecting from "+namespace().getFullyQualifiedName());
//			}
////			showStackTrace("Disconnecting from "+namespace().getFullyQualifiedName());
//		}
		// 2) Disconnect from the namespace. 
		setNamespace(null);
//		// 3) IS NOW DONE BY DEFAULT RECURSION Disconnecting the children.
//		for(NamespacePart nsp: namespaceParts()) {
//			nsp.disconnect();
//		}
	}

	/**
	 * A namespace part is disconnected if both its parent and its namespace are null.
	 */
 /*@
   @ public behavior
   @
   @ post \result == (parent() == null) && (namespace() == null);
   @*/
	@Override
	public boolean disconnected() {
		return super.disconnected() && namespaceLink().getOtherEnd() != null;
	}
	
	private Multi<NamespaceDeclaration> _subNamespaceParts = new Multi<NamespaceDeclaration>(this);

	public List<Declaration> declarations() {
      return _declarations.getOtherEnds();
	}
	
	public <T extends Declaration> List<T> declarations(Class<T> kind) {
    return new TypePredicate<Declaration,T>(kind).filterReturn(declarations());
  }
	
	public <D extends Declaration> List<D> declarations(DeclarationSelector<D> selector) throws LookupException {
		return selector.selection(declarations());
	}


	/*************
	 * NAMESPACE *
	 *************/
	private Single<Namespace> _namespaceLink = new Single<Namespace>(this,true);

	/**
	 * Return the namespace to which this namespacepart adds declarations.
	 * @return
	 * @throws LookupException 
	 */
	public Namespace namespace() {
		Namespace stored = _namespaceLink.getOtherEnd();
		if(stored == null) {
			//FIXME When multi-language support is added, this must change
			stored = view().namespace().getOrCreateNamespace(namespaceReference().toString());
			stored.addNamespacePart(this);
		}
		return stored;
	}
	
	public SimpleReference<Namespace> namespaceReference() {
		return _ref.getOtherEnd();
	}
	
	public Single<Namespace> namespaceLink() {
		return _namespaceLink;
	}

	public void setNamespace(Namespace namespace) {
		if (namespace != null) {
			namespace.addNamespacePart(this);
		} else {
			_namespaceLink.connectTo(null);
		}
	}


	/******************
	 * DEMAND IMPORTS *
	 ******************/

	private Multi<Import> _imports = new Multi<Import>(this);

	public List<? extends Import> imports() {
		List result = explicitImports();
		result.addAll(implicitImports());
		return result;
	}
	
	public List<? extends Import> explicitImports() {
		return _imports.getOtherEnds();
	}

	public List<? extends Import> implicitImports() {
		return Collections.EMPTY_LIST;
		
	}
	public void addImport(Import newImport) {
		add(_imports,newImport);
	}
	
	public void removeImport(Import removedImport) {
		remove(_imports,removedImport);
	}
	
	public void clearImports() {
		_imports.clear();
	}
	
	public void removeDuplicateImports() throws LookupException {
		List<? extends Import> imports = imports();
		int nbImports = imports.size();
		for(int i=0; i< nbImports;i++) {
			Import outer = imports.get(i);
			for(int j=i+1; j< nbImports;) {
				Import inner = imports.get(j);
				if(outer.importsSameAs(inner)) {
					removeImport(inner);
					imports.remove(j);
					nbImports--;
				} else {
					j++;
				}
			}
		}
	}

	/****************
	 * DECLARATIONS *
	 ****************/
	private Multi<Declaration> _declarations = new Multi<Declaration>(this);

	/**
	 * Add the given declaration to this namespace part.
	 */
 /*@
   @ public behavior
   @
   @ pre declaration != null;
   @
   @ post declarations().contains(declaration);
   @*/
	public void add(Declaration declaration) {
		add(_declarations,declaration);
	}
	
	public void addAll(Collection<Declaration> declarations) {
		for(Declaration decl: declarations) {
			add(decl);
		}
	}

	/**
	 * Remove the given declaration from this namespace part.
	 */
 /*@
   @ public behavior
   @
   @ pre declaration != null;
   @
   @ post !declarations().contains(declaration);
   @*/
	public void remove(Declaration declaration) {
		remove(_declarations,declaration);
	}

	/**
	 * The parents are possibly other PackageParts and the CompilationUnit
	 */



	/**
	 * ACCESSIBILITY
	 */

	public Language language(){
		Namespace namespace = null;
			namespace = namespace();
		if(namespace != null) {
		  return namespace.language();
		} else {
			return null;
		}
	}
	public LookupStrategyFactory getContextFactory() {
		return language().lookupFactory();
	}

 /*@
   @ public behavior
   @
   @ post \result = getDeclaredNamespace().getDefaultNamespace(); 
   @*/
	public Namespace getDefaultNamespace() throws LookupException {
		return namespace().defaultNamespace();
	}

  @Override
  public NamespaceDeclaration clone() {
  	NamespaceDeclaration result = cloneThis();
  	for(NamespaceDeclaration part: namespaceParts()) {
  		result.addNamespacePart(part.clone());
  	}
  	for(Declaration declaration:declarations()) {
  		result.add(declaration.clone());
  	}
  	for(Import importt:explicitImports()) {
  		result.addImport(importt.clone());
  	}
  	return result;
  }
  
  public NamespaceDeclaration cloneThis() {
  	return new NamespaceDeclaration(namespaceReference().clone());
  }

	private LookupStrategy _typeLocalContext;

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

	public List<? extends Declaration> locallyDeclaredDeclarations() throws LookupException {
		return declarations();
	}

	@Override
	public LookupStrategy localStrategy() throws LookupException {
		throw new ChameleonProgrammerException("This method should never be invoked.");
	}
}
