package chameleon.core.type.generics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rejuse.association.OrderedReferenceSet;

import chameleon.core.MetamodelException;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.ChameleonProgrammerException;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.member.FixedSignatureMember;
import chameleon.core.member.Member;
import chameleon.core.scope.LexicalScope;
import chameleon.core.scope.Scope;
import chameleon.core.type.ConstructedType;
import chameleon.core.type.Type;

/**
 * This class represents generic parameters as used in Java and C#.
 * 
 * @author Marko van Dooren
 */
public class FormalGenericParameter extends GenericParameter<FormalGenericParameter> {

	public FormalGenericParameter(SimpleNameSignature signature) {
		super(signature);
	}
	
  
	@Override
	public FormalGenericParameter clone() {
		FormalGenericParameter result = new FormalGenericParameter(signature().clone());
		for(TypeConstraint constraint: constraints()) {
			result.addConstraint(constraint.clone());
		}
		return result;
	}
	
//	/**
//	 * A generic parameter introduces itself. During lookup, the resolve() method will
//	 * introduce an alias.
//	 */
//	public List<Member> getIntroducedMembers() {
//		List<Member> result = new ArrayList<Member>();
//		result.add(this);
//		return result;
//	}
	
	/**
	 * Resolving a generic parameter results in a constructed type whose bound
	 * is the upper bound of this generic parameter as defined by the upperBound method.
	 */
	public Type selectionDeclaration() throws LookupException {
		return new ConstructedType(signature().clone(),upperBound(),this);
//		Type result = new LazyTypeAlias(signature().clone(), this);
//		result.setUniParent(parent());
//		return result;
	}
	
	public Type resolveForRoundTrip() throws LookupException {
  	Type result = new LazyTypeAlias(signature().clone(), this);
  	result.setUniParent(parent());
  	return result;
	}
	
	private static class LazyTypeAlias extends ConstructedType {

		public LazyTypeAlias(SimpleNameSignature sig, FormalGenericParameter param) {
			super(sig,null,param);
		}
		
		public Type aliasedType() {
			try {
				return parameter().upperBound();
			} catch (LookupException e) {
				throw new Error("LookupException while looking for aliasedType of a lazy alias",e);
			}
		}
	}
	


	public List<Element> children() {
		List<Element> result = new ArrayList<Element>();
		result.add(signature());
		result.addAll(constraints());
		return result;
	}
	
	private OrderedReferenceSet<FormalGenericParameter,TypeConstraint> _typeConstraints = new OrderedReferenceSet<FormalGenericParameter,TypeConstraint>(this);
	
	public List<TypeConstraint> constraints() {
		return _typeConstraints.getOtherEnds();
	}
	
	public void addConstraint(TypeConstraint constraint) {
		if(constraint != null) {
			_typeConstraints.add(constraint.parentLink());
		}
	}

	public Type upperBound() throws LookupException {
		Type result = language().getDefaultSuperClass();
		for(TypeConstraint constraint: constraints()) {
			result = result.intersection(constraint.upperBound());
		}
		return result;
	}

	public FormalGenericParameter alias(SimpleNameSignature signature) {
		throw new ChameleonProgrammerException();
	}


	@Override
	public boolean compatibleWith(GenericParameter other) throws LookupException {
		throw new ChameleonProgrammerException("DON'T KNOW WHAT TO DO HERE YET.");
	}

}
