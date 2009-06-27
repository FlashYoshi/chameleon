package chameleon.core.expression;

import java.util.Set;

import org.rejuse.java.collections.RobustVisitor;

import chameleon.core.MetamodelException;
import chameleon.core.context.Target;
import chameleon.core.context.TargetContext;
import chameleon.core.namespace.Namespace;
import chameleon.core.statement.CheckedExceptionList;
import chameleon.core.statement.ExceptionSource;
import chameleon.core.type.Type;

/**
 * @author Marko van Dooren
 */

public abstract class Expression<E extends Expression> extends InvocationTarget<E,ExpressionContainer> {

	/**
	 * Return the type of this expression.
	 */
	public abstract Type getType() throws MetamodelException;

	/**
	 * @see {@link Target#targetContext()}
	 */
  public TargetContext targetContext() throws MetamodelException {
    return getType().targetContext();
  }


//  public void setTarget(InvocationTarget prec) {
//    throw new IllegalArgumentException();
//  }

  

 /*@
   @ public behavior
   @
   @ post \result == getParent().getNearestType();
   @*/
  public Type getNearestType() {
    return parent().getNearestType();
  }


  public boolean subOf(InvocationTarget target) throws MetamodelException {
    return false;
  }
  
  public boolean compatibleWith(InvocationTarget target) throws MetamodelException {
    return superOf(target) || target.subOf(this);
  }
  
  public void prefix(InvocationTarget target) throws MetamodelException {
    // Do nothing by default.
  }
  
  public void prefixRecursive(InvocationTarget target) throws MetamodelException {
    // Do nothing by default.
  }
  
  public void substituteParameter(String name, Expression expr) throws MetamodelException {
    // Do nothing by default.
  }

  /**
   * Repeated because the Java type checker is dumb.
   */
  public abstract E clone();

  
  /**
   * See superclass.
   */
  public final Set getExceptions() throws MetamodelException {
    final Set result = getDirectExceptions();
    try {
      new RobustVisitor() {
        public Object visit(Object element) throws MetamodelException {
          result.addAll(((InvocationTarget)element).getExceptions());
          return null;
        }

        public void unvisit(Object element, Object undo) {
          //NOP
        }
      }.applyTo(children());
      return result;
    }
    catch (MetamodelException e) {
      throw e;
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new Error();
    }
  }

 /*@
   @ public behavior
   @
   @ post \result == getPackage().getDefaultPackage(); 
   @*/
  public Namespace getDefaultNamespace() {
    return getNamespace().rootNamespace();
  }
  
  public CheckedExceptionList getCEL() throws MetamodelException {
    final CheckedExceptionList cel = getDirectCEL();
    try {
      new RobustVisitor() {
        public Object visit(Object element) throws MetamodelException {
          cel.absorb(((ExceptionSource)element).getCEL());
          return null;
        }

        public void unvisit(Object element, Object undo) {
          //NOP
        }
      }.applyTo(children());
      return cel;
    }
    catch (MetamodelException e) {
      throw e;
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new Error();
    }
  }
  
  public CheckedExceptionList getDirectCEL() throws MetamodelException {
    return new CheckedExceptionList(language());
  }
  
  public CheckedExceptionList getAbsCEL() throws MetamodelException {
    final CheckedExceptionList cel = getDirectAbsCEL();
    try {
      new RobustVisitor() {
        public Object visit(Object element) throws MetamodelException {
          cel.absorb(((ExceptionSource)element).getAbsCEL());
          return null;
        }

        public void unvisit(Object element, Object undo) {
          //NOP
        }
      }.applyTo(children());
      return cel;
    }
    catch (MetamodelException e) {
      throw e;
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new Error();
    }
  }
  
  public CheckedExceptionList getDirectAbsCEL() throws MetamodelException {
    return getDirectCEL();
  }
  
}
