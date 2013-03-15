package chameleon.oo.type;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import be.kuleuven.cs.distrinet.rejuse.predicate.TypePredicate;
import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.validation.BasicProblem;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.member.Member;
import chameleon.oo.type.generics.TypeParameterBlock;
import chameleon.oo.type.inheritance.InheritanceRelation;
import chameleon.util.association.Multi;
import chameleon.util.association.Single;

/**
 * A class representing object-oriented classes that have a body with declarations.
 * 
 * @author Marko van Dooren
 */
public abstract class ClassWithBody extends ClassImpl {

	protected Single<ClassBody> _body = new Single<ClassBody>(this,true);

	
	public List<InheritanceRelation> nonMemberInheritanceRelations() {
		List<InheritanceRelation> result = explicitNonMemberInheritanceRelations();
		addImplicitInheritanceRelations(result);
		return result;
	}
	
	public List<InheritanceRelation> explicitNonMemberInheritanceRelations() {
		return _inheritanceRelations.getOtherEnds();
	}
	
	public List<InheritanceRelation> implicitNonMemberInheritanceRelations() {
		return Collections.EMPTY_LIST;
	}

	public LookupStrategy lexicalLookupStrategy(Element element) throws LookupException {
		List<ParameterBlock> parameterBlocks = parameterBlocks();
		if(parameterBlocks.contains(element)) { // || element.isDerived()
			int index = parameterBlocks.indexOf(element);
			if(index <= 0) {
			  return parent().lexicalLookupStrategy(this);
			} else {
				return parameterBlocks.get(index-1).lexicalLookupStrategy(element);
			}
		} else {
			return super.lexicalLookupStrategy(element);
		}
	}

	private Multi<InheritanceRelation> _inheritanceRelations = new Multi<InheritanceRelation>(this);

	public ClassBody body() {
		return _body.getOtherEnd();
	}

	public void setBody(ClassBody body) {
		if(body == null) {
			throw new Error();
		}
		set(_body,body);
	}

	public void add(TypeElement element) {
		  body().add(element);
		}

	@Override
	public void remove(TypeElement element) throws ChameleonProgrammerException {
		body().remove(element);
	}

	/**
	 * Return the members directly declared by this type.
	 * @return
	 * @throws LookupException 
	 */
	public List<Member> localMembers() throws LookupException {
	   return body().members();
	}

	protected Multi<ParameterBlock> _parameters = new Multi<ParameterBlock>(this);

	public void removeNonMemberInheritanceRelation(InheritanceRelation relation) {
		remove(_inheritanceRelations,relation);
	}

	/**
	 * Return a list containing both the explicit and implicit inheritance relations.
	 */
	@Override
	public List<InheritanceRelation> inheritanceRelations() throws LookupException {
		return nonMemberInheritanceRelations();
	}
	
	/**
	 * Add any implicit inheritance relations of this class to the given list. By default,
	 * no inheritance relations are added.
	 * 
	 * @param list The list of inheritance relations to which the implicit inheritance relations
	 *             must be added
	 * @throws LookupException
	 */
	protected void addImplicitInheritanceRelations(List<InheritanceRelation> list) {
		list.addAll(implicitNonMemberInheritanceRelations());
	}

	@Override
	public void addInheritanceRelation(InheritanceRelation relation) throws ChameleonProgrammerException {
		add(_inheritanceRelations,relation);
	}
	
	/**
	 * Remove redundant inheritance relations.
	 * @throws LookupException
	 */
	public void pruneInheritanceRelations() throws LookupException {
		Set<Type> types = new HashSet<Type>();
		Set<InheritanceRelation> toRemove = new HashSet<InheritanceRelation>();
		for(InheritanceRelation relation: nonMemberInheritanceRelations()) {
			Type superElement = (Type) relation.superElement();
			if(types.contains(superElement)) {
				toRemove.add(relation);
			} else {
				types.add(superElement);
			}
		}
		for(InheritanceRelation relation: toRemove) {
			removeNonMemberInheritanceRelation(relation);
		}
	}

	@Override
	public <D extends Member> List<D> localMembers(DeclarationSelector<D> selector) throws LookupException {
	//		return selector.selection(localMembers());
			return body().members(selector);
		}

	public void replace(TypeElement oldElement, TypeElement newElement) {
		body().replace(oldElement, newElement);
	}

	public static class MissingClassBody extends BasicProblem {
	
		public MissingClassBody(Element element) {
			super(element, "Class body is missing.");
		}
		
	}

	public List<ParameterBlock> parameterBlocks() {
		return _parameters.getOtherEnds();
	}

	public <P extends Parameter> void addParameter(Class<P> kind, P parameter) {
		parameterBlock(kind).add(parameter);
	}

	public <P extends Parameter> void removeParameter(Class<P> kind, P parameter) {
		parameterBlock(kind).add(parameter);
	}

	public <P extends Parameter> void replaceParameter(Class<P> kind, P oldParameter, P newParameter) {
		parameterBlock(kind).replace(oldParameter, newParameter);
	}

	@Override
	public List<? extends TypeElement> directlyDeclaredElements() {
		return body().elements();
	}

	public <T extends TypeElement> List<T> directlyDeclaredElements(Class<T> kind) {
  	List<TypeElement> tmp = (List<TypeElement>) directlyDeclaredElements();
  	new TypePredicate<TypeElement,T>(kind).filter(tmp);
    return (List<T>)tmp;
	}

//	@Override
//	public VerificationResult verifySelf() {
//		VerificationResult tmp = super.verifySelf();
//		if(body() != null) {
//		  return tmp;
//		} else {
//		  return tmp.and(new MissingClassBody(this));	
//		}
//	}

	public ClassWithBody(SimpleNameSignature sig) {
		super(sig);
		set(_body,new ClassBody());
		add(_parameters,new TypeParameterBlock());
	}

	public <P extends Parameter> void replaceAllParameter(Class<P> kind, List<P> newParameters) {
		int size = newParameters.size();
		List<P> old = parameters(kind);
		if(old.size() != size) {
			throw new ChameleonProgrammerException("Trying to substitute "+old.size()+" type parameters with "+size+" new parameters.");
		}
		for(int i = 0; i< size; i++) {
			replaceParameter(kind, old.get(i), newParameters.get(i));
		}
	}
	
	public void substituteParameters(List<ParameterSubstitution> substitutions) {
		for(ParameterSubstitution substitution: substitutions) {
			substituteParameters(substitution);
		}
	}
	public void substituteParameters(ParameterSubstitution substitution) {
		substituteParameters(substitution.parameterKind(),substitution.parameters());
	}

	public <P extends Parameter> void substituteParameters(Class<P> kind, List<P> parameters) {
		Iterator<P> parametersIterator = parameters(kind).iterator();
		Iterator<P> argumentsIterator = parameters.iterator();
		while (parametersIterator.hasNext()) {
			P parameter = parametersIterator.next();
			P argument = argumentsIterator.next();
			// The next call does not change the parent of 'argument'. It is stored in InstantiatedTypeParameter
			// using a regular reference.
			replaceParameter(kind, parameter, argument);
		}
	}
	
	public Declaration declarator() {
		return this;
	}

	public void addParameterBlock(ParameterBlock block) {
		if(block != null && parameterBlock(block.parameterType()) != null) {
			throw new ChameleonProgrammerException("There is already a parameter block containing the following kind of element "+block.parameterType().getName());
		}
		add(_parameters, block);
	}

	public Class<? extends Parameter> kindOf(ParameterBlock block) throws LookupException {
		for(ParameterBlock p: parameterBlocks()) {
			if(p.sameAs(block)) {
				return p.parameterType();
			}
		}
		return null;
	}

	public <P extends Parameter> ParameterBlock<P> parameterBlock(Class<P> kind) {
		for(ParameterBlock p: parameterBlocks()) {
			if(p.parameterType().equals(kind)) {
				return p;
			}
		}
		return null;
	}

	public void removeParameterBlock(ParameterBlock block) {
		remove(_parameters,block);
	}


}