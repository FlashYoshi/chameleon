package chameleon.core.type;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.declaration.TargetDeclaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReference;
import chameleon.core.reference.SpecificReference;

/**
 * @author Marko van Dooren
 */
public class BasicTypeReference extends SpecificReference<TypeReference,Element,Type> implements TypeReference {

  public BasicTypeReference(String fqn) {
    super(fqn, Type.class);
  }
  
  public BasicTypeReference(CrossReference<?, ?, ? extends TargetDeclaration> target, String name) {
    super(target, name, Type.class);
  }
  
  public BasicTypeReference(CrossReference<?, ?, ? extends TargetDeclaration> target, SimpleNameSignature signature) {
    super(target, signature, Type.class);
  }
  
  public Type getType() throws LookupException {
  	return getElement();
  }

  public TypeReference clone() {
    return new BasicTypeReference((getTarget() == null ? null : getTarget().clone()),(SimpleNameSignature)signature().clone());
  }
  
}
