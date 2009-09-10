package chameleon.output;

import chameleon.core.MetamodelException;
import chameleon.core.element.Element;
import chameleon.tool.ConnectorImpl;

/**
 * @author Marko van Dooren
 */
public abstract class Syntax extends ConnectorImpl {

  public abstract String toCode(Element element) throws MetamodelException;

}
