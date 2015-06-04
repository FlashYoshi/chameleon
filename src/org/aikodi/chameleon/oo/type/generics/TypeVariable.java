package org.aikodi.chameleon.oo.type.generics;

import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeFixer;
import org.aikodi.chameleon.oo.type.TypeIndirection;
import org.aikodi.chameleon.util.Pair;
import org.aikodi.chameleon.util.StackOverflowTracer;

import com.google.common.collect.ImmutableList;

/**
 * This class represents types created as a result of looking up (resolving) a generic parameter, which itself is
 * not a type.
 * 
 * @author Marko van Dooren
 */
public class TypeVariable extends TypeIndirection {

  public TypeVariable(String name, Type interval, FormalTypeParameter param) {
    super(name, interval);
    if(param == null) {
      throw new ChameleonProgrammerException("The formal type parameter corresponding to a constructed type cannot be null.");
    }
    _param = param;
  }

  @Override
  public boolean uniSameAs(Element type) throws LookupException {
    return type == this || 
        ((type instanceof TypeVariable) && (((TypeVariable)type).parameter().sameAs(parameter())));
  }

  @Override
  public int hashCode() {
    return parameter().hashCode();
  }


  @Override
  public List<Type> getDirectSuperTypes() throws LookupException {
    return ImmutableList.of(aliasedType());
  }


  @Override
  public String getFullyQualifiedName() {
    return name();
  }

  public FormalTypeParameter parameter() {
    return _param;
  }

  private final FormalTypeParameter _param;

  @Override
  public TypeVariable cloneSelf() {
    return new TypeVariable(name(), aliasedType(), parameter());
  }

  @Override
  public Verification verifySelf() {
    return Valid.create();
  }

  @Override
  public Type baseType() {
    return this;
  }


  @Override
  public boolean uniSameAs(Type type, TypeFixer trace) throws LookupException {
    return uniSameAs(type);
  }


  @Override
  public Declaration declarator() {
    return parameter();
  }

  /**
   * @{inheritDoc}
   */
  @Override
  public Type actualUpperBound() throws LookupException {
    return this;
  }

  /**
   * @{inheritDoc}
   */
  @Override
  public Type actualLowerBound() throws LookupException {
    return this;
  }

  /**
   * @{inheritDoc}
   */
  @Override
  public boolean uniSupertypeOf(Type other, TypeFixer trace) throws LookupException {
    if(trace.contains(other, this)) {
      return true;
    }
    trace.add(other,this);
    Type upperBound = other.upperBound();
    boolean result = false;
    if(upperBound != other) {
      result = upperBound.subtypeOf(this, trace.clone());
    }
    if(! result) {
      result = upperBound.subtypeOf(lowerBound(),trace);
    }
    return result;
  }

  private StackOverflowTracer tracer = new StackOverflowTracer(20);

  /**
   * @{inheritDoc}
   */
  @Override
  public boolean uniSubtypeOf(Type other, TypeFixer trace) throws LookupException {
    if(trace.contains(other, this)) {
      return true;
    }
    trace.add(other, this);
    Type lowerBound = other.lowerBound();
    boolean result = false;
    if(lowerBound != other) {
      result = this.subtypeOf(lowerBound, trace.clone());
    }
    if(! result) {
      result = upperBound().subtypeOf(lowerBound,trace);
    }
    return result;

    //    Type lowerBound = other.lowerBound();
    //    boolean result = upperBound().subtypeOf(lowerBound,trace);
    //    return result;
  }

  /**
   * @{inheritDoc}
   */
  @Override
  public Type upperBound() throws LookupException {
    return aliasedType().upperBound();
  }

  /**
   * @{inheritDoc}
   */
  @Override
  public Type lowerBound() throws LookupException {
    return aliasedType().lowerBound();
  }
}
