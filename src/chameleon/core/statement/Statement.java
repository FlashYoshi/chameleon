/*
 * Copyright 2000-2004 the Jnome development team.
 *
 * @author Marko van Dooren
 * @author Nele Smeets
 * @author Kristof Mertens
 * @author Jan Dockx
 *
 * This file is part of Jnome.
 *
 * Jnome is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Jnome is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Jnome; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package chameleon.core.statement;

import java.util.List;
import java.util.ListIterator;

import org.rejuse.java.collections.RobustVisitor;
import org.rejuse.predicate.TypePredicate;

import chameleon.core.MetamodelException;
import chameleon.core.language.Language;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespacepart.NamespacePartElementImpl;
import chameleon.core.type.Type;

/**
 * @author Marko van Dooren
 */

public abstract class Statement<E extends Statement> extends NamespacePartElementImpl<E,StatementContainer> implements ExceptionSource<E,StatementContainer> {


    protected Statement() {
    }


  public Type getNearestType() {
    return getParent().getNearestType();
  }

  public Namespace getNamespace() {
    return getNearestType().getNamespace();
  }
  
  public abstract E clone();

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
      }.applyTo(getExceptionSources());
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
      }.applyTo(getChildren());
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

	/**
	 * All children are of type ExceptionSource.
	 */
	public abstract List getChildren();


  public List getExceptionSources() {
    List result = getChildren();
    new TypePredicate(ExceptionSource.class).filter(result);
    return result;
  }

  public final List getSubStatements() {
    List result = getChildren();
    new TypePredicate(Statement.class).filter(result);
    return result;
  }

  public Language language() {
    return getNamespace().language();
  }

  public boolean before(Statement other) {
    StatementListContainer container = getNearestCommonStatementListContainer(other);
    List myParents = getAncestors();
    List otherParents = other.getAncestors();
    myParents.add(0, this);
    otherParents.add(0, other);
    Statement myAncestor = (Statement)myParents.get(myParents.indexOf(container) - 1);
    Statement otherAncestor = (Statement)otherParents.get(myParents.indexOf(container) - 1);
    return container.getIndexOf(myAncestor) < container.getIndexOf(otherAncestor);
  }

  public StatementListContainer getNearestCommonStatementListContainer(Statement other) {
    List myParents = getAncestors();
    List otherParents = other.getAncestors();
    ListIterator myIter = myParents.listIterator(myParents.size());
    ListIterator otherIter = otherParents.listIterator(myParents.size());
    Object common = null;
    while ((myIter.hasPrevious()) && (otherIter.hasPrevious())) {
      Object mine = myIter.previous();
      Object others = otherIter.previous();
      if(mine.equals(others)) {
        common = mine;
      }
    }
    if(common instanceof StatementListContainer) {
      return (StatementListContainer)common;
    }
    else {
      return null;
    }
  }
}
