package be.kuleuven.cs.distrinet.chameleon.util.action;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.rejuse.action.Action;

public class TopDown<T extends Element,E extends Exception> extends Sequence<T,E> {

	public TopDown(Walker<T,? extends E> walker) {
		super(walker.type(),walker, null);
		setSecond(new Recurse<T,E>(walker.type(),this));
	}

	public TopDown(Class<T> type, Action<? super T,? extends E> action) {
		super(type,action, null);
		setSecond(new Recurse<T,E>(type,this));
	}

	
}
