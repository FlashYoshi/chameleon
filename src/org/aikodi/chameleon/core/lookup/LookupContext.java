package org.aikodi.chameleon.core.lookup;

import org.aikodi.chameleon.core.declaration.Declaration;

/**
 * This is the top class of lookup contexts. A LookupContext, together with a
 * DeclarationSelector, is used to resolve cross-references such as type names, 
 * method invocations, and so forth.
 * 
 * The lookup is generalized such that implementing the lookup for a new kind of declaration
 * requires only the implementation of a declaration selector.
 * 
 * Terminology:
 * <ul>
 *    <li>Simple cross-reference: a cross-reference that is not explicitly declared relative to another
 *        cross-reference. For example a variable reference 'a' or method invocation 'f()'.</li>
 *    <li>Composite cross-reference: a cross-reference that is explicitly declared relative to another
 *        cross-reference. For example a variable reference 'a.b' or method invocation 'a.f()'.</li>
 *    <li>Target: a cross-reference relative to which another cross-reference is declared.
 *        For example in 'a.b.c', 'a.b' is the target of 'c' and 'a' is the target of 'b'.</li>
 *    <li>Target lookup context: a local lookup context that contains the declarations within a specific element.
 *        For example if 'A' if the type of 'a' in 'a.b', then the lookup procedure must only
 *        search for 'b' in 'A', and not proceed to the parent of 'A' if no element is found (as
 *        described below).</li>
 * </ul>
 * 
 * The responsibilities are divided as follows:
 * 
 * <ol>
 *   <li>The lookup context decides in which places in the model to look for
 *       potential declarations.</li>
 *       <ol>
 *          <li><p>For simple cross-references, the search is performed in the lexical context. This
 *          context is defined by the lookupContext() method in Element.</p>
 *          
 *          <p>By default, the lookupContext() method delegates to the parent element. If
 *          an element 'dc' is a DeclarationContainer, however, it must override the lookupContext()
 *          method. The method typically returns a LexicalLookupContext with references to two objects.
 *          First, a LocalLookupContext that is connected to 'dc'. Second, a LookupStrategySelector.
 *          The local lookup context will perform a local search. If no result is found, the selector is
 *          used to select a new lookup Context that will continue the search. In most cases, the selector
 *          will be a ParentLookupStrategySelector, which will continue with the lexical lookup context of
 *          the parent of 'dc'.</p>
 *          <p>A LocalLookupStrategy is connected to a DeclarationContainer, and uses the declarations(DeclarationSelector)
 *          method to perform a local search.</p>
 *          </li>
 *          <li><p>For composite cross-references, the cross-reference first resolves its target. Then
 *                the search continues in the target lookup strategy of the resulting declaration. The resulting
 *                declaration must implement the Target interface, which contains the targetLookupStrategy() method.</p>
 *          </li>
 *       </ol>
 *    <li>DeclarationContainer elements mark where in the model declarations can be found by
 *        overriding the lookupContext() method. In addition, the determine which 
 *        declarations can be found at that place in its declarations() and declarations(DeclarationSelector) methods.
 *    </li>
 *    <li>A DeclarationSelector performs that actual filtering of declarations. A selector decides if a declaration
 *    is appropriate, and determines the order in case multiple declarations are found. Each cross-reference
 *    typically defines its own selector. 
 *    </li>
 * </ol>      
 *            
 * Typical work involved to get the lookup mechanism to work for you language constructs.
 * 
 * <ol>
 *   <li>Make all elements that can be the result of a lookup implement Declaration.</li>
 *   <li>Make all elements that contain declarations implement DeclarationContainer, <em>and make
 *   them override the lookupContext() method</em>.
 *   </li>
 *   <li>Define a class that represents a cross-reference to the kind of declaration that you created.
 *   Make this class implement the CrossReference interface. If your declarations are refered to simply
 *   by a name, and have no order, then you can skip this step, and simply use SimpleReference&lt;D&gt; where
 *   D is the type of your declaration. Because of type erasure, you must provide D.class as well.
 *   </li>
 *   <li>Sit back and enjoy the fact that your lookup simply works.</li>
 * </ol>
 * 
 * 
 * @author Marko van Dooren
 */
public abstract class LookupContext {

	public LookupContext() {
	}

	public abstract <D extends Declaration> void lookUp(Collector<D> selector) throws LookupException;
	

	public void enableCache() {
		// Do nothing by default.
	}
	
	public void flushCache() {
	// Do nothing by default.
	}
}
