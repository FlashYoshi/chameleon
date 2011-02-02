package chameleon.core.namespacepart;

import java.util.List;

import org.apache.log4j.Logger;

import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;

/**
 * @author Marko van Dooren
 */
public abstract class Import<E extends Import> extends NamespaceElementImpl<E> {

  public Import() {
	}
  
  public abstract E clone();
  
  private static Logger logger = Logger.getLogger("lookup.import");

  protected Logger lookupLogger() {
  	return logger;
  }
  
// THIS IS COVERED IN NAMESPACEPART
  
  public abstract List<Declaration> directImports() throws LookupException;
  
  public abstract List<Declaration> demandImports() throws LookupException;

  public abstract <D extends Declaration> List<D> directImports(DeclarationSelector<D> selector) throws LookupException;
  
  public abstract <D extends Declaration> List<D> demandImports(DeclarationSelector<D> selector) throws LookupException;
}
