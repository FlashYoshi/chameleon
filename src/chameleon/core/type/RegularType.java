package chameleon.core.type;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rejuse.association.OrderedReferenceSet;
import org.rejuse.java.collections.Visitor;

import chameleon.core.element.ChameleonProgrammerException;
import chameleon.core.modifier.Modifier;

public class RegularType extends Type<RegularType> {

	public RegularType(TypeSignature sig) {
		super(sig);
	}

	@Override
	public RegularType clone() {
		RegularType result = cloneThis();
		result.copyContents(this);
		return result;
	}

	protected RegularType cloneThis() {
		return new RegularType(signature().clone());
	}

	private OrderedReferenceSet<Type, TypeElement> _elements = new OrderedReferenceSet<Type, TypeElement>(this);

	public void add(TypeElement element) {
	  if(element != null) {
	    _elements.add(element.getParentLink());
	  }
	}

  /**
   * Return the members directly declared by this type.
   * @return
   */
  public Set<TypeElement> directlyDeclaredElements() {
     Set<TypeElement> result = new HashSet<TypeElement>();
     for(TypeElement m: _elements.getOtherEnds()) {
       result.addAll(m.getIntroducedMembers());
     }
     return result;
  }

	private OrderedReferenceSet<Type, TypeReference> _superTypes = new OrderedReferenceSet<Type, TypeReference>(this);

	public void removeSuperType(TypeReference type) {
		_superTypes.remove(type.getParentLink());
	}

	@Override
	public List<TypeReference> getSuperTypeReferences() {
		return _superTypes.getOtherEnds();
	}
	
	@Override
	public void addSuperType(TypeReference type) throws ChameleonProgrammerException {
		_superTypes.add(type.getParentLink());
	}

}
