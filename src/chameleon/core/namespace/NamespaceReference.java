package chameleon.core.namespace;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.reference.CrossReferenceTarget;
import chameleon.core.reference.SpecificReference;

/**
 * @author marko
 */
public class NamespaceReference extends SpecificReference<NamespaceReference,Namespace> {

  public NamespaceReference(CrossReferenceTarget<?> target, String name) {
    super(target, name, Namespace.class);
  }
  
  public NamespaceReference(CrossReferenceTarget<?> target, SimpleNameSignature signature) {
    super(target, signature, Namespace.class);
  }
  
  public NamespaceReference(String qn) {
    super(qn, Namespace.class);
  }

  public NamespaceReference clone() {
    return new NamespaceReference((getTarget() == null ? null : getTarget().clone()),(SimpleNameSignature)signature().clone());
  }

  
}
