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
package chameleon.core.method.exception;

import java.util.Set;

import chameleon.core.MetamodelException;
import chameleon.core.context.LookupException;
import chameleon.core.expression.Invocation;
import chameleon.core.type.Type;
import chameleon.core.type.TypeDescendantImpl;

/**
 * @author marko
 */

public abstract class ExceptionDeclaration<E extends ExceptionDeclaration> extends TypeDescendantImpl<E,ExceptionClause> {

  public ExceptionDeclaration() {
	}


  public abstract boolean compatibleWith(ExceptionClause clause) throws LookupException;

  public abstract E clone();

  public abstract Set getExceptionTypes(Invocation invocation) throws LookupException;

	/**
	 *
	 * @uml.property name="worstCaseExceptionTypes"
	 */
	public abstract Set getWorstCaseExceptionTypes()
		throws LookupException;


  public abstract boolean hasValidAccessibility() throws LookupException;

  /**
   * @param done
   * @return
   */
  public abstract boolean isAcyclic(Set done) throws LookupException;

  public Type getNearestType() {
	return parent().getNearestType();
  }


}
