package org.aikodi.chameleon.plugin.output;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.exception.ModelException;
import org.aikodi.chameleon.plugin.LanguagePluginImpl;

/**
 * A syntax plugin is used to transform model elements into a String according to the
 * concrete syntax of the language.
 * 
 * TODO: We should have a processor that records the position of each element during input 
 *       (as we have for the Eclipse plugin) and use those positions and possibly the exact white
 *       space characters such as spaces and tabs to reconstruct the source code. Ideally, the output
 *       looks exactly like the input.
 *       
 * @author Marko van Dooren
 */
public abstract class Syntax extends LanguagePluginImpl {

  public abstract String toCode(Element element) throws ModelException;

}
