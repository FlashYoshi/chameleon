/**
 * 
 */
package org.aikodi.chameleon.oo.type.generics;

import java.util.List;
import java.util.Set;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeFixer;
import org.aikodi.chameleon.oo.type.TypeIndirection;
import org.aikodi.chameleon.util.Pair;
import org.aikodi.chameleon.util.Util;

public class InstantiatedParameterType extends TypeIndirection {

  public InstantiatedParameterType(String name, Type aliasedType, TypeParameter parameter) {
    super(name,aliasedType);
    setParameter(parameter);
  }

  @Override
  public String toString() {
    return parameter().toString();
  }

  @Override
  public List<Type> getDirectSuperTypes() throws LookupException {
    //			return aliasedType().getDirectSuperTypes();
    return Util.createNonNullList(aliasedType());
  }

  @Override
  public void accumulateAllSuperTypes(Set<Type> acc) throws LookupException {
    Type aliased =aliasedType();
    boolean add=true;
    for(Type acced: acc) {
      if(acced == aliased) {
        add=false;
        break;
      }
    }
    if(add) {
      acc.add(aliased);
      aliased.accumulateAllSuperTypes(acc);
    }
  }


  @Override
  public void newAccumulateAllSuperTypes(Set<Type> acc) throws LookupException {
    Type aliased =aliasedType();
    boolean add=true;
    for(Type acced: acc) {
      if(acced == aliased) {
        add=false;
        break;
      }
    }
    if(add) {
      aliased.newAccumulateSelfAndAllSuperTypes(acc);
    }
  }


  @Override
  public InstantiatedParameterType cloneSelf() {
    return new InstantiatedParameterType(name(), aliasedType(),parameter());
  }

  @Override
  public Type actualDeclaration() {
    return this;
  }

  @Override
  public boolean uniSameAs(Element element) throws LookupException {
    boolean result = false;
    if(element instanceof InstantiatedParameterType) {
      result = parameter().sameAs(((InstantiatedParameterType)element).parameter());
    } 
    if(! result) {
      result = element.sameAs(aliasedType());
    }
    return result;
  }

  @Override
  public boolean uniSameAs(Type other, TypeFixer trace) throws LookupException {

    boolean result = false;
    if(other instanceof InstantiatedParameterType) {
      TypeParameter mine = parameter();
      TypeParameter others = ((InstantiatedParameterType)other).parameter();
      result = mine.sameAs(others);
      if(! result) {
        TypeParameter firstParam = parameter();
        if(trace.contains(other, firstParam)) {
          return true;
        }
        trace.add(other, firstParam);
      }
    } 
    if(! result) {
      result = other.sameAs(aliasedType(),trace);
    }
    return result;
  }

  public TypeParameter parameter() {
    return _parameter;
  }

  private void setParameter(TypeParameter parameter) {
    _parameter = parameter;
  }

  private TypeParameter _parameter;

  @Override
  public Declaration declarator() {
    return parameter();
  }

  @Override
  public boolean properSubTypeOf(Type other) throws LookupException {
    return aliasedType().subTypeOf(other);
  }

  @Override
  public boolean properSuperTypeOf(Type type) throws LookupException {
    return type.subTypeOf(aliasedType());
  }

  @Override
  public boolean upperBoundAtLeastAsHighAs(Type other, TypeFixer trace) throws LookupException {
    TypeParameter secondParam = parameter();
    if(trace.contains(other,secondParam)) {
      return true;
    }
    if(other.sameAs(this)) {
      return true;
    }
    trace.add(other, secondParam);
    boolean result = other.upperBoundNotHigherThan(aliasedType(), trace);
    return result;
  }

  @Override
  public boolean upperBoundNotHigherThan(Type other, TypeFixer trace) throws LookupException {
    TypeParameter firstParam = parameter();
    if(trace.contains(other, firstParam)) {
      return true;
    }
    if(this.sameAs(other)) {
      return true;
    }
    trace.add(other, firstParam);
    boolean result = aliasedType().upperBoundNotHigherThan(other, trace);
    return result;
  }
  
}


