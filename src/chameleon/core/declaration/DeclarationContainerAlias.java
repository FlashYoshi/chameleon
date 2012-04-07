package chameleon.core.declaration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.element.ElementImpl;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.util.association.Multi;

public class DeclarationContainerAlias extends ElementImpl implements DeclarationContainer {

	public DeclarationContainerAlias(DeclarationContainer parent) {
		setUniParent(parent);
	}
	
	
	public DeclarationContainerAlias() {
		
	}
	
	@Override
	public DeclarationContainerAlias clone() {
		DeclarationContainerAlias result = new DeclarationContainerAlias();
		for(Declaration declaration: declarations()) {
			Declaration clone = declaration.clone();
			clone.setOrigin(declaration.origin());
			result.add(clone);
		}
		for(DeclarationContainerAlias alias: superContainers()) {
			result.addSuperContainer(alias.clone());
		}
		result.setUniParent(parent());
		return result;
	}

	@Override
	public LookupStrategy lexicalLookupStrategy(Element child) throws LookupException {
		return parent().lexicalLookupStrategy(child);
	}


	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = Valid.create();
		if(parent() == null) {
			result = result.and(new BasicProblem(this, "The declaration container alias does not alias any declaration."));
		}
		return result;
	}
	
	public List<Declaration> declarations() {
		return _elements.getOtherEnds();
	}
	
	public List<Declaration> allDeclarations() {
		List<Declaration> result = new ArrayList<Declaration>();
		for(DeclarationContainerAlias superContainer: superContainers()) {
			result.addAll(superContainer.allDeclarations());
		}
		result.addAll(declarations());
		return result;
	}

	public <D extends Declaration> List<D> declarations(DeclarationSelector<D> selector) throws LookupException {
		return selector.selection(declarations());
	}

	private Multi<Declaration> _elements = new Multi<Declaration>(this);

	public void add(Declaration element) {
	  add(_elements, element);
	}
	
	public void addAll(Collection<Declaration> elements) {
		for(Declaration declaration:elements) {
			add(declaration);
		}
	}
	
	public void remove(Declaration element) {
	  remove(_elements,element);
	}
	
	public List<DeclarationContainerAlias> superContainers() {
		return new ArrayList<DeclarationContainerAlias>(_superContainers);
	}
	
	public void addSuperContainer(DeclarationContainerAlias container) {
		_superContainers.add(container);
	}
	
	public void removeSuperContainer(DeclarationContainerAlias container) {
		_superContainers.remove(container);
	}
	
	private List<DeclarationContainerAlias> _superContainers = new ArrayList<DeclarationContainerAlias>();

	public List<? extends Declaration> locallyDeclaredDeclarations() throws LookupException {
		return _elements.getOtherEnds();
	}


	@Override
	public LookupStrategy localStrategy() throws LookupException {
		return ((DeclarationContainer)parent()).localStrategy();
	}
	
}
