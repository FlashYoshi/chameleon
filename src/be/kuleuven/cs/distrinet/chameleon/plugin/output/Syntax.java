package be.kuleuven.cs.distrinet.chameleon.plugin.output;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.exception.ModelException;
import be.kuleuven.cs.distrinet.chameleon.plugin.LanguagePluginImpl;

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
