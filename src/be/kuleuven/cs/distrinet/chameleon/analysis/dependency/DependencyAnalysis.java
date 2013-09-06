package be.kuleuven.cs.distrinet.chameleon.analysis.dependency;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import be.kuleuven.cs.distrinet.chameleon.analysis.Analysis;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.RootNamespace;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.contract.Contracts;
import be.kuleuven.cs.distrinet.rejuse.function.Function;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;

/**
 * An analysis the reports dependencies between elements and declarations.
 * 
 * You can choose of which elements you want to analyze the dependencies
 * via type parameter E, and the corresponding class object passed to
 * the constructor. You can choose in which dependencies you are interested
 * in a number of ways: 
 * 
 * <ol>
 *   <li>Via a predicate that is used to filter the elements 
 *       of which the dependencies must be computed.</li>
 *   <li>Via a predicate that filter the cross-references that must be analyzed.</li>
 *   <li>Via a function that maps dependency declarations to other dependency declarations.
 *       This can for example be used to map arrays to their component types. Typically
 *       a dependency on A[] is just a dependency on A. If there are no such cases in
 *       your analysis, simply provide the identity function.
 *   <li>Via a predicate that filters the resulting dependency declaration.</li>
 *   <li>Via a predicate that filters the entire dependency. Only use this when necessary
 *       as it is applied last. If the criteria for element, cross-reference, and dependency
 *       declaration are independent, it is best to use the more fine-grained predicates.
 *       That way, pruning happens earlier, avoiding pointless computations.</li>
 * </ol>
 * 
 * In the most interesting cases, E and T will typically be the same. Usually we
 * want to analyze dependencies between types or modules or other high-level
 * concepts. But you could also analyze on which methods the body of a for-loop depends.
 * 
 * @author Marko van Dooren
 *
 * @param <E> The type of the elements of which the dependencies must be analyzed.
 * @param <D> The type of the declarations that are analyzed as possible dependencies.
 */
public class DependencyAnalysis<E extends Element, D extends Declaration> extends Analysis<Element, DependencyResult> {

	
	public DependencyAnalysis(UniversalPredicate<E,Nothing> elementPredicate,
      UniversalPredicate<? super CrossReference<?>,Nothing> crossReferencePredicate,
      Function<D,D,Nothing> declarationMapper,
			UniversalPredicate<D,Nothing> declarationPredicate, 
			UniversalPredicate<? super Dependency<? super E, ? super CrossReference, ? super D>,Nothing> dependencyPredicate,
			HistoryFilter<E,D> historyFilter) {
		this(elementPredicate.type(), 
				 elementPredicate, 
				 crossReferencePredicate,
				 declarationPredicate.type(), 
				 declarationMapper,
				 declarationPredicate,
				 dependencyPredicate,
				 historyFilter);
	}

	public DependencyAnalysis(Class<? extends E> elementType,
														UniversalPredicate<? super E,Nothing> elementPredicate,
			                      UniversalPredicate<? super CrossReference<?>,Nothing> crossReferencePredicate,
														Class<D> declarationType,
			                      Function<D,D,Nothing> declarationMapper,
														UniversalPredicate<? super D,Nothing> declarationPredicate, 
														UniversalPredicate<? super Dependency<? super E, ? super CrossReference, ? super D>,Nothing> dependencyPredicate,
														HistoryFilter<E,D> historyFilter) {
		super(Element.class, new DependencyResult());
		Contracts.notNull(elementType, "The element type should not be null");
		Contracts.notNull(elementPredicate, "The element predicate should not be null");
		Contracts.notNull(crossReferencePredicate, "The cross-reference predicate should not be null");
		Contracts.notNull(declarationType, "The declaration type should not be null");
		Contracts.notNull(declarationMapper,"The declaration mapper should not be null");
		Contracts.notNull(declarationPredicate, "The declaration predicate should not be null");
		Contracts.notNull(dependencyPredicate, "The dependency predicate should not be null");
		_elementPredicate = elementPredicate;
		_declarationPredicate = declarationPredicate;
		_crossReferencePredicate = crossReferencePredicate.makeUniversal(CrossReference.class);
		_dependencyPredicate = _noSelfReference.and(dependencyPredicate);
		_declarationMapper = declarationMapper;
		_elementType = elementType;
		_declarationType = declarationType;
		_historyFilter = historyFilter;
		_dependencyFinder = new UniversalPredicate<D,Nothing>(_declarationType) {
			@Override
			public boolean uncheckedEval(D t) throws Nothing {
				return _declarationPredicate.eval(_declarationMapper.apply(t));
			}
		};

	}
	
	private UniversalPredicate<Dependency<? super E,? super CrossReference,? super D>,Nothing> _noSelfReference = (UniversalPredicate)new UniversalPredicate<Dependency, Nothing>(Dependency.class) {
		@Override
		public boolean uncheckedEval(Dependency t) throws Nothing {
			return t.source() != t.target();
		}
	};
	
	public static class HistoryFilter<E extends Element, D extends Declaration> {
		
		public boolean process(Dependency<E, CrossReference, D> dependency, DependencyResult result) {
			return true;
		}
		
		public boolean processSource(E source, DependencyResult result) {
			return true;
		}
	}
	
	public static class NOOP<E extends Element, D extends Declaration> extends HistoryFilter<E,D> {

	}
	
	private Class<D> _declarationType;

	private Class<? extends E> _elementType;

	private final UniversalPredicate<D,Nothing> _dependencyFinder;
	
	private final UniversalPredicate<?,Nothing> _elementPredicate;
	
	private final UniversalPredicate<? super D,Nothing> _declarationPredicate;
	
	private final UniversalPredicate<? super CrossReference<?>,Nothing> _crossReferencePredicate;
	
	public UniversalPredicate<? super Dependency<? super E, ? super CrossReference, ? super D>,Nothing> dependencyPredicate() {
		return _dependencyPredicate;
	}
	
//	private final UniversalPredicate<? super Pair<E, D>,Nothing> _dependencyPredicate;
	
	private final UniversalPredicate<? super Dependency<? super E, ? super CrossReference, ? super D>,Nothing> _dependencyPredicate;
	
	private final Function<D, D,Nothing> _declarationMapper;

	private HistoryFilter<E,D> _historyFilter;
	
	@Override
	public void doEnter(Element object) {
		if(_elementType.isInstance(object) && _elementPredicate.eval(object)) {
			_elements.addLast(object);
		}
	}
	
	@Override
	public void doExit(Element object) {
		if(! _elements.isEmpty()) {
			if(_elements.getLast() == object) {
				_elements.removeLast();
			}
		}
//		if(object instanceof RootNamespace) {
//			System.out.println(_stopwatch.elapsedTime(TimeUnit.MILLISECONDS));
//		}
	}
	
//	private Stopwatch _stopwatch = new Stopwatch();
	
	private LinkedList<Element> _elements = new LinkedList<>();
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doPerform(final Element element) throws Nothing {
		try {
			if(! _elements.isEmpty()) {
				boolean crossReferenceMatches = _crossReferencePredicate.eval(element);
				if(crossReferenceMatches) {
					CrossReference<?> cref = (CrossReference<?>) element;
//					_stopwatch.start();
					// This is the expensive one. File loading is relatively large part 
					// in the first run.
					Declaration decl = cref.getElement();
//					_stopwatch.stop();

					D unmappedDependee = decl.logical().nearestAncestorOrSelf(decl,_dependencyFinder);
					if(unmappedDependee != null) {
						for(Element e: _elements) {
							// SLOW mapping is applied twice, but that should be peanuts compared
							// to the rest of the analysis.
							// The search looks for an ancestor for which the mapping matches. Therefore
							// it must be mapped again afterwards.
							D dependee = _declarationMapper.apply(unmappedDependee);
							Dependency dependency = new Dependency(e,cref,dependee);
							if(_dependencyPredicate.eval(dependency)) {
								DependencyResult result = result();
								if(_historyFilter.process(dependency, result)) {
									result.add(dependency);
								}
							}
						}
					}
				}
			}
		} catch (LookupException e) {
//			if(_stopwatch.isRunning()) {
//				_stopwatch.stop();
//			}
			// No edge is added when we cannot determine the dependency
			e.printStackTrace();
		}
	}

}
