package org.aikodi.chameleon.oo.variable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.property.ChameleonProperty;
import org.aikodi.chameleon.core.relation.StrictPartialOrder;
import org.aikodi.chameleon.core.scope.Scope;
import org.aikodi.chameleon.core.scope.ScopeProperty;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.exception.ModelException;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.member.DeclarationComparator;
import org.aikodi.chameleon.oo.member.HidesRelation;
import org.aikodi.chameleon.oo.member.Member;
import org.aikodi.chameleon.oo.member.MemberRelationSelector;
import org.aikodi.chameleon.oo.member.OverridesRelation;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.util.Util;

/**
 * @author Marko van Dooren
 */
public class RegularMemberVariable extends RegularVariable implements MemberVariable {

   /**
    * @param name
    */
   public RegularMemberVariable(String name, TypeReference type) {
      super(name, type, null);
   }

   /**
    * @param name
    */
   public RegularMemberVariable(String name, TypeReference type, Expression initCode) {
      super(name, type, initCode);
   }

   @Override
   public boolean uniSameAs(Element other) throws LookupException {
      if (other instanceof RegularMemberVariable) {
         RegularMemberVariable var = (RegularMemberVariable) other;
         Element parent = parent();
         Element otherParent = other.parent();
         return (parent != null && otherParent != null && otherParent.equals(parent) && sameSignatureAs(var));
      } else {
         return false;
      }
   }

   /**********
    * ACCESS *
    **********/

   @Override
   protected RegularMemberVariable cloneSelf() {
      return new RegularMemberVariable(name(), null, null);
   }

   @Override
   public List<Member> getIntroducedMembers() {
      return Util.<Member> createSingletonList(this);
   }

   @Override
   public List<Member> declaredMembers() {
      return Util.<Member> createSingletonList(this);
   }

   @Override
   public List<? extends Member> directlyOverriddenMembers() throws LookupException {
      return nearestAncestor(Type.class).membersDirectlyOverriddenBy(overridesSelector());
   }

   @Override
   public List<? extends Member> directlyAliasedMembers() throws LookupException {
      return nearestAncestor(Type.class).membersDirectlyAliasedBy(aliasSelector());
   }

   @Override
   public List<? extends Member> directlyAliasingMembers() throws LookupException {
      return nearestAncestor(Type.class).membersDirectlyAliasing(aliasSelector());
   }

   @Override
   public boolean overrides(Member other) throws LookupException {
      return overridesSelector().selects(other);
   }

   @Override
   public boolean canImplement(Member other) throws LookupException {
      StrictPartialOrder<Member> implementsRelation = language(ObjectOrientedLanguage.class).implementsRelation();
      return implementsRelation.contains(this, other);
   }

   @Override
   public boolean hides(Member other) throws LookupException {
      // StrictPartialOrder<Member> hidesRelation =
      // language(ObjectOrientedLanguage.class).hidesRelation();
      // return hidesRelation.contains(this, other);
      return ((HidesRelation) hidesSelector()).contains(this, other);
   }

   @Override
   public MemberRelationSelector<MemberVariable> overridesSelector() {
      return new MemberRelationSelector<MemberVariable>(MemberVariable.class, this, _overridesSelector);
   }

   public OverridesRelation<MemberVariable> overridesRelation() {
      return _overridesSelector;
   }

   private static OverridesRelation<MemberVariable> _overridesSelector = new OverridesRelation<MemberVariable>(
         MemberVariable.class);

   public HidesRelation<? extends Member> hidesSelector() {
      return _hidesSelector;
   }

   private static HidesRelation<RegularMemberVariable> _hidesSelector = new HidesRelation<RegularMemberVariable>(
         RegularMemberVariable.class);

//   @Override
//   public Set<? extends Member> overriddenMembers() throws LookupException {
//      List<Member> todo = (List<Member>) directlyOverriddenMembers();
//      Set<Member> result = new HashSet<Member>();
//      while (!todo.isEmpty()) {
//         Member m = todo.get(0);
//         todo.remove(0);
//         if (result.add(m)) {
//            todo.addAll(m.overriddenMembers());
//         }
//      }
//      return result;
//   }
//
   @Override
   public MemberRelationSelector<Member> aliasSelector() {
      return new MemberRelationSelector<Member>(Member.class, this, _aliasSelector);
   }

   private static DeclarationComparator<Member> _aliasSelector = new DeclarationComparator<Member>(Member.class);

}
