package chameleon.oo.type.generics;

import java.util.ArrayList;
import java.util.List;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeIndirection;
import chameleon.util.Pair;

/**
 * This class represents types created as a result of looking up (resolving) a generic parameter, which itself is
 * not a type.
 * 
 * @author Marko van Dooren
 */
public class FormalParameterType extends TypeIndirection {

	public FormalParameterType(SimpleNameSignature sig, Type aliasedType, FormalTypeParameter param) {
		super(sig, aliasedType);
		if(param == null) {
			throw new ChameleonProgrammerException("The formal type parameter corresponding to a constructed type cannot be null.");
		}
		_param = param;
	}
	
	
	@Override
	public boolean uniSameAs(Element type) throws LookupException {
		return type == this || 
		       ((type instanceof FormalParameterType) && (((FormalParameterType)type).parameter().sameAs(parameter())));
	}
	
	@Override
	public int hashCode() {
		return parameter().hashCode();
	}
	
	
	@Override
	public List<Type> getDirectSuperTypes() throws LookupException {
		List<Type> result = new ArrayList<Type>();
		result.add(aliasedType());
		return result;
	}


	@Override
	public String getFullyQualifiedName() {
		return signature().name();
	}

	public FormalTypeParameter parameter() {
		return _param;
	}
	
	private final FormalTypeParameter _param;

	@Override
	public FormalParameterType clone() {
		return new FormalParameterType((SimpleNameSignature) signature().clone(), aliasedType(), parameter());
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

	@Override
	public Type baseType() {
		return this;
	}


	public boolean uniSameAs(Type type, List<Pair<TypeParameter, TypeParameter>> trace) throws LookupException {
		return uniSameAs(type);
	}


	public Declaration declarator() {
		return parameter();
	}


}
