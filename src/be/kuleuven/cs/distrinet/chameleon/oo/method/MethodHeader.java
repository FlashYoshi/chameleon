package be.kuleuven.cs.distrinet.chameleon.oo.method;

import be.kuleuven.cs.distrinet.chameleon.oo.member.DeclarationWithParametersHeader;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.chameleon.util.association.Single;

public abstract class MethodHeader extends DeclarationWithParametersHeader {

	public MethodHeader(TypeReference returnType) {
		setReturnTypeReference(returnType);
	}
	
//	public abstract MethodHeader clone();
	
	private Single<TypeReference> _typeReference = new Single<TypeReference>(this,"return type reference");

	public TypeReference returnTypeReference() {
		return _typeReference.getOtherEnd();
	}

	public void setReturnTypeReference(TypeReference type) {
		set(_typeReference,type);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		Type container = nearestAncestor(Type.class);
		int size = nbTypeParameters();
		if(size > 0) {
			builder.append('<');
			for(int i=1; i<=size;i++) {
				builder.append(typeParameter(i));
				if(i<size) {
					builder.append(",");
				}
			}
			builder.append('>');
		}
		builder.append(' ');
		builder.append(returnTypeReference().toString());
		builder.append(' ');
		builder.append((container == null ? "" : container.getFullyQualifiedName() +".")+signature().toString());
		return builder.toString();
	}

}
