package chameleon.core.type.generics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.member.Member;
import chameleon.core.member.MemberImpl;
import chameleon.core.type.Type;

public class GenericParameter extends MemberImpl<GenericParameter, Type, SimpleNameSignature> {

	public GenericParameter(SimpleNameSignature signature) {
		setSignature(signature);
	}
	
	@Override
	public GenericParameter clone() {
		GenericParameter result = new GenericParameter(signature().clone());
		return result;
	}

	public Set<Member> getIntroducedMembers() {
		Set<Member> result = new HashSet<Member>();
		result.add(this);
		return result;
	}

	public Type getNearestType() {
		return getParent().getNearestType();
	}

	public List<? extends Element> getChildren() {
		List<? extends Element> result = new ArrayList<Element>();
		return result;
	}

	public Type bound() {
		need_union_types();
	}

}
