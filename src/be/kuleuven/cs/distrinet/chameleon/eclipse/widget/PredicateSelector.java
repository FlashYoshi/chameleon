package be.kuleuven.cs.distrinet.chameleon.eclipse.widget;

import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;

public abstract class PredicateSelector<T>  {

	public PredicateSelector() {
	}
	
	public abstract <W> W createControl(WidgetFactory<W> factory);
	
	public abstract UniversalPredicate<? super T, Nothing> predicate();
	
//	public abstract void disposeWidget();
}
