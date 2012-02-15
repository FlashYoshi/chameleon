package chameleon.core.namespace;

import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.element.Element;
import chameleon.core.element.ElementImpl;
import chameleon.core.language.Language;
import chameleon.core.namespacepart.NamespacePart;

public abstract class NamespaceElementImpl extends ElementImpl implements NamespaceElement {

	public Namespace getNamespace() {
		NamespacePart ancestor = nearestAncestor(NamespacePart.class);
		if(ancestor != null) {
		  return ancestor.namespace();
		} else {
			return null;
		}
	}
	
}
