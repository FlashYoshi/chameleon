package be.kuleuven.cs.distrinet.chameleon.oo.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.InheritanceRelation;

public abstract class MultiType extends ClassImpl {

	public MultiType(String name, List<Type> types) {
		super(name);
		if(types.isEmpty()) {
			throw new ChameleonProgrammerException("Creating a union type with an empty collection of types.");
		}
		_types = new ArrayList<Type>(types);
	}

	public void removeType(Type type) {
		_types.remove(type);
	}

	protected List<Type> _types = new ArrayList<Type>();
	
	public List<Type> types() {
		return new ArrayList<Type>(_types);
	}

	@Override
	public void add(TypeElement element) throws ChameleonProgrammerException {
		throw new ChameleonProgrammerException("Trying to add an element to a union type.");
	}

	@Override
	public void remove(TypeElement element) throws ChameleonProgrammerException {
		throw new ChameleonProgrammerException("Trying to remove an element from a union type.");
	}

	@Override
	public void addInheritanceRelation(InheritanceRelation type) throws ChameleonProgrammerException {
		throw new ChameleonProgrammerException("Trying to add a super type to a union type.");
	}

	public void replace(TypeElement oldElement, TypeElement newElement) {
		throw new ChameleonProgrammerException("Trying to replace an element in a union type.");
	}

	@Override
	public void removeNonMemberInheritanceRelation(InheritanceRelation type) {
		throw new ChameleonProgrammerException("Trying to remove a super type from a union type.");
	}

	/**
	 * An intersection type has not type parameters. 
	 */
	@Override
	public <P extends Parameter> List<P> parameters(Class<P> kind) {
		return new ArrayList<P>();
	}

	@Override
	public <P extends Parameter> int nbTypeParameters(Class<P> kind) {
		return 0;
	}

	public <P extends Parameter> void replaceParameter(Class<P> kind, P oldParameter, P newParameter) {
		throw new ChameleonProgrammerException("Trying to replace a type parameter in a union type.");
	}

	public <P extends Parameter> void replaceAllParameters(Class<P> kind, List<P> newParameters) {
		throw new ChameleonProgrammerException("Trying to replace type parameters in a union type.");
	}

	public <P extends Parameter> void addParameter(Class<P> kind, P parameter) {
		throw new ChameleonProgrammerException("Trying to add a type parameter to a union type.");
	}

	public Declaration declarator() {
		return this;
	}

	public void addParameterBlock(ParameterBlock block) {
		throw new ChameleonProgrammerException("Trying to add a parameter block to a union type.");
	}

	public <P extends Parameter> ParameterBlock<P> parameterBlock(Class<P> kind) {
		return null;
	}

	public List<ParameterBlock> parameterBlocks() {
		return new ArrayList<ParameterBlock>();
	}

	public void removeParameterBlock(ParameterBlock block) {
		throw new ChameleonProgrammerException("Trying to remove a parameter block from a union type.");
	}

	@Override
	public Type baseType() {
		return this;
	}

	@Override
	public int hashCode() {
		int result = 0;
		for(Type type:types()) {
			result += type.hashCode();
		}
		return result;
	}

	@Override
	public List<InheritanceRelation> explicitNonMemberInheritanceRelations() {
		return nonMemberInheritanceRelations();
	}

	@Override
	public List<InheritanceRelation> implicitNonMemberInheritanceRelations() {
		return Collections.EMPTY_LIST;
	}


}
