package be.kuleuven.cs.distrinet.chameleon.core.lookup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.DeclarationContainer;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;

/**
 * A lookup context for search declarations locally in a declaration container.
 * A local lookup context never delegates the search another lookup context. If no
 * element is found, it returns null.
 * 
 * @author Marko van Dooren
 */
public class LocalLookupContext<E extends DeclarationContainer> extends LookupContext {

//	public static int CREATED;
	
	/**
	 * Create a new local lookup strategy that searches for declarations in the
	 * given declaration container.
	 */
 /*@
   @ public behavior
   @
   @ pre declarationContainer != null;
   @
   @ post declarationContainer() == declarationContainer;
   @*/
	public LocalLookupContext(E declarationContainer) {
		_declarationContainer = declarationContainer;
//		CREATED++;
//		report(declarationContainer);
	}

//	public static boolean ENABLED=true;
//
//	protected void report(Element element) {
//		try {
//			if(ENABLED) {
////				if(LEXICAL_DONE.contains(element)) {
//					Integer current = ALLOCATORS.get(element.getClass());
//					Integer newValue = current == null ? 1 : current + 1;
//					ALLOCATORS.put(element.getClass(), newValue);
////				} else {
////					LEXICAL_DONE.add(element);
////				}
//			}
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//	}
//
//	public final static Map<Class,Integer> ALLOCATORS = new HashMap<>();

	/*
	 * The declaration container in which this local lookup strategy will search for declarations 
	 */
 /*@
	 @ private invariant _declarationContainer != null; 
	 @*/
	private E _declarationContainer;

	/**
	 * Return the declaration container referenced by this local lookup strategy.
	 * @return
	 */
 /*@
   @ public behavior
   @
   @ post \result != null;
   @*/
	public E declarationContainer() {
	  return _declarationContainer; 
	}

  /**
   * Return those declarations of this declaration container that are selected
   * by the given declaration selector. The default implementation delegates the work
   * to declarationContainer().declarations(selector).
   * 
   * @param <D> The type of the arguments selected by the given signature selector. This type
   *            should be inferred automatically.
   * @param selector
   * @return
   * @throws LookupException
   */
  protected <D extends Declaration> List<? extends SelectionResult> declarations(DeclarationSelector<D> selector) throws LookupException {
  	return selector.declarations(declarationContainer());
  }

  /**
   * Perform a local search in the connected declaration container using a declarations(selector) invocation. If
   * the resulting collection contains a single declaration, that declaration is returned. If the resulting
   * collection is empty, null is returned. If the resulting collection contains multiple elements, a
   * LookupException is thrown since the lookup is ambiguous.
   */
	@Override
	public <D extends Declaration> void lookUp(Collector<D> collector) throws LookupException {
		collector.process(declarations(collector.selector()));
	}

}
