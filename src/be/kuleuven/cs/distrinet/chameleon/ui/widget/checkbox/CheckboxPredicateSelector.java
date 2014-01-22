package be.kuleuven.cs.distrinet.chameleon.ui.widget.checkbox;

import be.kuleuven.cs.distrinet.chameleon.ui.widget.CheckboxSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.PredicateSelector;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.True;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;

public class CheckboxPredicateSelector<T> extends CheckboxSelector implements PredicateSelector<T> {

	public CheckboxPredicateSelector(UniversalPredicate<? super T, Nothing> predicate, String message) {
		this(predicate,message,false);
	}
	
	public CheckboxPredicateSelector(UniversalPredicate<? super T, Nothing> predicate, String message, boolean initialValue) {
		super(message,initialValue);
		_predicate = predicate;
	}

	@Override
	public UniversalPredicate<? super T, Nothing> predicate() {
		if(selected()) {
			return _predicate;
		} else {
			return new True();
		}
	}

	private UniversalPredicate<? super T,Nothing> _predicate;

}
