package chameleon.core.document;

import java.util.Iterator;
import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.core.element.Element;
import chameleon.core.element.ElementImpl;
import chameleon.core.language.Language;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespacedeclaration.NamespaceDeclaration;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.util.association.Multi;
import chameleon.workspace.InputSource;

/**
 * A compilation unit represents a file in which elements of the program/model are defined.
 * 
 * A compilation unit does not directly contain the source code elements. It contains the namespace parts that contain
 * the source code. 
 * 
 * @author Marko van Dooren
 */
public class Document extends ElementImpl {

	public Document() {
		
	}
	
  /**
   * Create a new compilation unit with the given namespace part.
   * @param namespacePart
   */
	public Document(NamespaceDeclaration defaultNamespacePart) {
    add(defaultNamespacePart);
	}
	
	
	/************
	 * Children *
	 ************/

	/**
	 * NAMESPACEPARTS
	 */
	public List<NamespaceDeclaration> namespaceParts() {
		return _subNamespaceParts.getOtherEnds();
	}
	
	/**
	 * Indices start at 1.
	 * @param index
	 * @return
	 */
	public NamespaceDeclaration namespacePart(int index) {
		return _subNamespaceParts.elementAt(index);
	}

	public void add(NamespaceDeclaration pp) {
		add(_subNamespaceParts,pp);
	}

	public void remove(NamespaceDeclaration pp) {
		remove(_subNamespaceParts,pp);
	}
	
	private Multi<NamespaceDeclaration> _subNamespaceParts = new Multi<NamespaceDeclaration>(this);


	public LookupStrategy lexicalLookupStrategy(Element child) throws LookupException {
		throw new ChameleonProgrammerException("A compilation unit should not be involved in the lookup");
	}


	public Language language() {
		List<NamespaceDeclaration> parts = namespaceParts();
		Language result = null;
		if(parts.size() > 0) {
	    NamespaceDeclaration firstNamespace = parts.get(0);
	    if(firstNamespace != null) {
		    result = firstNamespace.language();
	    }
		}
		return result;
	}


  @Override
  public Document clone() {
    Document result = new Document();
    for(NamespaceDeclaration namespacePart: namespaceParts()) {
    	result.add(namespacePart.clone());
    }
    return result;
  }

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

	public Document cloneTo(Language targetLanguage) throws LookupException {
		Document clone = clone();
		List<NamespaceDeclaration> originalNamespaceParts = namespaceParts();
		List<NamespaceDeclaration> newNamespaceParts = clone.namespaceParts();
		Iterator<NamespaceDeclaration> originalIterator = originalNamespaceParts.iterator();
		Iterator<NamespaceDeclaration> newIterator = newNamespaceParts.iterator();
		while(originalIterator.hasNext()) {
			NamespaceDeclaration originalNamespacePart = originalIterator.next();
			NamespaceDeclaration newNamespacePart = newIterator.next();
			Namespace originalNamespace = originalNamespacePart.namespace();
			String fqn = originalNamespace.getFullyQualifiedName();
			Namespace newNamespace = targetLanguage.defaultNamespace().getOrCreateNamespace(fqn);
			newNamespace.addNamespacePart(newNamespacePart);
		}
		return clone;
	}
	
	public SingleAssociation<Document, InputSource> inputSourceLink() {
		return _inputSource;
	}

	protected SingleAssociation<Document, InputSource> _inputSource = new SingleAssociation<>(this);
	
 }
