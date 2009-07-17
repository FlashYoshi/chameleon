package chameleon.core.type.generics;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.lookup.LookupException;
import chameleon.core.member.FixedSignatureMember;
import chameleon.core.type.Type;

public abstract class GenericParameter<E extends GenericParameter<E>> extends FixedSignatureMember<E, Type, SimpleNameSignature,E>{
	
	public GenericParameter(SimpleNameSignature signature) {
		super(signature);
	}

//	public abstract boolean compatibleWith(GenericParameter other) throws LookupException;
	

}
