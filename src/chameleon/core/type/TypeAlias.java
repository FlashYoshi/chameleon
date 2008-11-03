package chameleon.core.type;

import java.util.List;
import java.util.Set;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.ChameleonProgrammerException;

public class TypeAlias<E extends TypeAlias<E>> extends Type<E> {

	public TypeAlias(SimpleNameSignature sig, Type<? extends Type> aliasedType) {
		super(sig);
		_aliasedType = aliasedType;
		setUniParent(aliasedType.getParent());
	}
	
	// @EXTENSIBILITY : change names of constructors?

	public Type aliasedType() {
		return _aliasedType;
	}
	
	private final Type _aliasedType;

	/**
	 * OVERRIDE IN SUBCLASSES !!!!
	 */
	public E clone() {
		return (E) new TypeAlias(signature().clone(), aliasedType());
	}

	@Override
	public void add(TypeElement element) {
		throw new ChameleonProgrammerException("Trying to add an element to a type alias.");
	}

	@Override
	public Set<TypeElement> directlyDeclaredElements() {
		return aliasedType().directlyDeclaredElements();
	}

	@Override
	public void removeSuperType(TypeReference type) {
		throw new ChameleonProgrammerException("Trying to remove a super type from a type alias.");
	}



	@Override
	public void addSuperType(TypeReference type) throws ChameleonProgrammerException {
		throw new ChameleonProgrammerException("Trying to add a super type to a type alias.");
	}



	@Override
	public List<TypeReference> getSuperTypeReferences() {
		return aliasedType().getSuperTypeReferences();
	}

}
